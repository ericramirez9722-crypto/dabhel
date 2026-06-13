package com.example

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MythosScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MythosViewModel
import com.example.util.MythosSyncWorker

class MainActivity : ComponentActivity() {
  private var networkCallback: ConnectivityManager.NetworkCallback? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Schedule background queue sync for any pending offline content
    MythosSyncWorker.scheduleSync(this)

    // Setup Network Connectivity Listener to trigger background sync immediately on transition to online mode
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    val isInitiallyOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    networkCallback = object : ConnectivityManager.NetworkCallback() {
      private var wasOffline = !isInitiallyOnline

      override fun onAvailable(network: Network) {
        super.onAvailable(network)
        if (wasOffline) {
          Log.i("MainActivity", "Device transitioned from OFFLINE to ONLINE. Triggering immediate mythos sync.")
          MythosSyncWorker.scheduleSync(this@MainActivity)
          wasOffline = false
        } else {
          Log.d("MainActivity", "Network callback: Connected (already online).")
        }
      }

      override fun onLost(network: Network) {
        super.onLost(network)
        Log.i("MainActivity", "Device network lost. Switched to offline-transition tracking.")
        wasOffline = true
      }
    }

    try {
      connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
    } catch (e: Exception) {
      Log.e("MainActivity", "Failed to register default network callback", e)
    }

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF030712) // DeepBackground obsidian color
        ) {
          val viewModel: MythosViewModel = viewModel()
          MythosScreen(viewModel = viewModel)
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      networkCallback?.let {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(it)
        Log.d("MainActivity", "Successfully unregistered network callback in onDestroy.")
      }
    } catch (e: Exception) {
      Log.e("MainActivity", "Error unregistering network callback", e)
    }
  }
}
