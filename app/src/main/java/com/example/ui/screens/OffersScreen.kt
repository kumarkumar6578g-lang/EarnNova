package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Offer
import com.example.data.model.UserProfile
import com.example.data.repository.EarnNovaRepository
import com.example.ui.components.DailyStreakCard
import com.example.ui.localization.loc
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Gold100
import com.example.ui.theme.Gold600
import com.example.ui.theme.Gold700
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.launch

@Composable
fun OffersScreen(
  userProfile: UserProfile?,
  offers: List<Offer>,
  repository: EarnNovaRepository,
  onClaimDailyReward: () -> Unit,
  isClaimingDaily: Boolean,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("all") }
  var selectedOfferForProof by remember { mutableStateOf<Offer?>(null) }
  var isSubmittingProof by remember { mutableStateOf(false) }

  val categories = listOf(
    Pair("all", loc("All", "सभी")),
    Pair("apps", loc("Apps", "ऐप्स")),
    Pair("games", loc("Games", "गेम्स")),
    Pair("surveys", loc("Surveys", "सर्वे")),
    Pair("quick", loc("Quick", "त्वरित"))
  )

  val filteredOffers = offers.filter { offer ->
    (selectedCategory == "all" || offer.category.equals(selectedCategory, ignoreCase = true)) &&
        (searchQuery.isBlank() || offer.title.contains(searchQuery, ignoreCase = true) ||
            offer.description.contains(searchQuery, ignoreCase = true))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Slate100)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        // Daily Check-in Streak Tracker
        DailyStreakCard(
          userProfile = userProfile,
          isClaiming = isClaimingDaily,
          onClaimDailyReward = onClaimDailyReward
        )
      }

      item {
        // Search bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text(loc("Search tasks & rewards...", "टास्क और रिवॉर्ड्स खोजें..."), fontSize = 14.sp) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = Slate400,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Clear",
                  tint = Slate400,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
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
            .testTag("search_offers_input")
        )
      }

      item {
        // Category filters row
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            val isSelected = selectedCategory == cat.first
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) Emerald600 else PureWhite,
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
              modifier = Modifier
                .clickable { selectedCategory = cat.first }
                .testTag("category_filter_${cat.first}")
            ) {
              Text(
                text = cat.second,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PureWhite else Slate700,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
            }
          }
        }
      }

      if (filteredOffers.isEmpty()) {
        item {
          // Empty state
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "📋", fontSize = 48.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = loc("No Offers Found", "कोई ऑफ़र नहीं मिला"),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
              )
              Text(
                text = loc("Try switching categories or check back soon for fresh tasks.", "श्रेणी बदलें या नए कार्यों के लिए जल्द ही देखें।"),
                fontSize = 13.sp,
                color = Slate500,
                modifier = Modifier.padding(top = 4.dp, start = 20.dp, end = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(filteredOffers, key = { it.id }) { offer ->
          OfferItemCard(
            offer = offer,
            onStartOffer = {
              if (offer.destinationUrl.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.destinationUrl))
                try {
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Opening destination: ${offer.destinationUrl}", Toast.LENGTH_SHORT).show()
                }
              }
            },
            onSubmitProof = {
              selectedOfferForProof = offer
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }

  // Submit Proof Dialog
  selectedOfferForProof?.let { offer ->
    SubmitProofDialog(
      offer = offer,
      isSubmitting = isSubmittingProof,
      onDismiss = { selectedOfferForProof = null },
      onSubmit = { proofText, proofImg ->
        isSubmittingProof = true
        coroutineScope.launch {
          val res = repository.submitOfferProof(
            userId = userProfile?.userId ?: "",
            userEmail = userProfile?.email ?: "",
            offer = offer,
            proofDetails = proofText,
            proofImageUrl = proofImg
          )
          isSubmittingProof = false
          selectedOfferForProof = null
          if (res.isSuccess) {
            Toast.makeText(context, "Proof submitted for admin review!", Toast.LENGTH_LONG).show()
          } else {
            Toast.makeText(context, "Failed to submit: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
          }
        }
      }
    )
  }
}

@Composable
fun OfferItemCard(
  offer: Offer,
  onStartOffer: () -> Unit,
  onSubmitProof: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coins = offer.rewardCoins
  val rupees = coins / 100.0

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
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Category tag & time estimate
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Emerald50
            ) {
              Text(
                text = offer.category.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald700,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time",
                tint = Slate400,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "~${offer.estimatedMinutes}m",
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = offer.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
        }

        // Reward Badge
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Gold100,
          modifier = Modifier.padding(start = 8.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🪙", fontSize = 13.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "+$coins",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Gold700
              )
            }
            Text(
              text = "₹${"%.1f".format(rupees)}",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Emerald700
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = offer.description,
        fontSize = 13.sp,
        color = Slate500,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onStartOffer,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("start_offer_${offer.id}")
        ) {
          Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = loc("Start Task", "शुरू करें"), fontSize = 13.sp)
        }

        Button(
          onClick = onSubmitProof,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
          modifier = Modifier
            .weight(1f)
            .testTag("submit_proof_${offer.id}")
        ) {
          Icon(
            imageVector = Icons.Default.UploadFile,
            contentDescription = "Submit",
            tint = PureWhite,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = loc("Submit Proof", "प्रमाण भेजें"), fontSize = 13.sp, color = PureWhite)
        }
      }
    }
  }
}

@Composable
fun SubmitProofDialog(
  offer: Offer,
  isSubmitting: Boolean,
  onDismiss: () -> Unit,
  onSubmit: (proofText: String, proofImg: String) -> Unit
) {
  var proofText by remember { mutableStateOf("") }
  var proofImgUrl by remember { mutableStateOf("https://earnnova.app/proof/screen_verified.png") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = loc("Submit Proof", "कार्य पूर्णता का प्रमाण"),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
        Text(
          text = offer.title,
          fontSize = 13.sp,
          color = Emerald700,
          fontWeight = FontWeight.SemiBold
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Emerald50,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = loc("Instructions:", "निर्देश:"),
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Emerald700
            )
            Text(
              text = offer.instructions,
              fontSize = 12.sp,
              color = Slate700
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = loc("Proof Requirement:", "प्रमाण आवश्यकता:"),
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = Slate800
        )
        Text(
          text = offer.proofRequirement,
          fontSize = 12.sp,
          color = Slate500
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = proofText,
          onValueChange = { proofText = it },
          label = { Text(loc("Enter details / account ID / note", "विवरण / खाता आईडी / नोट दर्ज करें"), fontSize = 12.sp) },
          minLines = 3,
          maxLines = 4,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Emerald600,
            unfocusedBorderColor = Slate200
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("proof_details_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Slate100,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Ready",
              tint = Emerald600,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = loc("Screenshot attached automatically (demo mode)", "स्क्रीनशॉट स्वतः संलग्न (डेमो मोड)"),
              fontSize = 11.sp,
              color = Slate500
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSubmit(proofText.ifBlank { "Completed task following all instructions." }, proofImgUrl) },
        enabled = !isSubmitting,
        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_submit_proof_button")
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PureWhite, strokeWidth = 2.dp)
        } else {
          Text(text = loc("Submit for Verification", "सत्यापन के लिए भेजें"), color = PureWhite)
        }
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(text = loc("Cancel", "रद्द करें"))
      }
    }
  )
}
