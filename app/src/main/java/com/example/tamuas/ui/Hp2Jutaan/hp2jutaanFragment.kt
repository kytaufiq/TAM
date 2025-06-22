package com.example.tamuas.ui.Hp2Jutaan

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

class hp2jutaanFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Hp2JutaanFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_hp2jutaan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Hp2JutaanFragment", "onViewCreated called")

        try {
            loadHp2JutaanSmartphones(view)
        } catch (e: Exception) {
            Log.e("Hp2JutaanFragment", "Error in onViewCreated", e)
        }
    }

    private fun loadHp2JutaanSmartphones(view: View) {
        Log.d("Hp2JutaanFragment", "Starting to load 2 jutaan smartphones")

        lifecycleScope.launch {
            try {
                val hp2jutaanSmartphones = smartphoneRepository.getPrice2MSmartphones()

                Log.d("Hp2JutaanFragment", "Got ${hp2jutaanSmartphones.size} 2 jutaan smartphones")

                if (hp2jutaanSmartphones.isNotEmpty()) {
                    // Populate champion card (first smartphone)
                    populateChampionCard(view, hp2jutaanSmartphones[0])

                    // Populate ranking cards (all smartphones)
                    populateRankingCards(view, hp2jutaanSmartphones)

                    Log.d("Hp2JutaanFragment", "Successfully populated 2 jutaan cards")
                } else {
                    Log.w("Hp2JutaanFragment", "No 2 jutaan smartphones found")
                    showNoDataMessage(view)
                }

            } catch (e: Exception) {
                Log.e("Hp2JutaanFragment", "Error loading 2 jutaan smartphones", e)
                showErrorMessage(view)
            }
        }
    }

    private fun showNoDataMessage(view: View) {
        try {
            val championContainer = view.findViewById<LinearLayout>(R.id.hp2jutaan_champion_container)
            championContainer?.removeAllViews()

            val noDataText = TextView(requireContext()).apply {
                text = "Tidak ada data HP 2 jutaan"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(noDataText)
        } catch (e: Exception) {
            Log.e("Hp2JutaanFragment", "Error showing no data message", e)
        }
    }

    private fun showErrorMessage(view: View) {
        try {
            val championContainer = view.findViewById<LinearLayout>(R.id.hp2jutaan_champion_container)
            championContainer?.removeAllViews()

            val errorText = TextView(requireContext()).apply {
                text = "Error loading HP 2 jutaan"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(errorText)
        } catch (e: Exception) {
            Log.e("Hp2JutaanFragment", "Error showing error message", e)
        }
    }

    private fun populateChampionCard(view: View, champion: com.example.tamuas.models.Smartphone) {
        val championContainer = view.findViewById<LinearLayout>(R.id.hp2jutaan_champion_container)

        if (championContainer != null) {
            // Clear existing views
            championContainer.removeAllViews()

            // Create champion card
            val championCard = createChampionCardProgrammatically(champion)
            championContainer.addView(championCard)
        }
    }

    private fun populateRankingCards(view: View, smartphones: List<com.example.tamuas.models.Smartphone>) {
        val rankingContainer = view.findViewById<LinearLayout>(R.id.hp2jutaan_ranking_container)

        if (rankingContainer != null) {
            // Clear existing views
            rankingContainer.removeAllViews()

            // Create ranking cards
            smartphones.forEachIndexed { index, smartphone ->
                val rankingCard = createRankingCardProgrammatically(smartphone, index + 1)
                rankingContainer.addView(rankingCard)
            }
        }
    }

    private fun createChampionCardProgrammatically(smartphone: com.example.tamuas.models.Smartphone): View {
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

        // Budget Crown header
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
            text = "🏆 Juara Kategori"
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

        // Rating and Best Choice layout
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

        val bestChoiceView = TextView(context).apply {
            text = "Best Choice"
            textSize = 12f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(16, 4, 16, 4)
            setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        }

        ratingLayout.addView(ratingView)
        ratingLayout.addView(bestChoiceView)

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
                bottomMargin = 24
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

        // Rating
        val ratingView = TextView(context).apply {
            text = "⭐ ${smartphone.rating}"
            textSize = 13f
            setTextColor(android.graphics.Color.BLACK)
        }

        // Budget Specs Layout
        val specsLayout1 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val processorView = TextView(context).apply {
            text = smartphone.processor
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val ramView = TextView(context).apply {
            text = smartphone.ram
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
        }

        specsLayout1.addView(processorView)
        specsLayout1.addView(ramView)

        // Camera and Battery Layout
        val specsLayout2 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val cameraView = TextView(context).apply {
            text = extractMainCamera(smartphone.camera)
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val batteryView = TextView(context).apply {
            text = smartphone.battery
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
        }

        specsLayout2.addView(cameraView)
        specsLayout2.addView(batteryView)

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
        detailsLayout.addView(ratingView)
        detailsLayout.addView(specsLayout1)
        detailsLayout.addView(specsLayout2)
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

    private fun extractMainCamera(cameraString: String): String {
        // Extract main camera MP from camera string
        val mpRegex = Regex("(\\d+)MP")
        val match = mpRegex.find(cameraString)
        return match?.value ?: "50MP"
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            Log.d("Hp2jutaanFragment", "Navigating to detail for smartphone: $smartphoneId")

            if (!isAdded || context == null) {
                Log.w("Hp2jutaanFragment", "Fragment not attached")
                return
            }

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_hp2jutaan_to_detail, bundle)

            Log.d("Hp2jutaanFragment", "Navigation to detail initiated")

        } catch (e: Exception) {
            Log.e("Hp2jutaanFragment", "Error navigating to detail", e)
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
