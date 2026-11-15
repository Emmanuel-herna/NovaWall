package com.novawall.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.savedDataStore by preferencesDataStore(name = "novawall_saved")

class SavedWallpapersStore(private val context: Context) {

    private fun keyForUser(userId: String) =
        stringSetPreferencesKey("saved_$userId")

    fun savedIds(userId: String): Flow<Set<String>> {
        val key = keyForUser(userId)
        return context.savedDataStore.data.map { prefs ->
            prefs[key] ?: emptySet()
        }
    }

    suspend fun toggle(userId: String, wallpaperId: String) {
        val key = keyForUser(userId)
        context.savedDataStore.edit { prefs ->
            val current = prefs[key]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(wallpaperId)) {
                current.remove(wallpaperId)
            }
            prefs[key] = current
        }
    }
}
