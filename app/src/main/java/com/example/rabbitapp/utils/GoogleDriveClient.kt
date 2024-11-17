package com.example.rabbitapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getString
import com.example.rabbitapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDateTime

class GoogleDriveClient(private val context: Context, private var internetConnection: Boolean) {

    fun setInternetConnection(internetConnection: Boolean) {
        this.internetConnection = internetConnection
    }

    fun uploadLocalDatabase() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.setSelectedAccount(googleSignInAccount!!.account)

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(context, R.string.app_name))
            .build()

        val dbPath = context.getDatabasePath("app_database").path
        val dbPathShm =
            context.getDatabasePath("app_database").parent?.plus("/app_database-shm")
        val dbPathWal =
            context.getDatabasePath("app_database").parent?.plus("/app_database-wal")
        val dbPathCache =
            context.cacheDir.path.plus("/app_database.lck")

        val filePath = java.io.File(dbPath)
        val filePathShm = dbPathShm?.let { java.io.File(it) }
        val filePathWal = dbPathWal?.let { java.io.File(it) }
        val filePathCache = java.io.File(dbPathCache)
        val storageFileContent = FileContent("", filePath)
        val storageFileShmContent = FileContent("", filePathShm)
        val storageFileWalContent = FileContent("", filePathWal)
        val storageFileCacheContent = FileContent("", filePathCache)

        uploadOrUpdateFile("app_database.lck", storageFileCacheContent, googleDriveService)
        uploadOrUpdateFile("app_database", storageFileContent, googleDriveService)
        uploadOrUpdateFile("app_database-shm", storageFileShmContent, googleDriveService)
        uploadOrUpdateFile("app_database-wal", storageFileWalContent, googleDriveService)
    }

    private fun findFileByName(fileName: String, googleDriveService: Drive): File? {
        val result = googleDriveService.files().list()
            .setQ("name = '$fileName' and 'appDataFolder' in parents")
            .setSpaces("appDataFolder")
            .execute()
        return result.files?.firstOrNull()
    }

    private fun uploadOrUpdateFile(
        fileName: String,
        mediaContent: FileContent,
        googleDriveService: Drive
    ): File? {
        try {
            val existingFile = findFileByName(fileName, googleDriveService)
            return if (existingFile != null) {
                googleDriveService.files().update(existingFile.id, null, mediaContent)
                    .execute()
            } else {
                val storageFile = File().apply {
                    parents = listOf("appDataFolder")
                    name = fileName
                }
                googleDriveService.files().create(storageFile, mediaContent).execute()
            }
        } catch (e: Exception) {
            Log.e("Google Drive", "Error: ${e.message}", e)
            return null
        }
    }

    fun downloadDatabase(activity: Activity) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.setSelectedAccount(googleSignInAccount!!.account)

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(context, R.string.app_name))
            .build()

        try {
            val dbPath = context.getDatabasePath("app_database").path
            val dbPathShm =
                context.getDatabasePath("app_database").parent?.plus("/app_database-shm")
            val dbPathWal =
                context.getDatabasePath("app_database").parent?.plus("/app_database-wal")
            val dbPathCache =
                context.cacheDir.path.plus("/app_database.lck")

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
                when (file.name) {
                    "app_database" -> {
                        val outputStream: OutputStream = FileOutputStream(dbPath)
                        googleDriveService.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)
                    }

                    "app_database-shm" -> {
                        val outputStream: OutputStream = FileOutputStream(dbPathShm)
                        googleDriveService.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)
                    }

                    "app_database-wal" -> {
                        val outputStream: OutputStream = FileOutputStream(dbPathWal)
                        googleDriveService.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)
                    }

                    "app_database.lck" -> {
                        val outputStream: OutputStream = FileOutputStream(dbPathCache)
                        googleDriveService.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)
                    }
                }
            }
        } catch (e: UserRecoverableAuthIOException) {
            startActivityForResult(activity, e.intent, 10, null)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun checkAndClaimDatabaseBlock(): Boolean {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.setSelectedAccount(googleSignInAccount!!.account)

        val googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(getString(context, R.string.app_name))
            .build()

        try {
            val files: FileList = googleDriveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(10)
                .execute()
            for (file in files.files) {
                when (file.name) {
                    "data_lock_file" -> {
                        val outputStream = ByteArrayOutputStream()
                        googleDriveService.files().get(file.id)
                            .executeMediaAndDownloadTo(outputStream)
                        val fileBytes = outputStream.toByteArray()
                        val dateTime = LocalDateTime.parse(String(fileBytes, Charsets.UTF_8))
//                        if (dateTime.plusMinutes(2).isAfter(LocalDateTime.now())) { //todo disabled for tests
//                            return false
//                        } else {
                        refreshLock()
                        return true
//                        }
                    }
                }
            }
            refreshLock()
            return true

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    private fun refreshLock() {
        CoroutineScope(Dispatchers.Default).launch {
            val googleSignInAccount =
                GoogleSignIn.getLastSignedInAccount(context)
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                setOf(Scopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
            )
            credential.setSelectedAccount(googleSignInAccount!!.account)

            val googleDriveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName(getString(context, R.string.app_name))
                .build()

            while (true) {
                if (internetConnection) {
                    val files: FileList = googleDriveService.files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name, createdTime)")
                        .setPageSize(10)
                        .execute()
                    for (file in files.files) {
                        if (file.name == "data_lock_file")
                            googleDriveService.files().delete(file.id).execute()
                    }

                    val storageFile = File().apply {
                        parents = listOf("appDataFolder")
                        name = "data_lock_file"
                    }
                    val fileBytes = LocalDateTime.now().toString().toByteArray(Charsets.UTF_8)
                    val byteArrayContent = ByteArrayContent(null, fileBytes)
                    googleDriveService.files().create(storageFile, byteArrayContent).execute()
                    Log.d("Synchronisation", "Database lock refreshed")
                }
                delay(60 * 1000L)
            }
        }
    }

}