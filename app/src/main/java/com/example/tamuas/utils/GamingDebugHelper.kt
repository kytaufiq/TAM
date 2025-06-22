package com.example.tamuas.utils

import android.util.Log
import com.example.tamuas.repository.SmartphoneRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GamingDebugHelper {

    fun testGamingData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = SmartphoneRepository()

                Log.d("GamingDebugHelper", "=== TESTING GAMING DATA ===")

                val allSmartphones = repository.getAllSmartphones()
                Log.d("GamingDebugHelper", "Total smartphones: ${allSmartphones.size}")

                allSmartphones.forEach { phone ->
                    Log.d("GamingDebugHelper", "Phone: ${phone.name}")
                    Log.d("GamingDebugHelper", "  - gamingType: ${phone.gamingType}")
                    Log.d("GamingDebugHelper", "  - score: ${phone.score}")
                    Log.d("GamingDebugHelper", "  - processor: ${phone.processor}")
                }

                val gamingSmartphones = repository.getGamingSmartphones()
                Log.d("GamingDebugHelper", "Gaming smartphones: ${gamingSmartphones.size}")

                gamingSmartphones.forEach { phone ->
                    Log.d("GamingDebugHelper", "Gaming: ${phone.name} (${phone.score})")
                }

            } catch (e: Exception) {
                Log.e("GamingDebugHelper", "Error in test", e)
            }
        }
    }
}
