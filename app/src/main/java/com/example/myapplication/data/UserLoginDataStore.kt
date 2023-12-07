package com.example.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_LOGIN_DATA = "user_login_data"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_LOGIN_DATA
)

class UserLoginDataStore(context: Context) {
    private val USER_EMAIL = stringPreferencesKey("user_email")
    private val USER_PASSWORD = stringPreferencesKey("user_password")

    val userEmail: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            // On the first run of the app, we will use LinearLayoutManager by default
            preferences[USER_EMAIL] ?: ""
        }

    val userPassword: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USER_PASSWORD] ?: ""
        }


    suspend fun saveUserLoginDataToDataStore(userEmail: String, userPassword: String, context: Context) {
        context.dataStore.edit { userCredentials ->
            userCredentials[USER_EMAIL] = userEmail
            userCredentials[USER_PASSWORD] = userPassword
        }
    }
}
