package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaansimsek.kotlinproject.databinding.ActivityLoginBinding
import com.kaansimsek.kotlinproject.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener{

            val fullName = binding.signupFullname.text.toString()
            val phoneNumber = binding.signupPhoneNumber.text.toString()
            val birthday = binding.signupBirthday.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            val userMap = hashMapOf(
                "fullName" to fullName,
                "phoneNumber" to phoneNumber,
                "birthday" to birthday,
                "email" to email,
                "password" to password
            )

            if(fullName.isNotEmpty() && phoneNumber.isNotEmpty() && birthday.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful){

                            //Email Verification
                            firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(this,"Please Verify your Email!",Toast.LENGTH_LONG).show()
                                }
                                ?.addOnFailureListener{
                                    Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                                }

                            Toast.makeText(this, "Your account has been created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                            //Saving data in database
                            val userID = FirebaseAuth.getInstance().currentUser!!.uid
                            db.collection("users").document(userID).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this,"Successfully added on database!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed!!", Toast.LENGTH_LONG).show()
                                }

                        }else{
                            if(password.length < 6){
                                Toast.makeText(this, "Password must be 6 or more characters!!", Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this, "An account has already been created with this email!!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else{
                    Toast.makeText(this, "Password does not matched!!", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Fields cannot be empty!!", Toast.LENGTH_LONG).show()
            }
        }

        //Password show/hide
        binding.signupPswCheckBox.setOnClickListener{
            if(binding.signupPswCheckBox.isChecked){
                binding.signupPassword.inputType = 1
            }else{
                binding.signupPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        //Confirm Password show/hide
        binding.signupConfirmPswCheckBox.setOnClickListener{
            if(binding.signupConfirmPswCheckBox.isChecked){
                binding.signupConfirm.inputType = 1
            }else{
                binding.signupConfirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        //Login Redirect Text
        binding.loginRedirectText.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}