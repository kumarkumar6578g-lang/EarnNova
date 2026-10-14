package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Offer
import com.example.data.model.Submission
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.Withdrawal
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EarnNovaRepository(val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be authenticated before performing database operations.")
  }

  // --- USER PROFILE & CHECK-IN ---

  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val docRef = db.collection("users").document(userId)
    docRef.snapshots()
      .map { snapshot -> snapshot.toObject(UserProfile::class.java) }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.READ, docRef.path)
        emit(null)
      }
      .collect { emit(it) }
  }

  suspend fun ensureUserProfile(user: FirebaseUser, referralInput: String? = null): UserProfile {
    val uid = user.uid
    val docRef = db.collection("users").document(uid)
    val snapshot = docRef.get().await()
    if (snapshot.exists()) {
      return snapshot.toObject(UserProfile::class.java) ?: UserProfile(
        userId = uid,
        displayName = user.displayName ?: "EarnNova Member",
        email = user.email ?: "",
        coinBalance = 0L
      )
    }

    // Generate unique referral code (e.g. NOVA + 4 hex chars)
    val generatedCode = "NOVA" + UUID.randomUUID().toString().take(4).uppercase(Locale.ROOT)
    val initialCoins = if (!referralInput.isNullOrBlank()) 500L else 100L // 500 welcome bonus if referred, else 100
    val profile = UserProfile(
      userId = uid,
      displayName = user.displayName?.ifBlank { "EarnNova Member" } ?: "EarnNova Member",
      email = user.email ?: "user@earnnova.com",
      referralCode = generatedCode,
      referredBy = referralInput?.trim(),
      coinBalance = initialCoins,
      lifetimeCoins = initialCoins,
      dailyStreak = 0L,
      lastCheckInDate = null,
      isAdmin = false
    )

    val payload = mapOf(
      "userId" to uid,
      "displayName" to profile.displayName,
      "email" to profile.email,
      "referralCode" to profile.referralCode,
      "referredBy" to (profile.referredBy ?: ""),
      "coinBalance" to profile.coinBalance,
      "lifetimeCoins" to profile.lifetimeCoins,
      "dailyStreak" to profile.dailyStreak,
      "lastCheckInDate" to "",
      "isAdmin" to false,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    docRef.set(payload).await()

    // Add initial welcome transaction
    val txRef = db.collection("transactions").document()
    txRef.set(
      mapOf(
        "transactionId" to txRef.id,
        "userId" to uid,
        "type" to if (!referralInput.isNullOrBlank()) "referral" else "earn",
        "title" to if (!referralInput.isNullOrBlank()) "Referral Signup Welcome Bonus" else "Welcome Starter Bonus",
        "coins" to initialCoins,
        "rupees" to (initialCoins / 100.0),
        "status" to "completed",
        "createdAt" to FieldValue.serverTimestamp()
      )
    ).await()

    return profile
  }

  suspend fun performDailyCheckIn(userId: String): Result<Long> {
    return try {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val userRef = db.collection("users").document(userId)
      val snapshot = userRef.get().await()
      val profile = snapshot.toObject(UserProfile::class.java)
        ?: return Result.failure(Exception("User not found"))

      if (profile.lastCheckInDate == today) {
        return Result.failure(Exception("Already claimed daily bonus today! Check back tomorrow."))
      }

      val newStreak = if (profile.lastCheckInDate != null) {
        // check if yesterday
        profile.dailyStreak + 1
      } else {
        1L
      }

      val bonusCoins = when (newStreak) {
        1L -> 100L
        2L -> 200L
        3L -> 300L
        else -> 500L
      }

      userRef.update(
        mapOf(
          "coinBalance" to FieldValue.increment(bonusCoins),
          "lifetimeCoins" to FieldValue.increment(bonusCoins),
          "dailyStreak" to newStreak,
          "lastCheckInDate" to today,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()

      val txRef = db.collection("transactions").document()
      txRef.set(
        mapOf(
          "transactionId" to txRef.id,
          "userId" to userId,
          "type" to "checkin",
          "title" to "Day $newStreak Daily Check-in Streak",
          "coins" to bonusCoins,
          "rupees" to (bonusCoins / 100.0),
          "status" to "completed",
          "createdAt" to FieldValue.serverTimestamp()
        )
      ).await()

      Result.success(bonusCoins)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "users/$userId")
      Result.failure(e)
    }
  }

  // --- OFFERS ---

  fun observeOffers(): Flow<List<Offer>> = flow {
    val collRef = db.collection("offers")
    collRef.snapshots()
      .map { snap ->
        snap.documents.mapNotNull { it.toObject(Offer::class.java) }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, "offers")
        emit(emptyList())
      }
      .collect { emit(it) }
  }

  suspend fun seedInitialOffersIfEmpty() {
    try {
      val existing = db.collection("offers").limit(1).get().await()
      if (!existing.isEmpty) return

      val defaultOffers = listOf(
        Offer(
          id = "off_prime_video",
          title = "Install Prime Video & Watch 1 Clip",
          description = "Download Amazon Prime Video app and stream any free preview or episode for at least 2 minutes.",
          category = "apps",
          rewardCoins = 1500L,
          instructions = "1. Click start offer\n2. Download Prime Video from Play Store\n3. Open app, stream any video for 2 minutes\n4. Take a screenshot of the video screen and submit here.",
          destinationUrl = "https://play.google.com/store/apps/details?id=com.amazon.avod.thirdpartyclient",
          status = "active",
          proofRequirement = "Screenshot of playback screen with timestamp",
          estimatedMinutes = 3
        ),
        Offer(
          id = "off_word_match",
          title = "Play Word Quest - Reach Level 5",
          description = "Engaging puzzle game! Install, reach level 5 and earn instant reward coins.",
          category = "games",
          rewardCoins = 2200L,
          instructions = "1. Tap Start and install Word Quest\n2. Clear levels 1 to 5\n3. Take screenshot of Level 5 Victory screen\n4. Submit proof with player nickname.",
          destinationUrl = "https://play.google.com",
          status = "active",
          proofRequirement = "Screenshot showing Level 5 completed",
          estimatedMinutes = 7
        ),
        Offer(
          id = "off_tech_survey",
          title = "Tech & Mobile Opinion Survey",
          description = "Short 8-question survey on digital payments and mobile shopping habits in India.",
          category = "surveys",
          rewardCoins = 1000L,
          instructions = "1. Answer all 8 questions honestly\n2. View survey completion thank you page\n3. Enter completion code / screenshot.",
          destinationUrl = "https://survey.earnnova.demo",
          status = "active",
          proofRequirement = "Survey completion code or screenshot",
          estimatedMinutes = 4
        ),
        Offer(
          id = "off_paytm_upi",
          title = "Paytm UPI Registration",
          description = "Register and verify UPI on Paytm. High reward task for verified accounts.",
          category = "apps",
          rewardCoins = 3000L,
          instructions = "1. Download or open Paytm\n2. Link bank account with UPI\n3. Capture screenshot of UPI ID profile screen (censor sensitive details)\n4. Submit.",
          destinationUrl = "https://paytm.com",
          status = "active",
          proofRequirement = "Screenshot of Paytm UPI profile badge",
          estimatedMinutes = 5
        ),
        Offer(
          id = "off_quick_review",
          title = "EarnNova Quick App Review",
          description = "Give us honest feedback and rating on how your earning experience has been.",
          category = "quick",
          rewardCoins = 800L,
          instructions = "1. Write a 2-sentence constructive review\n2. Submit feedback text in proof box below.",
          destinationUrl = "https://earnnova.app",
          status = "active",
          proofRequirement = "Constructive feedback text",
          estimatedMinutes = 2
        )
      )

      for (offer in defaultOffers) {
        db.collection("offers").document(offer.id).set(
          mapOf(
            "title" to offer.title,
            "description" to offer.description,
            "category" to offer.category,
            "rewardCoins" to offer.rewardCoins,
            "instructions" to offer.instructions,
            "destinationUrl" to offer.destinationUrl,
            "status" to offer.status,
            "proofRequirement" to offer.proofRequirement,
            "estimatedMinutes" to offer.estimatedMinutes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.BATCH, "offers")
    }
  }

  // --- SUBMISSIONS ---

  fun observeUserSubmissions(userId: String): Flow<List<Submission>> = flow {
    val collRef = db.collection("submissions")
    collRef.whereEqualTo("userId", userId)
      .snapshots()
      .map { snap ->
        snap.documents.mapNotNull { it.toObject(Submission::class.java) }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, "submissions?userId=$userId")
        emit(emptyList())
      }
      .collect { emit(it) }
  }

  suspend fun submitOfferProof(
    userId: String,
    userEmail: String,
    offer: Offer,
    proofDetails: String,
    proofImageUrl: String
  ): Result<String> {
    return try {
      val subRef = db.collection("submissions").document()
      val payload = mapOf(
        "userId" to userId,
        "userEmail" to userEmail,
        "offerId" to offer.id,
        "offerTitle" to offer.title,
        "rewardCoins" to offer.rewardCoins,
        "proofDetails" to proofDetails,
        "proofImageUrl" to proofImageUrl,
        "status" to "pending",
        "adminFeedback" to "",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      subRef.set(payload).await()
      Result.success(subRef.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "submissions")
      Result.failure(e)
    }
  }

  // --- CASHOUT / WITHDRAWALS ---

  fun observeUserWithdrawals(userId: String): Flow<List<Withdrawal>> = flow {
    val collRef = db.collection("withdrawals")
    collRef.whereEqualTo("userId", userId)
      .snapshots()
      .map { snap ->
        snap.documents.mapNotNull { it.toObject(Withdrawal::class.java) }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, "withdrawals?userId=$userId")
        emit(emptyList())
      }
      .collect { emit(it) }
  }

  suspend fun requestCashout(
    userId: String,
    userEmail: String,
    amountRupees: Long,
    coinsRequired: Long,
    payoutMethod: String,
    payoutAddress: String
  ): Result<String> {
    return try {
      val userRef = db.collection("users").document(userId)
      val userSnap = userRef.get().await()
      val profile = userSnap.toObject(UserProfile::class.java)
        ?: return Result.failure(Exception("User profile not found"))

      if (profile.coinBalance < coinsRequired) {
        return Result.failure(
          Exception("Insufficient balance! You have ${profile.coinBalance} coins, but need $coinsRequired coins.")
        )
      }

      // Deduct coins
      userRef.update(
        mapOf(
          "coinBalance" to FieldValue.increment(-coinsRequired),
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()

      // Create withdrawal doc
      val wRef = db.collection("withdrawals").document()
      wRef.set(
        mapOf(
          "userId" to userId,
          "userEmail" to userEmail,
          "amountRupees" to amountRupees,
          "coinsDebited" to coinsRequired,
          "payoutMethod" to payoutMethod,
          "payoutAddress" to payoutAddress,
          "status" to "pending",
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()

      // Record transaction
      val txRef = db.collection("transactions").document()
      txRef.set(
        mapOf(
          "transactionId" to txRef.id,
          "userId" to userId,
          "type" to "redeem",
          "title" to "Demo Cashout ₹$amountRupees ($payoutMethod)",
          "coins" to -coinsRequired,
          "rupees" to amountRupees.toDouble(),
          "status" to "pending",
          "createdAt" to FieldValue.serverTimestamp()
        )
      ).await()

      Result.success(wRef.id)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "withdrawals")
      Result.failure(e)
    }
  }

  // --- TRANSACTIONS ---

  fun observeUserTransactions(userId: String): Flow<List<TransactionRecord>> = flow {
    val collRef = db.collection("transactions")
    collRef.whereEqualTo("userId", userId)
      .snapshots()
      .map { snap ->
        snap.documents.mapNotNull { it.toObject(TransactionRecord::class.java) }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, "transactions?userId=$userId")
        emit(emptyList())
      }
      .collect { emit(it) }
  }

  // --- ADMIN FUNCTIONS ---

  fun observeAllSubmissions(): Flow<List<Submission>> = flow {
    db.collection("submissions")
      .snapshots()
      .map { snap -> snap.documents.mapNotNull { it.toObject(Submission::class.java) } }
      .catch { emit(emptyList()) }
      .collect { emit(it) }
  }

  fun observeAllWithdrawals(): Flow<List<Withdrawal>> = flow {
    db.collection("withdrawals")
      .snapshots()
      .map { snap -> snap.documents.mapNotNull { it.toObject(Withdrawal::class.java) } }
      .catch { emit(emptyList()) }
      .collect { emit(it) }
  }

  suspend fun reviewSubmission(submission: Submission, approve: Boolean, feedback: String): Result<Unit> {
    return try {
      val subRef = db.collection("submissions").document(submission.id)
      val newStatus = if (approve) "approved" else "rejected"

      subRef.update(
        mapOf(
          "status" to newStatus,
          "adminFeedback" to feedback,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()

      if (approve) {
        // Credit coins to user
        val userRef = db.collection("users").document(submission.userId)
        userRef.update(
          mapOf(
            "coinBalance" to FieldValue.increment(submission.rewardCoins),
            "lifetimeCoins" to FieldValue.increment(submission.rewardCoins),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()

        // Write ledger transaction
        val txRef = db.collection("transactions").document()
        txRef.set(
          mapOf(
            "transactionId" to txRef.id,
            "userId" to submission.userId,
            "type" to "earn",
            "title" to "Approved Task: ${submission.offerTitle}",
            "coins" to submission.rewardCoins,
            "rupees" to (submission.rewardCoins / 100.0),
            "status" to "completed",
            "createdAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }

      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "submissions/${submission.id}")
      Result.failure(e)
    }
  }

  suspend fun reviewWithdrawal(withdrawal: Withdrawal, approve: Boolean): Result<Unit> {
    return try {
      val wRef = db.collection("withdrawals").document(withdrawal.id)
      val newStatus = if (approve) "approved" else "rejected"

      wRef.update(
        mapOf(
          "status" to newStatus,
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()

      if (!approve) {
        // Refund coins back to user
        val userRef = db.collection("users").document(withdrawal.userId)
        userRef.update(
          mapOf(
            "coinBalance" to FieldValue.increment(withdrawal.coinsDebited),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()

        val txRef = db.collection("transactions").document()
        txRef.set(
          mapOf(
            "transactionId" to txRef.id,
            "userId" to withdrawal.userId,
            "type" to "earn",
            "title" to "Refund: Demo Cashout Rejected",
            "coins" to withdrawal.coinsDebited,
            "rupees" to (withdrawal.coinsDebited / 100.0),
            "status" to "completed",
            "createdAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }

      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "withdrawals/${withdrawal.id}")
      Result.failure(e)
    }
  }

  suspend fun createCustomOffer(offer: Offer): Result<Unit> {
    return try {
      val offerRef = db.collection("offers").document()
      offerRef.set(
        mapOf(
          "title" to offer.title,
          "description" to offer.description,
          "category" to offer.category,
          "rewardCoins" to offer.rewardCoins,
          "instructions" to offer.instructions,
          "destinationUrl" to offer.destinationUrl,
          "status" to "active",
          "proofRequirement" to offer.proofRequirement,
          "estimatedMinutes" to offer.estimatedMinutes,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
      ).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "offers")
      Result.failure(e)
    }
  }
}
