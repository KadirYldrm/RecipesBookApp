package com.example.recipesbook.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.recipesbook.R
import kotlinx.android.synthetic.main.fr_recipes.*
import kotlinx.android.synthetic.main.fr_recipes.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class FRRecipes : Fragment() {

    private var selectedImage: Uri? = null
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fr_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSave.setOnClickListener {
            save(it)
        }

        ivSelect.setOnClickListener {
            ClickFood(it)
        }

        //btnDelete.setOnClickListener(this::delete)

        arguments?.let {

            val incomingInfo = FRRecipesArgs.fromBundle(it).info

            if (incomingInfo == "fromToMenu") {

                etFoodName.text?.clear()
                etFoodMaterial.text?.clear()
                btnSave.visibility = View.VISIBLE
                btnDelete.visibility = View.INVISIBLE
                val imageSelect =
                    BitmapFactory.decodeResource(context?.resources, R.drawable.bg_selected_image)
                ivSelect.setImageBitmap(imageSelect)

            } else {

                btnSave.visibility = View.INVISIBLE
                btnDelete.visibility = View.VISIBLE

                val selectedId = FRRecipesArgs.fromBundle(it).id
                context?.let { context ->
                    try {
                        val db = context.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                        val cursor = db.rawQuery(
                            "SELECT * FROM foods WHERE id = ?",
                            arrayOf(selectedId.toString())
                        )

                        val foodNameIndex = cursor.getColumnIndex("foodName")
                        val foodMaterialIndex = cursor.getColumnIndex("foodMaterial")
                        val foodImage = cursor.getColumnIndex("images")

                        while (cursor.moveToNext()) {

                            etFoodName.setText(cursor.getString(foodNameIndex))
                            etFoodMaterial.setText(cursor.getString(foodMaterialIndex))

                            val byteArray = cursor.getBlob(foodImage)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            ivSelect.setImageBitmap(bitmap)

                        }

                        btnDelete.setOnClickListener {
                            context?.let {
                                val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                                database.delete("Foods","id=?", arrayOf(selectedId.toString()))
                            }
                        }

                        cursor.close()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }
        }

    }

    private fun save(view: View) {

        val foodName = etFoodName.text.toString()
        val foodMaterials = etFoodMaterial.text.toString()

        if (selectedBitmap != null) {

            val shortBitmap = bitmapDownSize(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            shortBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                context?.let {

                    val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods (id INTEGER PRIMARY KEY,foodName VARCHAR,foodMaterial VARCHAR, images BLOG)")

                    val sqlString =
                        "INSERT INTO foods (foodName,foodMaterial,images) VALUES (?,?,?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, foodName)
                    statement.bindString(2, foodMaterials)
                    statement.bindBlob(3, byteArray)
                    statement.execute()

                }

            } catch (e: Exception) {

                e.printStackTrace()
            }

            val action = FRRecipesDirections.actionFRRecipesToFRFoodName()
            Navigation.findNavController(view).navigate(action)

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(galleryIntent, 2)

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data
            try {
                context?.let {
                    if (selectedImage != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(it.contentResolver, selectedImage!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            ivSelect.setImageBitmap(selectedBitmap)

                        } else {

                            selectedBitmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, selectedImage)
                            ivSelect.setImageBitmap(selectedBitmap)

                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
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

    fun ClickFood(view: View) {
        activity?.let {

            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

            } else {

                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(galleryIntent, 2)

            }

        }
    }


   /* private fun delete(view: View){

        //val foodName = tvFoodName.text.toString()
        //val foodMaterials = etFoodMaterial.text.toString()
        //val outputStream = ByteArrayOutputStream()
        //val byteArray = outputStream.toByteArray()

        context?.let {

            val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
            val sqlString = "DELETE FROM Foods WHERE id =?"
            val statement = database.compileStatement(sqlString)

            statement.clearBindings()
            //statement.bindString(1, foodName)
            //statement.bindString(2, foodMaterials)
            //statement.bindBlob(3, byteArray)

            statement.execute()
            database.close()

        }


    }*/

}