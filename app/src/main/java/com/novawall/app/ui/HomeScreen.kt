package com.novawall.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novawall.app.data.WallpaperItem
import com.novawall.app.ui.theme.NovaBackgroundDark
import com.novawall.app.ui.theme.NovaPrimary
import com.novawall.app.ui.theme.NovaSecondary

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val visibleWallpapers = if (state.showSavedOnly) {
        state.wallpapers.filter { it.id in state.savedIds }
    } else {
        state.wallpapers
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NovaBackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier = Modifier.height(12.dp))

            Text(
                text = "NovaWall",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Live wallpapers",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(Modifier = Modifier.height(20.dp))

            AuthSection(
                displayName = state.session?.displayName,
                email = state.session?.email,
                isLoading = state.isLoading,
                signedIn = state.session != null,
                onSignIn = viewModel::signIn,
                onSignOut = viewModel::signOut
            )

            Spacer(Modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !state.showSavedOnly,
                    onClick = { viewModel.setShowSavedOnly(false) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = state.showSavedOnly,
                    onClick = { viewModel.setShowSavedOnly(true) },
                    label = { Text("Saved") }
                )
            }

            Spacer(Modifier = Modifier.height(12.dp))

            if (state.showSavedOnly && state.session == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign in with Google to see saved wallpapers",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else if (visibleWallpapers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved wallpapers yet",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(visibleWallpapers, key = { it.id }) { item ->
                        WallpaperCard(
                            item = item,
                            saved = item.id in state.savedIds,
                            onToggleSave = { viewModel.toggleSaved(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthSection(
    displayName: String?,
    email: String?,
    isLoading: Boolean,
    signedIn: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (signedIn) {
                Text(
                    text = displayName.orEmpty().ifBlank { "Signed in" },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                if (!email.isNullOrBlank()) {
                    Text(
                        text = email,
                        color = Color.White.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onSignOut, enabled = !isLoading) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Sign out")
                }
            } else {
                Text(
                    text = "Sign in to save wallpapers",
                    color = Color.White
                )
                Text(
                    text = "Use your Google account to keep favorites synced on this device",
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSignIn,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    Text("Sign in with Google")
                }
            }
        }
    }
}

@Composable
private fun WallpaperCard(
    item: WallpaperItem,
    saved: Boolean,
    onToggleSave: () -> Unit
) {
    val accent = try {
        Color(android.graphics.Color.parseColor(item.accentHex))
    } catch (_: Exception) {
        NovaSecondary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(accent.copy(alpha = 0.85f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = item.category,
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onToggleSave) {
                    Icon(
                        imageVector = if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (saved) "Remove from saved" else "Save",
                        tint = if (saved) NovaSecondary else Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
