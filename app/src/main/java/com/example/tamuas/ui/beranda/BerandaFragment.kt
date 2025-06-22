package com.example.tamuas.ui.beranda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import com.example.tamuas.R
import com.example.tamuas.repository.SmartphoneRepository
import com.example.tamuas.adapters.SmartphoneAdapter
import android.widget.Toast

class BerandaFragment : Fragment() {

    private lateinit var adapter: SmartphoneAdapter
    private val smartphoneRepository = SmartphoneRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BerandaFragment", "onViewCreated called")

        setupRecyclerView(view)
        setupBrandClickListeners(view)
        addTestDataAndLoad()
    }

    private fun addTestDataAndLoad() {
        lifecycleScope.launch {
            try {
                kotlinx.coroutines.delay(1000)
                loadRecommendedSmartphones()
            } catch (e: Exception) {
                Log.e("BerandaFragment", "Error in addTestDataAndLoad", e)
                loadRecommendedSmartphones()
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        Log.d("BerandaFragment", "Setting up RecyclerView")

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_recommended)

        if (recyclerView == null) {
            Log.e("BerandaFragment", "RecyclerView not found!")
            return
        }

        adapter = SmartphoneAdapter(emptyList()) { smartphone ->
            Log.d("BerandaFragment", "Clicked smartphone: ${smartphone.name}")
            navigateToDetail(smartphone.id)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        Log.d("BerandaFragment", "RecyclerView setup complete")
    }

    private fun loadRecommendedSmartphones() {
        Log.d("BerandaFragment", "Starting to load recommended smartphones")

        lifecycleScope.launch {
            try {
                val smartphones = smartphoneRepository.getRecommendedSmartphones()

                Log.d("BerandaFragment", "Got ${smartphones.size} smartphones")
                smartphones.forEach { phone ->
                    Log.d("BerandaFragment", "Phone: ${phone.name} from ${if (phone.id.startsWith("test-")) "Firebase" else "Local"}")
                }

                adapter.updateData(smartphones)
                Log.d("BerandaFragment", "Adapter updated with data")

            } catch (e: Exception) {
                Log.e("BerandaFragment", "Error loading smartphones", e)
            }
        }
    }

    private fun navigateToDetail(smartphoneId: String) {
        try {
            Log.d("BerandaFragment", "Navigating to detail for smartphone: $smartphoneId")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val bundle = Bundle().apply {
                putString("smartphone_id", smartphoneId)
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_detail, bundle)

            Log.d("BerandaFragment", "Navigation to detail initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to detail", e)
            if (isAdded && context != null) {
                Toast.makeText(
                    requireContext(),
                    "Error navigating to detail: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupBrandClickListeners(view: View) {
        Log.d("BerandaFragment", "Setting up brand click listeners")

        view.findViewById<View>(R.id.brand_apple)?.setOnClickListener {
            Log.d("BerandaFragment", "=== APPLE BRAND CLICKED ===")
            navigateToAppleFragment()
        }

        view.findViewById<View>(R.id.brand_samsung)?.setOnClickListener {
            Log.d("BerandaFragment", "=== SAMSUNG BRAND CLICKED ===")
            navigateToSamsungFragment()
        }

        view.findViewById<View>(R.id.brand_xiaomi)?.setOnClickListener {
            Log.d("BerandaFragment", "=== XIAOMI BRAND CLICKED ===")
            navigateToXiaomiFragment()
        }

        view.findViewById<View>(R.id.brand_oppo)?.setOnClickListener {
            Log.d("BerandaFragment", "=== OPPO BRAND CLICKED ===")
            navigateToOppoFragment()
        }

        view.findViewById<View>(R.id.brand_vivo)?.setOnClickListener {
            Log.d("BerandaFragment", "=== VIVO BRAND CLICKED ===")
            navigateToVivoFragment()
        }

        view.findViewById<View>(R.id.brand_realme)?.setOnClickListener {
            Log.d("BerandaFragment", "=== REALME BRAND CLICKED ===")
            navigateToRealmeFragment()
        }
    }

    private fun navigateToAppleFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Apple Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_apple)

            Log.d("BerandaFragment", "Apple Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Apple Fragment", e)
            fallbackNavigation("apple")
        }
    }

    private fun navigateToSamsungFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Samsung Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_samsung)

            Log.d("BerandaFragment", "Samsung Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Samsung Fragment", e)
            fallbackNavigation("samsung")
        }
    }

    private fun navigateToXiaomiFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Xiaomi Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_xiaomi)

            Log.d("BerandaFragment", "Xiaomi Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Xiaomi Fragment", e)
            fallbackNavigation("xiaomi")
        }
    }

    private fun navigateToOppoFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Oppo Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_oppo)

            Log.d("BerandaFragment", "Oppo Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Oppo Fragment", e)
            fallbackNavigation("oppo")
        }
    }

    private fun navigateToVivoFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Vivo Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_vivo)

            Log.d("BerandaFragment", "Vivo Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Vivo Fragment", e)
            fallbackNavigation("vivo")
        }
    }

    private fun navigateToRealmeFragment() {
        try {
            Log.d("BerandaFragment", "Navigating to Realme Fragment...")

            if (!isAdded || context == null) {
                Log.w("BerandaFragment", "Fragment not attached")
                return
            }

            val navController = findNavController()
            navController.navigate(R.id.action_beranda_to_realme)

            Log.d("BerandaFragment", "Realme Fragment navigation initiated")

        } catch (e: Exception) {
            Log.e("BerandaFragment", "Error navigating to Realme Fragment", e)
            fallbackNavigation("realme")
        }
    }

    private fun fallbackNavigation(brand: String) {
        try {
            val fragment = when (brand) {
                "apple" -> com.example.tamuas.ui.apple.AppleFragment()
                "samsung" -> com.example.tamuas.ui.samsung.SamsungFragment()
                "xiaomi" -> com.example.tamuas.ui.xiaomi.XiaomiFragment()
                "oppo" -> com.example.tamuas.ui.oppo.OppoFragment()
                "vivo" -> com.example.tamuas.ui.vivo.VivoFragment()
                "realme" -> com.example.tamuas.ui.realme.RealmeFragment()
                else -> return
            }

            val fragmentManager = parentFragmentManager

            if (fragmentManager.isStateSaved) {
                Log.w("BerandaFragment", "FragmentManager state already saved")
                return
            }

            val containerId = R.id.nav_host_fragment_content_main

            fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(brand)
                .commitAllowingStateLoss()

            Log.d("BerandaFragment", "Fallback navigation successful for $brand")

        } catch (fallbackError: Exception) {
            Log.e("BerandaFragment", "Fallback navigation also failed for $brand", fallbackError)
            if (isAdded && context != null) {
                Toast.makeText(
                    requireContext(),
                    "Navigation Error: ${fallbackError.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}