package com.novawall.app.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.novawall.app.R
import com.novawall.app.data.UserSession
import java.util.UUID

class GoogleAuthHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): Result<UserSession> {
        val webClientId = context.getString(R.string.default_web_client_id)
        if (webClientId.startsWith("REPLACE_WITH_")) {
            return Result.failure(
                IllegalStateException(
                    "Google Sign-In is not configured yet. Add your Web client ID in strings.xml (default_web_client_id)."
                )
            )
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleId = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(
                    UserSession(
                        id = googleId.id.ifBlank { UUID.randomUUID().toString() },
                        email = googleId.id,
                        displayName = googleId.displayName.orEmpty().ifBlank { "NovaWall user" }
                    )
                )
            } else {
                Result.failure(IllegalStateException("Unexpected credential type"))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Ignore clear failures; local session is still cleared by caller
        }
    }
}
