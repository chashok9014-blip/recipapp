package com.example.recipeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R

class IngredientListAdapter(
    private val ingredients: List<Pair<String, String>>
) : RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvIngredientName)
        val tvMeasure: TextView = itemView.findViewById(R.id.tvIngredientMeasure)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = ingredients[position]
        holder.tvName.text = item.first
        holder.tvMeasure.text = item.second
    }

    override fun getItemCount(): Int = ingredients.size
}
