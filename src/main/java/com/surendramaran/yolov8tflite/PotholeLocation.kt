package com.surendramaran.yolov8tflite

data class PotholeLocation(
    val latitude: Double = 0.0,        // Latitude of the pothole
    val longitude: Double = 0.0,       // Longitude of the pothole
    val timestamp: Long = System.currentTimeMillis() // Timestamp of when the pothole was reported
)