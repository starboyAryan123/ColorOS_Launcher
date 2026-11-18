package com.coloros.launcher.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import kotlin.math.max
import kotlin.math.min

/**
 * Real-time Gyroscope and Motion Sensor tracker for ColorOS 3D Depth Parallax.
 * Provides filtered, smooth pitch and roll offsets to create the illusion of physical
 * depth between the wallpaper, cards, and icon layers.
 */
class GyroscopeParallaxSensor(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _tiltX = mutableFloatStateOf(0f)
    val tiltX: State<Float> = _tiltX

    private val _tiltY = mutableFloatStateOf(0f)
    val tiltY: State<Float> = _tiltY

    // Low-pass filter smoothing coefficient (alpha)
    private val smoothingFactor = 0.12f
    private var rawTiltX = 0f
    private var rawTiltY = 0f

    fun startListening() {
        rotationSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                // roll: azimuth = 0, pitch = 1, roll = 2
                val pitch = orientation[1] // -PI/2 to PI/2 (vertical tilt)
                val roll = orientation[2]  // -PI to PI (horizontal tilt)

                // Target normalized range [-1.0f, 1.0f] clamped around 30-degree tilt
                val targetX = min(max(roll / 0.5f, -1.0f), 1.0f)
                val targetY = min(max((pitch - 0.5f) / 0.5f, -1.0f), 1.0f) // baseline comfortable holding angle

                rawTiltX += smoothingFactor * (targetX - rawTiltX)
                rawTiltY += smoothingFactor * (targetY - rawTiltY)

                _tiltX.floatValue = rawTiltX
                _tiltY.floatValue = rawTiltY
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val ax = event.values[0] // X-axis acceleration
                val ay = event.values[1] // Y-axis acceleration

                val targetX = min(max(ax / 5.0f, -1.0f), 1.0f)
                val targetY = min(max((ay - 5.0f) / 5.0f, -1.0f), 1.0f)

                rawTiltX += smoothingFactor * (targetX - rawTiltX)
                rawTiltY += smoothingFactor * (targetY - rawTiltY)

                _tiltX.floatValue = rawTiltX
                _tiltY.floatValue = rawTiltY
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
