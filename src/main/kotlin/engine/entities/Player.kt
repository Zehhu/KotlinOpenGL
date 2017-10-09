package engine.entities

import engine.models.TextureModel
import engine.render.DisplayManager
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f

class Player(model: TextureModel, position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) : Entity(model, position, rotX, rotY, rotZ, scale) {

    companion object {
        const val RUN_SPEED = 20f
        const val TURN_SPEED = 160f
    }

    private var currentSpeed = 0f
    private var currentTurnSpeed = 0f

    fun move() {
        checkInputs()
        super.increaseRotation(0f, currentTurnSpeed * DisplayManager.delta, 0f)
        val distance = currentSpeed * DisplayManager.delta
    }

    private fun checkInputs() {
        currentSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_W) -> RUN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_S) -> -RUN_SPEED
            else -> 0f
        }

        currentTurnSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_D) -> TURN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_A) -> -TURN_SPEED
            else -> 0f
        }
    }
}