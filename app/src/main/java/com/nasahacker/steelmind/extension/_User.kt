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

val gson: Gson by lazy { Gson() }

fun User.toJson(): String {
    return gson.toJson(this)
}

fun String.toUser(): User? {
    return try {
        gson.fromJson(this, User::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun Context.saveData(data: String): Boolean {
    return try {
        val fileName = "data.json"
        val outputStream: OutputStream

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri =
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("Failed to create file in MediaStore")

            outputStream = contentResolver.openOutputStream(uri)
                ?: throw Exception("Failed to open output stream")
        } else {
            // For Android 9 and below, use direct file access
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val file = File(downloadsDir, fileName)
            outputStream = FileOutputStream(file)
        }

        // Write the JSON data to the file
        outputStream.use { it.write(data.toByteArray()) }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun AppCompatActivity.saveDataJson(data: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 and above - directly save the data
        saveData(data)
    } else {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                101
            )
            false
        } else {
            // Permission is already granted
            saveData(data)
        }
    }
}

fun Context.readJsonFromUri(uri: Uri): String? {
    return try {
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
