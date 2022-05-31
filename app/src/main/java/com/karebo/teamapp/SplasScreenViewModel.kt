package com.the.firsttask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SplasScreenViewModel : ViewModel() {

    fun getIsReady(): Boolean {
        val result = viewModelScope.runCatching {
            runBlocking {
                //do some blocking call check for Firebase result or something
                delay(100)
            }
            true //return the result
        }
        return result.isSuccess
    }
}