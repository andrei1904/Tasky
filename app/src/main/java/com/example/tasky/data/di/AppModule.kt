package com.example.tasky.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tasky.data.local.AppDatabase
import com.google.firebase.FirebaseApp
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
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java,"tasky")
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        return Firebase.auth
    }
}