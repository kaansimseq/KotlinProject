package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kaansimsek.kotlinproject.databinding.ActivityAppDesc1Binding
import com.kaansimsek.kotlinproject.databinding.ActivityAppDesc2Binding

class AppDesc2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAppDesc2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDesc2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton2.setOnClickListener{
            val intent = Intent(this,AppDesc3Activity::class.java)
            startActivity(intent)
        }

    }
}