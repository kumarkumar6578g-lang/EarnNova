package com.example

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.model.Offer
import com.example.data.model.Submission
import com.example.data.model.TransactionRecord
import com.example.data.model.UserProfile
import com.example.data.model.Withdrawal
import com.example.data.repository.EarnNovaRepository
import com.example.ui.navigation.EarnNovaBottomNav
import com.example.ui.components.TopRewardsBar
import com.example.ui.localization.LanguageState
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.navigation.MainTab
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CashoutScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MyOffersScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReferScreen
import com.example.ui.theme.EarnNovaTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class ActiveSubScreen {
  MAIN,
  PROFILE,
  HISTORY,
  ADMIN
}

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val languageState = remember { LanguageState() }

      CompositionLocalProvider(LocalAppLanguage provides languageState) {
        EarnNovaTheme {
          EarnNovaApp()
        }
      }
    }
  }
}

@Composable
fun EarnNovaApp() {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val repository = remember { EarnNovaRepository(context) }
  val credentialManager = remember { CredentialManager.create(context) }

  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
  var pendingInviteCode by remember { mutableStateOf<String?>(null) }

  // Listen to Firebase Auth state
  DisposableEffect(Unit) {
    val authListener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
    }
    Firebase.auth.addAuthStateListener(authListener)
    onDispose {
      Firebase.auth.removeAuthStateListener(authListener)
    }
  }

  // Silent auto sign-in attempt on startup if no current session
  LaunchedEffect(Unit) {
    if (currentUser == null) {
      attemptSilentAutoSignIn(
        activity = context as? Activity,
        credentialManager = credentialManager,
        onSuccess = { currentUser = Firebase.auth.currentUser }
      )
    }
  }

  if (currentUser == null) {
    AuthScreen(
      onAuthSuccess = { inviteCode ->
        pendingInviteCode = inviteCode
        currentUser = Firebase.auth.currentUser
      }
    )
  } else {
    val user = currentUser!!

    // State collections
    val userProfile by repository.observeUserProfile(user.uid).collectAsState(initial = null)
    val offers by repository.observeOffers().collectAsState(initial = emptyList())
    val submissions by repository.observeUserSubmissions(user.uid).collectAsState(initial = emptyList())
    val withdrawals by repository.observeUserWithdrawals(user.uid).collectAsState(initial = emptyList())
    val transactions by repository.observeUserTransactions(user.uid).collectAsState(initial = emptyList())

    // Admin streams (cached for admin dashboard)
    val allSubmissions by repository.observeAllSubmissions().collectAsState(initial = emptyList())
    val allWithdrawals by repository.observeAllWithdrawals().collectAsState(initial = emptyList())

    // Navigation state
    var currentTab by remember { mutableStateOf(MainTab.OFFERS) }
    var currentSubScreen by remember { mutableStateOf(ActiveSubScreen.MAIN) }
    var isClaimingDaily by remember { mutableStateOf(false) }

    // Ensure user profile & initial offers exist
    LaunchedEffect(user.uid) {
      repository.ensureUserProfile(user, pendingInviteCode)
      pendingInviteCode = null
      repository.seedInitialOffersIfEmpty()
    }

    when (currentSubScreen) {
      ActiveSubScreen.PROFILE -> {
        ProfileScreen(
          userProfile = userProfile,
          onBack = { currentSubScreen = ActiveSubScreen.MAIN },
          onOpenHistory = { currentSubScreen = ActiveSubScreen.HISTORY },
          onOpenAdmin = { currentSubScreen = ActiveSubScreen.ADMIN },
          onSignOut = {
            Firebase.auth.signOut()
            coroutineScope.launch {
              try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
              } catch (_: Exception) {
              }
              currentUser = null
              currentSubScreen = ActiveSubScreen.MAIN
            }
          }
        )
      }

      ActiveSubScreen.HISTORY -> {
        HistoryScreen(
          transactions = transactions,
          onBack = { currentSubScreen = ActiveSubScreen.MAIN }
        )
      }

      ActiveSubScreen.ADMIN -> {
        AdminDashboardScreen(
          submissions = allSubmissions,
          withdrawals = allWithdrawals,
          offers = offers,
          repository = repository,
          onBack = { currentSubScreen = ActiveSubScreen.MAIN }
        )
      }

      ActiveSubScreen.MAIN -> {
        Scaffold(
          topBar = {
            TopRewardsBar(
              userProfile = userProfile,
              onProfileClick = { currentSubScreen = ActiveSubScreen.PROFILE },
              onCoinBalanceClick = { currentTab = MainTab.CASHOUT }
            )
          },
          bottomBar = {
            EarnNovaBottomNav(
              currentTab = currentTab,
              onTabSelected = { currentTab = it }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentTab) {
              MainTab.OFFERS -> {
                OffersScreen(
                  userProfile = userProfile,
                  offers = offers,
                  repository = repository,
                  isClaimingDaily = isClaimingDaily,
                  onClaimDailyReward = {
                    isClaimingDaily = true
                    coroutineScope.launch {
                      val result = repository.performDailyCheckIn(user.uid)
                      isClaimingDaily = false
                      if (result.isSuccess) {
                        Toast.makeText(
                          context,
                          "Daily bonus +${result.getOrNull()} coins claimed! 🎉",
                          Toast.LENGTH_SHORT
                        ).show()
                      } else {
                        Toast.makeText(
                          context,
                          result.exceptionOrNull()?.message ?: "Check-in failed",
                          Toast.LENGTH_SHORT
                        ).show()
                      }
                    }
                  }
                )
              }

              MainTab.MY_OFFERS -> {
                MyOffersScreen(
                  submissions = submissions,
                  onExploreOffersClick = { currentTab = MainTab.OFFERS }
                )
              }

              MainTab.REFER -> {
                ReferScreen(
                  userProfile = userProfile
                )
              }

              MainTab.CASHOUT -> {
                CashoutScreen(
                  userProfile = userProfile,
                  withdrawals = withdrawals,
                  repository = repository,
                  onOpenHistoryClick = { currentSubScreen = ActiveSubScreen.HISTORY }
                )
              }
            }
          }
        }
      }
    }
  }
}

suspend fun attemptSilentAutoSignIn(
  activity: Activity?,
  credentialManager: CredentialManager,
  onSuccess: () -> Unit
) {
  if (activity == null) return
  val clientId = try {
    activity.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    return
  }

  val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(true)
    .setServerClientId(clientId)
    .setAutoSelectEnabled(true)
    .build()

  val request = androidx.credentials.GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

  try {
    val result = credentialManager.getCredential(activity, request)
    val credential = result.credential
    if (credential is androidx.credentials.CustomCredential &&
      credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
      val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
      val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
      Firebase.auth.signInWithCredential(authCredential).await()
      onSuccess()
    }
  } catch (_: Exception) {
    // Silent attempt failed gracefully; user will click the visible Google button
  }
}
