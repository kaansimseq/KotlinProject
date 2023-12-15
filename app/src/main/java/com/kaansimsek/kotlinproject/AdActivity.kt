package com.kaansimsek.kotlinproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner

class AdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)

        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val genderOptions = resources.getStringArray(R.array.gender_options)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerGender.adapter = adapter
    }
}
