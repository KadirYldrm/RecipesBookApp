package com.example.recipesbook.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesbook.R
import com.example.recipesbook.adapter.FRFoodNameAdapter
import kotlinx.android.synthetic.main.fr_food_name.*
import kotlinx.android.synthetic.main.row_food_name.*

class FRFoodName : Fragment() {

    private var foodNameList = ArrayList<String>()
    private var foodIdList = ArrayList<Int>()
    private lateinit var listAdapter: FRFoodNameAdapter
    private var imageList = ArrayList<Bitmap>()
    private var foodRecipesList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        return inflater.inflate(R.layout.fr_food_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        listAdapter = FRFoodNameAdapter(foodNameList, foodIdList, imageList, foodRecipesList)
        rvFoodName.layoutManager = LinearLayoutManager(context)
        rvFoodName.adapter = listAdapter

        sqlDataAccess()

    }

    private fun sqlDataAccess() {

        try {

            context?.let {

                val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM foods", null)
                val foodNameIndex = cursor.getColumnIndex("foodName")
                val foodIdIndex = cursor.getColumnIndex("id")
                val imageIndex = cursor.getColumnIndex("images")
                val foodRecipesIndex = cursor.getColumnIndex("foodMaterial")

                foodIdList.clear()
                foodNameList.clear()
                imageList.clear()
                foodRecipesList.clear()

                while (cursor.moveToNext()) {

                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))
                    foodRecipesList.add(cursor.getString(foodRecipesIndex))
                    val byteArray = cursor.getBlob(imageIndex)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    imageList.add(bitmap)


                }
                listAdapter.notifyDataSetChanged()

                cursor.close()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun bitmapDownSize(userSelectedBitmap: Bitmap, maximumSize: Int): Bitmap {

        var width = userSelectedBitmap.width
        var height = userSelectedBitmap.height

        val bitmapRate: Double = width.toDouble() / height.toDouble()

        if (bitmapRate > 1) {

            width = maximumSize
            val shortHeight = width / bitmapRate
            height = shortHeight.toInt()

        } else {

            height = maximumSize
            val shortWidth = height * bitmapRate
            width = shortWidth.toInt()

        }

        return Bitmap.createScaledBitmap(userSelectedBitmap, width, height, true)

    }

}