package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kaansimsek.kotlinproject.databinding.ActivityAppDesc3Binding

class AppDesc3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAppDesc3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDesc3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //Page transition (MainActivity)
        binding.nextButton3.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}