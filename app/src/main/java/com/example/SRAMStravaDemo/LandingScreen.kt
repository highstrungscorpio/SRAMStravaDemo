package com.example.sramstravademo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sramstravademo.ui.theme.SramRed
import com.example.sramstravademo.ui.theme.SramLightGray
import com.example.sramstravademo.ui.theme.SramDarkGray

object LandingScreen {
    var connected by mutableStateOf(false)
    var athleteName by mutableStateOf("")
    var rideMiles by mutableStateOf("0 mi")
    var rideCount by mutableStateOf("0 rides")
    var runMiles by mutableStateOf("0 mi")
    var runCount by mutableStateOf("0 runs")
}

@Composable
fun LandingScreenContent(onConnect: () -> Unit, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SramDarkGray) // Custom dark background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!LandingScreen.connected) {
            // LOGIN STATE
            Text(
                text = "SRAM",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = SramRed
            )
            Text(
                text = "STRAVA DEMO",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(4.dp), // Sharper, industrial look
                colors = ButtonDefaults.buttonColors(containerColor = SramLightGray)
            ) {
                Text("CONNECT WITH STRAVA", fontWeight = FontWeight.Bold)
            }
        } else {
            // DASHBOARD STATE
            Text(
                text = "RIDER: ${LandingScreen.athleteName.uppercase()}",
                color = SramRed,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Rides Card
            if (LandingScreen.rideCount != "0 rides") {
                DashboardCard("CYCLING", LandingScreen.rideMiles, LandingScreen.rideCount)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Runs Card
            if (LandingScreen.runCount != "0 runs") {
                DashboardCard("RUNNING", LandingScreen.runMiles, LandingScreen.runCount)
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onSignOut,
                border = BorderStroke(1.dp, Color.DarkGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
            ) {
                Text("SIGN OUT")
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, val1: String, val2: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = SramRed, style = MaterialTheme.typography.labelSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(val1, color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(val2, color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
