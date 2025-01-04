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

    private val _quotes = MutableLiveData<List<Quote>>()
    private val _currentQuote = MutableLiveData<Quote>()
    private val _currentQuoteIndex = MutableLiveData(0)

    val quotes: LiveData<List<Quote>> get() = _quotes
    val currentQuote: LiveData<Quote> get() = _currentQuote
    val currentQuoteIndex: LiveData<Int> get() = _currentQuoteIndex

    fun incrementQuoteIndex() {
        val quotesList = _quotes.value
        Log.d("HACKER", "incrementQuoteIndex: ${quotesList?.get(0)?.h}")
        val currentIndex = _currentQuoteIndex.value ?: 0
        if (!quotesList.isNullOrEmpty()) {
            if (currentIndex >= quotesList.size - 10) {
                fetchQuotes()
            }
            if (currentIndex < quotesList.size - 1) {
                _currentQuoteIndex.postValue(currentIndex + 1)
                _currentQuote.postValue(quotesList[currentIndex + 1])
            }
        } else {
            fetchQuotes()
            _currentQuoteIndex.postValue(0)
        }
    }

    fun fetchQuotes() {
        viewModelScope.launch(Dispatchers.IO) {
            RequestManager.getQuotes(object : ResponseListener<List<Quote>> {
                override fun onSuccess(data: List<Quote>, msg: String) {
                    _quotes.postValue(data)
                    if (data.isNotEmpty()) {
                        _currentQuote.postValue(data[0])
                        _currentQuoteIndex.postValue(0)
                    }
                }

                override fun onFailure(msg: String) {
                }
            })
        }
    }
}
