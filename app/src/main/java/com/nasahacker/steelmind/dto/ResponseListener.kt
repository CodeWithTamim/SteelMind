package com.nasahacker.steelmind.dto

interface ResponseListener<T> {
    fun onSuccess(data: T, msg: String)
    fun onFailure(msg: String)
}