package com.example.thriftwears

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.thriftwears.databinding.ActivitySignupBinding
import com.example.thriftwears.item.UserItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var currentState: String? = null
    private var showPassword = false
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore


    companion object {
        private const val MODEL_KEY = "SIGNUP_KEY"
        private const val TAG = "SignupActivity"
    }

    public override fun onStart() {
        super.onStart()
        auth = Firebase.auth

        auth.currentUser?.let {
            Log.d(TAG, "User is already signed in: ${it.email}")
            updateToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Inflate layout with ViewBinding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.primary)

        // Restore previous state or set initial UI state
        currentState = savedInstanceState?.getString(MODEL_KEY)
        if (currentState == null) {
            initializeUI()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentState)
    }

    private fun initializeUI() {
        // Set up UI elements
        binding.createAccountButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        binding.createAccountGoogleButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)


        binding.progressBar.visibility = android.view.View.GONE

        binding.backToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.goToLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.togglePassword.setOnClickListener {
            showPassword = !showPassword
            binding.togglePassword.text = if (showPassword) "Hide Password" else "Show Password"
            binding.signUpPassword.inputType = if (showPassword) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            binding.signUpPassword.setSelection(binding.signUpPassword.text?.length ?: 0)
        }

        binding.createAccountButton.setOnClickListener {

            auth = Firebase.auth
            val firstName = binding.signUpFirstName.text.toString()
            val lastName = binding.signUpLastName.text.toString()
            val email = binding.signUpEmail.text.toString()
            val phoneNumber = binding.signUpPhoneNumber.text.toString()
            val password = binding.signUpPassword.text.toString()

            if (!validateInput(firstName, lastName, email, phoneNumber, password)) return@setOnClickListener

            binding.createAccountButton.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE

            val userItemClassData = UserItem(
                email,
                firstName,
                lastName,
                phoneNumber
            )

            createFirebaseUser(email, password, userItemClassData)
        }

    }

    private fun validateInput(firstName: String, lastName: String, email: String, phoneNumber: String, password: String): Boolean {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            showToast("All fields are required")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format")
            return false
        }

        if (!phoneNumber.matches(Regex("^\\+\\d{10,15}$"))) {
            showToast("Invalid phone number format: Ensure it starts with '+' and has country code")
            return false
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }
        return true
    }


    private fun updateUI() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun updateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun createFirebaseUser(email: String, password: String, userItemClassData: UserItem){
        binding.createAccountButton.isEnabled = true
        binding.progressBar.visibility = android.view.View.GONE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserToFireStore(user!!.uid, userItemClassData)
                } else {
                    showToast("Authentication failed: ${task.exception?.message}")
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    private fun saveUserToFireStore(uid: String, userItemClassData: UserItem) {
        userItemClassData.id = uid
        userItemClassData.timeStamp = com.google.firebase.Timestamp.now()

        db.collection("users")
            .document(uid)
            .set(userItemClassData)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "createUserWithEmail:success, user: $docRef")
                updateUI()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                showToast("Failed to save user data")
            }
    }

}