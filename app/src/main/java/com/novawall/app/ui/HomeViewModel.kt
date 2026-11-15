package com.novawall.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.novawall.app.auth.GoogleAuthHelper
import com.novawall.app.data.SavedWallpapersStore
import com.novawall.app.data.SessionStore
import com.novawall.app.data.UserSession
import com.novawall.app.data.WallpaperCatalog
import com.novawall.app.data.WallpaperItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val session: UserSession? = null,
    val wallpapers: List<WallpaperItem> = WallpaperCatalog.all,
    val savedIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val showSavedOnly: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionStore = SessionStore(application)
    private val savedStore = SavedWallpapersStore(application)
    private val authHelper = GoogleAuthHelper(application)

    private val loading = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val showSavedOnly = MutableStateFlow(false)

    private val savedIdsFlow = sessionStore.session.flatMapLatest { session ->
        if (session == null) flowOf(emptySet())
        else savedStore.savedIds(session.id)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        sessionStore.session,
        savedIdsFlow,
        loading,
        message,
        showSavedOnly
    ) { session, savedIds, isLoading, msg, savedOnly ->
        HomeUiState(
            session = session,
            wallpapers = WallpaperCatalog.all,
            savedIds = savedIds,
            isLoading = isLoading,
            message = msg,
            showSavedOnly = savedOnly
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun signIn() {
        viewModelScope.launch {
            loading.value = true
            message.value = null
            val result = authHelper.signIn()
            result.onSuccess { session ->
                sessionStore.save(session)
                message.value = "Signed in as ${session.displayName.ifBlank { session.email }}"
            }.onFailure { e ->
                message.value = e.message ?: "Sign-in failed"
            }
            loading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            loading.value = true
            authHelper.signOut()
            sessionStore.clear()
            showSavedOnly.value = false
            message.value = "Signed out"
            loading.value = false
        }
    }

    fun toggleSaved(wallpaperId: String) {
        val session = uiState.value.session ?: run {
            message.value = "Sign in to save wallpapers"
            return
        }
        viewModelScope.launch {
            savedStore.toggle(session.id, wallpaperId)
        }
    }

    fun setShowSavedOnly(value: Boolean) {
        if (value && uiState.value.session == null) {
            message.value = "Sign in to view saved wallpapers"
            return
        }
        showSavedOnly.value = value
    }

    fun clearMessage() {
        message.value = null
    }
}
