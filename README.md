# Pothole Hunter

Pothole Hunter is an Android application that detects potholes from a live camera feed using a TensorFlow Lite object detection model, captures the device location, and stores pothole coordinates in Firebase Realtime Database for later viewing on a map.

## Features

- Live pothole detection using CameraX + TensorFlow Lite
- Real-time bounding box overlay on the camera preview
- GPS-based pothole logging with Firebase Realtime Database
- Map view to inspect recorded pothole locations
- Warning sound when approaching a logged pothole area

## Project Overview

The app is organized around three main screens:

1. **Welcome Screen**
   - Entry point with buttons to start detection or open the map.

2. **Detection Screen**
   - Uses the back camera to scan the road.
   - Runs a TFLite model from the assets folder.
   - Logs detected pothole coordinates to Firebase.

3. **Map Screen**
   - Displays saved pothole locations from Firebase.
   - Shows the user’s current location and provides proximity warnings.

## Tech Stack

- Kotlin
- AndroidX CameraX
- TensorFlow Lite
- Google Maps SDK
- Google Play Services Location
- Firebase Realtime Database
- Material Components

## Prerequisites

Before building the project, make sure you have:

- Android Studio (latest stable recommended)
- JDK 17 or newer
- Android SDK with API level 35 installed
- A physical device or emulator with:
  - Camera
  - GPS/location services
- A Firebase project with Realtime Database enabled
- A Google Maps API key

## Project Structure

- [app](app) - Android application source (not present in this repo root; use the project root for app code)
- [src/main/assets/model.tflite](src/main/assets/model.tflite) - TensorFlow Lite pothole detection model
- [src/main/assets/labels.txt](src/main/assets/labels.txt) - Labels used by the model
- [src/main/java](src/main/java) - Kotlin source code
- [src/main/res](src/main/res) - Layouts, strings, themes, and resources
- [google-services.json](google-services.json) - Firebase configuration file

## Setup Instructions

1. Clone the repository
   ```bash
   git clone <repository-url>
   cd PotholeDetectionSystem
   ```

2. Open the project in Android Studio.

3. Ensure [google-services.json](google-services.json) is present in the project root.
   - Download the file from your Firebase project and replace the existing one if needed.

4. Configure your Google Maps API key.
   - The key is referenced in [src/main/res/values/google_maps_api.xml](src/main/res/values/google_maps_api.xml).
   - Replace the placeholder/default value with your own valid API key for production use.

5. Sync Gradle files and let Android Studio download the required dependencies.

## Build and Run

### From Android Studio

1. Select a connected device or emulator.
2. Click **Run**.
3. The app should launch with the welcome screen.

### From the command line

```bash
./gradlew assembleDebug
```

To install directly on a connected device:

```bash
./gradlew installDebug
```

## Required Permissions

The app requests the following runtime permissions:

- `CAMERA`
- `ACCESS_FINE_LOCATION`
- `INTERNET`

These are declared in [src/main/AndroidManifest.xml](src/main/AndroidManifest.xml).

## Firebase Configuration

The app writes pothole detections to the `pothole_locations` path in Firebase Realtime Database.

Make sure your Firebase project has:

- Realtime Database enabled
- Rules configured for read/write access during development if needed
- The `google-services.json` file linked to the correct Firebase project

## Model Notes

The TensorFlow Lite model used by the app is stored in [src/main/assets/model.tflite](src/main/assets/model.tflite).

If you want to improve detection accuracy, replace the model and update the label list in [src/main/assets/labels.txt](src/main/assets/labels.txt).

## Troubleshooting

- If the app cannot access the camera, verify that camera permission was granted.
- If the map does not load, confirm that your Google Maps API key is valid.
- If Firebase data is not appearing, check the Realtime Database rules and ensure the app is using the correct Firebase configuration.

## License

This project is intended for educational and demonstration purposes. Add your preferred license file if you plan to distribute it publicly.
