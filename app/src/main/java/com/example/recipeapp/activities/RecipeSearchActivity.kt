package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.repositories.FavoritesRepository
import com.example.recipeapp.repositories.MealRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeSearchActivity : AppCompatActivity() {
    private lateinit var mealRepository: MealRepository
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var tvSearchTitle: TextView
    private lateinit var rvRecipes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_search)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        mealRepository = MealRepository()
        favoritesRepository = FavoritesRepository(this)

        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvSearchTitle = findViewById(R.id.tvSearchTitle)
        rvRecipes = findViewById(R.id.rvRecipes)

        rvRecipes.layoutManager = GridLayoutManager(this, 2)

        recipeAdapter = RecipeAdapter(
            emptyList(),
            onRecipeClick = { meal ->
                val intent = Intent(this, RecipeDetailsActivity::class.java).apply {
                    putExtra("meal_id", meal.idMeal)
                }
                startActivity(intent)
            },
            onFavoriteClick = { meal ->
                if (favoritesRepository.isFavorite(meal.idMeal)) {
                    favoritesRepository.removeFavorite(meal.idMeal)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favoritesRepository.addFavorite(meal)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                recipeAdapter.notifyDataSetChanged()
            },
            isFavoriteChecker = { id -> favoritesRepository.isFavorite(id) }
        )
        rvRecipes.adapter = recipeAdapter

        val query = intent.getStringExtra("search_query") ?: ""
        val isIngredient = intent.getBooleanExtra("is_ingredient", false)

        tvSearchTitle.text = if (isIngredient) "Recipes with '$query'" else "Search: '$query'"

        searchRecipes(query, isIngredient)
    }

    override fun onResume() {
        super.onResume()
        if (::recipeAdapter.isInitialized) {
            recipeAdapter.notifyDataSetChanged()
        }
    }

    private fun searchRecipes(query: String, isIngredient: Boolean) {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val recipes = if (isIngredient) {
                mealRepository.filterByIngredient(query)
            } else {
                mealRepository.searchRecipes(query)
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                if (recipes.isNotEmpty()) {
                    recipeAdapter.updateList(recipes)
                } else {
                    tvEmptyState.visibility = View.VISIBLE
                }
            }
        }
    }
}
