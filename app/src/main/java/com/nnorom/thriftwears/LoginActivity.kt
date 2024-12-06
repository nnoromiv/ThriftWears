package com.nnorom.thriftwears

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nnorom.thriftwears.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var currentState: String? = null
    private var showPassword = false
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val MODEL_KEY = "LOGIN_KEY"
        private const val TAG = "LoginActivity"
    }

    public override fun onStart() {
        super.onStart()
        auth = Firebase.auth

        auth.currentUser?.let {
            Log.d(TAG, "User is already signed in: ${it.email}")
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()
        window.statusBarColor = getColor(R.color.primary)

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
        binding.loginEmailButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        binding.loginGoogleButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)

        binding.progressBar.visibility = android.view.View.GONE

        binding.closeLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
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

            auth = Firebase.auth
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (!validateInput(email, password)) return@setOnClickListener

            binding.loginEmailButton.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.progressBar.bringToFront()

            loginFirebaseUser(email, password)

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

    private fun updateUI() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("All fields are required")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format")
            return false
        }

        return true
    }

    private fun loginFirebaseUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showToast("Authentication failed: ${task.exception?.message}")
                }
            }
        binding.loginEmailButton.isEnabled = true
        binding.progressBar.visibility = android.view.View.GONE
    }

}