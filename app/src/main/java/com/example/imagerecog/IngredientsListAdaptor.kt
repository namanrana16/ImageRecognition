package com.example.imagerecog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientsListAdaptor(private val items: ArrayList<String>, private val listener:IngredientItemClicked): RecyclerView.Adapter<IngredientsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        val viewHolder = IngredientsViewHolder(view)
        view.setOnClickListener{
            listener.onItemClicked(items[viewHolder.adapterPosition])

        }
        return IngredientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val currentItem=items[position]
        holder.titleView.text=currentItem
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class IngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById<TextView>(R.id.title)

}

interface IngredientItemClicked{
    fun onItemClicked(item: String)
}