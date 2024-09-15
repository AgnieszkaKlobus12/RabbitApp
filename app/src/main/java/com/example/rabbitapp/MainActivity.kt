package com.example.rabbitapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rabbitapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.material.navigation.NavigationView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        GlobalScope.launch(Dispatchers.IO) {
            download()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun upload() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val credential = GoogleAccountCredential.usingOAuth2(
            this,
            setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.setSelectedAccount(googleSignInAccount!!.account)

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(R.string.app_name))
            .build()

        val dbPath = applicationContext.getDatabasePath("app_database").path
        val dbPathShm =
            applicationContext.getDatabasePath("app_database").parent?.plus("/app_database-shm")
        val dbPathWal =
            applicationContext.getDatabasePath("app_database").parent?.plus("/app_database-wal")

        val storageFile = File().apply {
            parents = listOf("appDataFolder")
            name = "app_database"
        }

        val storageFileShm = File().apply {
            parents = listOf("appDataFolder")
            name = "app_database-shm"
        }

        val storageFileWal = File().apply {
            parents = listOf("appDataFolder")
            name = "app_database-wal"
        }

        val filePath = java.io.File(dbPath)
        val filePathShm = dbPathShm?.let { java.io.File(it) }
        val filePathWal = dbPathWal?.let { java.io.File(it) }
        val mediaContent = FileContent("", filePath)
        val mediaContentShm = FileContent("", filePathShm)
        val mediaContentWal = FileContent("", filePathWal)

        try {
            val file = googleDriveService.files().create(storageFile, mediaContent).execute()
            Log.d("Google Drive", "Filename: ${file.name} File ID: ${file.id}")

            val fileShm =
                googleDriveService.files().create(storageFileShm, mediaContentShm).execute()
            Log.d("Google Drive", "Filename: ${fileShm.name} File ID: ${fileShm.id}")

            val fileWal =
                googleDriveService.files().create(storageFileWal, mediaContentWal).execute()
            Log.d("Google Drive", "Filename: ${fileWal.name} File ID: ${fileWal.id}")
        } catch (e: UserRecoverableAuthIOException) {
            startActivityForResult(e.intent, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun download() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val credential = GoogleAccountCredential.usingOAuth2(
            this,
            setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.setSelectedAccount(googleSignInAccount!!.account)

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(R.string.app_name))
            .build()

        try {
            val dbDir = applicationContext.getDatabasePath("app_database").parent
            val dir = dbDir?.let { java.io.File(it) }
            if (dir != null) {
                if (dir.isDirectory) {
                    val children = dir.list()
                    if (children != null) {
                        for (i in children.indices) {
                            java.io.File(dir, children[i]).delete()
                        }
                    }
                }
            }

            val dbPath = applicationContext.getDatabasePath("app_database").path
            val dbPathShm =
                applicationContext.getDatabasePath("app_database").parent?.plus("/app_database-shm")
            val dbPathWal =
                applicationContext.getDatabasePath("app_database").parent?.plus("/app_database-wal")

            val files: FileList = googleDriveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()
            if (files.files.size == 0) Log.e("Synchronisation Error", "No DB file exists in Drive")
            for (file in files.files) {
                System.out.printf(
                    "Found file: %s (%s) %s\n",
                    file.name, file.id, file.createdTime
                )
                if (file.name == "app_database") {
                    val outputStream: OutputStream = FileOutputStream(dbPath)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                } else if (file.name == "app_database-shm") {
                    val outputStream: OutputStream = FileOutputStream(dbPathShm)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                } else if (file.name == "app_database-wal") {
                    val outputStream: OutputStream = FileOutputStream(dbPathWal)
                    googleDriveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}