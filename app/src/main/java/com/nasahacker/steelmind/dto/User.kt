package com.nasahacker.steelmind.dto

import com.nasahacker.steelmind.util.MmkvManager

data class User(
    val history: List<History> = MmkvManager.getHistory(),
    val startTime: Long = MmkvManager.getStartTime(),
    val isStarted : Boolean  = MmkvManager.getIsStarted()
)
