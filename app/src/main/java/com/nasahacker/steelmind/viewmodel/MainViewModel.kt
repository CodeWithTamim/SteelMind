package com.nasahacker.steelmind.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasahacker.steelmind.dto.ResponseListener
import com.nasahacker.steelmind.dto.Quote
import com.nasahacker.steelmind.util.RequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val TAG = "[MainViewModel]"
    private val _quotes = MutableLiveData<List<Quote>>()
    private val _currentQuote = MutableLiveData<Quote>()
    private val _currentQuoteIndex = MutableLiveData(0)

    val quotes: LiveData<List<Quote>> get() = _quotes
    val currentQuote: LiveData<Quote> get() = _currentQuote
    val currentQuoteIndex: LiveData<Int> get() = _currentQuoteIndex

    fun incrementQuoteIndex() {
        val quotesList = _quotes.value
        Log.d(TAG, "incrementQuoteIndex: Current quote: ${quotesList?.get(0)?.h}")
        val currentIndex = _currentQuoteIndex.value ?: 0
        if (!quotesList.isNullOrEmpty()) {
            Log.d(TAG, "Current index: $currentIndex, Quotes list size: ${quotesList.size}")
            if (currentIndex >= quotesList.size - 10) {
                Log.d(TAG, "Fetching more quotes...")
                fetchQuotes()
            }
            if (currentIndex < quotesList.size - 1) {
                val nextIndex = currentIndex + 1
                _currentQuoteIndex.postValue(nextIndex)
                _currentQuote.postValue(quotesList[nextIndex])
                Log.d(TAG, "Set new current quote index: $nextIndex")
            }
        } else {
            Log.d(TAG, "Quotes list is empty, fetching quotes...")
            fetchQuotes()
            _currentQuoteIndex.postValue(0)
        }
    }

    fun fetchQuotes() {
        Log.d(TAG, "Fetching quotes from the server...")
        viewModelScope.launch(Dispatchers.IO) {
            RequestManager.getQuotes(object : ResponseListener<List<Quote>> {
                override fun onSuccess(data: List<Quote>, msg: String) {
                    _quotes.postValue(data)
                    if (data.isNotEmpty()) {
                        _currentQuote.postValue(data[0])
                        _currentQuoteIndex.postValue(0)
                        Log.d(TAG, "Fetched quotes successfully. First quote: ${data[0].h}")
                    }
                }

                override fun onFailure(msg: String) {
                    Log.e(TAG, "Failed to fetch quotes: $msg")
                }
            })
        }
    }
}
