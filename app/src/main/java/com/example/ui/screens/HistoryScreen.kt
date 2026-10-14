package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold700
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Rose100
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(
  transactions: List<TransactionRecord>,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableStateOf("earning") }

  val earningTx = transactions.filter { it.type != "redeem" }
  val redeemTx = transactions.filter { it.type == "redeem" }

  val activeList = if (selectedTab == "earning") earningTx else redeemTx

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
  ) {
    // Top Bar
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
          modifier = Modifier.testTag("history_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Slate800
          )
        }
        Text(
          text = loc("Transaction History", "लेनदेन इतिहास"),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
      }
    }

    // Tabs: Earning & Redeem History
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(PureWhite)
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
      val isEarning = selectedTab == "earning"
      val isRedeem = selectedTab == "redeem"

      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { selectedTab = "earning" }
          .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = loc("Earning History", "कमाई का इतिहास"),
          fontWeight = if (isEarning) FontWeight.Bold else FontWeight.Medium,
          fontSize = 14.sp,
          color = if (isEarning) Emerald700 else Slate500
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .height(3.dp)
            .fillMaxWidth(0.8f)
            .background(if (isEarning) Emerald700 else PureWhite, RoundedCornerShape(2.dp))
        )
      }

      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { selectedTab = "redeem" }
          .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = loc("Redeem History", "निकासी का इतिहास"),
          fontWeight = if (isRedeem) FontWeight.Bold else FontWeight.Medium,
          fontSize = 14.sp,
          color = if (isRedeem) Emerald700 else Slate500
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .height(3.dp)
            .fillMaxWidth(0.8f)
            .background(if (isRedeem) Emerald700 else PureWhite, RoundedCornerShape(2.dp))
        )
      }
    }

    if (activeList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📜", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = loc("No Transactions Found", "कोई लेनदेन नहीं मिला"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
          )
          Text(
            text = loc(
              if (selectedTab == "earning") "Complete offers or check-in to see your earnings."
              else "Submit a cashout request to see your redemption ledger.",
              if (selectedTab == "earning") "अपनी कमाई देखने के लिए ऑफ़र पूरे करें या चेक-इन करें।"
              else "अपनी निकासी देखने के लिए कैशआउट अनुरोध सबमिट करें।"
            ),
            fontSize = 12.sp,
            color = Slate500,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(activeList, key = { it.id.ifBlank { it.hashCode().toString() } }) { tx ->
          TransactionItem(tx = tx)
        }
      }
    }
  }
}

@Composable
fun TransactionItem(tx: TransactionRecord) {
  val isPositive = tx.coins >= 0
  val dateStr = tx.createdAt?.toDate()?.let {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
  } ?: "Recent"

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (isPositive) Emerald100 else Rose100),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
            contentDescription = if (isPositive) "Credit" else "Debit",
            tint = if (isPositive) Emerald700 else Rose600,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = tx.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
          Text(
            text = dateStr,
            fontSize = 11.sp,
            color = Slate500
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = (if (isPositive) "+" else "") + "${tx.coins} 🪙",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 14.sp,
          color = if (isPositive) Emerald700 else Rose600
        )
        Text(
          text = (if (isPositive) "+₹" else "-₹") + "%.2f".format(kotlin.math.abs(tx.rupees)),
          fontSize = 11.sp,
          color = Slate500
        )
      }
    }
  }
}
