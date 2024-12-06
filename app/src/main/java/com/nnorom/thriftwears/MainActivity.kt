package com.nnorom.thriftwears

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.nnorom.thriftwears.databinding.ActivityMainBinding
import com.nnorom.thriftwears.item.CartItem
import com.nnorom.thriftwears.viewmodel.GlobalCartViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentTag: String? = null
    private val globalCartViewModel : GlobalCartViewModel by viewModels()

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

        window.statusBarColor = getColor(R.color.primary)

        currentFragmentTag = savedInstanceState?.getString(MODEL_KEY)
        if (currentFragmentTag == null) {
            replaceFragment(this, Home(globalCartViewModel), Home::class.java.simpleName)
        } else {
            val fragment = supportFragmentManager.findFragmentByTag(currentFragmentTag) ?: Home(globalCartViewModel)
            replaceFragment(this, fragment, currentFragmentTag!!)
        }

        val cartData : ArrayList<CartItem>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cart_data", CartItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("cart_data")
        }

        if (cartData != null) {
            globalCartViewModel.clearCart()
            for (item in cartData) {
                globalCartViewModel.addItem(item)
            }
        }

        // Set up bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> Home(globalCartViewModel)
                R.id.profile -> Profile(globalCartViewModel)
                R.id.search -> Search(globalCartViewModel)
                R.id.wish_list -> Saved()
                R.id.upload -> Upload()
                else -> return@setOnItemSelectedListener false
            }

            binding.bottomNavigationView.visibility =
                if (item.itemId == R.id.upload) View.GONE else View.VISIBLE

            replaceFragment(this, selectedFragment, selectedFragment::class.java.simpleName)
            true
        }

        onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MODEL_KEY, currentFragmentTag)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val scheme = data.scheme // "myapp"
            val host = data.host     // "example"
            val path = data.path     // "/login" (if applicable)

            // Do something with the data
            Log.d("CustomScheme", "Scheme: $scheme, Host: $host, Path: $path")
        }
    }


}