package com.example.tamuas.models

data class Smartphone(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val price: Long = 0,
    val rating: Double = 0.0,
    val score: Double = 0.0,
    val processor: String = "",
    val camera: String = "",
    val battery: String = "",
    val display: String = "",
    val ram: String = "",
    val storage: String = "",
    val os: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val priceRange: String = "",
    val isRecommended: Boolean = false,
    val isTrending: Boolean = false,
    val trendingPercentage: String = "",
    val network: Network = Network(),

    // ✅ FIXED: Renamed Boolean fields to avoid conflicts
    val gamingType: Boolean = false,        // Gaming Terbaik (was isGaming)
    val cameraType: Boolean = false,        // Kamera Terbaik (was isCamera)
    val price2MType: Boolean = false,       // HP 2 Jutaan Terbaik (was isPrice2M)
    val price3MType: Boolean = false,       // HP 3 Jutaan Terbaik (was isPrice3M)
    val price4MType: Boolean = false        // HP 4 Jutaan Terbaik (was isPrice4M)
)

data class Network(
    val technology: String = "",
    val bands2G: String = "",
    val bands3G: String = "",
    val bands4G: String = ""
)