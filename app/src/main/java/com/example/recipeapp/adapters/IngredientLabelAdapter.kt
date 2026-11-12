package com.example.recipeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.models.IngredientLabel
import com.google.android.material.button.MaterialButton

class IngredientLabelAdapter(
    private val labels: List<IngredientLabel>,
    private val onLabelClick: (IngredientLabel) -> Unit
) : RecyclerView.Adapter<IngredientLabelAdapter.LabelViewHolder>() {

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLabelName: TextView = itemView.findViewById(R.id.tvLabelName)
        val tvConfidence: TextView = itemView.findViewById(R.id.tvConfidence)
        val btnFindRecipes: MaterialButton = itemView.findViewById(R.id.btnFindRecipes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient_label, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label = labels[position]
        holder.tvLabelName.text = label.name
        holder.tvConfidence.text = "Confidence: ${String.format("%.1f", label.confidence * 100)}%"

        holder.btnFindRecipes.setOnClickListener { onLabelClick(label) }
        holder.itemView.setOnClickListener { onLabelClick(label) }
    }

    override fun getItemCount(): Int = labels.size
}
