package com.example.recipeapp.api

import com.example.recipeapp.models.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("filter.php")
    suspend fun filterByIngredient(@Query("i") ingredient: String): MealResponse

    @GET("search.php")
    suspend fun searchRecipesByName(@Query("s") name: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") id: String): MealResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}
