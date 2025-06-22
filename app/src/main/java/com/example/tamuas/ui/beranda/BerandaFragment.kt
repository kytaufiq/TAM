package com.example.tamuas.ui.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamuas.R

class BerandaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)
        setupDetailButtons(view)
        return view
    }

    private fun setupDetailButtons(view: View) {
        val btnDetailIphone = view.findViewById<LinearLayout>(R.id.btn_detail_iphone)
        btnDetailIphone.setOnClickListener {
            // Gunakan action yang sudah didefinisikan di navigation graph
            findNavController().navigate(R.id.action_beranda_to_detail)
        }

        val btnDetailSamsung = view.findViewById<LinearLayout>(R.id.btn_detail_samsung)
        btnDetailSamsung?.setOnClickListener {
            findNavController().navigate(R.id.action_beranda_to_detail)
        }

        val btnDetailGoogle = view.findViewById<LinearLayout>(R.id.btn_detail_google)
        btnDetailGoogle?.setOnClickListener {
            findNavController().navigate(R.id.action_beranda_to_detail)
        }
    }
}