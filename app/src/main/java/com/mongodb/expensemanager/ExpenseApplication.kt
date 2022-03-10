package com.mongodb.expensemanager

import android.app.Application
import com.mongodb.expensemanager.di.koinModules
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.log.LogLevel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ExpenseApplication : Application() {

    lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()

        setupKoin()

        val config = RealmConfiguration
            .Builder(schema = setOf(ExpenseInfo::class))
            .name("expenseDB.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .log(LogLevel.ALL)
            .build()

        realm = Realm.open(configuration = config)
    }

    private fun setupKoin() {
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@ExpenseApplication)
            modules(modules = koinModules())
        }
    }

}