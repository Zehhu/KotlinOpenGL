package engine.entities

import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f

class Camera {

    val position = Vector3f(0f, 5f, 0f)
    var pitch = 10f
    var yaw = 0f
    var roll = 0f

    fun move() {

    }
}