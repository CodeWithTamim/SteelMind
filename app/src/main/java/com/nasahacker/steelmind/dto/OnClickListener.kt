package com.nasahacker.steelmind.dto

interface OnClickListener {

    fun onClick(data:History): Unit
    fun onLongPress(data:History): Unit

}