package com.bodnar.carpathians.data.auth.local

import eu.anifantakis.lib.ksafe.KSafe

/** На цій платформі кеш KSafe завантажується синхронно — чекати не потрібно. */
internal actual suspend fun KSafe.awaitReady() = Unit
