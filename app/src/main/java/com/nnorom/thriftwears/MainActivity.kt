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
        const val MAIN_ACTIVITY_KEY = "MAIN_ACTIVITY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        window.statusBarColor = getColor(R.color.primary)

        if (savedInstanceState?.getString(MAIN_ACTIVITY_KEY) == null) {
            replaceFragment(Home(), Home::class.java.simpleName)
        }

        currentFragmentTag?.let { replaceFragment(Home(), it) }

        val cartData : ArrayList<CartItem>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cart_data", CartItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("cart_data")
        }

        cartData?.let {
            globalCartViewModel.clearCart()
            for (item in it) {
                globalCartViewModel.addItem(item)
            }
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

            binding.bottomNavigationView.visibility =
                if (item.itemId == R.id.upload) View.GONE else View.VISIBLE

            replaceFragment(selectedFragment, selectedFragment::class.java.simpleName)
            true
        }

        onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MAIN_ACTIVITY_KEY, currentFragmentTag)
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

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        // Find existing Fragment
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        if (existingFragment != null) {
            // Show the existing Fragment
            supportFragmentManager.fragments.forEach { transaction.hide(it) }
            transaction.show(existingFragment)
        } else {
            // Create and add the new Fragment
            transaction.add(R.id.frame_layout, fragment, tag)
            supportFragmentManager.fragments.forEach { transaction.hide(it) }
        }

        transaction.commit()
        currentFragmentTag = tag
    }

}