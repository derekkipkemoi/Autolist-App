package org.carlistingapp.autolist.ui.home.listCar.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.carlistingapp.autolist.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class ListCarViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListCarViewModel(userRepository) as T
    }
}