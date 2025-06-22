package com.example.tamuas.ui.trending

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

class TrendingFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTrendingSmartphones(view)
    }

    private fun loadTrendingSmartphones(view: View) {
        lifecycleScope.launch {
            try {
                val trendingSmartphones = smartphoneRepository.getTrendingSmartphones()

                Log.d("TrendingFragment", "Got ${trendingSmartphones.size} trending smartphones")

                // Cari container
                val container = view.findViewById<LinearLayout>(R.id.trending_container)

                if (container != null) {
                    // Clear existing views
                    container.removeAllViews()

                    // Add dynamic smartphone cards
                    trendingSmartphones.forEachIndexed { index, smartphone ->
                        val smartphoneCard = createSmartphoneCard(smartphone, index + 1)
                        container.addView(smartphoneCard)
                    }
                } else {
                    Log.e("TrendingFragment", "Container not found")
                }

            } catch (e: Exception) {
                Log.e("TrendingFragment", "Error loading trending smartphones", e)
            }
        }
    }

    private fun createSmartphoneCard(smartphone: com.example.tamuas.models.Smartphone, rank: Int): View {
        val inflater = LayoutInflater.from(requireContext())

        // Create card layout programmatically
        val cardLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Margin dalam dp yang dikonversi ke pixel
                val marginDp = (12 * resources.displayMetrics.density).toInt()
                bottomMargin = marginDp
            }
            orientation = LinearLayout.HORIZONTAL
            // Padding dalam dp yang dikonversi ke pixel
            val paddingDp = (12 * resources.displayMetrics.density).toInt()
            setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            setBackgroundColor(android.graphics.Color.WHITE)
            elevation = 8f
        }

        // Rank TextView
        val rankSize = (32 * resources.displayMetrics.density).toInt()
        val rankMargin = (12 * resources.displayMetrics.density).toInt()
        val rankView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(rankSize, rankSize).apply {
                marginEnd = rankMargin
            }
            text = "#$rank"
            textSize = 12f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setBackgroundResource(R.drawable.circle_red)
        }

        // Image
        val imageSize = (60 * resources.displayMetrics.density).toInt()
        val imageMargin = (12 * resources.displayMetrics.density).toInt()
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).apply {
                marginEnd = imageMargin
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this)
            .load(smartphone.imageUrl)
            .placeholder(R.drawable.hp_iphone15pro)
            .error(R.drawable.hp_iphone15pro)
            .into(imageView)

        // Details Layout
        val detailsLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }

        // Header Layout untuk nama dan trending
        val headerLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        // Left content layout (nama, brand, rating)
        val leftContentLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }

        // Name
        val nameView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = smartphone.name
            textSize = 16f
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Brand
        val brandView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = smartphone.brand
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#666666"))
        }

        // Rating
        val ratingView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginTop = (2 * resources.displayMetrics.density).toInt()
                topMargin = marginTop
            }
            text = "⭐ ${smartphone.rating}"
            textSize = 12f
            setTextColor(android.graphics.Color.BLACK)
        }

        // Add to left content
        leftContentLayout.addView(nameView)
        leftContentLayout.addView(brandView)
        leftContentLayout.addView(ratingView)

        // Trending Percentage
        val trendingPadding = (8 * resources.displayMetrics.density).toInt()
        val trendingView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = smartphone.trendingPercentage
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            setPadding(trendingPadding, trendingPadding/2, trendingPadding, trendingPadding/2)
            setBackgroundResource(R.drawable.bg_green_light)
        }

        // Add to header
        headerLayout.addView(leftContentLayout)
        headerLayout.addView(trendingView)

        // Description
        val descMargin = (4 * resources.displayMetrics.density).toInt()
        val descView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = descMargin
            }
            text = smartphone.description
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#666666"))
        }

        // Bottom Layout untuk price dan button
        val bottomLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val marginTop = (8 * resources.displayMetrics.density).toInt()
                topMargin = marginTop
            }
            orientation = LinearLayout.HORIZONTAL
        }

        // Price
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
        val priceView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = formattedPrice
            textSize = 16f
            setTextColor(android.graphics.Color.parseColor("#2196F3"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Detail Button
        val buttonPadding = (16 * resources.displayMetrics.density).toInt()
        val buttonPaddingVertical = (8 * resources.displayMetrics.density).toInt()
        val detailButton = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "Lihat Detail"
            textSize = 12f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(buttonPadding, buttonPaddingVertical, buttonPadding, buttonPaddingVertical)
            setBackgroundColor(android.graphics.Color.parseColor("#333333"))
            setOnClickListener {
                navigateToDetail(smartphone.id)
            }
        }

        // Add to bottom layout
        bottomLayout.addView(priceView)
        bottomLayout.addView(detailButton)

        // Add all views to details layout
        detailsLayout.addView(headerLayout)
        detailsLayout.addView(descView)
        detailsLayout.addView(bottomLayout)

        // Add all to main card
        cardLayout.addView(rankView)
        cardLayout.addView(imageView)
        cardLayout.addView(detailsLayout)

        return cardLayout
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            Log.d("TrendingFragment", "Navigating to detail for smartphone: $smartphoneId")

            if (!isAdded || context == null) {
                Log.w("TrendingFragment", "Fragment not attached")
                return
            }

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_trending_to_detail, bundle)

            Log.d("TrendingFragment", "Navigation to detail initiated")

        } catch (e: Exception) {
            Log.e("TrendingFragment", "Error navigating to detail", e)
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