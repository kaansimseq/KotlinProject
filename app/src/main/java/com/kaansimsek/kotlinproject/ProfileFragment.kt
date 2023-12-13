package com.kaansimsek.kotlinproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.kaansimsek.kotlinproject.databinding.ActivitySignupBinding
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

// ... Diğer import ifadeleri ...

class ProfileFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var biographyTextView: TextView
    private lateinit var phonenumberTextView: TextView
    private lateinit var profileImageView: ImageView

    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // TextView'ları layout dosyasındaki elemanlarla eşleştir
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        biographyTextView = view.findViewById(R.id.biographyTextView)
        phonenumberTextView = view.findViewById(R.id.phonenumberTextView)
        profileImageView = view.findViewById(R.id.profileImageView)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ref = db.collection("users").document(userId)
            ref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("fullName")
                    val email = documentSnapshot.getString("email")
                    val biography = documentSnapshot.getString("biography")
                    val phonenumber = documentSnapshot.getString("phoneNumber")
                    val image = documentSnapshot.getString("imageUrl")

                    // Null olmayan değerleri TextView'lara atama
                    nameTextView.text = "${name ?: ""}"
                    emailTextView.text = "MAIL: ${email ?: ""}"
                    biographyTextView.text = " ${biography ?: ""}"
                    phonenumberTextView.text = "CALL: ${phonenumber ?: ""}"

                    // Resmi yükleme işlemi
                    if (!image.isNullOrEmpty()) {
                        LoadImageTask().execute(image)
                    }
                }
            }.addOnFailureListener { exception ->
                // Hata durumunda Toast mesajı göster
                Toast.makeText(requireContext(), "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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
                profileImageView.setImageBitmap(roundedBitmap)
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


