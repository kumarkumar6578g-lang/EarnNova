package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold700
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Rose100
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun ProfileScreen(
  userProfile: UserProfile?,
  onBack: () -> Unit,
  onOpenHistory: () -> Unit,
  onOpenAdmin: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val languageState = LocalAppLanguage.current

  var showFeedbackDialog by remember { mutableStateOf(false) }
  var showHelpDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var feedbackInput by remember { mutableStateOf("") }

  val uid = userProfile?.userId ?: "16489412"
  val coins = userProfile?.coinBalance ?: 0L

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
  ) {
    // Header
    Surface(
      color = PureWhite,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("profile_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Slate800
          )
        }
        Text(
          text = loc("Me / Profile", "मेरा प्रोफ़ाइल"),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // User Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(Emerald600),
            contentAlignment = Alignment.Center
          ) {
            val initial = userProfile?.displayName?.firstOrNull()?.uppercase() ?: "U"
            Text(
              text = initial,
              color = PureWhite,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = userProfile?.displayName?.ifBlank { "EarnNova Member" } ?: "EarnNova Member",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = userProfile?.email ?: "user@earnnova.com",
            fontSize = 12.sp,
            color = Slate400
          )

          Spacer(modifier = Modifier.height(8.dp))

          // UID pill with copy
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Slate800,
            modifier = Modifier.clickable {
              val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              cm.setPrimaryClip(ClipData.newPlainText("UID", uid))
              Toast.makeText(context, "User ID copied", Toast.LENGTH_SHORT).show()
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "UID: ${uid.take(12)}",
                fontSize = 11.sp,
                color = Slate400
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = Slate400,
                modifier = Modifier.size(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Balance tag
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Gold100
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🪙 $coins coins ≈ ₹${"%.2f".format(coins / 100.0)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gold700)
            }
          }
        }
      }

      // Settings List Items
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          ProfileOptionRow(
            icon = Icons.Default.History,
            iconTint = Emerald600,
            title = loc("Transaction & Redeem History", "लेनदेन और निकासी का इतिहास"),
            onClick = onOpenHistory
          )

          ProfileDivider()

          ProfileOptionRow(
            icon = Icons.Default.Language,
            iconTint = Emerald600,
            title = loc("Language / भाषा", "भाषा / Language"),
            trailingText = languageState.currentLanguage.label,
            onClick = { languageState.toggle() }
          )

          ProfileDivider()

          ProfileOptionRow(
            icon = Icons.AutoMirrored.Filled.Help,
            iconTint = Emerald600,
            title = loc("Help & FAQ", "सहायता और अक्सर पूछे जाने वाले प्रश्न"),
            onClick = { showHelpDialog = true }
          )

          ProfileDivider()

          ProfileOptionRow(
            icon = Icons.Default.Feedback,
            iconTint = Emerald600,
            title = loc("Send Feedback", "प्रतिक्रिया भेजें"),
            onClick = { showFeedbackDialog = true }
          )

          ProfileDivider()

          ProfileOptionRow(
            icon = Icons.Default.Policy,
            iconTint = Emerald600,
            title = loc("Privacy Policy & Terms", "गोपनीयता नीति और शर्तें"),
            onClick = { showPrivacyDialog = true }
          )

          ProfileDivider()

          ProfileOptionRow(
            icon = Icons.Default.SystemUpdate,
            iconTint = Emerald600,
            title = loc("Version Update", "संस्करण अद्यतन"),
            trailingText = "v1.0.5",
            onClick = {
              Toast.makeText(context, "EarnNova is up to date!", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      // Admin Dashboard Switcher Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        ProfileOptionRow(
          icon = Icons.Default.AdminPanelSettings,
          iconTint = Gold700,
          title = loc("Protected Admin Portal", "सुरक्षित व्यवस्थापक पोर्टल"),
          trailingText = "Admin Access",
          onClick = onOpenAdmin
        )
      }

      // Logout Button
      Button(
        onClick = onSignOut,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Rose100),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("profile_logout_button")
      ) {
        Icon(
          imageVector = Icons.Default.Logout,
          contentDescription = "Logout",
          tint = Rose600,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = loc("Log Out", "लॉग आउट करें"),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Rose600
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Feedback Dialog
  if (showFeedbackDialog) {
    AlertDialog(
      onDismissRequest = { showFeedbackDialog = false },
      title = { Text(loc("Send Feedback", "प्रतिक्रिया भेजें"), fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(loc("We appreciate your feedback to improve EarnNova:", "EarnNova को बेहतर बनाने के लिए आपकी प्रतिक्रिया की सराहना करते हैं:"))
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = feedbackInput,
            onValueChange = { feedbackInput = it },
            placeholder = { Text(loc("Type your comments here...", "अपनी टिप्पणी यहां लिखें...")) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showFeedbackDialog = false
            Toast.makeText(context, "Thank you! Your feedback has been recorded.", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
        ) {
          Text(loc("Submit", "जमा करें"), color = PureWhite)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showFeedbackDialog = false }) {
          Text(loc("Cancel", "रद्द करें"))
        }
      }
    )
  }

  // Help & FAQ Dialog
  if (showHelpDialog) {
    AlertDialog(
      onDismissRequest = { showHelpDialog = false },
      title = { Text(loc("Help & FAQ", "सहायता और प्रश्न"), fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("• How to earn coins?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text("Complete offers from the Offers screen or invite friends via your referral code.", fontSize = 12.sp, color = Slate700)
          Text("• What is the coin rate?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text("100 coins = ₹1 INR. Minimum demo cashout is ₹100 (10,000 coins).", fontSize = 12.sp, color = Slate700)
          Text("• How are proofs reviewed?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text("Submissions are reviewed by our team and approved coins are credited automatically to your ledger.", fontSize = 12.sp, color = Slate700)
        }
      },
      confirmButton = {
        Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) {
          Text(loc("Close", "बंद करें"), color = PureWhite)
        }
      }
    )
  }

  // Privacy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text(loc("Privacy Policy & Terms", "गोपनीयता नीति"), fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("EarnNova prioritizes user security and data privacy. We never share your personal identity, payment numbers, or private screenshots with third parties. All rewarded tasks are audited for fairness.", fontSize = 12.sp, color = Slate700)
        }
      },
      confirmButton = {
        Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Emerald600)) {
          Text(loc("Got it", "समझ गया"), color = PureWhite)
        }
      }
    )
  }
}

@Composable
fun ProfileOptionRow(
  icon: ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  title: String,
  trailingText: String? = null,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Emerald50),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate800
      )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      if (trailingText != null) {
        Text(
          text = trailingText,
          fontSize = 12.sp,
          color = Slate500,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
      }
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = "Arrow",
        tint = Slate400,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}

@Composable
fun ProfileDivider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(Slate100)
  )
}
