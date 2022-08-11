package com.example.myfit_exercisecompanion.network
import com.example.myfit_exercisecompanion.models.FoodResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "https://trackapi.nutritionix.com/v2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FoodApiService {
    @Headers("x-app-id: 04f456ad", "x-app-key: edd950576ce6d35572d7d25f80f92519")
    @GET("search/instant")
    suspend fun getListOf(
        @Query("query") foodName: String,
        @Query("detailed") isDetailed: Boolean): FoodResponse
}

/**
 * Instance of Retrofit API service
 */
object FoodApi {
    val retrofitService : FoodApiService by lazy {
        retrofit.create(FoodApiService::class.java)
    }
}