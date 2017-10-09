package engine.shaders

import engine.entities.Camera
import engine.entities.Light
import engine.utils.MatrixUtil
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f

class TerrainShader : ShaderProgram {

    var locationTransformationMatrix: Int = 0
    var locationProjectionMatrix: Int = 0
    var locationViewMatrix: Int = 0
    var locationLightPosition: Int = 0
    var locationLightColor: Int = 0
    var locationShineDamper: Int = 0
    var locationReflectivity: Int = 0
    var locationSkyColor: Int = 0
    var locationBackgroundTexture: Int = 0
    var locationRTexture: Int = 0
    var locationGTexture: Int = 0
    var locationBTexture: Int = 0
    var locationBlendMap: Int = 0


    companion object {
        const val VERTEX_FILE = "terrainVertexShader.txt"
        const val FRAGMENT_FILE = "terrainFragmentShader.txt"
    }

    constructor() : super(VERTEX_FILE, FRAGMENT_FILE)

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")
        super.bindAttribute(2, "normal")
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix")
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix")
        locationViewMatrix = super.getUniformLocation("viewMatrix")
        locationLightPosition = super.getUniformLocation("lightPosition")
        locationLightColor = super.getUniformLocation("lightColor")
        locationShineDamper = super.getUniformLocation("shineDamper")
        locationReflectivity = super.getUniformLocation("reflectivity")
        locationSkyColor = super.getUniformLocation("skyColor")
        locationBackgroundTexture = super.getUniformLocation("backgroundTexture")
        locationRTexture = super.getUniformLocation("rTexture")
        locationGTexture = super.getUniformLocation("gTexture")
        locationBTexture = super.getUniformLocation("bTexture")
        locationBlendMap = super.getUniformLocation("blendMap")
    }

    fun loadTransformationMatrix(matrix4f: Matrix4f) {
        super.loadMatrix(locationTransformationMatrix, matrix4f)
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        super.loadMatrix(locationProjectionMatrix, projection)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = MatrixUtil.createViewMatrix(camera)
        super.loadMatrix(locationViewMatrix, viewMatrix)
    }

    fun loadLight(light: Light) {
        super.loadVector(locationLightPosition, light.position)
        super.loadVector(locationLightColor, light.color)
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        super.loadFloat(locationShineDamper, damper)
        super.loadFloat(locationReflectivity, reflectivity)
    }

    fun loadSkyColor(r: Float, g: Float, b: Float) {
        super.loadVector(locationSkyColor, Vector3f(r, g, b))
    }

    fun connectTextureUnits() {
        super.loadInt(locationBackgroundTexture, 0)
        super.loadInt(locationRTexture, 1)
        super.loadInt(locationGTexture, 2)
        super.loadInt(locationBTexture, 3)
        super.loadInt(locationBlendMap, 4)
    }
}