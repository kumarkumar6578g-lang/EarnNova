package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Submission
import com.example.ui.localization.loc
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber600
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald600
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
fun MyOffersScreen(
  submissions: List<Submission>,
  onExploreOffersClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("all") }

  val tabs = listOf(
    Pair("all", loc("All", "सभी")),
    Pair("pending", loc("Pending Review", "समीक्षाधीन")),
    Pair("approved", loc("Approved", "स्वीकृत")),
    Pair("rejected", loc("Rejected", "अस्वीकृत"))
  )

  val filteredSubmissions = submissions.filter {
    selectedTab == "all" || it.status.equals(selectedTab, ignoreCase = true)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(14.dp))

    // Header title
    Text(
      text = loc("My Task Submissions", "मेरे प्रस्तुत कार्य"),
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Slate900
    )
    Text(
      text = loc(
        "Track verification progress, review statuses, and rewards.",
        "सत्यापन प्रगति, समीक्षा स्थिति और पुरस्कारों को ट्रैक करें।"
      ),
      fontSize = 12.sp,
      color = Slate500,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    // Filter pills
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
    ) {
      items(tabs) { tab ->
        val isSelected = selectedTab == tab.first
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) Emerald600 else PureWhite,
          border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
          modifier = Modifier
            .clickable { selectedTab = tab.first }
            .testTag("my_offers_tab_${tab.first}")
        ) {
          Text(
            text = tab.second,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PureWhite else Slate700,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      }
    }

    if (filteredSubmissions.isEmpty()) {
      // Empty state
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Text(text = "📑", fontSize = 52.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = loc("No Submissions Yet", "अभी तक कोई प्रस्तुतिकरण नहीं"),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = loc(
              "Start an offer from the Offers tab, complete instructions, and submit proof here to earn coins!",
              "ऑफ़र टैब से कोई कार्य शुरू करें, निर्देश पूरे करें और सिक्के अर्जित करने के लिए यहां प्रमाण जमा करें!"
            ),
            fontSize = 13.sp,
            color = Slate500,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredSubmissions, key = { it.id.ifBlank { it.hashCode().toString() } }) { sub ->
          SubmissionCard(submission = sub)
        }
        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
fun SubmissionCard(
  submission: Submission,
  modifier: Modifier = Modifier
) {
  val dateStr = submission.createdAt?.toDate()?.let {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
  } ?: "Recent"

  val statusBg = when (submission.status.lowercase()) {
    "approved" -> Emerald100
    "rejected" -> Rose100
    else -> Amber100
  }
  val statusColor = when (submission.status.lowercase()) {
    "approved" -> Emerald700
    "rejected" -> Rose600
    else -> Amber600
  }
  val statusIcon = when (submission.status.lowercase()) {
    "approved" -> Icons.Default.CheckCircle
    "rejected" -> Icons.Default.ReportProblem
    else -> Icons.Default.HourglassEmpty
  }
  val statusLabel = when (submission.status.lowercase()) {
    "approved" -> loc("Approved (+${submission.rewardCoins} coins)", "स्वीकृत (+${submission.rewardCoins} सिक्के)")
    "rejected" -> loc("Rejected", "अस्वीकृत")
    else -> loc("In Review", "समीक्षाधीन")
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = submission.offerTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = dateStr,
            fontSize = 11.sp,
            color = Slate500
          )
        }

        // Status chip
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = statusBg
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = statusIcon,
              contentDescription = "Status",
              tint = statusColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = statusLabel,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = statusColor
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = loc("Submitted Note:", "प्रस्तुत विवरण:"),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate700
      )
      Text(
        text = submission.proofDetails.ifBlank { "Completed task following instructions." },
        fontSize = 12.sp,
        color = Slate500
      )

      if (submission.status.lowercase() == "rejected" && submission.adminFeedback.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Rose100,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Reason",
              tint = Rose600,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = loc("Review Feedback: ", "समीक्षा प्रतिक्रिया: ") + submission.adminFeedback,
              fontSize = 12.sp,
              color = Rose600,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}
