package com.nasahacker.steelmind.ui

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mikepenz.aboutlibraries.LibsBuilder
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.databinding.ActivityMainBinding
import com.nasahacker.steelmind.extension.readJsonFromUri
import com.nasahacker.steelmind.extension.saveDataJson
import com.nasahacker.steelmind.extension.toJson
import com.nasahacker.steelmind.extension.toUser
import com.nasahacker.steelmind.dto.History
import com.nasahacker.steelmind.dto.User
import com.nasahacker.steelmind.util.AppUtils
import com.nasahacker.steelmind.util.Constants
import com.nasahacker.steelmind.util.MmkvManager
import com.nasahacker.steelmind.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupInsets()
        initializeUI()
        observeViewModel()
    }

    private fun setupInsets() {
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
    }

    private fun initializeUI() {
        loadDefaultState()
        binding.btnStartOrReset.setOnClickListener {
            if (MmkvManager.getIsStarted()) {
                showResetDialog()
            } else {
                startTracking()
            }
        }

        binding.btnFetchQuote.setOnClickListener {
            mainViewModel.incrementQuoteIndex()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem.itemId)
        }
    }

    private fun startTracking() {
        val currentTimeInMS = System.currentTimeMillis()
        binding.btnStartOrReset.text = getString(R.string.reset)
        MmkvManager.setStartTime(currentTimeInMS)
        startUpdatingUI()
        MmkvManager.addHistory(
            History(
                remarks = getString(R.string.started_successfully),
                action = Constants.ACTION.ACTION_START
            )
        )
        binding.lottieAnim.apply {
            visibility = VISIBLE
            playAnimation()
        }
    }

    private fun startUpdatingUI() {
        isUpdating = true
        lifecycleScope.launch(Dispatchers.Main) {
            while (isUpdating) {
                updateUI()
                delay(1000)
            }
        }
    }

    private fun updateUI() {
        val startTime = MmkvManager.getStartTime()
        binding.tvCount.text = AppUtils.getTimeHMS(startTime)
        binding.tvTime.text = AppUtils.getTimeDD(startTime)
        val progress = AppUtils.getDayProgressPercentage()
        Log.d("MainActivity", "Day progress: $progress%")
        if (Build.VERSION.SDK_INT >= 24) {
            binding.progressTime.setProgress(progress, true)
        } else {
            binding.progressTime.setProgress(progress)
        }


    }

    private fun stopUpdatingUI() {
        isUpdating = false
    }

    private fun loadDefaultState() {
        if (MmkvManager.getIsStarted()) {
            binding.lottieAnim.visibility = VISIBLE
            startUpdatingUI()
        }
        binding.btnStartOrReset.text = if (MmkvManager.getIsStarted()) {
            getString(R.string.reset)
        } else {
            getString(R.string.start)
        }
    }

    private fun observeViewModel() {
        mainViewModel.fetchQuotes()
        mainViewModel.currentQuote.observe(this) { quote ->
            binding.txtQuote.text = Html.fromHtml(quote.h)
        }
    }

    private fun handleMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.top_history -> startActivity(Intent(this, HistoryActivity::class.java))
            R.id.top_export -> handleExportAction()
            R.id.top_import -> pickJsonFileLauncher.launch(arrayOf("application/json"))
            R.id.top_license -> LibsBuilder()
                .withActivityTitle(getString(R.string.open_source_licenses))
                .start(this)
        }
        return true
    }

    private fun handleExportAction() {
        val isExported = saveDataJson(User().toJson())
        val messageRes = if (isExported) {
            R.string.export_success
        } else {
            R.string.export_failure
        }
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
    }

    private fun showResetDialog() {
        val inputLayout = createInputLayout()
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reset_confirmation)
            .setMessage(R.string.reset_message)
            .setView(inputLayout)
            .setPositiveButton(R.string.yes) { _, _ ->
                val remarks = inputLayout.editText?.text.toString()
                resetProgress(remarks)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createInputLayout(): TextInputLayout {
        return TextInputLayout(this).apply {
            hint = getString(R.string.enter_remarks)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val editText = TextInputEditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(18, 8, 18, 8)
                }
            }
            addView(editText)
        }
    }


    private fun resetProgress(remarks: String) {
        MmkvManager.setStartTime(0)
        MmkvManager.setIsStarted(false)
        binding.btnStartOrReset.text = getString(R.string.start)
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
                remarks = remarks.ifEmpty { getString(R.string.no_remarks) },
                action = Constants.ACTION.ACTION_ENDED
            )
        )
        binding.lottieAnim.visibility = GONE
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
    }

    private val pickJsonFileLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { handleFileImport(it) }
        }

    private fun handleFileImport(uri: Uri) {
        val jsonContent = readJsonFromUri(uri)
        val user = jsonContent?.toUser()
        if (user != null) {
            MmkvManager.setStartTime(user.startTime)
            MmkvManager.setIsStarted(user.isStarted)
            MmkvManager.addHistoryList(user.history)
            showToastAndFinish(R.string.import_success)
        } else {
            Toast.makeText(this, R.string.invalid_file_format, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showToastAndFinish(messageRes: Int) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(1000)
            finish()
        }
    }
}
