package engine.render

import engine.entities.Camera
import engine.entities.Entity
import engine.entities.Light
import engine.models.TextureModel
import engine.shaders.StaticShader
import engine.shaders.TerrainShader
import engine.terrian.Terrain
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import java.util.*

class MasterRenderer {

    companion object {
        const val FOV = 70f
        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 1000f
        const val RED = 0.5f
        const val GREEN = 0.5f
        const val BLUE = 0.5f

        fun enableCulling() {
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glCullFace(GL11.GL_BACK)
        }

        fun disableCulling() {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }

    private val shader = StaticShader()
    private val terrainShader = TerrainShader()
    private val renderer : EntityRenderer
    private val terrainRenderer: TerrainRenderer
    private val entities = HashMap<TextureModel, MutableList<Entity>>()
    private val terrains = ArrayList<Terrain>()
    private val projectionMatrix: Matrix4f

    init {
        enableCulling()
        projectionMatrix = creationProjectionMatrix()
        renderer = EntityRenderer(shader, projectionMatrix)
        terrainRenderer = TerrainRenderer(terrainShader, projectionMatrix)
    }

    fun render(sun: Light, camera: Camera) {
        prepare()
        shader.start()
        shader.loadSkyColor(RED, GREEN, BLUE)
        shader.loadLight(sun)
        shader.loadViewMatrix(camera)
        renderer.render(entities)
        shader.stop()
        terrainShader.start()
        terrainShader.loadSkyColor(RED, GREEN, BLUE)
        terrainShader.loadLight(sun)
        terrainShader.loadViewMatrix(camera)
        terrainRenderer.render(terrains)
        terrainShader.stop()
        terrains.clear()
        entities.clear()
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT.xor(GL11.GL_DEPTH_BUFFER_BIT))
        GL11.glClearColor(RED, BLUE, GREEN, 1f)
    }

    fun processTerrain(terrain: Terrain) {
        terrains.add(terrain)
    }

    fun processEntity(entity: Entity) {
        val entityModel = entity.model
        val batch = entities[entityModel]
        if (batch != null) {
            batch.add(entity)
        } else {
            val newBatch = ArrayList<Entity>()
            newBatch.add(entity)
            entities.put(entityModel, newBatch)
        }
    }

    fun cleanUp() {
        shader.cleanUp()
        terrainShader.cleanUp()
    }

    private fun creationProjectionMatrix() : Matrix4f {
        val aspectRatio = Display.getWidth().toFloat() / Display.getHeight().toFloat()
        val yScale = ((1f / Math.tan(Math.toRadians((FOV / 2f).toDouble()))) * aspectRatio).toFloat()
        val xScale = yScale / aspectRatio
        val frustumLength = FAR_PLANE - NEAR_PLANE

        return Matrix4f().apply {
            m00 = xScale
            m11 = yScale
            m22 = - ((FAR_PLANE + NEAR_PLANE) / frustumLength)
            m23 = -1f
            m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength)
            m33 = 0f
        }
    }
}