package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold500
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(
  onAuthSuccess: (inviteCode: String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  val languageState = LocalAppLanguage.current

  var inviteCode by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Emerald50,
            Slate50,
            PureWhite
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Language switch at top right
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = PureWhite,
          shadowElevation = 2.dp,
          modifier = Modifier.testTag("auth_language_toggle")
        ) {
          Button(
            onClick = { languageState.toggle() },
            colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Language",
              tint = Emerald600,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (languageState.currentLanguage.code == "EN") "English | हिन्दी" else "हिन्दी | English",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Slate800
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Logo Image
      Image(
        painter = painterResource(id = R.drawable.ic_earnnova_logo),
        contentDescription = "EarnNova Logo",
        modifier = Modifier
          .size(120.dp)
          .clip(CircleShape)
      )

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "EarnNova",
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Slate900,
        letterSpacing = 0.5.sp
      )

      Text(
        text = loc(
          "Easy way to earn rewards & real rupees online!",
          "ऑनलाइन वास्तविक पुरस्कार और सिक्के कमाने का आसान तरीका!"
        ),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate700,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )

      Text(
        text = loc(
          "Complete tasks, try top apps, refer friends, and redeem coins directly to UPI or Paytm.",
          "टास्क पूरे करें, नए ऐप्स आज़माएं, दोस्तों को रेफ़र करें और सीधे यूपीआई या पेटीएम पर रिडीम करें।"
        ),
        fontSize = 13.sp,
        color = Slate500,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 20.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Referral / Invite Code (Optional)
      OutlinedTextField(
        value = inviteCode,
        onValueChange = { inviteCode = it.uppercase() },
        label = {
          Text(
            text = loc("Enter invite code (Optional +500 coins)", "रेफ़रल कोड दर्ज करें (+500 सिक्के)")
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.CardGiftcard,
            contentDescription = "Invite Code",
            tint = Emerald600
          )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Emerald600,
          unfocusedBorderColor = Slate200,
          focusedContainerColor = PureWhite,
          unfocusedContainerColor = PureWhite
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("invite_code_input")
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Error message if any
      if (errorMessage != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Text(
            text = errorMessage ?: "",
            color = Rose600,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
          )
        }
      }

      // Google Sign-In Button
      Button(
        onClick = {
          isLoading = true
          errorMessage = null
          performGoogleSignIn(
            context = context,
            credentialManager = credentialManager,
            coroutineScope = coroutineScope,
            onSuccess = {
              isLoading = false
              onAuthSuccess(inviteCode.ifBlank { null })
            },
            onError = { error ->
              isLoading = false
              errorMessage = error
            },
            onCancelled = {
              isLoading = false
            }
          )
        },
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Emerald600,
          disabledContainerColor = Emerald100
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("google_sign_in_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = PureWhite,
            strokeWidth = 2.5.dp
          )
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "G",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = Gold500
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = loc("Sign in with Google", "गूगल के साथ साइन इन करें"),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Trust badge & Legal disclaimer
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Secure",
          modifier = Modifier.size(16.dp),
          tint = Emerald600
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = loc(
            "100% Secure & Verified Rewards System",
            "100% सुरक्षित और सत्यापित रिवॉर्ड्स प्रणाली"
          ),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = Slate500
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = loc(
          "By continuing, you agree to our Terms of Service & Privacy Policy.",
          "आगे बढ़कर, आप हमारी सेवा की शर्तें और गोपनीयता नीति से सहमत होते हैं।"
        ),
        fontSize = 11.sp,
        color = Slate500,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }
  }
}

fun performGoogleSignIn(
  context: Context,
  credentialManager: CredentialManager,
  coroutineScope: CoroutineScope,
  onSuccess: () -> Unit,
  onError: (String) -> Unit,
  onCancelled: () -> Unit
) {
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onError("Google Sign-In configuration missing: default_web_client_id")
    return
  }

  val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
  val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

  coroutineScope.launch {
    try {
      val result = credentialManager.getCredential(context as Activity, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onSuccess()
      } else {
        onError("Unexpected credential type received.")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.w("Auth", "Google Sign-In cancelled or dismissed: ${e.message}", e)
      onCancelled()
    } catch (e: Exception) {
      Log.e("Auth", "Google Sign-In failed", e)
      onError(e.localizedMessage ?: "Sign-in failed. Please retry.")
    }
  }
}
