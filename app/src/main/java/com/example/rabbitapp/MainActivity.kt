package com.example.rabbitapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rabbitapp.databinding.ActivityMainBinding
import com.example.rabbitapp.utils.GoogleDriveClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var googleDriveClient: GoogleDriveClient

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_rabbit_list,
                R.id.navigation_vaccine_list,
                R.id.navigation_matings,
                R.id.navigation_sicknesses,
                R.id.navigation_settings
            ), binding.mainActivityDrawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.navView.visibility = View.GONE
        binding.appBarMain.root.visibility = View.GONE
        binding.progressBarMain.visibility = View.VISIBLE
        binding.progressBarMain.bringToFront()

        googleDriveClient = GoogleDriveClient(this)
        GlobalScope.launch(Dispatchers.IO) {
            googleDriveClient.downloadDatabase()
            runOnUiThread {
                binding.navView.visibility = View.VISIBLE
                binding.progressBarMain.visibility = View.GONE
                binding.appBarMain.root.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}