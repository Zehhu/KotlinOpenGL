package engine.render

import engine.shaders.TerrainShader
import engine.terrian.Terrain
import engine.utils.MatrixUtil
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f

class TerrainRenderer(val shader: TerrainShader, projectionMatrix: Matrix4f) {

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.connectTextureUnits()
        shader.stop()
    }

    fun render(terrains: List<Terrain>) {
        terrains.forEach {
            prepareTerrain(it)
            loadModelMatrix(it)
            GL11.glDrawElements(GL11.GL_TRIANGLES, it.model.vertrixCount, GL11.GL_UNSIGNED_INT, 0)
            unbindTexturedModel()
        }
    }

    fun prepareTerrain(terrain: Terrain) {
        val rawModel = terrain.model
        val texture = terrain.texture
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        bindTextures(terrain)
        shader.loadShineVariables(1f, 0f)
    }

    fun bindTextures(terrain: Terrain) {
        val texturePack = terrain.texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.background.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.rTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.gTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE4)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap.id)

    }

    fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    fun loadModelMatrix(terrain: Terrain) {
        val transformationMatrix = MatrixUtil.createTransformationMatrix(Vector3f(terrain.x, 0f, terrain.z), 0f, 0f, 0f, 1f)
        shader.loadTransformationMatrix(transformationMatrix)
    }

}