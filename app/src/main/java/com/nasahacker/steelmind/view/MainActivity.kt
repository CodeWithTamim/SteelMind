package com.nasahacker.steelmind.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.databinding.ActivityMainBinding
import com.nasahacker.steelmind.ext.readJsonFromUri
import com.nasahacker.steelmind.ext.saveDataJson
import com.nasahacker.steelmind.ext.toJson
import com.nasahacker.steelmind.ext.toUser
import com.nasahacker.steelmind.model.History
import com.nasahacker.steelmind.model.User
import com.nasahacker.steelmind.util.AppUtils
import com.nasahacker.steelmind.util.Constants
import com.nasahacker.steelmind.util.MmkvManager
import com.nasahacker.steelmind.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        loadDefaultState()

        binding.btnStartOrReset.setOnClickListener {
            val currentTimeInMS = System.currentTimeMillis()
            if (MmkvManager.getIsStarted()) {
                showResetDialog()
            } else {
                binding.btnStartOrReset.text = "Reset"
                MmkvManager.setStartTime(currentTimeInMS)
                startUpdatingUI()
                MmkvManager.addHistory(
                    History(
                        remarks = "Started successfully",
                        action = Constants.ACTION.ACTION_START
                    )
                )
            }
        }
    }

    private fun startUpdatingUI() {
        isUpdating = true
        lifecycleScope.launch(Dispatchers.Main) {
            while (isUpdating) {
                binding.tvCount.text = AppUtils.getTimeHMS(MmkvManager.getStartTime())
                binding.tvTime.text = AppUtils.getTimeDD(MmkvManager.getStartTime())
                Log.d("MainActivity", "Day progress: ${AppUtils.getDayProgressPercentage()}%")

                if (Build.VERSION.SDK_INT >= 24) {
                    binding.progressTime.setProgress(AppUtils.getDayProgressPercentage(), true)
                } else {
                    binding.progressTime.setProgress(AppUtils.getDayProgressPercentage())
                }
                delay(1000)
            }
        }
    }

    private fun stopUpdatingUI() {
        isUpdating = false
    }

    private fun loadDefaultState() {
        if (MmkvManager.getIsStarted()) {
            startUpdatingUI()
        }
        binding.btnStartOrReset.text = if (MmkvManager.getIsStarted()) "Reset" else "Start"

        mainViewModel.fetchQuotes()
        mainViewModel.currentQuote.observe(this) { quote ->
            binding.txtQuote.text = Html.fromHtml(quote.h)
        }

        binding.btnFetchQuote.setOnClickListener {
            mainViewModel.incrementQuoteIndex()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.top_about -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.top_history -> startActivity(Intent(this, HistoryActivity::class.java))
                R.id.top_export -> handleExportAction()
                R.id.top_import -> pickJsonFileLauncher.launch(arrayOf("application/json", "*/*"))
            }
            true
        }
    }

    private fun handleExportAction() {
        val isExported = saveDataJson(User().toJson())
        val message = if (isExported) {
            "Export completed successfully!"
        } else {
            "Export failed. Please try again."
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showResetDialog() {
        val inputLayout = TextInputLayout(this).apply {
            hint = "Enter remarks"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val inputEditText = TextInputEditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        inputLayout.addView(inputEditText)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
            addView(inputLayout)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Reset Confirmation")
            .setMessage("Are you sure you want to reset? This will erase the current progress.")
            .setView(container)
            .setPositiveButton("Yes") { _, _ ->
                val remarks = inputEditText.text?.toString() ?: "No remarks provided"
                resetProgress(remarks)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun resetProgress(remarks: String) {
        MmkvManager.setStartTime(0)
        MmkvManager.setIsStarted(false)
        binding.btnStartOrReset.text = "Start"
        stopUpdatingUI()
        binding.tvCount.text = "-"
        binding.tvTime.text = "-"

        if (Build.VERSION.SDK_INT >= 24) {
            binding.progressTime.setProgress(0, true)
        } else {
            binding.progressTime.setProgress(0)
        }

        MmkvManager.addHistory(
            History(
                remarks = remarks,
                action = Constants.ACTION.ACTION_ENDED
            )
        )
    }

    private val pickJsonFileLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                val jsonContent = readJsonFromUri(it)
                val user = jsonContent?.toUser()
                if (user != null) {
                    MmkvManager.setStartTime(user.startTime)
                    MmkvManager.setIsStarted(user.isStarted)
                    MmkvManager.addHistoryList(user.history)
                    Toast.makeText(this, "Import completed successfully. Restarting...", Toast.LENGTH_SHORT).show()
                    runBlocking { delay(1000) }
                    finish()
                } else {
                    Toast.makeText(this, "Invalid file format. Please check and try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
}
