package com.nasahacker.steelmind.extension

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.nasahacker.steelmind.dto.User
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.util.Log

private const val TAG = "[USER]"
val gson: Gson by lazy { Gson() }

fun User.toJson(): String {
    Log.d(TAG, "Converting User to JSON")
    return gson.toJson(this)
}

fun String.toUser(): User? {
    Log.d(TAG, "Converting String to User: $this")
    return try {
        val user = gson.fromJson(this, User::class.java)
        Log.d(TAG, "Successfully converted String to User")
        user
    } catch (e: Exception) {
        Log.e(TAG, "Error converting string to User", e)
        null
    }
}

private fun Context.saveData(data: String): Boolean {
    Log.d(TAG, "Saving data to file: $data")
    return try {
        val fileName = "data.json"
        val outputStream: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Using MediaStore for Android 10 and above")
            // For Android 10 and above, use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri =
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create file in MediaStore")
            Log.d(TAG, "Created file in MediaStore, URI: $uri")
            contentResolver.openOutputStream(uri) ?: throw Exception("Failed to open output stream")
        } else {
            Log.d(TAG, "Using direct file access for Android 9 and below")
            // For Android 9 and below, use direct file access
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
                Log.d(TAG, "Created Downloads directory: $downloadsDir")
            }
            FileOutputStream(File(downloadsDir, fileName))
        }

        // Write the JSON data to the file
        outputStream.use { it.write(data.toByteArray()) }
        Log.d(TAG, "Data successfully saved to file")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error saving data", e)
        false
    }
}

fun AppCompatActivity.saveDataJson(data: String): Boolean {
    Log.d(TAG, "Requesting permission to save data")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 and above - directly save the data
        Log.d(TAG, "Android 13 and above, directly saving data")
        saveData(data)
    } else {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Permission not granted, requesting permission")
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                101
            )
            false
        } else {
            Log.d(TAG, "Permission already granted, saving data")
            // Permission is already granted
            saveData(data)
        }
    }
}

fun Context.readJsonFromUri(uri: Uri): String? {
    Log.d(TAG, "Reading JSON from URI: $uri")
    return try {
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }?.also {
            Log.d(TAG, "Successfully read JSON from URI")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error reading JSON from URI", e)
        null
    }
}
