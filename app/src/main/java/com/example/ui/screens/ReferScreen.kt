package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold700
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun ReferScreen(
  userProfile: UserProfile?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val code = userProfile?.referralCode?.ifBlank { "NOVA8829" } ?: "NOVA8829"
  val referralLink = "https://earnnova.app/ref/$code"
  var copied by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(14.dp))

    // Hero Banner Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Emerald50, PureWhite)
            )
          )
          .padding(20.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(Gold100),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🎁", fontSize = 34.sp)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = loc("Refer Friends & Earn ₹15 Per Friend", "दोस्तों को रेफ़र करें और प्रति मित्र ₹15 कमाएं"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = loc(
              "Get +500 coins instantly when your friend registers, plus +1,000 coins after their first approved task!",
              "जब आपका मित्र पंजीकरण करता है तो तुरंत +500 सिक्के प्राप्त करें, साथ ही उनके पहले स्वीकृत कार्य के बाद +1,000 सिक्के!"
            ),
            fontSize = 13.sp,
            color = Slate500,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Referral Code Box
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald600),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = loc("YOUR REFERRAL CODE", "आपका रेफ़रल कोड"),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500
                )
                Text(
                  text = code,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Emerald700,
                  letterSpacing = 2.sp
                )
              }

              OutlinedButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("EarnNova Code", code)
                  clipboard.setPrimaryClip(clip)
                  copied = true
                  Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("copy_referral_code_button")
              ) {
                Icon(
                  imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = "Copy",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (copied) loc("Copied", "कॉपी किया") else loc("Copy", "कॉपी"),
                  fontSize = 12.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Share Button
          Button(
            onClick = {
              val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                  Intent.EXTRA_TEXT,
                  "Earn money with EarnNova! Use my invite code $code to get 500 bonus coins on signup: $referralLink"
                )
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share EarnNova Invite"))
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("share_referral_button")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = PureWhite,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = loc("Share Invite Link", "आमंत्रण लिंक साझा करें"),
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = PureWhite
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Real Statistics Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = loc("Your Referral Performance", "आपका रेफ़रल प्रदर्शन"),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
        Text(
          text = loc("Based on live verified records only.", "केवल लाइव सत्यापित रिकॉर्ड के आधार पर।"),
          fontSize = 11.sp,
          color = Slate500
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          StatItem(
            icon = Icons.Default.People,
            value = if (userProfile?.referredBy != null) "1" else "0",
            label = loc("Friends Joined", "जुड़े मित्र")
          )
          StatItem(
            icon = Icons.Default.Star,
            value = if (userProfile?.referredBy != null) "+500" else "0",
            label = loc("Bonus Coins", "बोनस सिक्के")
          )
          StatItem(
            icon = Icons.Default.CardGiftcard,
            value = "₹" + if (userProfile?.referredBy != null) "5.0" else "0.0",
            label = loc("Total Value", "कुल मूल्य")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // How it works steps
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = loc("How Referral Rewards Work", "रेफ़रल रिवॉर्ड्स कैसे काम करते हैं"),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )

        ReferStepItem(
          stepNumber = "1",
          title = loc("Send Your Invite", "अपना आमंत्रण भेजें"),
          desc = loc("Share your exclusive code with friends via WhatsApp, Telegram, or SMS.", "व्हाट्सएप, टेलीग्राम या एसएमएस के माध्यम से दोस्तों के साथ कोड साझा करें।")
        )

        ReferStepItem(
          stepNumber = "2",
          title = loc("Friend Enters Code", "मित्र कोड दर्ज करता है"),
          desc = loc("They get +500 starter bonus coins upon signing in.", "उन्हें साइन इन करने पर +500 शुरुआती बोनस सिक्के मिलते हैं।")
        )

        ReferStepItem(
          stepNumber = "3",
          title = loc("First Task Verified", "पहला कार्य सत्यापित"),
          desc = loc("When they complete and get their first offer approved, you receive +1,000 coins!", "जब वे अपना पहला कार्य पूरा करते हैं और स्वीकृत होते हैं, तो आपको +1,000 सिक्के मिलते हैं!")
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun StatItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  value: String,
  label: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = Emerald600,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = value,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 16.sp,
      color = Slate900
    )
    Text(
      text = label,
      fontSize = 11.sp,
      color = Slate500
    )
  }
}

@Composable
fun ReferStepItem(
  stepNumber: String,
  title: String,
  desc: String
) {
  Row(verticalAlignment = Alignment.Top) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(Emerald600),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stepNumber,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = PureWhite
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Slate800
      )
      Text(
        text = desc,
        fontSize = 12.sp,
        color = Slate500
      )
    }
  }
}
