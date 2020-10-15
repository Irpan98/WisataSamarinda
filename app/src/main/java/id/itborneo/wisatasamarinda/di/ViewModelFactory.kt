package id.itborneo.wisatasamarinda.di

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.itborneo.wisatasamarinda.data.WiRepository
import id.itborneo.wisatasamarinda.ui.addUpdate.AddUpdateViewModel
import id.itborneo.wisatasamarinda.ui.main.MainViewModel
import id.itborneo.wisatasamarinda.ui.wisata.WisataViewModel

class ViewModelFactory(private val repository: WiRepository) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {

        @Volatile
        private var instance: ViewModelFactory? = null


        fun getInstance(application: Application): ViewModelFactory {
            Log.d("ViewModelFactory", "getInstance $instance")

            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(application))
            }
        }

    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                return MainViewModel() as T
            }
            modelClass.isAssignableFrom(WisataViewModel::class.java) -> {
                return WisataViewModel(repository) as T
            }

            modelClass.isAssignableFrom(AddUpdateViewModel::class.java) -> {
                return AddUpdateViewModel(repository) as T
            }
            else -> throw Throwable("Tidak diketahui View Model ${modelClass.name}")
        }

    }
}