package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Offer
import com.example.data.model.Submission
import com.example.data.model.Withdrawal
import com.example.data.repository.EarnNovaRepository
import com.example.ui.localization.loc
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
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
  submissions: List<Submission>,
  withdrawals: List<Withdrawal>,
  offers: List<Offer>,
  repository: EarnNovaRepository,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var currentAdminTab by remember { mutableStateOf("overview") } // overview, proofs, withdrawals, offers
  var showCreateOfferDialog by remember { mutableStateOf(false) }

  val pendingSubmissions = submissions.filter { it.status == "pending" }
  val pendingWithdrawals = withdrawals.filter { it.status == "pending" }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
  ) {
    // Admin Top Bar
    Surface(
      color = Slate900,
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = PureWhite
            )
          }
          Column {
            Text(
              text = "EarnNova Admin Console",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
            Text(
              text = "Live Ledger & Operations",
              fontSize = 11.sp,
              color = Emerald600
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Emerald700,
          modifier = Modifier.padding(end = 8.dp)
        ) {
          Text(
            text = "AUTHORIZED",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Admin Navigation Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(PureWhite)
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      val navItems = listOf(
        Pair("overview", "Overview"),
        Pair("proofs", "Proofs (${pendingSubmissions.size})"),
        Pair("withdrawals", "Cashouts (${pendingWithdrawals.size})"),
        Pair("offers", "Offers (${offers.size})")
      )

      navItems.forEach { item ->
        val isSel = currentAdminTab == item.first
        Text(
          text = item.second,
          fontSize = 12.sp,
          fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
          color = if (isSel) Emerald700 else Slate500,
          modifier = Modifier
            .clickable { currentAdminTab = item.first }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("admin_nav_${item.first}")
        )
      }
    }

    // Body based on active tab
    when (currentAdminTab) {
      "overview" -> {
        AdminOverviewTab(
          submissions = submissions,
          withdrawals = withdrawals,
          offers = offers,
          onNavigateToProofs = { currentAdminTab = "proofs" },
          onNavigateToWithdrawals = { currentAdminTab = "withdrawals" },
          onNavigateToOffers = { currentAdminTab = "offers" }
        )
      }

      "proofs" -> {
        AdminProofsTab(
          pendingSubmissions = pendingSubmissions,
          onApprove = { sub ->
            coroutineScope.launch {
              val res = repository.reviewSubmission(sub, approve = true, feedback = "Approved & verified by Admin.")
              if (res.isSuccess) {
                Toast.makeText(context, "Submission approved! Coins credited.", Toast.LENGTH_SHORT).show()
              }
            }
          },
          onReject = { sub, feedback ->
            coroutineScope.launch {
              val res = repository.reviewSubmission(sub, approve = false, feedback = feedback)
              if (res.isSuccess) {
                Toast.makeText(context, "Submission rejected with feedback.", Toast.LENGTH_SHORT).show()
              }
            }
          }
        )
      }

      "withdrawals" -> {
        AdminWithdrawalsTab(
          pendingWithdrawals = pendingWithdrawals,
          onApprove = { w ->
            coroutineScope.launch {
              val res = repository.reviewWithdrawal(w, approve = true)
              if (res.isSuccess) {
                Toast.makeText(context, "Demo cashout marked as paid!", Toast.LENGTH_SHORT).show()
              }
            }
          },
          onReject = { w ->
            coroutineScope.launch {
              val res = repository.reviewWithdrawal(w, approve = false)
              if (res.isSuccess) {
                Toast.makeText(context, "Cashout rejected. Coins refunded to user.", Toast.LENGTH_SHORT).show()
              }
            }
          }
        )
      }

      "offers" -> {
        AdminOffersTab(
          offers = offers,
          onAddNewOffer = { showCreateOfferDialog = true }
        )
      }
    }
  }

  // Add Offer Dialog
  if (showCreateOfferDialog) {
    CreateOfferDialog(
      onDismiss = { showCreateOfferDialog = false },
      onCreate = { newOffer ->
        showCreateOfferDialog = false
        coroutineScope.launch {
          val res = repository.createCustomOffer(newOffer)
          if (res.isSuccess) {
            Toast.makeText(context, "New offer created and published live!", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(context, "Failed to create offer", Toast.LENGTH_SHORT).show()
          }
        }
      }
    )
  }
}

@Composable
fun AdminOverviewTab(
  submissions: List<Submission>,
  withdrawals: List<Withdrawal>,
  offers: List<Offer>,
  onNavigateToProofs: () -> Unit,
  onNavigateToWithdrawals: () -> Unit,
  onNavigateToOffers: () -> Unit
) {
  val pendingSubmissions = submissions.filter { it.status == "pending" }
  val pendingWithdrawals = withdrawals.filter { it.status == "pending" }
  val approvedSubmissions = submissions.filter { it.status == "approved" }
  val totalCoinsDistributed = approvedSubmissions.sumOf { it.rewardCoins }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "System Real-Time Metrics",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Slate900
    )

    // Metric Cards Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      AdminMetricCard(
        title = "Pending Proofs",
        value = "${pendingSubmissions.size}",
        subtitle = "Needs review",
        color = Emerald600,
        modifier = Modifier.weight(1f).clickable { onNavigateToProofs() }
      )
      AdminMetricCard(
        title = "Cashout Queue",
        value = "${pendingWithdrawals.size}",
        subtitle = "Payout requests",
        color = Gold700,
        modifier = Modifier.weight(1f).clickable { onNavigateToWithdrawals() }
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      AdminMetricCard(
        title = "Live Offers",
        value = "${offers.size}",
        subtitle = "Active tasks",
        color = Slate800,
        modifier = Modifier.weight(1f).clickable { onNavigateToOffers() }
      )
      AdminMetricCard(
        title = "Coins Awarded",
        value = "$totalCoinsDistributed 🪙",
        subtitle = "≈ ₹${"%.1f".format(totalCoinsDistributed / 100.0)}",
        color = Emerald700,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "Security & Ledger Rules Active",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = Slate900
    )

    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text("• Exchange rate enforced: 100 Coins = ₹1 INR", fontSize = 12.sp, color = Slate700)
        Text("• Minimum cashout enforced: ₹100 = 10,000 Coins", fontSize = 12.sp, color = Slate700)
        Text("• Duplicate payouts prevention: Balance subtracted at request time, refunded on rejection", fontSize = 12.sp, color = Slate700)
        Text("• Anti-tampering: Security rules validate schema & user ownership bounds", fontSize = 12.sp, color = Slate700)
      }
    }
  }
}

@Composable
fun AdminMetricCard(
  title: String,
  value: String,
  subtitle: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, fontSize = 12.sp, color = Slate500, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = subtitle, fontSize = 11.sp, color = Slate400)
    }
  }
}

@Composable
fun AdminProofsTab(
  pendingSubmissions: List<Submission>,
  onApprove: (Submission) -> Unit,
  onReject: (Submission, reason: String) -> Unit
) {
  if (pendingSubmissions.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No pending proofs to review 🎉", fontSize = 14.sp, color = Slate500)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(pendingSubmissions, key = { it.id }) { sub ->
        var rejectReason by remember { mutableStateOf("") }
        var showRejectDialog by remember { mutableStateOf(false) }

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = sub.offerTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                Text(text = "User: ${sub.userEmail.ifBlank { sub.userId }}", fontSize = 11.sp, color = Slate500)
              }
              Surface(shape = RoundedCornerShape(8.dp), color = Gold100) {
                Text(
                  text = "+${sub.rewardCoins} coins",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Gold700,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "User Proof Note:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
            Text(text = sub.proofDetails.ifBlank { "No text note provided." }, fontSize = 12.sp, color = Slate500)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { showRejectDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                modifier = Modifier.weight(1f)
              ) {
                Text("Reject")
              }
              Button(
                onClick = { onApprove(sub) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                modifier = Modifier.weight(1f)
              ) {
                Text("Approve & Pay", color = PureWhite)
              }
            }
          }
        }

        if (showRejectDialog) {
          AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Submission") },
            text = {
              Column {
                Text("Provide feedback reason for user:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = rejectReason,
                  onValueChange = { rejectReason = it },
                  placeholder = { Text("e.g. Screenshot unclear, task step incomplete") },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            },
            confirmButton = {
              Button(
                onClick = {
                  showRejectDialog = false
                  onReject(sub, rejectReason.ifBlank { "Requirements not met." })
                },
                colors = ButtonDefaults.buttonColors(containerColor = Rose600)
              ) {
                Text("Confirm Reject", color = PureWhite)
              }
            },
            dismissButton = {
              OutlinedButton(onClick = { showRejectDialog = false }) { Text("Cancel") }
            }
          )
        }
      }
    }
  }
}

@Composable
fun AdminWithdrawalsTab(
  pendingWithdrawals: List<Withdrawal>,
  onApprove: (Withdrawal) -> Unit,
  onReject: (Withdrawal) -> Unit
) {
  if (pendingWithdrawals.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No pending withdrawal requests 🎉", fontSize = 14.sp, color = Slate500)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(pendingWithdrawals, key = { it.id }) { w ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = "₹${w.amountRupees} (${w.payoutMethod})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text(text = "User: ${w.userEmail.ifBlank { w.userId }}", fontSize = 11.sp, color = Slate500)
                Text(text = "Destination: ${w.payoutAddress}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Emerald700)
              }
              Surface(shape = RoundedCornerShape(8.dp), color = Slate100) {
                Text(
                  text = "${w.coinsDebited} coins",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Slate700,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { onReject(w) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                modifier = Modifier.weight(1f)
              ) {
                Text("Reject & Refund")
              }
              Button(
                onClick = { onApprove(w) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                modifier = Modifier.weight(1f)
              ) {
                Text("Mark as Paid", color = PureWhite)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminOffersTab(
  offers: List<Offer>,
  onAddNewOffer: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp)
  ) {
    Button(
      onClick = onAddNewOffer,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
      modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = PureWhite)
      Spacer(modifier = Modifier.width(6.dp))
      Text("Create New Offer", fontWeight = FontWeight.Bold, color = PureWhite)
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      items(offers, key = { it.id }) { offer ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = offer.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
              Text(text = "${offer.category.uppercase()} • ~${offer.estimatedMinutes} mins", fontSize = 11.sp, color = Slate500)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Gold100) {
              Text(
                text = "+${offer.rewardCoins} 🪙",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Gold700,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CreateOfferDialog(
  onDismiss: () -> Unit,
  onCreate: (Offer) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("apps") }
  var rewardCoinsStr by remember { mutableStateOf("1000") }
  var description by remember { mutableStateOf("") }
  var instructions by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create New Reward Task") },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = rewardCoinsStr,
          onValueChange = { rewardCoinsStr = it.filter { char -> char.isDigit() } },
          label = { Text("Reward Coins (100 = ₹1)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it.lowercase() },
          label = { Text("Category (apps, games, surveys, quick)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Short Description") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = instructions,
          onValueChange = { instructions = it },
          label = { Text("Task Instructions (Step 1, Step 2...)") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isBlank()) return@Button
          val coins = rewardCoinsStr.toLongOrNull() ?: 1000L
          onCreate(
            Offer(
              id = "custom_" + System.currentTimeMillis(),
              title = title,
              description = description.ifBlank { "Complete task to earn rewards." },
              category = category.ifBlank { "apps" },
              rewardCoins = coins,
              instructions = instructions.ifBlank { "1. Complete task\n2. Submit screenshot proof" },
              destinationUrl = "https://play.google.com",
              status = "active",
              proofRequirement = "Screenshot of completion",
              estimatedMinutes = 5
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
      ) {
        Text("Publish Offer", color = PureWhite)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
