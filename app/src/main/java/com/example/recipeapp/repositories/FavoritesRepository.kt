package com.example.recipeapp.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.recipeapp.models.Meal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("recipe_favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val keyFavorites = "favorite_meals"

    fun getFavorites(): List<Meal> {
        val json = prefs.getString(keyFavorites, null) ?: return emptyList()
        val type = object : TypeToken<List<Meal>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isFavorite(mealId: String): Boolean {
        return getFavorites().any { it.idMeal == mealId }
    }

    fun addFavorite(meal: Meal) {
        val current = getFavorites().toMutableList()
        if (!current.any { it.idMeal == meal.idMeal }) {
            current.add(meal)
            saveFavorites(current)
        }
    }

    fun removeFavorite(mealId: String) {
        val current = getFavorites().toMutableList()
        val removed = current.removeAll { it.idMeal == mealId }
        if (removed) {
            saveFavorites(current)
        }
    }

    private fun saveFavorites(list: List<Meal>) {
        val json = gson.toJson(list)
        prefs.edit().putString(keyFavorites, json).apply()
    }
}
