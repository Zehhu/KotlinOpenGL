package engine.shaders

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_VERSION
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.FloatBuffer


abstract class ShaderProgram(vertexFile: String, fragmentFile: String) {

    private val programId: Int = GL20.glCreateProgram()
    private val vertexShaderId: Int
    private val fragmentShaderId: Int

    companion object {

        val matrixBuffer: FloatBuffer = BufferUtils.createFloatBuffer(16)

        fun loadShader(file: String, type: Int): Int {
            val shaderSource = StringBuilder()
            try {
                val reader = BufferedReader(FileReader("src/main/resources/shaders/$file"))
                reader.forEachLine { shaderSource.append(it).append("//\n") }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
                System.exit(-1)
            }
            val shaderID = GL20.glCreateShader(type)
            GL20.glShaderSource(shaderID, shaderSource)
            GL20.glCompileShader(shaderID)
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.out.println(GL20.glGetShaderInfoLog(shaderID, 500))
                val version = GL11.glGetString( GL_VERSION )
                System.err.println("Could not compile shader for file <$file> using $version!")
                System.exit(-1)
            }
            return shaderID
        }
    }

    init {
        vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)
        bindAttributes()
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)
        getAllUniformLocations()
    }

    fun start() {
        GL20.glUseProgram(programId)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun cleanUp() {
        stop()
        GL20.glDetachShader(programId, vertexShaderId)
        GL20.glDetachShader(programId, fragmentShaderId)
        GL20.glDeleteShader(vertexShaderId)
        GL20.glDeleteShader(fragmentShaderId)
        GL20.glDeleteProgram(programId)
    }

    fun bindAttribute(attribute : Int, variableName : String) {
        GL20.glBindAttribLocation(programId, attribute, variableName)
    }

    abstract fun getAllUniformLocations()

    abstract fun bindAttributes()

    protected fun getUniformLocation(uniformName: String) : Int {
        return GL20.glGetUniformLocation(programId, uniformName)
    }

    protected fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected fun loadVector(location: Int, vector3f: Vector3f) {
        GL20.glUniform3f(location, vector3f.x, vector3f.y, vector3f.z)
    }

    protected fun loadBoolean(location: Int, value: Boolean) {
        GL20.glUniform1f(location, if (value) 1f else 0f)
    }

    protected fun loadMatrix(location: Int, matrix: Matrix4f) {
        matrix.store(matrixBuffer)
        matrixBuffer.flip()
        GL20.glUniformMatrix4(location, false, matrixBuffer)
    }

    protected fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

}