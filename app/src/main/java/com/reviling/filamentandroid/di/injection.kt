package com.reviling.filamentandroid.di

import android.content.Context
import com.reviling.filamentandroid.data.RepositoryData
import com.reviling.filamentandroid.data.preferences.UserPreferences
import com.reviling.filamentandroid.data.preferences.dataStore
import com.reviling.filamentandroid.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    suspend fun provideRepo(context: Context): RepositoryData {
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first()}
        val apiService = ApiConfig.getApiService(user.access_token)

        return RepositoryData.getInstance(context, apiService, pref)
    }

}