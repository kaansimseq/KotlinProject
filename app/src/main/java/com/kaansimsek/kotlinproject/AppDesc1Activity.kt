package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kaansimsek.kotlinproject.databinding.ActivityAppDesc1Binding
import com.kaansimsek.kotlinproject.databinding.ActivityLoginBinding

class AppDesc1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityAppDesc1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDesc1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //Page transition (AppDesc2Activity)
        binding.nextButton1.setOnClickListener{
            val intent = Intent(this,AppDesc2Activity::class.java)
            startActivity(intent)
        }

    }
}