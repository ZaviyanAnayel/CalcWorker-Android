package com.zaviyanllc.calcworker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
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
import java.io.PrintWriter
import java.io.StringWriter

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

        // Prevent Android OS "App has stopped" crash dialog by capturing any fatal exception
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CalcWorker", "FATAL UNCAUGHT EXCEPTION on thread ${thread.name}", throwable)
            runOnUiThread {
                showFatalCrashUI(throwable)
            }
        }

        try {
            enableEdgeToEdge()
        } catch (t: Throwable) {
            Log.w("CalcWorker", "enableEdgeToEdge fallback", t)
        }

        try {
            setContent {
                val prefs = remember { PrefsRepository(applicationContext) }
                val fx = remember { FxRepository() }
                val ai = remember { AiRepository(isOnline = ::isOnline) }
                val theme by prefs.theme.collectAsState(initial = "dark")
                CalcWorkerTheme(theme = theme) {
                    AppNav(prefs = prefs, fx = fx, ai = ai)
                }
            }
        } catch (t: Throwable) {
            Log.e("CalcWorker", "setContent failed", t)
            showFatalCrashUI(t)
        }
    }

    private fun showFatalCrashUI(t: Throwable) {
        try {
            val sw = StringWriter()
            t.printStackTrace(PrintWriter(sw))
            val trace = sw.toString()

            val scroll = ScrollView(this).apply {
                setBackgroundColor(android.graphics.Color.parseColor("#06080D"))
            }
            val tv = TextView(this).apply {
                text = "CalcWorker Diagnostics (v2.0.3 vc 7):\n\n" +
                       "Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (Android ${android.os.Build.VERSION.RELEASE}, API ${android.os.Build.VERSION.SDK_INT})\n\n" +
                       "Error Details:\n$trace"
                setTextColor(android.graphics.Color.parseColor("#38BDF8"))
                textSize = 13f
                setPadding(40, 80, 40, 60)
                setTextIsSelectable(true)
            }
            scroll.addView(tv)
            setContentView(scroll)
        } catch (_: Throwable) {}
    }
}
