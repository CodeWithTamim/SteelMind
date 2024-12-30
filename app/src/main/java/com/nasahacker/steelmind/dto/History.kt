package com.nasahacker.steelmind.dto

import com.nasahacker.steelmind.util.AppUtils


data class History(
    val time: String = AppUtils.getCurrentDate(),
    val remarks: String = "No remarks",
    val action: String
)