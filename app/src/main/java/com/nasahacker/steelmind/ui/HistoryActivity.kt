package com.nasahacker.steelmind.ui

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.databinding.ActivityHistoryBinding
import com.nasahacker.steelmind.dto.History
import com.nasahacker.steelmind.dto.HistoryAdapter
import com.nasahacker.steelmind.dto.OnClickListener
import com.nasahacker.steelmind.util.MmkvManager
import android.util.Log


class HistoryActivity : AppCompatActivity(), OnClickListener {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val adapter by lazy { HistoryAdapter(this, this) }
    private val TAG = "[HISTORY]"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HistoryActivity onCreate() called")
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setupRecyclerView()
        setupToolbar()

        //use when using compose

        /*
        binding.composeView.setContent {
            MaterialTheme {
                HistoryScreen {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
         */
    }

    private fun setupWindowInsets() {
        Log.d(TAG, "Setting up window insets")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            Log.d(TAG, "Window insets applied: $systemBars")
            insets
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        binding.rvHistory.adapter = adapter
    }

    private fun setupToolbar() {
        Log.d(TAG, "Setting up toolbar")
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            Log.d(TAG, "Navigation icon clicked, going back")
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            Log.d(TAG, "Menu item clicked: ${item.itemId}")
            if (item.itemId == R.id.deleteAll) {
                showDeleteDialog()
            }
            true
        }
    }

    private fun showDeleteDialog() {
        Log.d(TAG, "Showing delete confirmation dialog")
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.deleteAll))
            .setMessage(getString(R.string.delete_confirmation))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                Log.d(TAG, "Delete all history confirmed")
                MmkvManager.clearAllHistory()
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                adapter.refresh()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                Log.d(TAG, "Delete all history cancelled")
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "Creating options menu")
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onClick(data: History) {
        Log.d(TAG, "Item clicked: $data")
        //TODO()
    }

    override fun onLongPress(data: History) {
        Log.d(TAG, "Item long pressed: $data")
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_item))
            .setMessage(getString(R.string.deleteItem_message))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                Log.d(TAG, "Delete item confirmed: $data")
                MmkvManager.deleteHistory(history = data)
                adapter.refresh()
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                Log.d(TAG, "Delete item cancelled: $data")
                dialog.dismiss()
            }
            .show()
    }
}
