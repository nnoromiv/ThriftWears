package com.example.thriftwears

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.thriftwears.databinding.ProfileBinding
import com.example.thriftwears.item.UserItemClass
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import java.util.Date

class Profile : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val db = com.google.firebase.Firebase.firestore
    private var userItemClassData = UserItemClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth // Initialize FirebaseAuth instance
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            updateUI()
        } else {
            getUserData(currentUser.uid)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    private fun updateUI() {
        // Navigate to LoginActivity and finish the parent activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getUserData(uid: String) {
        val docRef = db.collection("users").document(uid)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userItemClassData = document.toObject(UserItemClass::class.java)!!

                    val fullName = "${userItemClassData.firstName ?: ""} ${userItemClassData.lastName ?: ""}".trim()
                    binding.profileBar.findViewById<TextView>(R.id.fullName)?.text = fullName

                    val memberSince = userItemClassData.timeStamp?.let { memberDate(it) }
                    binding.profileBar.findViewById<TextView>(R.id.memberSince)?.text = memberSince
                } else {
                    Log.d("PROFILE", "No such document found for UID: $uid")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PROFILE", "Error fetching document: ${exception.message}", exception)
            }
    }

    private fun memberDate(timestamp: Timestamp): String {
        val date = Date(timestamp.seconds * 1000)
        // Define the output format for the desired output
        val outputFormat = SimpleDateFormat("MMM. yyyy", ULocale.ENGLISH)

        return "Member since" + " " + outputFormat.format(date)
    }

}