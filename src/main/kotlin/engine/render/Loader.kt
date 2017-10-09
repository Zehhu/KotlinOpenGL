package engine.render

import engine.models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.newdawn.slick.opengl.Texture
import org.newdawn.slick.opengl.TextureLoader
import java.io.FileInputStream
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*

class Loader {

    private val vaos : ArrayList<Int> = ArrayList()
    private val vbos : ArrayList<Int> = ArrayList()
    private val textures : ArrayList<Int> = ArrayList()

    fun loadToVAO(positions : Array<Float>, textureCoords: Array<Float>, normals: Array<Float>, indices : Array<Int>) : RawModel {
        val id = bindVAO()
        bindIndicesBuffer(indices)
        storeDataInAttributeList(0, 3, positions)
        storeDataInAttributeList(1, 2, textureCoords)
        storeDataInAttributeList(2, 3, normals)
        unbindVAO()
        return RawModel(id, indices.size)
    }

    fun loadTexture(file : String) : Int {
        var texture : Texture? = null
        try {
            texture = TextureLoader.getTexture("PNG", FileInputStream("src/main/resources/textures/$file.png"))
        } catch (e : Exception) {
            e.printStackTrace()
        }
        val id = texture?.textureID ?: 0
        textures.add(id)
        return id
    }

    fun cleanUp() {
        vaos.forEach { GL30.glDeleteVertexArrays(it) }
        vbos.forEach { GL15.glDeleteBuffers(it) }
        textures.forEach { GL11.glDeleteTextures(it) }
        vaos.clear()
        vbos.clear()
        textures.clear()
    }

    private fun bindVAO() : Int {
        val id = GL30.glGenVertexArrays()
        vaos.add(id)
        GL30.glBindVertexArray(id)
        return id
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun bindIndicesBuffer(indices : Array<Int>) {
        val id = GL15.glGenBuffers()
        vbos.add(id)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id)
        val buffer = storeDataInIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    private fun storeDataInAttributeList(number : Int, coordinateSize : Int, data : Array<Float>) {
        val id = GL15.glGenBuffers()
        vbos.add(id)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
        val buffer = storeDataInFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(number, coordinateSize, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun storeDataInIntBuffer(data : Array<Int>) : IntBuffer {
        return BufferUtils.createIntBuffer(data.size).apply {
            put(data.toIntArray())
            flip()
        }
    }

    private fun storeDataInFloatBuffer(data: Array<Float>): FloatBuffer {
        return BufferUtils.createFloatBuffer(data.size).apply {
            put(data.toFloatArray())
            flip()
        }
    }
}