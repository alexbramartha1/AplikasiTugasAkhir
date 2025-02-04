package com.reviling.filamentandroid.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.nama
            preferences[TOKEN_KEY] = user.access_token
            preferences[EMAIL_KEY] = user.email
            preferences[USER_ID] = user.user_id
            preferences[FOTO_PROFILE] = user.foto_profile
            preferences[ROLE] = user.role
            preferences[STATUS] = user.status
            preferences[IS_LOGIN_KEY] = true
            preferences[DOCUMENT] = user.document
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[USER_ID] ?: "",
                preferences[FOTO_PROFILE] ?: "",
                preferences[ROLE] ?: "",
                preferences[STATUS] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[DOCUMENT] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val FOTO_PROFILE = stringPreferencesKey("foto_profile")
        private val ROLE = stringPreferencesKey("role")
        private val STATUS = stringPreferencesKey("status")
        private val USER_ID = stringPreferencesKey("user_id")
        private val DOCUMENT = stringPreferencesKey("support_document")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}