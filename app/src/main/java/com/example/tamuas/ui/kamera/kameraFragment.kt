package com.example.tamuas.ui.kamera

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

class kameraFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("KameraFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_kamera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("KameraFragment", "onViewCreated called")

        try {
            loadCameraSmartphones(view)
        } catch (e: Exception) {
            Log.e("KameraFragment", "Error in onViewCreated", e)
        }
    }

    private fun loadCameraSmartphones(view: View) {
        Log.d("KameraFragment", "Starting to load camera smartphones")

        lifecycleScope.launch {
            try {
                val cameraSmartphones = smartphoneRepository.getCameraSmartphones()

                Log.d("KameraFragment", "Got ${cameraSmartphones.size} camera smartphones")

                if (cameraSmartphones.isNotEmpty()) {
                    // Populate champion card (first smartphone)
                    populateChampionCard(view, cameraSmartphones[0])

                    // Populate ranking cards (all smartphones)
                    populateRankingCards(view, cameraSmartphones)

                    Log.d("KameraFragment", "Successfully populated camera cards")
                } else {
                    Log.w("KameraFragment", "No camera smartphones found")
                    showNoDataMessage(view)
                }

            } catch (e: Exception) {
                Log.e("KameraFragment", "Error loading camera smartphones", e)
                showErrorMessage(view)
            }
        }
    }

    private fun showNoDataMessage(view: View) {
        try {
            val championContainer = view.findViewById<LinearLayout>(R.id.camera_champion_container)
            championContainer?.removeAllViews()

            val noDataText = TextView(requireContext()).apply {
                text = "Tidak ada data camera smartphone"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(noDataText)
        } catch (e: Exception) {
            Log.e("KameraFragment", "Error showing no data message", e)
        }
    }

    private fun showErrorMessage(view: View) {
        try {
            val championContainer = view.findViewById<LinearLayout>(R.id.camera_champion_container)
            championContainer?.removeAllViews()

            val errorText = TextView(requireContext()).apply {
                text = "Error loading camera smartphones"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(errorText)
        } catch (e: Exception) {
            Log.e("KameraFragment", "Error showing error message", e)
        }
    }

    private fun populateChampionCard(view: View, champion: com.example.tamuas.models.Smartphone) {
        val championContainer = view.findViewById<LinearLayout>(R.id.camera_champion_container)

        if (championContainer != null) {
            // Clear existing views
            championContainer.removeAllViews()

            // Create champion card
            val championCard = createChampionCardProgrammatically(champion)
            championContainer.addView(championCard)
        }
    }

    private fun populateRankingCards(view: View, smartphones: List<com.example.tamuas.models.Smartphone>) {
        val rankingContainer = view.findViewById<LinearLayout>(R.id.camera_ranking_container)

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

        // Camera Crown header
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

        // Camera Specs Layout
        val specsLayout1 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        // Extract camera specs from camera field
        val cameraSpecs = extractCameraSpecs(smartphone.camera)

        val mainCameraView = TextView(context).apply {
            text = cameraSpecs.main
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val ultraCameraView = TextView(context).apply {
            text = cameraSpecs.ultra
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
        }

        specsLayout1.addView(mainCameraView)
        specsLayout1.addView(ultraCameraView)

        // Telephoto and Feature Layout
        val specsLayout2 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val teleCameraView = TextView(context).apply {
            text = cameraSpecs.tele
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = 16
            }
        }

        val featureView = TextView(context).apply {
            text = cameraSpecs.feature
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#444444"))
        }

        specsLayout2.addView(teleCameraView)
        specsLayout2.addView(featureView)

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

    private fun extractCameraSpecs(cameraString: String): CameraSpecs {
        // Parse camera string to extract different camera specs
        // This is a simple implementation, you might need to adjust based on your data format
        return when {
            cameraString.contains("iPhone", ignoreCase = true) ->
                CameraSpecs("48MP Main", "12MP Ultra", "12MP Tele 5x", "ProRAW")
            cameraString.contains("Samsung", ignoreCase = true) ->
                CameraSpecs("200MP Main", "12MP Ultra", "50MP Tele 5x", "10MP Tele 3x")
            cameraString.contains("Pixel", ignoreCase = true) ->
                CameraSpecs("50MP Main", "48MP Ultra", "48MP Tele 5x", "Magic Eraser")
            cameraString.contains("Xiaomi", ignoreCase = true) ->
                CameraSpecs("50MP 1-inch", "50MP Ultra", "50MP Tele 3.2x", "50MP Tele 5x")
            cameraString.contains("OPPO", ignoreCase = true) ->
                CameraSpecs("50MP 1-inch", "50MP Ultra", "50MP Tele 3x", "50MP Tele 6x")
            else -> {
                // Try to extract MP values from the string
                val mpValues = Regex("(\\d+)MP").findAll(cameraString).map { it.value }.toList()
                CameraSpecs(
                    main = mpValues.getOrNull(0) ?: "48MP Main",
                    ultra = mpValues.getOrNull(1) ?: "12MP Ultra",
                    tele = mpValues.getOrNull(2) ?: "12MP Tele",
                    feature = "AI Photo"
                )
            }
        }
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            Log.d("kameraFragment", "Navigating to detail for smartphone: $smartphoneId")

            if (!isAdded || context == null) {
                Log.w("kameraFragment", "Fragment not attached")
                return
            }

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_kamera_to_detail, bundle)

            Log.d("kameraFragment", "Navigation to detail initiated")

        } catch (e: Exception) {
            Log.e("KameraFragment", "Error navigating to detail", e)
            if (isAdded && context != null) {
                Toast.makeText(
                    requireContext(),
                    "Error navigating to detail: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    data class CameraSpecs(
        val main: String,
        val ultra: String,
        val tele: String,
        val feature: String
    )
}
