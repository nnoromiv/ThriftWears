package com.example.thriftwears

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.thriftwears.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentTag: String? = null

    companion object {
        private const val MODEL_KEY = "MODEL_KEY"

        fun replaceFragment(mainActivity: MainActivity, fragment: Fragment, tag: String) {
            if (mainActivity.currentFragmentTag == tag) return // Avoid replacing with the same fragment

            mainActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, fragment, tag)
                commit()
            }
            mainActivity.currentFragmentTag = tag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Restore fragment by tag if available, else show the default fragment
        currentFragmentTag = savedInstanceState?.getString(MODEL_KEY)
        if (currentFragmentTag == null) {
            replaceFragment(this, Home(), Home::class.java.simpleName)
        } else {
            val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag) ?: Home()
            replaceFragment(this, fragment, currentFragmentTag!!)
        }

        // Set up bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> Home()
                R.id.profile -> Profile()
                R.id.search -> Search()
                R.id.wish_list -> Saved()
                R.id.upload -> Upload()
                else -> return@setOnItemSelectedListener false
            }

            // Toggle BottomNavigationView visibility
            binding.bottomNavigationView.visibility =
                if (item.itemId == R.id.upload) View.GONE else View.VISIBLE

            replaceFragment(this, selectedFragment, selectedFragment::class.java.simpleName)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentFragmentTag)
    }

}