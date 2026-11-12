package com.example.recipeapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.adapters.IngredientListAdapter
import com.example.recipeapp.models.Meal
import com.example.recipeapp.repositories.FavoritesRepository
import com.example.recipeapp.repositories.MealRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var mealRepository: MealRepository
    private lateinit var favoritesRepository: FavoritesRepository
    private var currentMeal: Meal? = null

    private lateinit var imgRecipeDetail: ImageView
    private lateinit var tvRecipeName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvArea: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var rvIngredients: RecyclerView
    private lateinit var btnFavoriteDetail: MaterialButton
    private lateinit var btnYoutube: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        mealRepository = MealRepository()
        favoritesRepository = FavoritesRepository(this)

        imgRecipeDetail = findViewById(R.id.imgRecipeDetail)
        tvRecipeName = findViewById(R.id.tvRecipeName)
        tvCategory = findViewById(R.id.tvCategory)
        tvArea = findViewById(R.id.tvArea)
        tvInstructions = findViewById(R.id.tvInstructions)
        rvIngredients = findViewById(R.id.rvIngredients)
        btnFavoriteDetail = findViewById(R.id.btnFavoriteDetail)
        btnYoutube = findViewById(R.id.btnYoutube)

        rvIngredients.layoutManager = LinearLayoutManager(this)

        val mealId = intent.getStringExtra("meal_id")
        if (mealId != null) {
            loadMealDetails(mealId)
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnFavoriteDetail.setOnClickListener {
            currentMeal?.let { meal ->
                if (favoritesRepository.isFavorite(meal.idMeal)) {
                    favoritesRepository.removeFavorite(meal.idMeal)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favoritesRepository.addFavorite(meal)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                updateFavoriteButtonState(meal.idMeal)
            }
        }

        btnYoutube.setOnClickListener {
            currentMeal?.strYoutube?.let { url ->
                if (url.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Unable to open YouTube tutorial", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "YouTube tutorial not available", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "YouTube tutorial not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMealDetails(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val meal = mealRepository.getMealDetails(id)
            withContext(Dispatchers.Main) {
                if (meal != null) {
                    currentMeal = meal
                    bindMealData(meal)
                } else {
                    Toast.makeText(this@RecipeDetailsActivity, "Failed to load recipe details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindMealData(meal: Meal) {
        tvRecipeName.text = meal.strMeal
        tvCategory.text = meal.strCategory ?: "General"
        tvArea.text = meal.strArea ?: "International"
        tvInstructions.text = meal.strInstructions ?: "No instructions provided."

        Glide.with(this)
            .load(meal.strMealThumb)
            .placeholder(R.drawable.ic_launcher_background)
            .into(imgRecipeDetail)

        val ingredientsList = meal.getIngredientsList()
        rvIngredients.adapter = IngredientListAdapter(ingredientsList)

        updateFavoriteButtonState(meal.idMeal)
    }

    private fun updateFavoriteButtonState(mealId: String) {
        val isFav = favoritesRepository.isFavorite(mealId)
        btnFavoriteDetail.setIconResource(
            if (isFav) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        )
    }
}
