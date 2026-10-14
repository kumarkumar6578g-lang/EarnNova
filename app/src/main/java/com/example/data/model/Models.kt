package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val referralCode: String = "",
  val referredBy: String? = null,
  val coinBalance: Long = 0L,
  val lifetimeCoins: Long = 0L,
  val dailyStreak: Long = 0L,
  val lastCheckInDate: String? = null,
  val isAdmin: Boolean = false,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Offer(
  @DocumentId val id: String = "",
  val title: String = "",
  val description: String = "",
  val category: String = "apps", // apps, games, surveys, quick
  val rewardCoins: Long = 500L,
  val instructions: String = "",
  val destinationUrl: String = "",
  val status: String = "active", // active, paused
  val proofRequirement: String = "Upload completion screenshot or account ID",
  val estimatedMinutes: Long = 5L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Submission(
  @DocumentId val id: String = "",
  val userId: String = "",
  val userEmail: String = "",
  val offerId: String = "",
  val offerTitle: String = "",
  val rewardCoins: Long = 0L,
  val proofDetails: String = "",
  val proofImageUrl: String = "",
  val status: String = "pending", // pending, approved, rejected
  val adminFeedback: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Withdrawal(
  @DocumentId val id: String = "",
  val userId: String = "",
  val userEmail: String = "",
  val amountRupees: Long = 0L,
  val coinsDebited: Long = 0L,
  val payoutMethod: String = "UPI", // UPI, Paytm, Bank
  val payoutAddress: String = "",
  val status: String = "pending", // pending, approved, rejected
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class TransactionRecord(
  @DocumentId val id: String = "",
  val userId: String = "",
  val type: String = "earn", // earn, redeem, checkin, referral
  val title: String = "",
  val coins: Long = 0L,
  val rupees: Double = 0.0,
  val status: String = "completed", // completed, pending, rejected
  @ServerTimestamp val createdAt: Timestamp? = null
)
