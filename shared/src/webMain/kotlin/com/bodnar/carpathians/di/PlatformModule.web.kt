package com.bodnar.carpathians.di

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // appNamespace відокремлює дані цього застосунку від інших на Desktop / Web
    single { KSafe(config = KSafeConfig(appNamespace = "com.bodnar.carpathians")) }
}
