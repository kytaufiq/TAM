package com.example.tamuas

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tamuas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the default action bar to prevent black header
        supportActionBar?.hide()

        // Setup custom hamburger menu click listener
        setupCustomHeader()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_beranda, R.id.nav_rekomendasi, R.id.nav_trending, R.id.nav_kategori,
                R.id.nav_hp_2jutaan, R.id.nav_hp_3jutaan, R.id.nav_hp_4jutaan,
                R.id.nav_gaming, R.id.nav_kamera, R.id.nav_tentang,
                R.id.nav_apple, R.id.nav_samsung, R.id.nav_xiaomi,
                R.id.nav_oppo, R.id.nav_vivo, R.id.nav_realme
            ), drawerLayout
        )

        // Setup navigation with NavController
        navView.setupWithNavController(navController)
    }

    private fun setupCustomHeader() {
        // Find the hamburger menu from the included custom header
        val hamburgerMenu = findViewById<ImageView>(R.id.hamburger_menu)

        hamburgerMenu?.setOnClickListener {
            val drawerLayout = binding.drawerLayout
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Don't inflate menu to avoid action bar issues
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val drawerLayout = binding.drawerLayout
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Method untuk mendapatkan container ID yang benar
    fun getFragmentContainerId(): Int {
        return R.id.nav_host_fragment_content_main
    }
}