package com.example.recipeapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.adapters.IngredientLabelAdapter
import com.example.recipeapp.models.IngredientLabel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.IOException

class IngredientResultsActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoLabels: TextView
    private lateinit var rvLabels: RecyclerView
    private lateinit var imgCaptured: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_results)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        progressBar = findViewById(R.id.progressBar)
        tvNoLabels = findViewById(R.id.tvNoLabels)
        rvLabels = findViewById(R.id.rvLabels)
        imgCaptured = findViewById(R.id.imgCaptured)

        rvLabels.layoutManager = LinearLayoutManager(this)

        val uriString = intent.getStringExtra("image_uri")
        if (uriString != null) {
            val uri = Uri.parse(uriString)
            Glide.with(this).load(uri).into(imgCaptured)
            processImageWithMLKit(uri)
        } else {
            progressBar.visibility = View.GONE
            tvNoLabels.visibility = View.VISIBLE
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageWithMLKit(uri: Uri) {
        progressBar.visibility = View.VISIBLE
        tvNoLabels.visibility = View.GONE

        try {
            val image = InputImage.fromFilePath(this, uri)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    progressBar.visibility = View.GONE
                    val sortedLabels = labels
                        .sortedByDescending { it.confidence }
                        .take(3)
                        .map { IngredientLabel(it.text, it.confidence) }

                    if (sortedLabels.isNotEmpty()) {
                        val adapter = IngredientLabelAdapter(sortedLabels) { label ->
                            val intent = Intent(this, RecipeSearchActivity::class.java).apply {
                                putExtra("search_query", label.name)
                                putExtra("is_ingredient", true)
                            }
                            startActivity(intent)
                        }
                        rvLabels.adapter = adapter
                    } else {
                        tvNoLabels.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    tvNoLabels.visibility = View.VISIBLE
                    Toast.makeText(this, "Failed to recognize image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            progressBar.visibility = View.GONE
            tvNoLabels.visibility = View.VISIBLE
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
