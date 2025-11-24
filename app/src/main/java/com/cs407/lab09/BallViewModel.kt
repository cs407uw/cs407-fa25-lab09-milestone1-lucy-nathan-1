package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            // initialize ball instance
            ball = Ball(fieldWidth, fieldHeight, ballSizePx)

            // update StateFlow with initial position
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // ball initialized ?
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                // calculate time difference + convert to seconds
                val nano = 1.0f / 1000000000.0f
                val dT = (event.timestamp - lastTimestamp) * nano

                // Apply dead zone to filter out small sensor noise ???
                val DEAD_ZONE = 0.5f
                var xAcc = -event.values[0]
                var yAcc = -event.values[1]

                // If acceleration is below threshold, set to zero
                if (kotlin.math.abs(xAcc) < DEAD_ZONE) xAcc = 0f
                if (kotlin.math.abs(yAcc) < DEAD_ZONE) yAcc = 0f

                // Update the ball's position and velocity
                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )

                // Check boundaries after updating position
                currentBall.checkBoundaries()

                // Update the StateFlow to notify the UI
                _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
            }

            // Update the lastTimestamp for next iteration
            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()
        // update state flow
        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }

        // Reset the lastTimestamp
        lastTimestamp = 0L
    }
}