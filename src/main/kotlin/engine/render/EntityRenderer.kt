package engine.render

import engine.entities.Entity
import engine.models.TextureModel
import engine.shaders.StaticShader
import engine.utils.MatrixUtil
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f

class EntityRenderer(val shader: StaticShader, projectionMatrix: Matrix4f) {

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(entities: Map<TextureModel, List<Entity>>) {
        entities.keys.forEach {
            prepareTexturedModel(it)
            val batch = entities[it]
            batch?.forEach {
                prepareInstance(it)
                GL11.glDrawElements(GL11.GL_TRIANGLES, it.model.rawModel.vertrixCount, GL11.GL_UNSIGNED_INT, 0)
            }
        }
        unbindTexturedModel()
    }

    fun prepareTexturedModel(model: TextureModel) {
        val rawModel = model.rawModel
        val texture = model.texture
        GL30.glBindVertexArray(rawModel.vaoID)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        if (texture.hasTransparency) {
            MasterRenderer.disableCulling()
        }
        shader.loadFakeLightingVariable(texture.useFakeLighting)
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture.textureId)
    }

    fun unbindTexturedModel() {
        MasterRenderer.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    fun prepareInstance(entity: Entity) {
        val transformationMatrix = MatrixUtil.createTransformationMatrix(
                entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale)
        shader.loadTransformationMatrix(transformationMatrix)
    }
}