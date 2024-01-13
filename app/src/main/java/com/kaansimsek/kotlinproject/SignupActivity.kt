package com.kaansimsek.kotlinproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kaansimsek.kotlinproject.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var fullName: String = ""
    private var phoneNumber: String = ""
    private var birthday: String = ""
    private var email: String = ""
    private var password: String = ""

    //Retrieves data and moves it to the next page(PhotoAndBioActivity)
    private fun getData() {
        fullName = binding.signupFullname.text.toString()
        phoneNumber = binding.signupPhoneNumber.text.toString()
        birthday = binding.signupBirthday.text.toString()
        email = binding.signupEmail.text.toString()
        password = binding.signupPassword.text.toString()
        nextPage()
    }

    //Function to move data to the next page
    private fun nextPage() {
        val intent = Intent(this, PhotoAndBioActivity::class.java)
        intent.putExtra("fullName", fullName)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("birthday", birthday)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupNextButton.setOnClickListener{

            val fullName = binding.signupFullname.text.toString()
            val phoneNumber = binding.signupPhoneNumber.text.toString()
            val birthday = binding.signupBirthday.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()


            if(fullName.isNotEmpty() && phoneNumber.isNotEmpty() && birthday.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if(password == confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful){

                            //getData function works
                            getData()

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