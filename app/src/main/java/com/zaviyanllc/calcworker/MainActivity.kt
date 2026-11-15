package com.zaviyanllc.calcworker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.zaviyanllc.calcworker.data.AiRepository
import com.zaviyanllc.calcworker.data.FxRepository
import com.zaviyanllc.calcworker.data.PrefsRepository
import com.zaviyanllc.calcworker.ui.AppNav
import com.zaviyanllc.calcworker.ui.theme.CalcWorkerTheme

class MainActivity : ComponentActivity() {

    private fun isOnline(): Boolean {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val net = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(net) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (_: Exception) {
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            enableEdgeToEdge()
        } catch (t: Throwable) {
            Log.w("CalcWorker", "enableEdgeToEdge fallback", t)
        }

        setContent {
            val prefs = remember { PrefsRepository(applicationContext) }
            val fx = remember { FxRepository() }
            val ai = remember { AiRepository(isOnline = ::isOnline) }
            val theme by prefs.theme.collectAsState()
            CalcWorkerTheme(theme = theme) {
                AppNav(prefs = prefs, fx = fx, ai = ai)
            }
        }
    }
}
