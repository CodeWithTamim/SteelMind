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

class HistoryActivity : AppCompatActivity(), OnClickListener {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val adapter by lazy { HistoryAdapter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setupRecyclerView()
        setupToolbar()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.adapter = adapter
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.deleteAll) {
                showDeleteDialog()
            }
            true
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.deleteAll))
            .setMessage(getString(R.string.delete_confirmation))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                MmkvManager.clearAllHistory()
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                adapter.refresh()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }

    override fun onClick(data: History) {
        //TODO()
    }

    override fun onLongPress(data: History) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_item))
            .setMessage(getString(R.string.deleteItem_message))
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                MmkvManager.deleteHistory(history = data)
                adapter.refresh()
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}