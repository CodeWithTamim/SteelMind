package com.nasahacker.steelmind.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nasahacker.steelmind.BuildConfig
import com.nasahacker.steelmind.R
import com.nasahacker.steelmind.databinding.ActivityAboutBinding
import com.nasahacker.steelmind.util.AppUtils

class AboutActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAboutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.appVersion.text = getString(R.string.label_version, BuildConfig.VERSION_NAME)
        setupToolbar()
        setupButtonListeners()


    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupButtonListeners() {
        binding.githubBtn.setOnClickListener {
            AppUtils.openLink(this, getString(R.string.github_link))
        }
        binding.discordBtn.setOnClickListener {
            AppUtils.openLink(this, getString(R.string.discord_group_link))
        }
        binding.telegramBtn.setOnClickListener {
            AppUtils.openLink(this, getString(R.string.telegram_group_link))
        }
    }
}