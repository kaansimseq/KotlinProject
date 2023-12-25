package com.kaansimsek.kotlinproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AdDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?,) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        val ad_id = intent.getIntExtra("cardId",0)


        val db = Firebase.firestore

        val ad = db.collection("animals").document(ad_id.toString())
        ad.get().addOnSuccessListener {
            documentSnapshot ->
            if (documentSnapshot.exists()) {
                val animalname = documentSnapshot.getString("name")
                val animalage = documentSnapshot.getString("age")
                val animalbio = documentSnapshot.getString("bio")
                val animalgender = documentSnapshot.getString("gender")
                val location = documentSnapshot.getString("location")
                val animalrace = documentSnapshot.getString("race")
                val animalowner = documentSnapshot.getString("username")
                val ownerphone = documentSnapshot.getString("userphonenumber")
            }


        }
    }




}