package com.example.rabbitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
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
import com.example.rabbitapp.utils.MainListViewModel
import com.example.rabbitapp.utils.NetworkChangeReceiver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.loginWithGoogle(account, ::loginCallback)
        } catch (e: ApiException) {
            Log.w("LOGIN", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun loginCallback(success: Boolean) {
        if (success) {
            viewModel.setLock(viewModel.getGoogleDriveClient().checkAndClaimDatabaseBlock())
            this.onCreate(null)
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkReceiver = NetworkChangeReceiver { isConnected ->
            if (isConnected) {
                viewModel.setInternet(true)
                viewModel.getGoogleDriveClient().setInternetConnection(true)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.internet_error), //todo custom bigger toast
                    Toast.LENGTH_LONG
                ).show()
                viewModel.getGoogleDriveClient().setInternetConnection(false)
                viewModel.setInternet(false)
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
            viewModel.setInternet(false)
        } else {
            viewModel.setInternet(true)
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.getGoogleDriveClient().downloadDatabase(this@MainActivity)
                viewModel.dataRefresh.postValue(Random.nextInt(1000))
                if (!viewModel.getGoogleDriveClient().checkAndClaimDatabaseBlock()) {
                    viewModel.setLock(false)
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.no_lock), //todo custom bigger toast
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
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