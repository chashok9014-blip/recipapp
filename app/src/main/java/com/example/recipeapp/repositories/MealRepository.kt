package com.example.recipeapp.repositories

import com.example.recipeapp.api.RetrofitClient
import com.example.recipeapp.models.Meal

class MealRepository {
    private val api = RetrofitClient.api

    suspend fun searchRecipes(query: String): List<Meal> {
        return try {
            val response = api.searchRecipesByName(query)
            response.meals ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun filterByIngredient(ingredient: String): List<Meal> {
        return try {
            val response = api.filterByIngredient(ingredient)
            response.meals ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMealDetails(id: String): Meal? {
        return try {
            val response = api.getMealDetails(id)
            response.meals?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
