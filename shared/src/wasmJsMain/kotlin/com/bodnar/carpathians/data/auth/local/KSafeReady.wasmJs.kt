package com.bodnar.carpathians.data.auth.local

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.awaitCacheReady

/** WebCrypto працює лише асинхронно: чекаємо, поки KSafe розшифрує кеш. */
internal actual suspend fun KSafe.awaitReady() = awaitCacheReady()
