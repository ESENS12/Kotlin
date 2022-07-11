package kr.esens.mvi_architecture_example.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.esens.mvi_architecture_example.api.ApiHelper
import kr.esens.mvi_architecture_example.repository.MainRepository
import kr.esens.mvi_architecture_example.viewmodel.MainViewModel

class ViewModelFactory (private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}