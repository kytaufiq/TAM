package com.example.tamuas

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tamuas.repository.SmartphoneRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class SmartphoneDetailActivity : AppCompatActivity() {

    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smartphone_detail)

        val smartphoneId = intent.getStringExtra("smartphone_id")

        if (smartphoneId != null) {
            loadSmartphoneDetail(smartphoneId)
        } else {
            Log.e("SmartphoneDetailActivity", "No smartphone ID provided")
            finish()
        }

        setupBackButton()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun loadSmartphoneDetail(smartphoneId: String) {
        lifecycleScope.launch {
            try {
                val smartphone = smartphoneRepository.getSmartphoneById(smartphoneId)

                if (smartphone != null) {
                    displaySmartphoneDetail(smartphone)
                } else {
                    Log.e("SmartphoneDetailActivity", "Smartphone not found: $smartphoneId")
                    finish()
                }
            } catch (e: Exception) {
                Log.e("SmartphoneDetailActivity", "Error loading smartphone detail", e)
                finish()
            }
        }
    }

    private fun displaySmartphoneDetail(smartphone: com.example.tamuas.models.Smartphone) {
        // Basic Info
        findViewById<TextView>(R.id.tv_product_name).text = smartphone.name
        findViewById<TextView>(R.id.tv_rating).text = "★ ${smartphone.rating}"

        // Price
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
        findViewById<TextView>(R.id.tv_price).text = formattedPrice

        // Trending badge
        val trendingView = findViewById<TextView>(R.id.tv_trending)
        if (smartphone.isTrending) {
            trendingView.text = "Trending ${smartphone.trendingPercentage}"
        } else {
            trendingView.visibility = android.view.View.GONE
        }

        // Product Image
        Glide.with(this)
            .load(smartphone.imageUrl)
            .placeholder(R.drawable.hp_iphone15pro)
            .error(R.drawable.hp_iphone15pro)
            .into(findViewById<ImageView>(R.id.iv_product_image))

        // Network Specifications
        findViewById<TextView>(R.id.tv_technology).text = smartphone.network.technology
        findViewById<TextView>(R.id.tv_2g_bands).text = smartphone.network.bands2G
        findViewById<TextView>(R.id.tv_3g_bands).text = smartphone.network.bands3G
        findViewById<TextView>(R.id.tv_4g_bands).text = smartphone.network.bands4G

        // Basic Specifications
        findViewById<TextView>(R.id.tv_processor).text = smartphone.processor
        findViewById<TextView>(R.id.tv_ram).text = smartphone.ram
        findViewById<TextView>(R.id.tv_storage).text = smartphone.storage
        findViewById<TextView>(R.id.tv_display).text = smartphone.display
        findViewById<TextView>(R.id.tv_camera).text = smartphone.camera
        findViewById<TextView>(R.id.tv_battery).text = smartphone.battery
        findViewById<TextView>(R.id.tv_os).text = smartphone.os
    }
}