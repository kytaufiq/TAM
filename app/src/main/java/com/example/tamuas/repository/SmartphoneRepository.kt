package com.example.tamuas.repository

import android.util.Log
import com.example.tamuas.models.Smartphone
import com.example.tamuas.data.LocalSmartphoneData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SmartphoneRepository {
    private val db = FirebaseFirestore.getInstance()
    private val smartphonesCollection = db.collection("smartphones")

    suspend fun getAllSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING HYBRID DATA ===")

            // 1. Ambil dari Firebase dengan manual mapping
            val firebaseData = try {
                val snapshot = smartphonesCollection.get().await()
                val firebaseSmartphones = mutableListOf<Smartphone>()

                snapshot.documents.forEach { document ->
                    try {
                        // 🔧 MANUAL MAPPING untuk fix boolean issue
                        val smartphone = Smartphone(
                            id = document.getString("id") ?: "",
                            name = document.getString("name") ?: "",
                            brand = document.getString("brand") ?: "",
                            price = document.getLong("price") ?: 0L,
                            rating = document.getDouble("rating") ?: 0.0,
                            score = document.getDouble("score") ?: 0.0,
                            processor = document.getString("processor") ?: "",
                            camera = document.getString("camera") ?: "",
                            battery = document.getString("battery") ?: "",
                            display = document.getString("display") ?: "",
                            ram = document.getString("ram") ?: "",
                            storage = document.getString("storage") ?: "",
                            os = document.getString("os") ?: "",
                            description = document.getString("description") ?: "",
                            imageUrl = document.getString("imageUrl") ?: "",
                            category = document.getString("category") ?: "",
                            priceRange = document.getString("priceRange") ?: "",

                            // 🔥 MANUAL BOOLEAN MAPPING
                            isRecommended = document.getBoolean("isRecommended") ?: false,
                            isTrending = document.getBoolean("isTrending") ?: false,
                            trendingPercentage = document.getString("trendingPercentage") ?: "",

                            // Category booleans
                            gamingType = document.getBoolean("gamingType") ?: false,
                            cameraType = document.getBoolean("cameraType") ?: false,
                            price2MType = document.getBoolean("price2MType") ?: false,
                            price3MType = document.getBoolean("price3MType") ?: false,
                            price4MType = document.getBoolean("price4MType") ?: false
                        )

                        firebaseSmartphones.add(smartphone)

                        Log.d("SmartphoneRepository", "Firebase phone mapped: ${smartphone.name}")
                        Log.d("SmartphoneRepository", "  - price3MType: ${smartphone.price3MType}")
                        Log.d("SmartphoneRepository", "  - Raw price3MType: ${document.get("price3MType")}")

                    } catch (e: Exception) {
                        Log.e("SmartphoneRepository", "Error mapping document ${document.id}", e)
                    }
                }

                Log.d("SmartphoneRepository", "Firebase raw data: ${firebaseSmartphones.size}")
                firebaseSmartphones

            } catch (e: Exception) {
                Log.e("SmartphoneRepository", "Firebase error: ${e.message}")
                emptyList()
            }

            // 2. Ambil dari Local
            val localData = LocalSmartphoneData.getLocalSmartphones()
            Log.d("SmartphoneRepository", "Local data: ${localData.size}")

            // 3. Gabungkan (Local data prioritas lebih tinggi)
            val combinedData = mutableListOf<Smartphone>()
            val localIds = localData.map { it.id }.toSet()

            // Tambah local data dulu
            combinedData.addAll(localData)

            // Tambah Firebase data yang tidak ada di local
            firebaseData.forEach { firebasePhone ->
                if (firebasePhone.id !in localIds) {
                    combinedData.add(firebasePhone)
                }
            }

            Log.d("SmartphoneRepository", "Firebase: ${firebaseData.size}, Local: ${localData.size}, Combined: ${combinedData.size}")

            combinedData.sortedByDescending { it.score }

        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error in hybrid fetch", e)
            LocalSmartphoneData.getLocalSmartphones()
        }
    }

    suspend fun getRecommendedSmartphones(): List<Smartphone> {
        return try {
            val allSmartphones = getAllSmartphones()

            // Filter recommended smartphones
            val recommendedSmartphones = allSmartphones.filter { it.isRecommended }

            Log.d("SmartphoneRepository", "All smartphones: ${allSmartphones.size}")
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "  - ${phone.name}: isRecommended=${phone.isRecommended}, source=${if (phone.id.contains("iphone-15-pro") && !phone.id.contains("max")) "Firebase" else "Local"}")
            }
            Log.d("SmartphoneRepository", "Recommended smartphones: ${recommendedSmartphones.size}")

            // Return recommended smartphones, sorted by score
            val result = recommendedSmartphones.sortedByDescending { it.score }.take(5)

            Log.d("SmartphoneRepository", "Final recommended result: ${result.size}")
            result.forEach { phone ->
                Log.d("SmartphoneRepository", "Recommended: ${phone.name} (${phone.score})")
            }

            result

        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching recommended smartphones", e)
            emptyList()
        }
    }

    suspend fun getTrendingSmartphones(): List<Smartphone> {
        return try {
            val allSmartphones = getAllSmartphones()

            // Filter trending smartphones
            val trendingSmartphones = allSmartphones.filter { it.isTrending }

            Log.d("SmartphoneRepository", "All smartphones: ${allSmartphones.size}")
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "  - ${phone.name}: isTrending=${phone.isTrending}, source=${if (phone.id.contains("iphone-15-pro") && !phone.id.contains("max")) "Firebase" else "Local"}")
            }
            Log.d("SmartphoneRepository", "Trending smartphones: ${trendingSmartphones.size}")

            // Return trending smartphones, sorted by trending percentage
            val result = trendingSmartphones.sortedByDescending {
                it.trendingPercentage.replace("+", "").replace("%", "").toDoubleOrNull() ?: 0.0
            }.take(10)

            Log.d("SmartphoneRepository", "Final trending result: ${result.size}")
            result.forEach { phone ->
                Log.d("SmartphoneRepository", "Trending: ${phone.name} (${phone.trendingPercentage})")
            }

            result

        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching trending smartphones", e)
            emptyList()
        }
    }

    suspend fun getSmartphonesByBrand(brand: String): List<Smartphone> {
        return try {
            val allSmartphones = getAllSmartphones()
            allSmartphones.filter { it.brand.equals(brand, ignoreCase = true) }
                .sortedByDescending { it.score }
                .take(5)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSmartphoneById(id: String): Smartphone? {
        return try {
            val allSmartphones = getAllSmartphones()
            allSmartphones.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getBrandCount(brand: String): Int {
        return try {
            val allSmartphones = getAllSmartphones()
            allSmartphones.count { it.brand.equals(brand, ignoreCase = true) }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getGamingSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING GAMING SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Debug: Print all smartphones with their gamingType
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "Phone: ${phone.name}, gamingType: ${phone.gamingType}")
            }

            // Filter gaming smartphones
            val gamingSmartphones = allSmartphones.filter { it.gamingType }
            Log.d("SmartphoneRepository", "Gaming smartphones (gamingType=true): ${gamingSmartphones.size}")

            if (gamingSmartphones.isNotEmpty()) {
                val result = gamingSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} gaming smartphones")
                result
            } else {
                Log.d("SmartphoneRepository", "No gamingType=true found, using fallback logic")
                // Fallback: smartphones dengan kriteria gaming
                val fallbackGaming = allSmartphones.filter {
                    it.description.contains("gaming", ignoreCase = true) ||
                            it.processor.contains("Snapdragon", ignoreCase = true) ||
                            it.processor.contains("A17", ignoreCase = true) ||
                            it.score >= 8.5
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback gaming smartphones: ${fallbackGaming.size}")
                fallbackGaming
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching gaming smartphones", e)
            // Return empty list instead of crashing
            emptyList()
        }
    }

    suspend fun getCameraSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING CAMERA SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Debug: Print all smartphones with their cameraType
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "Phone: ${phone.name}, cameraType: ${phone.cameraType}")
            }

            // Filter camera smartphones
            val cameraSmartphones = allSmartphones.filter { it.cameraType }
            Log.d("SmartphoneRepository", "Camera smartphones (cameraType=true): ${cameraSmartphones.size}")

            if (cameraSmartphones.isNotEmpty()) {
                val result = cameraSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} camera smartphones")
                result
            } else {
                Log.d("SmartphoneRepository", "No cameraType=true found, using fallback logic")
                // Fallback: smartphones dengan kriteria camera
                val fallbackCamera = allSmartphones.filter {
                    it.camera.contains("MP", ignoreCase = true) ||
                            it.description.contains("camera", ignoreCase = true) ||
                            it.description.contains("photo", ignoreCase = true) ||
                            it.score >= 8.0
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback camera smartphones: ${fallbackCamera.size}")
                fallbackCamera
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching camera smartphones", e)
            // Return empty list instead of crashing
            emptyList()
        }
    }

    suspend fun getPrice2MSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING PRICE 2M SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Debug: Print all smartphones with their price2MType
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "Phone: ${phone.name}, price2MType: ${phone.price2MType}, price: ${phone.price}")
            }

            // Filter price 2M smartphones
            val price2MSmartphones = allSmartphones.filter { it.price2MType }
            Log.d("SmartphoneRepository", "Price 2M smartphones (price2MType=true): ${price2MSmartphones.size}")

            if (price2MSmartphones.isNotEmpty()) {
                val result = price2MSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} price 2M smartphones")
                result
            } else {
                Log.d("SmartphoneRepository", "No price2MType=true found, using fallback logic")
                // Fallback: smartphones dengan harga 2 jutaan
                val fallbackPrice2M = allSmartphones.filter {
                    it.price in 1000000..2999999
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback price 2M smartphones: ${fallbackPrice2M.size}")
                fallbackPrice2M
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching price 2M smartphones", e)
            emptyList()
        }
    }

    suspend fun getPrice3MSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING PRICE 3M SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Debug: Print all smartphones with their price3MType
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "Phone: ${phone.name}, price3MType: ${phone.price3MType}, price: ${phone.price}")
            }

            // Filter price 3M smartphones
            val price3MSmartphones = allSmartphones.filter { it.price3MType }
            Log.d("SmartphoneRepository", "Price 3M smartphones (price3MType=true): ${price3MSmartphones.size}")

            if (price3MSmartphones.isNotEmpty()) {
                val result = price3MSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} price 3M smartphones")
                result
            } else {
                Log.d("SmartphoneRepository", "No price3MType=true found, using fallback logic")
                // Fallback: smartphones dengan harga 3 jutaan
                val fallbackPrice3M = allSmartphones.filter {
                    it.price in 2000000..3999999
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback price 3M smartphones: ${fallbackPrice3M.size}")
                fallbackPrice3M
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching price 3M smartphones", e)
            emptyList()
        }
    }

    suspend fun getPrice4MSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING PRICE 4M SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Debug: Print all smartphones with their price4MType
            allSmartphones.forEach { phone ->
                Log.d("SmartphoneRepository", "Phone: ${phone.name}, price4MType: ${phone.price4MType}, price: ${phone.price}")
            }

            // Filter price 4M smartphones
            val price4MSmartphones = allSmartphones.filter { it.price4MType }
            Log.d("SmartphoneRepository", "Price 4M smartphones (price4MType=true): ${price4MSmartphones.size}")

            if (price4MSmartphones.isNotEmpty()) {
                val result = price4MSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} price 4M smartphones")
                result
            } else {
                Log.d("SmartphoneRepository", "No price4MType=true found, using fallback logic")
                // Fallback: smartphones dengan harga 4 jutaan
                val fallbackPrice4M = allSmartphones.filter {
                    it.price in 3000000..4999999
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback price 4M smartphones: ${fallbackPrice4M.size}")
                fallbackPrice4M
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching price 4M smartphones", e)
            emptyList()
        }
    }

    suspend fun getAppleSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING APPLE SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Filter Apple smartphones by brand
            val appleSmartphones = allSmartphones.filter {
                it.brand.equals("Apple", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Apple smartphones (brand=Apple): ${appleSmartphones.size}")

            if (appleSmartphones.isNotEmpty()) {
                val result = appleSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Apple smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Apple: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Apple smartphones found, using fallback logic")
                // Fallback: smartphones dengan kriteria Apple
                val fallbackApple = allSmartphones.filter {
                    it.name.contains("iPhone", ignoreCase = true) ||
                            it.processor.contains("A1", ignoreCase = true) ||
                            it.os.contains("iOS", ignoreCase = true) ||
                            it.brand.contains("Apple", ignoreCase = true)
                }.sortedByDescending { it.score }

                Log.d("SmartphoneRepository", "Fallback Apple smartphones: ${fallbackApple.size}")
                fallbackApple
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Apple smartphones", e)
            emptyList()
        }
    }

    suspend fun getSamsungSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING SAMSUNG SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Filter Samsung smartphones by brand
            val samsungSmartphones = allSmartphones.filter {
                it.brand.equals("Samsung", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Samsung smartphones (brand=Samsung): ${samsungSmartphones.size}")

            if (samsungSmartphones.isNotEmpty()) {
                val result = samsungSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Samsung smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Samsung: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Samsung smartphones found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Samsung smartphones", e)
            emptyList()
        }
    }

    suspend fun getXiaomiSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING XIAOMI SMARTPHONES ===")

            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")

            // Filter Xiaomi smartphones by brand
            val xiaomiSmartphones = allSmartphones.filter {
                it.brand.equals("Xiaomi", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Xiaomi smartphones (brand=Xiaomi): ${xiaomiSmartphones.size}")

            if (xiaomiSmartphones.isNotEmpty()) {
                val result = xiaomiSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Xiaomi smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Xiaomi: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Xiaomi smartphones found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Xiaomi smartphones", e)
            emptyList()
        }
    }
    suspend fun getOppoSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING OPPO SMARTPHONES ===")
            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")
            // Filter Oppo smartphones by brand
            val oppoSmartphones = allSmartphones.filter {
                it.brand.equals("Oppo", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Oppo smartphones (brand=Oppo): ${oppoSmartphones.size}")

            if (oppoSmartphones.isNotEmpty()) {
                val result = oppoSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Oppo smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Oppo: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Oppo smartphones found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Oppo smartphones", e)
            emptyList()
     }
    }

    suspend fun getVivoSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING VIVO SMARTPHONES ===")
            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")
            // Filter Vivo smartphones by brand
            val vivoSmartphones = allSmartphones.filter {
                it.brand.equals("Vivo", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Vivo smartphones (brand=Vivo): ${vivoSmartphones.size}")

            if (vivoSmartphones.isNotEmpty()) {
                val result = vivoSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Vivo smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Vivo: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Vivo smartphones found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Vivo smartphones", e)
            emptyList()
        }
    }

    suspend fun getRealmeSmartphones(): List<Smartphone> {
        return try {
            Log.d("SmartphoneRepository", "=== FETCHING REALME SMARTPHONES ===")
            val allSmartphones = getAllSmartphones()
            Log.d("SmartphoneRepository", "Total smartphones: ${allSmartphones.size}")
            // Filter Realme smartphones by brand
            val realmeSmartphones = allSmartphones.filter {
                it.brand.equals("Realme", ignoreCase = true)
            }
            Log.d("SmartphoneRepository", "Realme smartphones (brand=Realme): ${realmeSmartphones.size}")

            if (realmeSmartphones.isNotEmpty()) {
                val result = realmeSmartphones.sortedByDescending { it.score }
                Log.d("SmartphoneRepository", "Returning ${result.size} Realme smartphones")
                result.forEach { phone ->
                    Log.d("SmartphoneRepository", "Realme: ${phone.name} (${phone.score})")
                }
                result
            } else {
                Log.d("SmartphoneRepository", "No Realme smartphones found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SmartphoneRepository", "Error fetching Realme smartphones", e)
            emptyList()
        }
    }
}
