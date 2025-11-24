package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 *
 * Constructor parameters:
 * - backgroundWidth: the width of the background, of type Float
 * - backgroundHeight: the height of the background, of type Float
 * - ballSize: the width/height of the ball, of type Float
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     * (See lab handout for physics equations)
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if(isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        // calculate new velocities using EQUATION 1:
        // v1 = v0 + 1/2 * (a1 + a0) * (t1 - t0)
        val newVelocityX = velocityX + (0.5f * (xAcc + accX) * dT)
        val newVelocityY = velocityY + (0.5f * (yAcc + accY) * dT)

        // calculate distance traveled using EQUATION 2:
        // l = v0 * (t1 - t0) + 1/6 * (t1 - t0)^2 * (3*a0 + a1)
        val deltaX = velocityX * dT + (1f / 6f) * dT * dT * (3f * accX + xAcc)
        val deltaY = velocityY * dT + (1f / 6f) * dT * dT * (3f * accY + yAcc)

        // update position + velocity
        posX += deltaX
        posY += deltaY
        velocityX = newVelocityX
        velocityY = newVelocityY

        // store current acceleration for next iteration
        accX = xAcc
        accY = yAcc
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     * When it collides, velocity and acceleration perpendicular to the
     * boundary should be set to 0.
     */
    fun checkBoundaries() {
        // left boundary
        if (posX < 0) {
            posX = 0f
            velocityX = 0f
            accX = 0f
        }

        // right boundary
        if (posX + ballSize > backgroundWidth) {
            posX = backgroundWidth - ballSize
            velocityX = 0f
            accX = 0f
        }

        // top boundary
        if (posY < 0) {
            posY = 0f
            velocityY = 0f
            accY = 0f
        }

        // bottom boundary
        if (posY + ballSize > backgroundHeight) {
            posY = backgroundHeight - ballSize
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets the ball to the center of the screen with zero
     * velocity and acceleration.
     */
    fun reset() {
        // position ball to center screen
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f

        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f
        isFirstUpdate = true
    }
}