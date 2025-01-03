package com.reviling.filamentandroid

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.di.Injection
import com.reviling.filamentandroid.ui.detailgamelan.DetailGamelanViewModel
import com.reviling.filamentandroid.ui.detailinstrument.DetailInstrumentViewModel
import com.reviling.filamentandroid.ui.detailsanggar.DetailSanggarViewModel
import com.reviling.filamentandroid.ui.home.HomeViewModel
import com.reviling.filamentandroid.ui.inputgamelan.InputGamelanViewModel
import com.reviling.filamentandroid.ui.inputinstrument.InputInstrumentViewModel
import com.reviling.filamentandroid.ui.inputsanggar.InputDataSanggarViewModel
import com.reviling.filamentandroid.ui.login.LoginViewModel
import com.reviling.filamentandroid.ui.regsiter.RegisterViewModel
import com.reviling.filamentandroid.ui.seeallgamelan.SeeAllGamelanViewModel
import com.reviling.filamentandroid.ui.seeallinstrument.SeeAllInstrumentViewModel
import com.reviling.filamentandroid.ui.seeallsanggar.SeeAllSanggarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException


class ViewModelFactory private constructor(
    private val repositoryData: RepositoryData
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(DetailGamelanViewModel::class.java)) {
            return DetailGamelanViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(DetailInstrumentViewModel::class.java)) {
            return DetailInstrumentViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(DetailSanggarViewModel::class.java)) {
            return DetailSanggarViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(SeeAllInstrumentViewModel::class.java)) {
            return SeeAllInstrumentViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(SeeAllGamelanViewModel::class.java)) {
            return SeeAllGamelanViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(SeeAllSanggarViewModel::class.java)) {
            return SeeAllSanggarViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(InputDataSanggarViewModel::class.java)) {
            return InputDataSanggarViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(InputInstrumentViewModel::class.java)) {
            return InputInstrumentViewModel(repositoryData) as T
        } else if (modelClass.isAssignableFrom(InputGamelanViewModel::class.java)) {
            return InputGamelanViewModel(repositoryData) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        suspend fun getInstance(context: Context): ViewModelFactory =
            instance ?: Mutex().withLock {
                ViewModelFactory(
                    Injection.run {
                        withContext(Dispatchers.IO) {
                            provideRepo(context)
                        }
                    }
                ).also {
                    instance = it
                    Log.d("view-model-factory", "created")
                }
            }

    }
}