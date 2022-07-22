package com.example.recipesbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.recipesbook.fragment.FRFoodNameDirections

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.food_adding_menu, menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.miFoodAdding) {

            val action = FRFoodNameDirections.actionFRFoodNameToFRRecipes("fromtomenu", 0)
            Navigation.findNavController(this, R.id.fcvMain).navigate(action)

        }

        return super.onOptionsItemSelected(item)
    }
}