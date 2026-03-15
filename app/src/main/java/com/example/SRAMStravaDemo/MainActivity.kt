package com.example.sramstravademo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sramstravademo.ui.theme.sramstravademotheme
import okhttp3.*
import okhttp3.FormBody
import org.json.JSONObject
import java.io.IOException
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    // NOTE: invalid
    val STRAVA_CLIENT_ID="11111"
    val STRAVA_CLIENT_SECRET="11111"

    private var accessToken: String? = null
    private var athleteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if the app was started via the Strava redirect
        intent?.data?.let { handleStravaCallback(it) }

        setContent {
            sramstravademotheme {
                LandingScreenContent(
                    onConnect = { startStravaAuth() }, // Your function to launch browser
                    onSignOut = { signOut() }          // Your function to reset state
                )
            }
        }
    }

    private fun startStravaAuth() {
        val redirectUri = "stravademo://callback"
        val intentUri = "https://www.strava.com/oauth/mobile/authorize".toUri()
            .buildUpon()
            .appendQueryParameter("client_id", STRAVA_CLIENT_ID) // Put your ID here
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", "read,activity:read_all")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // When the app is already open in the background
        intent?.data?.let { handleStravaCallback(it) }
    }

    private fun handleStravaCallback(uri: android.net.Uri) {
        if (uri.toString().startsWith("stravademo://callback")) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                exchangeCodeForToken(code)
            }
        }
    }

    private fun exchangeCodeForToken(authCode: String) {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("client_id", STRAVA_CLIENT_ID)
            .add("client_secret", STRAVA_CLIENT_SECRET)
            .add("code", authCode)
            .add("grant_type", "authorization_code")
            .build()

        val request = Request.Builder()
            .url("https://www.strava.com/oauth/token")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OAuth", "Token exchange failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val json = JSONObject(body)
                        accessToken = json.getString("access_token")
                        val athlete = json.getJSONObject("athlete")
                        val fullName = "${athlete.getString("firstname")} ${athlete.getString("lastname")}"

                        athleteId = athlete.getString("id") // Grab the ID here!
                        // Now call the stats function
                        athleteId?.let { fetchAthleteStats(it) }

                        // update UI state
                        runOnUiThread {
                            LandingScreen.connected = true
                            LandingScreen.athleteName = fullName
                        }
                    } catch (e: Exception) {
                        Log.e("OAuth", "Failed to parse token response", e)
                    }
                }
            }
        })
    }

    private fun fetchAthleteStats(id: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.strava.com/api/v3/athletes/$id/stats")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("StravaStats", "Failed to fetch stats", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val json = JSONObject(body)
                        // Parse Rides
                        val rideTotals = json.getJSONObject("all_ride_totals")
                        val rMiles = (rideTotals.getDouble("distance") * 0.000621371).toInt()
                        val rCount = rideTotals.getInt("count")

                        // Parse Runs
                        val runTotals = json.getJSONObject("all_run_totals")
                        val runMilesVal = (runTotals.getDouble("distance") * 0.000621371).toInt()
                        val runCountVal = runTotals.getInt("count")

                        runOnUiThread {
                            LandingScreen.rideMiles = "$rMiles mi"
                            LandingScreen.rideCount = "$rCount rides"
                            LandingScreen.runMiles = "$runMilesVal mi"
                            LandingScreen.runCount = "$runCountVal runs"
                        }
                    } catch (e: Exception) {
                        Log.e("StravaStats", "Parsing error", e)
                    }
                }
            }
        })
    }

    fun signOut() {
        // Reset all state variables
        LandingScreen.connected = false
        LandingScreen.athleteName = ""
        LandingScreen.rideMiles = "0 mi"
        LandingScreen.rideCount = "0 rides"
        LandingScreen.runMiles = "0 mi"
        LandingScreen.runCount = "0 runs"
    }
}
