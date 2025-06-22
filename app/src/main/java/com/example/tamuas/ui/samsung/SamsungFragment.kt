package com.example.tamuas.ui.samsung

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.example.tamuas.R
import com.example.tamuas.repository.SmartphoneRepository
import com.example.tamuas.SmartphoneDetailActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class SamsungFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SamsungFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_samsung, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SamsungFragment", "onViewCreated called")

        // Setup back button dengan proper navigation
        view.findViewById<TextView>(R.id.btn_back)?.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("SamsungFragment", "Error navigating back with NavController", e)
                try {
                    parentFragmentManager.popBackStack()
                } catch (fallbackError: Exception) {
                    Log.e("SamsungFragment", "Fallback navigation also failed", fallbackError)
                    activity?.onBackPressed()
                }
            }
        }

        // Load data dengan proper error handling
        loadSamsungSmartphones(view)
    }

    private fun loadSamsungSmartphones(view: View) {
        Log.d("SamsungFragment", "Starting to load Samsung smartphones")

        if (!isAdded || context == null) {
            Log.w("SamsungFragment", "Fragment not attached, skipping data load")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val samsungSmartphones = smartphoneRepository.getSamsungSmartphones()

                if (!isAdded || context == null) {
                    Log.w("SamsungFragment", "Fragment detached during data load")
                    return@launch
                }

                Log.d("SamsungFragment", "Got ${samsungSmartphones.size} Samsung smartphones")

                if (samsungSmartphones.isNotEmpty()) {
                    populateChampionCard(view, samsungSmartphones[0])
                    populateRankingCards(view, samsungSmartphones)
                    Log.d("SamsungFragment", "Successfully populated Samsung cards")
                } else {
                    Log.w("SamsungFragment", "No Samsung smartphones found")
                    showNoDataMessage(view)
                }

            } catch (e: Exception) {
                Log.e("SamsungFragment", "Error loading Samsung smartphones", e)
                if (isAdded && context != null) {
                    showErrorMessage(view)
                }
            }
        }
    }

    private fun showNoDataMessage(view: View) {
        try {
            if (!isAdded || context == null) return

            val championContainer = view.findViewById<LinearLayout>(R.id.samsung_champion_container)
            championContainer?.removeAllViews()

            val noDataText = TextView(requireContext()).apply {
                text = "Tidak ada data Samsung smartphones"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(noDataText)
        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error showing no data message", e)
        }
    }

    private fun showErrorMessage(view: View) {
        try {
            if (!isAdded || context == null) return

            val championContainer = view.findViewById<LinearLayout>(R.id.samsung_champion_container)
            championContainer?.removeAllViews()

            val errorText = TextView(requireContext()).apply {
                text = "Error loading Samsung smartphones"
                textSize = 16f
                setPadding(32, 32, 32, 32)
            }
            championContainer?.addView(errorText)
        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error showing error message", e)
        }
    }

    private fun populateChampionCard(view: View, champion: com.example.tamuas.models.Smartphone) {
        try {
            if (!isAdded || context == null) return

            val championContainer = view.findViewById<LinearLayout>(R.id.samsung_champion_container)

            if (championContainer != null) {
                championContainer.removeAllViews()
                val championCard = createChampionCardProgrammatically(champion)
                championContainer.addView(championCard)
            }
        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error populating champion card", e)
        }
    }

    private fun populateRankingCards(view: View, smartphones: List<com.example.tamuas.models.Smartphone>) {
        try {
            if (!isAdded || context == null) return

            val rankingContainer = view.findViewById<LinearLayout>(R.id.samsung_ranking_container)

            if (rankingContainer != null) {
                rankingContainer.removeAllViews()

                smartphones.forEachIndexed { index, smartphone ->
                    val rankingCard = createRankingCardFromXML(smartphone, index + 1)
                    rankingContainer.addView(rankingCard)
                }
            }
        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error populating ranking cards", e)
        }
    }

    private fun createChampionCardProgrammatically(smartphone: com.example.tamuas.models.Smartphone): View {
        val context = requireContext()

        // Main card layout dengan Samsung theme
        val cardLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 48
            }
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD")) // Samsung blue theme
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
            text = "🏆 Terbaik dari Samsung"
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#1565C0"))
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

        try {
            if (isAdded && context != null) {
                Glide.with(this)
                    .load(smartphone.imageUrl)
                    .placeholder(R.drawable.hp_iphone15pro)
                    .error(R.drawable.hp_iphone15pro)
                    .into(imageView)
            }
        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error loading image with Glide", e)
            imageView.setImageResource(R.drawable.hp_iphone15pro)
        }

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
            setBackgroundColor(android.graphics.Color.parseColor("#1976D2")) // Samsung blue
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
            setTextColor(android.graphics.Color.parseColor("#1976D2"))
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

    private fun createRankingCardFromXML(smartphone: com.example.tamuas.models.Smartphone, rank: Int): View {
        val inflater = LayoutInflater.from(requireContext())
        val cardView = inflater.inflate(R.layout.item_smartphone_samsung, null)

        try {
            // Set rank
            val rankNumber = cardView.findViewById<TextView>(R.id.tv_samsung_rank_number)
            rankNumber.text = "#$rank"

            // Set rank background color based on position
            when (rank) {
                1 -> rankNumber.setBackgroundResource(R.drawable.rounded_box)
                2 -> rankNumber.setBackgroundResource(R.drawable.capsule_grey)
                3 -> rankNumber.setBackgroundResource(R.drawable.rounded_box_bronze)
                else -> rankNumber.setBackgroundResource(R.drawable.rounded_box_blue)
            }

            // Set image
            val imageView = cardView.findViewById<ImageView>(R.id.iv_samsung_image)
            try {
                if (isAdded && context != null) {
                    Glide.with(this)
                        .load(smartphone.imageUrl)
                        .placeholder(R.drawable.hp_iphone15pro)
                        .error(R.drawable.hp_iphone15pro)
                        .into(imageView)
                }
            } catch (e: Exception) {
                Log.e("SamsungFragment", "Error loading ranking image", e)
                imageView.setImageResource(R.drawable.hp_iphone15pro)
            }

            // Set name
            val nameView = cardView.findViewById<TextView>(R.id.tv_samsung_name)
            nameView.text = smartphone.name

            // Set brand
            val brandView = cardView.findViewById<TextView>(R.id.tv_samsung_brand)
            brandView.text = smartphone.brand

            // Set rating
            val ratingView = cardView.findViewById<TextView>(R.id.tv_samsung_rating)
            ratingView.text = "⭐ ${smartphone.rating}"

            // Set processor
            val processorView = cardView.findViewById<TextView>(R.id.tv_samsung_processor)
            processorView.text = smartphone.processor

            // Set storage
            val storageView = cardView.findViewById<TextView>(R.id.tv_samsung_storage)
            storageView.text = smartphone.storage.split("/")[0] // Take first storage option

            // Set camera
            val cameraView = cardView.findViewById<TextView>(R.id.tv_samsung_camera)
            val cameraText = extractMainCamera(smartphone.camera)
            cameraView.text = cameraText

            // Set display
            val displayView = cardView.findViewById<TextView>(R.id.tv_samsung_display)
            val displayText = extractDisplaySize(smartphone.display)
            displayView.text = displayText

            // Set description
            val descriptionView = cardView.findViewById<TextView>(R.id.tv_samsung_description)
            descriptionView.text = smartphone.description

            // Set price
            val priceView = cardView.findViewById<TextView>(R.id.tv_samsung_price)
            val formatter = NumberFormat.getInstance(Locale("id", "ID"))
            val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
            priceView.text = formattedPrice

            // Set detail button click
            val detailButton = cardView.findViewById<TextView>(R.id.btn_samsung_detail)
            detailButton.setOnClickListener {
                navigateToDetail(smartphone.id)
            }

        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error setting up ranking card", e)
        }

        return cardView
    }

    private fun extractMainCamera(cameraString: String): String {
        return when {
            cameraString.contains("200MP") -> "200MP"
            cameraString.contains("50MP") -> "50MP"
            cameraString.contains("108MP") -> "108MP"
            else -> "50MP"
        }
    }

    private fun extractDisplaySize(displayString: String): String {
        val regex = Regex("(\\d+\\.\\d+)\\s*inch")
        val match = regex.find(displayString)
        return if (match != null) {
            "${match.groupValues[1]} inch"
        } else {
            "6.8 inch"
        }
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            if (!isAdded || context == null) return

            Log.d("SamsungFragment", "Navigating to detail for smartphone: $smartphoneId")

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_samsung_to_detail, bundle)

            Log.d("SamsungFragment", "Navigation to detail initiated from Samsung")

        } catch (e: Exception) {
            Log.e("SamsungFragment", "Error navigating to detail", e)
        }
    }
}