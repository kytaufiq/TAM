package com.example.tamuas.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tamuas.R
import com.example.tamuas.repository.SmartphoneRepository
import com.example.tamuas.models.Smartphone
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class DetailFragment : Fragment() {

    private val smartphoneRepository = SmartphoneRepository()
    private var smartphoneId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            smartphoneId = it.getString("smartphone_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton(view)
        loadSmartphoneDetail(view)
    }

    private fun setupBackButton(view: View) {
        view.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("DetailFragment", "Error navigating back", e)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun loadSmartphoneDetail(view: View) {
        val id = smartphoneId
        if (id == null) {
            Log.e("DetailFragment", "Smartphone ID is null")
            showErrorMessage(view)
            return
        }

        if (!isAdded || context == null) {
            Log.w("DetailFragment", "Fragment not attached")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val smartphone = smartphoneRepository.getSmartphoneById(id)

                if (!isAdded || context == null) {
                    Log.w("DetailFragment", "Fragment detached during data load")
                    return@launch
                }

                if (smartphone != null) {
                    populateSmartphoneDetail(view, smartphone)
                } else {
                    Log.w("DetailFragment", "Smartphone not found with ID: $id")
                    showErrorMessage(view)
                }
            } catch (e: Exception) {
                Log.e("DetailFragment", "Error loading smartphone detail", e)
                if (isAdded && context != null) {
                    showErrorMessage(view)
                }
            }
        }
    }

    private fun populateSmartphoneDetail(view: View, smartphone: Smartphone) {
        try {
            if (!isAdded || context == null) return

            // Set product name
            view.findViewById<TextView>(R.id.tv_product_name)?.text = smartphone.name

            // Set rating
            view.findViewById<TextView>(R.id.tv_rating)?.text = smartphone.rating.toString()

            // Set score
            view.findViewById<TextView>(R.id.tv_score)?.text = "Score: ${smartphone.score}"

            // Set product image
            val imageView = view.findViewById<ImageView>(R.id.iv_product_image)
            if (imageView != null) {
                try {
                    Glide.with(this)
                        .load(smartphone.imageUrl)
                        .placeholder(R.drawable.hp_iphone15pro)
                        .error(R.drawable.hp_iphone15pro)
                        .into(imageView)
                } catch (e: Exception) {
                    Log.e("DetailFragment", "Error loading image", e)
                    imageView.setImageResource(R.drawable.hp_iphone15pro)
                }
            }

            // Set price
            val formatter = NumberFormat.getInstance(Locale("id", "ID"))
            val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
            view.findViewById<TextView>(R.id.tv_price)?.text = formattedPrice

            // Set description
            view.findViewById<TextView>(R.id.tv_description)?.text = smartphone.description

            // Set specifications
            populateSpecifications(view, smartphone)

        } catch (e: Exception) {
            Log.e("DetailFragment", "Error populating smartphone detail", e)
        }
    }

    private fun populateSpecifications(view: View, smartphone: Smartphone) {
        try {
            // Network specifications
            view.findViewById<TextView>(R.id.tv_technology)?.text = extractTechnology(smartphone)
            view.findViewById<TextView>(R.id.tv_2g_bands)?.text = extract2GBands(smartphone)
            view.findViewById<TextView>(R.id.tv_3g_bands)?.text = extract3GBands(smartphone)
            view.findViewById<TextView>(R.id.tv_4g_bands)?.text = extract4GBands(smartphone)

            // Basic specifications
            view.findViewById<TextView>(R.id.tv_processor)?.text = smartphone.processor
            view.findViewById<TextView>(R.id.tv_ram)?.text = smartphone.ram
            view.findViewById<TextView>(R.id.tv_storage)?.text = smartphone.storage
            view.findViewById<TextView>(R.id.tv_display)?.text = smartphone.display
            view.findViewById<TextView>(R.id.tv_camera)?.text = smartphone.camera
            view.findViewById<TextView>(R.id.tv_battery)?.text = smartphone.battery
            view.findViewById<TextView>(R.id.tv_os)?.text = smartphone.os

        } catch (e: Exception) {
            Log.e("DetailFragment", "Error populating specifications", e)
        }
    }

    private fun extractTechnology(smartphone: Smartphone): String {
        return when (smartphone.brand.lowercase()) {
            "apple" -> "GSM / CDMA / HSPA / EVDO / LTE / 5G"
            "samsung" -> "GSM / HSPA / LTE / 5G"
            "xiaomi" -> "GSM / HSPA / LTE / 5G"
            "oppo" -> "GSM / HSPA / LTE"
            "vivo" -> "GSM / HSPA / LTE"
            "realme" -> "GSM / HSPA / LTE"
            else -> "GSM / HSPA / LTE"
        }
    }

    private fun extract2GBands(smartphone: Smartphone): String {
        return "GSM 850 / 900 / 1800 / 1900"
    }

    private fun extract3GBands(smartphone: Smartphone): String {
        return when (smartphone.brand.lowercase()) {
            "apple" -> "HSDPA 850 / 900 / 1700(AWS) / 1900 / 2100"
            else -> "HSDPA 850 / 900 / 1700(AWS) / 1900 / 2100"
        }
    }

    private fun extract4GBands(smartphone: Smartphone): String {
        return when (smartphone.brand.lowercase()) {
            "apple" -> "1, 2, 3, 4, 5, 7, 8, 12, 13, 17, 18, 19, 20, 25, 26, 28, 30, 32, 34, 38, 39, 40, 41, 42, 46, 48, 66"
            "samsung" -> "1, 2, 3, 4, 5, 7, 8, 12, 17, 18, 19, 20, 26, 28, 38, 40, 41"
            else -> "1, 2, 3, 4, 5, 7, 8, 12, 17, 18, 19, 20, 26, 28, 38, 40, 41"
        }
    }

    private fun showErrorMessage(view: View) {
        try {
            if (!isAdded || context == null) return

            view.findViewById<TextView>(R.id.tv_product_name)?.text = "Error Loading Data"
            view.findViewById<TextView>(R.id.tv_description)?.text = "Gagal memuat detail smartphone"
        } catch (e: Exception) {
            Log.e("DetailFragment", "Error showing error message", e)
        }
    }

    companion object {
        fun newInstance(smartphoneId: String): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString("smartphone_id", smartphoneId)
            fragment.arguments = args
            return fragment
        }
    }
}