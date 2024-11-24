package com.nasahacker.steelmind.listener

interface ResponseListener<T> {
    fun onSuccess(data: T, msg: String)
    fun onFailure(msg: String)
}