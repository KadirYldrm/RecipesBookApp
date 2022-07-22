package com.example.recipesbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesbook.R
import com.example.recipesbook.fragment.FRFoodNameDirections
import kotlinx.android.synthetic.main.row_food_name.view.*

class FRFoodNameAdapter(
    private val foodList: ArrayList<String>,
    private val idList: ArrayList<Int>

) :
    RecyclerView.Adapter<FRFoodNameAdapter.FoodNameHolder>() {

    class FoodNameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodNameHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_food_name, parent, false)
        return FoodNameHolder(view)

    }

    override fun onBindViewHolder(holder: FoodNameHolder, position: Int) {

        holder.itemView.tvFoodName.text = foodList[position]

        holder.itemView.setOnClickListener {

            val action =
                FRFoodNameDirections.actionFRFoodNameToFRRecipes("fromtorecycler", idList[position])
            Navigation.findNavController(it).navigate(action)

        }

    }

    override fun getItemCount(): Int {

        return foodList.size

    }

}