package com.example.recipeapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.adapters.RecipeAdapter
import com.example.recipeapp.repositories.FavoritesRepository
import com.example.recipeapp.repositories.MealRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mealRepository: MealRepository
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var etSearchRecipe: TextInputEditText

    private var photoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            val intent = Intent(this, IngredientResultsActivity::class.java).apply {
                data = photoUri
                putExtra("image_uri", photoUri.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
    }

    private val pickGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(this, IngredientResultsActivity::class.java).apply {
                data = uri
                putExtra("image_uri", uri.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mealRepository = MealRepository()
        favoritesRepository = FavoritesRepository(this)

        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserEmail.text = try { FirebaseAuth.getInstance().currentUser?.email } catch (_: Exception) { null } ?: "Welcome"

        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        etSearchRecipe = findViewById(R.id.etSearchRecipe)

        val rvPopularRecipes = findViewById<RecyclerView>(R.id.rvPopularRecipes)
        rvPopularRecipes.layoutManager = GridLayoutManager(this, 2)

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
        rvPopularRecipes.adapter = recipeAdapter

        findViewById<MaterialButton>(R.id.btnSearch).setOnClickListener {
            val query = etSearchRecipe.text.toString().trim()
            val intent = Intent(this, RecipeSearchActivity::class.java).apply {
                putExtra("search_query", query)
            }
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener {
            dispatchTakePictureIntent()
        }

        findViewById<MaterialButton>(R.id.btnGallery).setOnClickListener {
            pickGalleryLauncher.launch("image/*")
        }

        findViewById<MaterialButton>(R.id.btnFavoritesShortcut).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        loadPopularRecipes()
    }

    override fun onResume() {
        super.onResume()
        if (::recipeAdapter.isInitialized) {
            recipeAdapter.notifyDataSetChanged()
        }
    }

    private fun loadPopularRecipes() {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            val recipes = mealRepository.searchRecipes("chicken")
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

    private fun dispatchTakePictureIntent() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri!!)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
