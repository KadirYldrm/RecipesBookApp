package com.example.recipesbook.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesbook.R
import com.example.recipesbook.adapter.FRFoodNameAdapter
import kotlinx.android.synthetic.main.f_r_food_name.*

class FRFoodName : Fragment() {

    private var foodNameList = ArrayList<String>()
    private var foodIdList = ArrayList<Int>()
    private lateinit var listAdapter: FRFoodNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        return inflater.inflate(R.layout.f_r_food_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        listAdapter = FRFoodNameAdapter(foodNameList, foodIdList)
        rvFoodName.layoutManager = LinearLayoutManager(context)
        rvFoodName.adapter = listAdapter

        sqlDataAccess()

    }

    private fun sqlDataAccess() {

        try {

            context?.let {

                val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM foods", null)
                val foodNameIndex = cursor.getColumnIndex("foodname")
                val foodIdIndex = cursor.getColumnIndex("id")

                foodIdList.clear()
                foodNameList.clear()

                while (cursor.moveToNext()) {

                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))

                }
                listAdapter.notifyDataSetChanged()

                cursor.close()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}