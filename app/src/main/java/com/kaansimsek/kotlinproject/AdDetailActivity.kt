package com.kaansimsek.kotlinproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.toUpperCase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AdDetailActivity : AppCompatActivity() {
    private lateinit var AnimalImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        val ad_id = intent.getStringExtra("cardId")

        val db = Firebase.firestore

        val ad = db.collection("animals").document(ad_id.toString())
        ad.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val animalname = documentSnapshot.getString("name")
                val animalage = documentSnapshot.get("age")
                val animalbio = documentSnapshot.getString("bio")
                val animalgender = documentSnapshot.getString("gender")
                val location = documentSnapshot.getString("location")
                val animalrace = documentSnapshot.getString("race")
                val animalowner = documentSnapshot.getString("username")
                val ownerphone = documentSnapshot.getString("userphonenumber")
                val image = documentSnapshot.getString("photoUrl")


                val AnimalName = findViewById<TextView>(R.id.AnimalName)
                val AnimalAge = findViewById<TextView>(R.id.AnimalAge)
                val AnimalBio = findViewById<Button>(R.id.AnimalBio)
                val AnimalGender = findViewById<TextView>(R.id.AnimalGender)
                val AnimalLocation = findViewById<TextView>(R.id.AnimalLocation)
                val AnimalRace = findViewById<TextView>(R.id.AnimalRace)
                val AnimalOwner = findViewById<TextView>(R.id.AnimalOwner)
                val AnimalOwnerPhone = findViewById<TextView>(R.id.AnimalOwnerPhone)
                AnimalImageView = findViewById<ImageView>(R.id.AnimalImageView)

                AnimalName.text = "$animalname".toUpperCase()
                AnimalAge.text = "Age: $animalage"
                AnimalBio.text = " $animalbio"
                AnimalGender.text = "Gender: $animalgender"
                AnimalLocation.text = "$location"
                AnimalRace.text = "Race: $animalrace"
                AnimalOwner.text = "Owner: $animalowner"
                AnimalOwnerPhone.text = "Phone: $ownerphone"

                if (!image.isNullOrEmpty()) {
                    LoadImageTask().execute(image)
                }
            }
        }
    }


    private inner class LoadImageTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            val imageUrl = params[0]
            return try {
                val connection = URL(imageUrl).openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            result?.let {
                // Bitmap'i yuvarlak yap
                val roundedBitmap = getRoundedBitmap(it)

                // Yuvarlak Bitmap'i ImageView içinde gösterme
                AnimalImageView.setImageBitmap(roundedBitmap)
            }
        }
    }

    fun getRoundedBitmap(bitmap: Bitmap): Bitmap {
        val roundedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(roundedBitmap)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2, paint)

        return roundedBitmap
    }
}