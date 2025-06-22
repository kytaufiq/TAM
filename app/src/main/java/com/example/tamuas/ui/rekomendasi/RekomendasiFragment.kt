package com.example.tamuas.ui.rekomendasi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.example.tamuas.R
import com.example.tamuas.repository.SmartphoneRepository
import com.example.tamuas.SmartphoneDetailActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class RekomendasiFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rekomendasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadRecommendedSmartphones(view)
    }

    private fun loadRecommendedSmartphones(view: View) {
        lifecycleScope.launch {
            try {
                val smartphones = smartphoneRepository.getRecommendedSmartphones()

                Log.d("RekomendasiFragment", "Got ${smartphones.size} recommended smartphones")

                if (smartphones.isNotEmpty()) {
                    // Populate winner card (first smartphone)
                    populateWinnerCard(view, smartphones[0])

                    // Populate ranking cards (all smartphones)
                    populateRankingCards(view, smartphones)
                }

            } catch (e: Exception) {
                Log.e("RekomendasiFragment", "Error loading recommended smartphones", e)
            }
        }
    }

    private fun populateWinnerCard(view: View, winner: com.example.tamuas.models.Smartphone) {
        val winnerContainer = view.findViewById<LinearLayout>(R.id.winner_container)

        if (winnerContainer != null) {
            // Clear existing views
            winnerContainer.removeAllViews()

            // Create winner card programmatically
            val winnerCard = createWinnerCardProgrammatically(winner)
            winnerContainer.addView(winnerCard)
        }
    }

    private fun populateRankingCards(view: View, smartphones: List<com.example.tamuas.models.Smartphone>) {
        val rankingContainer = view.findViewById<LinearLayout>(R.id.ranking_container)

        if (rankingContainer != null) {
            // Clear existing views
            rankingContainer.removeAllViews()

            // Create ranking cards programmatically
            smartphones.forEachIndexed { index, smartphone ->
                val rankingCard = createRankingCardProgrammatically(smartphone, index + 1)
                rankingContainer.addView(rankingCard)
            }
        }
    }

    private fun createWinnerCardProgrammatically(smartphone: com.example.tamuas.models.Smartphone): View {
        val context = requireContext()

        // Main card layout
        val cardLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 48
            }
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.parseColor("#FFF8E1"))
            elevation = 8f
        }

        // Crown header
        val crownLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            orientation = LinearLayout.HORIZONTAL
        }

        val crownIcon = TextView(context).apply {
            text = "👑"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val crownText = TextView(context).apply {
            text = "🏆 Juara All Rounder"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#B8860B"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        crownLayout.addView(crownIcon)
        crownLayout.addView(crownText)

        // Content layout
        val contentLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            orientation = LinearLayout.HORIZONTAL
        }

        // Image
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(160, 160).apply {
                rightMargin = 32
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this)
            .load(smartphone.imageUrl)
            .placeholder(R.drawable.hp_iphone15pro)
            .error(R.drawable.hp_iphone15pro)
            .into(imageView)

        // Details layout
        val detailsLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }

        // Name
        val nameView = TextView(context).apply {
            text = smartphone.name
            textSize = 18f
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Brand
        val brandView = TextView(context).apply {
            text = smartphone.brand
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#666666"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Rating and Score layout
        val ratingLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
            orientation = LinearLayout.HORIZONTAL
        }

        val ratingView = TextView(context).apply {
            text = "⭐ ${smartphone.rating}"
            textSize = 14f
            setTextColor(android.graphics.Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val scoreView = TextView(context).apply {
            text = "Score: ${smartphone.score}/10"
            textSize = 14f
            setTextColor(android.graphics.Color.BLACK)
            setPadding(16, 4, 16, 4)
            setBackgroundColor(android.graphics.Color.parseColor("#FFB74D"))
        }

        ratingLayout.addView(ratingView)
        ratingLayout.addView(scoreView)

        // Description
        val descView = TextView(context).apply {
            text = smartphone.description
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        // Price
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
        val priceView = TextView(context).apply {
            text = formattedPrice
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#2196F3"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Add views to details layout
        detailsLayout.addView(nameView)
        detailsLayout.addView(brandView)
        detailsLayout.addView(ratingLayout)
        detailsLayout.addView(descView)
        detailsLayout.addView(priceView)

        // Add to content layout
        contentLayout.addView(imageView)
        contentLayout.addView(detailsLayout)

        // Detail Button
        val detailButton = TextView(context).apply {
            text = "Lihat Detail"
            textSize = 14f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(40, 20, 40, 20)
            setBackgroundColor(android.graphics.Color.parseColor("#333333"))
            setOnClickListener {
                navigateToDetail(smartphone.id)
            }
        }

        // Add all to card
        cardLayout.addView(crownLayout)
        cardLayout.addView(contentLayout)
        cardLayout.addView(detailButton)

        return cardLayout
    }

    private fun createRankingCardProgrammatically(smartphone: com.example.tamuas.models.Smartphone, rank: Int): View {
        val context = requireContext()

        // Main card layout
        val cardLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.WHITE)
            elevation = 4f
        }

        // Rank badge
        val rankView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                rightMargin = 24
            }
            text = "#$rank"
            textSize = 12f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER

            // Set background based on rank
            when (rank) {
                1 -> setBackgroundResource(R.drawable.rounded_box)
                2 -> setBackgroundResource(R.drawable.capsule_grey)
                3 -> setBackgroundResource(R.drawable.rounded_box_bronze)
                else -> setBackgroundResource(R.drawable.rounded_box_blue)
            }
        }

        // Image
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                rightMargin = 24
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this)
            .load(smartphone.imageUrl)
            .placeholder(R.drawable.hp_iphone15pro)
            .error(R.drawable.hp_iphone15pro)
            .into(imageView)

        // Details layout
        val detailsLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }

        // Name
        val nameView = TextView(context).apply {
            text = smartphone.name
            textSize = 16f
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Brand
        val brandView = TextView(context).apply {
            text = smartphone.brand
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#666666"))
        }

        // Rating and Score
        val ratingScoreLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val ratingView = TextView(context).apply {
            text = "⭐ ${smartphone.rating}"
            textSize = 13f
            setTextColor(android.graphics.Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val scoreView = TextView(context).apply {
            text = "Score: ${smartphone.score}/10"
            textSize = 13f
            setTextColor(android.graphics.Color.BLACK)
        }

        ratingScoreLayout.addView(ratingView)
        ratingScoreLayout.addView(scoreView)

        // Description
        val descView = TextView(context).apply {
            text = smartphone.description
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#444444"))
        }

        // Price
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
        val priceView = TextView(context).apply {
            text = formattedPrice
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#2196F3"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Add views to details layout
        detailsLayout.addView(nameView)
        detailsLayout.addView(brandView)
        detailsLayout.addView(ratingScoreLayout)
        detailsLayout.addView(descView)
        detailsLayout.addView(priceView)

        // Detail Button
        val detailButton = TextView(context).apply {
            text = "Lihat Detail"
            textSize = 12f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(32, 16, 32, 16)
            setBackgroundColor(android.graphics.Color.parseColor("#333333"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            setOnClickListener {
                navigateToDetail(smartphone.id)
            }
        }

        // Add all to card
        cardLayout.addView(rankView)
        cardLayout.addView(imageView)
        cardLayout.addView(detailsLayout)
        cardLayout.addView(detailButton)

        return cardLayout
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            Log.d("RekomendasiFragment", "Navigating to detail for smartphone: $smartphoneId")

            if (!isAdded || context == null) {
                Log.w("RekomendasiFragment", "Fragment not attached")
                return
            }

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_rekomendasi_to_detail, bundle)

            Log.d("RekomendasiFragment", "Navigation to detail initiated")

        } catch (e: Exception) {
            Log.e("RekomendasiFragment", "Error navigating to detail", e)
            if (isAdded && context != null) {
                Toast.makeText(
                    requireContext(),
                    "Error navigating to detail: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
