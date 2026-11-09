package com.bodnar.carpathians

import com.bodnar.carpathians.data.auth.local.KSafeTokenStorage
import com.bodnar.carpathians.domain.auth.AuthTokens
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Перевіряє збереження токенів у справжньому KSafe на Desktop (окремий тестовий файл сховища). */
class KSafeTokenStorageTest {

    @Test
    fun tokensAreSavedReadAndCleared() = runBlocking {
        val ksafe = KSafe(fileName = "carpathianstest")
        val storage = KSafeTokenStorage(ksafe)
        try {
            storage.clear()
            assertNull(storage.getTokens())

            val tokens = AuthTokens("access-token", "refresh-token")
            storage.saveTokens(tokens)
            assertEquals(tokens, storage.getTokens())

            storage.clear()
            assertNull(storage.getTokens())
        } finally {
            ksafe.clearAll()
        }
    }
}
