package com.example.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald600
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate900

enum class MainTab(
  val enLabel: String,
  val hiLabel: String,
  val icon: ImageVector,
  val testTag: String
) {
  OFFERS("Offers", "ऑफ़र", Icons.Default.LocalFireDepartment, "tab_offers"),
  MY_OFFERS("My Offers", "मेरे ऑफ़र", Icons.AutoMirrored.Filled.Assignment, "tab_my_offers"),
  REFER("Refer & Earn", "रेफ़र करें", Icons.Default.Share, "tab_refer"),
  CASHOUT("Cashout", "कैशआउट", Icons.Default.AccountBalanceWallet, "tab_cashout")
}

@Composable
fun EarnNovaBottomNav(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.navigationBarsPadding(),
    containerColor = PureWhite,
    tonalElevation = 6.dp
  ) {
    MainTab.entries.forEach { tab ->
      val selected = tab == currentTab
      NavigationBarItem(
        selected = selected,
        onClick = { onTabSelected(tab) },
        icon = {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.enLabel,
            modifier = Modifier.size(22.dp)
          )
        },
        label = {
          Text(
            text = loc(tab.enLabel, tab.hiLabel),
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Emerald600,
          selectedTextColor = Emerald600,
          unselectedIconColor = Slate400,
          unselectedTextColor = Slate400,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.testTag(tab.testTag)
      )
    }
  }
}
