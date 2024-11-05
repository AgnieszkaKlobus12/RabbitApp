package com.example.rabbitapp

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rabbitapp.databinding.ActivityMainBinding
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.GoogleDriveClient
import com.example.rabbitapp.utils.NetworkChangeReceiver
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var googleDriveClient: GoogleDriveClient
    private lateinit var viewModel: MainListViewModel
    private lateinit var networkReceiver: NetworkChangeReceiver

    override fun onStart() {
        super.onStart()
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkReceiver)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkReceiver = NetworkChangeReceiver { isConnected ->
            if (isConnected) {
                viewModel.setEditable(true)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.internet_error), //todo custom bigger toast
                    Toast.LENGTH_LONG
                ).show()
                viewModel.setEditable(false)
            }
        }

        viewModel = ViewModelProvider(this)[MainListViewModel::class.java]
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

        if (!networkReceiver.isInternetAvailable(this)) {
            Toast.makeText(
                this,
                getString(R.string.internet_error), //todo custom bigger toast
                Toast.LENGTH_LONG
            ).show()

            binding.navView.visibility = View.VISIBLE
            binding.progressBarMain.visibility = View.GONE
            binding.appBarMain.root.visibility = View.VISIBLE
            viewModel.setEditable(false)
        } else {
            viewModel.setEditable(true)
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}