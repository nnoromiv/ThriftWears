package com.example.thriftwears

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.thriftwears.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var currentState: String? = null
    private var showPassword = false

    companion object {
        private const val MODEL_KEY = "LOGIN_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Inflate layout with ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }
    }

    private fun initializeUI() {
        // Set up UI elements

        binding.closeLogin.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            this.startActivity(intent)
        }

        binding.goToSignup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            this.startActivity(intent)
        }

        binding.togglePassword.setOnClickListener {
            binding.togglePassword.setOnClickListener {
                showPassword = !showPassword
                binding.togglePassword.text = if (showPassword) "Hide Password" else "Show Password"
                binding.loginPassword.inputType = if (showPassword) {
                    InputType.TYPE_CLASS_TEXT // Show password as plain text
                } else {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // Mask password
                }
                binding.loginPassword.setSelection(binding.loginPassword.text!!.length) // Ensure cursor stays at the end
            }
        }

        binding.loginEmailButton.setOnClickListener {
            val email = binding.loginEmail.text
            val password = binding.loginPassword.text

            // Perform login logic here
            if(email.isNullOrEmpty() || password.isNullOrEmpty()){
                Toast.makeText(this, "A compulsory field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!Regex(SignupActivity.EMAIL_PATTERN).matches(email.toString())){
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.i("LOGIN INFO", "Email: $email, Password: $password")

//            val intent = Intent(this, MainActivity::class.java)
//            this.startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

}