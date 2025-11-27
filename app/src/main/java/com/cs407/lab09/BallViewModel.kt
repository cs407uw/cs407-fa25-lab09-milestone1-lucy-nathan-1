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
            ball = Ball(fieldWidth, fieldHeight, ballSizePx)
            _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        val currentBall = ball ?: return

        val NS2S = 1.0f / 1_000_000_000.0f

        val ACC_SCALE = 30.0f

        if (event.sensor.type != Sensor.TYPE_GRAVITY) return

        if (lastTimestamp != 0L) {
            val dT = (event.timestamp - lastTimestamp) * NS2S

            // Raw gravity readings (opposite to real gravity, in sensor coords)
            val rawX = event.values[0]
            val rawY = event.values[1]

            //  - Multiply by ACC_SCALE so the ball moves faster.
            val xAcc = -rawX * ACC_SCALE
            val yAcc = rawY * ACC_SCALE

            currentBall.updatePositionAndVelocity(
                xAcc = xAcc,
                yAcc = yAcc,
                dT = dT
            )

            currentBall.checkBoundaries()

            _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
        }

        // Remember timestamp for next frame
        lastTimestamp = event.timestamp
    }

    fun reset() {
        ball?.reset()
        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }
        lastTimestamp = 0L
    }
}
