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
import kotlinx.android.synthetic.main.f_r_recipes.*
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

        return inflater.inflate(R.layout.f_r_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        btSave.setOnClickListener {

            save(it)

        }

        ivSelect.setOnClickListener {

            ivSelectFood(it)

        }

        arguments?.let {

            val incomingInfo = FRRecipesArgs.fromBundle(it).info

            if (incomingInfo.equals("fromtomenu")) {

                etFoodName.setText("")
                etFoodMaterial.setText("")
                btSave.visibility = View.VISIBLE

                val imageselect =
                    BitmapFactory.decodeResource(context?.resources, R.drawable.selectimage)
                ivSelect.setImageBitmap(imageselect)

            } else {

                btSave.visibility = View.INVISIBLE

                val selectedId = FRRecipesArgs.fromBundle(it).id
                context?.let {

                    try {

                        val db = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE, null)
                        val cursor = db.rawQuery(
                            "SELECT * FROM foods WHERE id = ?",
                            arrayOf(selectedId.toString())
                        )

                        val foodNameIndex = cursor.getColumnIndex("foodname")
                        val foodMaterialIndex = cursor.getColumnIndex("foodmaterial")
                        val foodImage = cursor.getColumnIndex("images")

                        while (cursor.moveToNext()) {

                            etFoodName.setText(cursor.getString(foodNameIndex))
                            etFoodMaterial.setText(cursor.getString(foodMaterialIndex))

                            val byteArray = cursor.getBlob(foodImage)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            ivSelect.setImageBitmap(bitmap)

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
                    database.execSQL("CREATE TABLE IF NOT EXISTS foods (id INTEGER PRIMARY KEY,foodname VARCHAR,foodmaterial VARCHAR, images BLOG)")

                    val sqlString =
                        "INSERT INTO foods (foodname,foodmaterial,images) VALUES (?,?,?)"
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

    private fun ivSelectFood(view: View) {

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


}