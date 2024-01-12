package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kaansimsek.kotlinproject.databinding.ActivityAppDesc1Binding
import com.kaansimsek.kotlinproject.databinding.ActivityVerifyEmailBinding

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.verifyEmailNextButton.setOnClickListener {

            val email = binding.verifyEmail.text.toString()
            val password = binding.verifyPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){

                        //Email Verification
                        val verification = firebaseAuth.currentUser?.isEmailVerified
                        if(verification == true){
                            Toast.makeText(this, "Your account has been created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,AppDesc1Activity::class.java)
                            startActivity(intent)

                        }else{
                            Toast.makeText(this,"Please Verify your Email!", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Incorrect email or password!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Fields cannot be empty!!", Toast.LENGTH_SHORT).show()

            }

        }

        //Password show/hide
        binding.verifyPswCheckBox.setOnClickListener{
            if(binding.verifyPswCheckBox.isChecked){
                binding.verifyPassword.inputType = 1
            }else{
                binding.verifyPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

    }
}