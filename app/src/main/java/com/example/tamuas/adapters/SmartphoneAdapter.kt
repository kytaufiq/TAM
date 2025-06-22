package com.example.tamuas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tamuas.R
import com.example.tamuas.models.Smartphone
import java.text.NumberFormat
import java.util.*

class SmartphoneAdapter(
    private var smartphones: List<Smartphone>,
    private val onItemClick: (Smartphone) -> Unit
) : RecyclerView.Adapter<SmartphoneAdapter.SmartphoneViewHolder>() {

    class SmartphoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_smartphone)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        val brandTextView: TextView = itemView.findViewById(R.id.tv_brand)
        val priceTextView: TextView = itemView.findViewById(R.id.tv_price)
        val ratingTextView: TextView = itemView.findViewById(R.id.tv_rating)
        val processorTextView: TextView = itemView.findViewById(R.id.tv_processor)
        val cameraTextView: TextView = itemView.findViewById(R.id.tv_camera)
        val batteryTextView: TextView = itemView.findViewById(R.id.tv_battery)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_description)
        val detailButton: View = itemView.findViewById(R.id.detail_button_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartphoneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_smartphone, parent, false)
        return SmartphoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmartphoneViewHolder, position: Int) {
        val smartphone = smartphones[position]

        holder.nameTextView.text = smartphone.name
        holder.brandTextView.text = smartphone.brand
        holder.ratingTextView.text = "★ ${smartphone.rating}"
        holder.processorTextView.text = smartphone.processor
        holder.cameraTextView.text = smartphone.camera
        holder.batteryTextView.text = smartphone.battery
        holder.descriptionTextView.text = smartphone.description

        // Format price
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        val formattedPrice = "Rp ${formatter.format(smartphone.price)}"
        holder.priceTextView.text = formattedPrice

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(smartphone.imageUrl)
            .placeholder(R.drawable.hp_iphone15pro)
            .error(R.drawable.hp_iphone15pro)
            .into(holder.imageView)

        // Set click listeners for both item and detail button
        holder.itemView.setOnClickListener {
            onItemClick(smartphone)
        }

        holder.detailButton.setOnClickListener {
            onItemClick(smartphone)
        }
    }

    override fun getItemCount(): Int = smartphones.size

    fun updateData(newSmartphones: List<Smartphone>) {
        smartphones = newSmartphones
        notifyDataSetChanged()
    }
}