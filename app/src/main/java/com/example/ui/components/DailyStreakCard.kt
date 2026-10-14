package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyStreakCard(
  userProfile: UserProfile?,
  isClaiming: Boolean,
  onClaimDailyReward: () -> Unit,
  modifier: Modifier = Modifier
) {
  val streak = userProfile?.dailyStreak ?: 0L
  val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  val alreadyClaimedToday = userProfile?.lastCheckInDate == today

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = loc("Daily Check-in Streak", "दैनिक चेक-इन स्ट्रीक"),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Slate800
          )
          Text(
            text = if (streak > 0) loc("Current Streak: $streak Days 🔥", "वर्तमान स्ट्रीक: $streak दिन 🔥")
            else loc("Check in daily for bonus coins!", "अतिरिक्त सिक्कों के लिए रोज़ाना चेक इन करें!"),
            fontSize = 12.sp,
            color = Slate500
          )
        }

        Button(
          onClick = onClaimDailyReward,
          enabled = !alreadyClaimedToday && !isClaiming,
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Emerald600,
            disabledContainerColor = Emerald100
          ),
          modifier = Modifier.testTag("claim_daily_checkin_button")
        ) {
          if (isClaiming) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = PureWhite,
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = if (alreadyClaimedToday) loc("Claimed ✓", "प्राप्त किया ✓")
              else loc("Claim", "प्राप्त करें"),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (alreadyClaimedToday) Emerald700 else PureWhite
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4-step streak tracker (+100, +200, +300, +500)
      val steps = listOf(
        Pair("Day 1", "+100"),
        Pair("Day 2", "+200"),
        Pair("Day 3", "+300"),
        Pair("Day 4+", "+500")
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        steps.forEachIndexed { index, pair ->
          val stepNumber = index + 1
          val isCurrentOrPast = streak >= stepNumber || (streak == 0L && index == 0 && !alreadyClaimedToday)
          val isDone = streak >= stepNumber

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            // Pill with reward amount
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrentOrPast) Emerald600 else Slate200)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = pair.second,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentOrPast) PureWhite else Slate500
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Circle indicator with coin icon
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isDone) Gold100 else Emerald50)
                .border(
                  width = 2.dp,
                  color = if (isDone) Gold500 else if (isCurrentOrPast) Emerald600 else Slate200,
                  shape = CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (index == 3) "🎁" else "🪙",
                fontSize = 16.sp
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = loc(pair.first, "दिन ${index + 1}"),
              fontSize = 11.sp,
              color = if (isCurrentOrPast) Slate800 else Slate500,
              fontWeight = if (isCurrentOrPast) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }
  }
}
