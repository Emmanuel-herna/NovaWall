package com.novawall.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "novawall_session")

class SessionStore(private val context: Context) {

    private val keyId = stringPreferencesKey("user_id")
    private val keyEmail = stringPreferencesKey("user_email")
    private val keyName = stringPreferencesKey("user_name")

    val session: Flow<UserSession?> = context.sessionDataStore.data.map { prefs ->
        val id = prefs[keyId] ?: return@map null
        UserSession(
            id = id,
            email = prefs[keyEmail].orEmpty(),
            displayName = prefs[keyName].orEmpty()
        )
    }

    suspend fun save(session: UserSession) {
        context.sessionDataStore.edit { prefs ->
            prefs[keyId] = session.id
            prefs[keyEmail] = session.email
            prefs[keyName] = session.displayName
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}
