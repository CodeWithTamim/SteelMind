package com.nasahacker.steelmind.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasahacker.steelmind.dto.History
import com.tencent.mmkv.MMKV

object MmkvManager {
    private val mmkv by lazy { MMKV.defaultMMKV() }
    fun setIsStarted(isStarted: Boolean) {
        mmkv.encode(Constants.MMKV.IS_STARTED, isStarted)
    }

    fun getIsStarted(): Boolean {
        return mmkv.decodeBool(Constants.MMKV.IS_STARTED, false)
    }


    fun setStartTime(startTime: Long) {
        setIsStarted(true)
        mmkv.encode(Constants.MMKV.START_TIME, startTime)
    }

    fun getStartTime(): Long {
        return mmkv.decodeLong(Constants.MMKV.START_TIME, 0)
    }

    fun addHistory(history: History) {
        val list: MutableList<History> = getHistory().toMutableList()
        list.add(history)
        val jsonData = Gson().toJson(list)
        mmkv.encode(Constants.MMKV.HISTORY, jsonData)
    }

    fun clearAllHistory() {
        val emptyList = Gson().toJson(emptyList<History>())
        mmkv.encode(Constants.MMKV.HISTORY, emptyList)
    }

    fun deleteHistory(history: History) {
        val jsonString = mmkv.decodeString(Constants.MMKV.HISTORY)
        val type = object : TypeToken<List<History>>() {}.type
        val historyList = if (jsonString.isNullOrEmpty()) mutableListOf<History>()
        else Gson().fromJson<List<History>>(jsonString, type).toMutableList()
        historyList.remove(history)
        addHistoryList(historyList)
    }

    fun addHistoryList(history: List<History>) {
        val jsonData = Gson().toJson(history)
        mmkv.encode(Constants.MMKV.HISTORY, jsonData)
    }


    fun getHistory(): List<History> {
        val jsonString = mmkv.decodeString(Constants.MMKV.HISTORY)
        if (jsonString.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<History>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

}