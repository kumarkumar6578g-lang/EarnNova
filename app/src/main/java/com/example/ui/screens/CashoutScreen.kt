package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.Withdrawal
import com.example.data.repository.EarnNovaRepository
import com.example.ui.localization.loc
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber600
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold500
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

data class CashoutOption(
  val rupees: Long,
  val coinsRequired: Long,
  val discountLabel: String? = null
)

@Composable
fun CashoutScreen(
  userProfile: UserProfile?,
  withdrawals: List<Withdrawal>,
  repository: EarnNovaRepository,
  onOpenHistoryClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val coins = userProfile?.coinBalance ?: 0L
  val rupees = coins / 100.0

  val cashoutOptions = listOf(
    CashoutOption(100L, 10000L),
    CashoutOption(200L, 20000L),
    CashoutOption(300L, 29000L, "Save 1,000 coins! 🎉")
  )

  var selectedOption by remember { mutableStateOf<CashoutOption?>(cashoutOptions.first()) }
  var showConfirmDialog by remember { mutableStateOf(false) }
  var isSubmittingCashout by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(10.dp))

      // Available Balance Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = loc("Withdrawable Balance", "निकासी योग्य शेष राशि"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate500
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "₹${"%.2f".format(rupees)}",
                  fontSize = 26.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Slate900
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Gold100
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = "🪙", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                      text = "$coins",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Gold700
                    )
                  }
                }
              }
            }

            // History Button
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Emerald50,
              modifier = Modifier
                .clickable { onOpenHistoryClick() }
                .testTag("cashout_history_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = "History",
                  tint = Emerald700,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = loc("History", "इतिहास"),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Emerald700
                )
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                  contentDescription = "Arrow",
                  tint = Emerald700,
                  modifier = Modifier.size(10.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Progress to minimum cashout (₹100 = 10,000 coins)
          val progress = (coins.toFloat() / 10000f).coerceIn(0f, 1f)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = loc("Progress to ₹100 Min Cashout", "₹100 न्यूनतम निकासी की प्रगति"),
                fontSize = 11.sp,
                color = Slate500
              )
              Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald700
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = Emerald600,
              trackColor = Slate200
            )
          }
        }
      }
    }

    item {
      // Demo Disclaimer Banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Amber100,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Demo Notice",
            tint = Amber600,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = loc("DEMO REWARDS SIMULATION", "डेमो रिवॉर्ड्स सिमुलेशन"),
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Amber600
            )
            Text(
              text = loc(
                "All payout requests are handled as simulated demo transactions. No real bank accounts or funds are transferred until live banking gateway is connected.",
                "सभी भुगतान अनुरोध सिमुलेटेड डेमो लेनदेन के रूप में संसाधित किए जाते हैं। कोई वास्तविक बैंक राशि स्थानांतरित नहीं की जाती है।"
              ),
              fontSize = 11.sp,
              color = Slate700
            )
          }
        }
      }
    }

    item {
      Text(
        text = loc("Choose Payout Amount", "भुगतान राशि चुनें"),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900
      )
    }

    // Cashout Options Grid / Cards
    items(cashoutOptions) { opt ->
      val isSelected = selectedOption?.rupees == opt.rupees
      val hasEnoughCoins = coins >= opt.coinsRequired

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { selectedOption = opt }
          .testTag("cashout_option_${opt.rupees}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Emerald600) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) Emerald50 else Slate100),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "₹",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Emerald700 else Slate700
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "₹${opt.rupees}",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate900
                )
                if (opt.discountLabel != null) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Emerald50
                  ) {
                    Text(
                      text = opt.discountLabel,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Emerald700,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
              Text(
                text = loc("Via UPI / Paytm / IMPS", "यूपीआई / पेटीएम / आईएमपीएस द्वारा"),
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          // Coin requirement badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasEnoughCoins) Gold100 else Slate100
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🪙", fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${opt.coinsRequired}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (hasEnoughCoins) Gold700 else Slate400
              )
            }
          }
        }
      }
    }

    item {
      // Primary Cashout Action Button
      val opt = selectedOption
      val canCashout = opt != null && coins >= opt.coinsRequired

      Button(
        onClick = { showConfirmDialog = true },
        enabled = canCashout && !isSubmittingCashout,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Emerald600,
          disabledContainerColor = Slate200
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("cashout_submit_button")
      ) {
        if (isSubmittingCashout) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PureWhite, strokeWidth = 2.dp)
        } else {
          Text(
            text = if (canCashout) loc("Request Demo Cashout ₹${opt?.rupees}", "डेमो कैशआउट अनुरोध करें ₹${opt?.rupees}")
            else loc("Need More Coins to Cashout", "कैशआउट के लिए और सिक्के चाहिए"),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (canCashout) PureWhite else Slate400
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = loc("Recent Cashout Requests", "हालिया निकासी अनुरोध"),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900
      )
    }

    if (withdrawals.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "💳", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = loc("No cashout requests yet", "अभी तक कोई निकासी अनुरोध नहीं"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
              )
              Text(
                text = loc("Your submitted withdrawals will be tracked here.", "आपके प्रस्तुत अनुरोध यहां दिखेंगे।"),
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }
        }
      }
    } else {
      items(withdrawals) { w ->
        WithdrawalHistoryCard(w)
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Confirmation & Payout Destination Modal
  if (showConfirmDialog && selectedOption != null) {
    val opt = selectedOption!!
    var payoutMethod by remember { mutableStateOf("UPI") }
    var payoutAddress by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showConfirmDialog = false },
      title = {
        Text(
          text = loc("Confirm Demo Cashout", "डेमो कैशआउट की पुष्टि करें"),
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Slate900
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = loc(
              "You are redeeming ${opt.coinsRequired} coins for ₹${opt.rupees}.",
              "आप ₹${opt.rupees} के लिए ${opt.coinsRequired} सिक्के रिडीम कर रहे हैं।"
            ),
            fontSize = 13.sp,
            color = Slate700
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = loc("Select Payout Method:", "भुगतान विधि चुनें:"),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("UPI", "Paytm", "Bank").forEach { method ->
              val isSel = payoutMethod == method
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSel) Emerald600 else Slate100,
                modifier = Modifier
                  .clickable { payoutMethod = method }
                  .weight(1f)
              ) {
                Text(
                  text = method,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) PureWhite else Slate700,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = payoutAddress,
            onValueChange = { payoutAddress = it },
            label = {
              Text(
                text = when (payoutMethod) {
                  "UPI" -> loc("UPI ID (e.g. mobile@upi)", "यूपीआई आईडी (उदा. mobile@upi)")
                  "Paytm" -> loc("Paytm Mobile Number", "पेटीएम मोबाइल नंबर")
                  else -> loc("Bank Account & IFSC", "बैंक खाता और आईएफएससी")
                },
                fontSize = 12.sp
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Emerald600,
              unfocusedBorderColor = Slate200
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("payout_address_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Amber100,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = loc(
                "Notice: Handled in sandbox demonstration mode. No live banking transfer.",
                "सूचना: सैंडबॉक्स प्रदर्शन मोड में संसाधित। कोई वास्तविक बैंक स्थानांतरण नहीं।"
              ),
              fontSize = 10.sp,
              color = Amber600,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (payoutAddress.isBlank()) {
              Toast.makeText(context, "Please enter your payout details", Toast.LENGTH_SHORT).show()
              return@Button
            }
            showConfirmDialog = false
            isSubmittingCashout = true
            coroutineScope.launch {
              val res = repository.requestCashout(
                userId = userProfile?.userId ?: "",
                userEmail = userProfile?.email ?: "",
                amountRupees = opt.rupees,
                coinsRequired = opt.coinsRequired,
                payoutMethod = payoutMethod,
                payoutAddress = payoutAddress
              )
              isSubmittingCashout = false
              if (res.isSuccess) {
                Toast.makeText(context, "Cashout request submitted successfully!", Toast.LENGTH_LONG).show()
              } else {
                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Cashout failed", Toast.LENGTH_LONG).show()
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("confirm_cashout_dialog_button")
        ) {
          Text(text = loc("Confirm & Submit", "पुष्टि करें और जमा करें"), color = PureWhite)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showConfirmDialog = false },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(text = loc("Cancel", "रद्द करें"))
        }
      }
    )
  }
}

@Composable
fun WithdrawalHistoryCard(w: Withdrawal) {
  val dateStr = w.createdAt?.toDate()?.let {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
  } ?: "Recent"

  val statusColor = when (w.status.lowercase()) {
    "approved" -> Emerald700
    "rejected" -> Rose600
    else -> Amber600
  }
  val statusBg = when (w.status.lowercase()) {
    "approved" -> Emerald100
    "rejected" -> Rose100
    else -> Amber100
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "₹${w.amountRupees} (${w.payoutMethod})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
        Text(
          text = w.payoutAddress.ifBlank { "Demo Wallet" } + " • " + dateStr,
          fontSize = 11.sp,
          color = Slate500
        )
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = statusBg
      ) {
        Text(
          text = w.status.uppercase(),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = statusColor,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }
  }
}
