package com.bodnar.carpathians.data.auth.local

import com.bodnar.carpathians.domain.auth.AuthTokens
import com.bodnar.carpathians.domain.auth.TokenStorage
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.serialization.Serializable

@Serializable
private data class StoredTokens(
    val accessToken: String = "",
    val refreshToken: String = "",
)

/**
 * Токени зберігаються через KSafe — зашифровано (AES-256-GCM).
 * Ключ шифрування живе в Android Keystore / сховищі ОС (Desktop) / WebCrypto (Web).
 */
class KSafeTokenStorage(private val ksafe: KSafe) : TokenStorage {

    override suspend fun getTokens(): AuthTokens? {
        ksafe.awaitReady()
        val stored = ksafe.get(KEY_TOKENS, StoredTokens())
        return if (stored.accessToken.isNotEmpty() && stored.refreshToken.isNotEmpty()) {
            AuthTokens(stored.accessToken, stored.refreshToken)
        } else {
            null
        }
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        ksafe.awaitReady()
        ksafe.put(KEY_TOKENS, StoredTokens(tokens.accessToken, tokens.refreshToken))
    }

    override suspend fun clear() {
        ksafe.awaitReady()
        ksafe.delete(KEY_TOKENS)
    }

    private companion object {
        const val KEY_TOKENS = "auth_tokens"
    }
}

/** У браузері KSafe розшифровує кеш асинхронно — перед читанням треба дочекатися. */
internal expect suspend fun KSafe.awaitReady()
