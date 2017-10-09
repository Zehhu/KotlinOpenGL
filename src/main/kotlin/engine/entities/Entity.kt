package engine.entities

import engine.models.TextureModel
import org.lwjgl.util.vector.Vector3f

open class Entity(val model: TextureModel, val position: Vector3f, var rotX: Float, var rotY: Float, var rotZ: Float, var scale: Float ) {

    fun increasePosition(dx : Float, dy: Float, dz: Float) {
        position.apply{
            x += dx
            y += dy
            z += dz
        }
    }

    fun increaseRotation(dx: Float, dy: Float, dz: Float) {
        rotX += dx
        rotY += dy
        rotZ += dz
    }

}