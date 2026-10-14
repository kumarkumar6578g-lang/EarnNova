package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
import com.example.ui.theme.Gold700
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate900

@Composable
fun TopRewardsBar(
  userProfile: UserProfile?,
  onProfileClick: () -> Unit,
  onCoinBalanceClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val languageState = LocalAppLanguage.current
  val coins = userProfile?.coinBalance ?: 0L
  val rupees = coins / 100.0

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = PureWhite,
    shadowElevation = 3.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Logo & Brand name
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onProfileClick() }
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_earnnova_logo),
          contentDescription = "EarnNova Logo",
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "EarnNova",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Slate900
          )
          Text(
            text = loc("Rewards", "रिवॉर्ड्स"),
            fontSize = 11.sp,
            color = Emerald600,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Right actions: Balance Pill, Language switcher, Profile Avatar
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Balance Badge
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Gold100,
          modifier = Modifier
            .testTag("coin_balance_badge")
            .clickable { onCoinBalanceClick() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "🪙",
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$coins",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Gold700
            )
            Text(
              text = " ≈ ₹${"%.1f".format(rupees)}",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Emerald600
            )
          }
        }

        // Language toggle button
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .testTag("language_toggle_button")
            .clickable { languageState.toggle() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Language Switcher",
              modifier = Modifier.size(14.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = languageState.currentLanguage.code,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Profile Avatar
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Emerald600)
            .testTag("profile_avatar_button")
            .clickable { onProfileClick() },
          contentAlignment = Alignment.Center
        ) {
          val initial = userProfile?.displayName?.firstOrNull()?.uppercase() ?: "U"
          Text(
            text = initial,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
