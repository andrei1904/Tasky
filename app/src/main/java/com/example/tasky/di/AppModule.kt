package com.example.tasky.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.example.tasky.data.local.AppDatabase
import com.example.tasky.utils.preferences.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule() {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java,"tasky")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext applicationContext: Context): PreferenceHelper {
        return PreferenceHelper(applicationContext)
    }
//
//    @Provides
//    @Singleton
//    fun provideSharedPreferences(@ApplicationContext applicationContext: Context):
//            SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

//    @Provides
//    @Singleton
//    fun provideAppContext(app: Application): Context = app.applicationContext
}