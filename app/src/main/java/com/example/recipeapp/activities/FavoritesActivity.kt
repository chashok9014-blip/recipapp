package com.example.recipeapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.repositories.FavoritesRepository

class FavoritesActivity : AppCompatActivity() {
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var tvEmptyFavorites: TextView
    private lateinit var rvFavorites: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        favoritesRepository = FavoritesRepository(this)
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites)
        rvFavorites = findViewById(R.id.rvFavorites)

        rvFavorites.layoutManager = GridLayoutManager(this, 2)

        recipeAdapter = RecipeAdapter(
            emptyList(),
            onRecipeClick = { meal ->
                val intent = Intent(this, RecipeDetailsActivity::class.java).apply {
                    putExtra("meal_id", meal.idMeal)
                }
                startActivity(intent)
            },
            onFavoriteClick = { meal ->
                favoritesRepository.removeFavorite(meal.idMeal)
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                loadFavorites()
            },
            isFavoriteChecker = { id -> favoritesRepository.isFavorite(id) }
        )
        rvFavorites.adapter = recipeAdapter

        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        val favorites = favoritesRepository.getFavorites()
        if (favorites.isNotEmpty()) {
            tvEmptyFavorites.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
            recipeAdapter.updateList(favorites)
        } else {
            tvEmptyFavorites.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
            recipeAdapter.updateList(emptyList())
        }
    }
}
