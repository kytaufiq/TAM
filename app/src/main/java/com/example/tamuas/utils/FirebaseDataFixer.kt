package com.example.tamuas.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseDataFixer {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fixFirebaseDocument() {
        try {
            val documentRef = db.collection("smartphones").document("iphone-15-pro")

            // Update dengan explicit boolean values
            val updates = mapOf(
                "isRecommended" to true,
                "isTrending" to true,
                "gamingType" to true,
                "cameraType" to true,
                "price2MType" to false,
                "price3MType" to false,
                "price4MType" to false,
                "score" to 9.3,
                "rating" to 4.8,
                "trendingPercentage" to "+18%"
            )

            documentRef.update(updates).await()

            Log.d("FirebaseDataFixer", "Document updated successfully!")

        } catch (e: Exception) {
            Log.e("FirebaseDataFixer", "Error updating document", e)
        }
    }
}
