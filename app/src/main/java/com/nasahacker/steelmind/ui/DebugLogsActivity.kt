package com.nasahacker.steelmind.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nasahacker.steelmind.BuildConfig
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.compose.ui.screen.DebugLogsScreen
import com.nasahacker.steelmind.databinding.ActivityDebugLogsBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class DebugLogsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDebugLogsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.deleteAll) {
                binding.logstv.text = ""
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
            }
            true
        }

         startLogcatReader()


        /*binding.composeView.setContent {
            DebugLogsScreen(onNavigationClick = {
                onBackPressedDispatcher.onBackPressed()
            })
        }*/


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

        private fun startLogcatReader() {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val process = Runtime.getRuntime().exec("logcat")
                    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        val log = line!!

                        withContext(Dispatchers.Main) {
                            binding.logstv.append("\n$log")
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

}
