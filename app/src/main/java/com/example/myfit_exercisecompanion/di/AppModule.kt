package com.example.myfit_exercisecompanion.di

import android.content.Context
import androidx.room.Room
import com.example.myfit_exercisecompanion.db.RunSessionDatabase
import com.example.myfit_exercisecompanion.network.FoodApiService
import com.example.myfit_exercisecompanion.other.Constants.FOOD_DATABASE_NAME
import com.example.myfit_exercisecompanion.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) //ApplicationCompoenent deprecated
object AppModule {

    @Singleton
    @Provides
    fun provideRunSessionDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunSessionDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()


    @Singleton
    @Provides
    fun provideRunningSessionDao(db: RunSessionDatabase) = db.getRunningSessionDao()

    @Singleton
    @Provides
    fun provideAnalyticsService(): FoodApiService {
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(FoodApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideFoodDao(db: RunSessionDatabase) = db.getFoodDao()

}