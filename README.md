# SRAM + Strava Performance Hub Demo
## Overview

This is a Kotlin-based Android application built with Jetpack Compose that integrates with the Strava API. The goal of the project is to provide a "Performance Hub" where athletes can view their lifetime cycling and running statistics.

## Features
- **OAuth 2.0 Integration:** Secure authentication flow using custom URL schemes (stravademo://callback).
- **Dynamic Dashboard:** Conditionally renders "Cycling" or "Running" sections only if the athlete has data for those sports.
- **SRAM Aesthetic:** Custom color palette using SRAM Red (#E30613) and industrial dark grays, featuring high-fidelity typography and card-based layouts.
- **Measurement Units:** Converts Strava's base metric units (meters) to imperial (miles/feet) for the target user profile.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)

## Getting Started (Local Run Instructions)
1. Prerequisites
- Android Studio Ladybug or newer.
- An Android Emulator or physical device with API 24 (Nougat) or higher.

2. Configuration
- The project is pre-configured with a development Client ID and Client Secret for ease of review.
- Redirect URI: stravademo://callback (already configured in the manifest and Strava dashboard).
- Scope: The app requests read,activity:read_all so that it can pull full lifetime totals.

3. Running the App
    1. Clone the repository and open it in Android Studio.
    1. Build and Run the app on your device.
    1. Tap "CONNECT WITH STRAVA".
    1. Log in with any Strava account.
    1. After authorization, you will be redirected back to the landing page to view your stats.
    1. Note: you could replace the credentials with your own in MainActivity.kt.  If you do this, callback will need to be added as the Authorization Callback Domain for your own account at https://www.strava.com/settings/api

## Design Decisions
- **Unit Conversion: Strava provides raw data in meters.** I implemented a conversion utility to display miles and feet, aligning with the common preferences of the US/UK cycling markets.
- **Industrial UI: I used RoundedCornerShape(4.dp)** instead of standard rounded buttons to mimic the precision-machined aesthetic of SRAM drivetrain components.
- **Hard Coded Credentials:** for the purposes of this demo, credentials are hard-coded to ensure the project runs out of the box

## Future Improvements
1. **Production Readiness**
    1. **Credential Handling**: Credentials are included in the app code for this demo.  In production these would need to be moved to something like a proxy server so that instead of the app talking directly to Strava to exchange codes, it would talk to the server and the credentials are hidden from the phone and user.
    1. **Stay Logged In**: Right now if the user closes the app, variables are wiped.  The user has to log in everytime they open the app.  Would persist access_token and refresh_token to local storage and then retrieve saved token when the app starts
    1. **Handle Strava Token Expiration**: Strava tokens expire every 6 hours.  Before making an API call, check the timestamp and if the token is old ask Strava for new token in the background
    1. **Error Handling**: Wrap network calls in try/catch blocks so handle things like user having no signal, app's access on Strava being revoked, Strava api down, etc
1. **Features**
    1.  **Unit Conversion Toggle**: Allow the user to select Metric vs Imperial (or get preferred metrics from Strava Profile and automatically select based on this)
    1.  **Button to Launch Strava**: Allow user to launch Strava app if installed
