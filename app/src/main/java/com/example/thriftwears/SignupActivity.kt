package com.example.thriftwears

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.thriftwears.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var currentState: String? = null
    private var showPassword = false
//    val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"

    companion object {
        const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        private const val MODEL_KEY = "LOGIN_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Inflate layout with ViewBinding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }
    }

    private fun initializeUI() {
        // Set up UI elements

        binding.backToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
        }

        binding.goToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
        }

        binding.togglePassword.setOnClickListener {
            binding.togglePassword.setOnClickListener {
                showPassword = !showPassword
                binding.togglePassword.text = if (showPassword) "Hide Password" else "Show Password"
                binding.signUpPassword.inputType = if (showPassword) {
                    InputType.TYPE_CLASS_TEXT // Show password as plain text
                } else {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Mask password
                }
                binding.signUpPassword.setSelection(binding.signUpPassword.text!!.length) // Ensure cursor stays at the end
            }
        }

        binding.createAccountButton.setOnClickListener {
            val firstName = binding.signUpFirstName.text
            val lastName = binding.signUpLastName.text
            val email = binding.signUpEmail.text
            val password = binding.signUpPassword.text

            // Perform login logic here
            if(firstName.isNullOrEmpty() || lastName.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()){
                Toast.makeText(this, "A compulsory field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!Regex(EMAIL_PATTERN).matches(email.toString())){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.i("SIGNUP INFO", "Email: $email, Password: $password, First Name: $firstName, Last Name: $lastName")

//            val intent = Intent(this, MainActivity::class.java)
//            this.startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

}