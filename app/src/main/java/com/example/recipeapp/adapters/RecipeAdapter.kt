package com.example.recipeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.models.Meal

class RecipeAdapter(
    private var recipes: List<Meal>,
    private val onRecipeClick: (Meal) -> Unit,
    private val onFavoriteClick: (Meal) -> Unit,
    private val isFavoriteChecker: (String) -> Boolean
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMeal: ImageView = itemView.findViewById(R.id.imgMeal)
        val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)
        val tvMealCategory: TextView = itemView.findViewById(R.id.tvMealCategory)
        val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.tvMealName.text = recipe.strMeal
        holder.tvMealCategory.text = recipe.strCategory ?: recipe.strArea ?: "Recipe"

        Glide.with(holder.itemView.context)
            .load(recipe.strMealThumb)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgMeal)

        val isFav = isFavoriteChecker(recipe.idMeal)
        holder.btnFavorite.setImageResource(
            if (isFav) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        )

        holder.itemView.setOnClickListener { onRecipeClick(recipe) }
        holder.btnFavorite.setOnClickListener { onFavoriteClick(recipe) }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newRecipes: List<Meal>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
