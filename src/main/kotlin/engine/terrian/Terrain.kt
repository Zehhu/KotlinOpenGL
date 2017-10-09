package engine.terrian

import engine.models.RawModel
import engine.render.Loader
import engine.textures.TerrainTexture
import engine.textures.TerrainTexturePack

class Terrain(gridX: Int, gridZ: Int, loader: Loader, val texture: TerrainTexturePack, val blendMap: TerrainTexture) {

    companion object {
        const val SIZE = 800f
        const val VERTEX_COUNT = 128
    }

    var x = 0f
        private set
    var z = 0f
        private set
    val model: RawModel

    init {
        x = gridX * SIZE
        z = gridZ * SIZE
        model = generateTerrain(loader)
    }

    private fun generateTerrain(loader: Loader): RawModel {
        val count = VERTEX_COUNT * VERTEX_COUNT
        val vertices = Array(count * 3, { 0f })
        val normals = Array(count * 3, { 0f })
        val textureCoords = Array(count * 2, { 0f })
        val indices = Array(6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1), { 0 })
        var vertexPointer = 0
        for (i in 0..VERTEX_COUNT - 1) {
            for (j in 0..VERTEX_COUNT - 1) {
                vertices[vertexPointer * 3] = j.toFloat() / (VERTEX_COUNT - 1) * SIZE
                vertices[vertexPointer * 3 + 1] = 0f
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (VERTEX_COUNT - 1) * SIZE
                normals[vertexPointer * 3] = 0f
                normals[vertexPointer * 3 + 1] = 1f
                normals[vertexPointer * 3 + 2] = 0f
                textureCoords[vertexPointer * 2] = j.toFloat() / (VERTEX_COUNT - 1)
                textureCoords[vertexPointer * 2 + 1] = i.toFloat() / (VERTEX_COUNT - 1)
                vertexPointer++
            }
        }
        var pointer = 0
        for (gz in 0..VERTEX_COUNT - 2) {
            for (gx in 0..VERTEX_COUNT - 2) {
                val topLeft = (gz * VERTEX_COUNT) + gx
                val topRight = topLeft + 1
                val bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx
                val bottomRight = bottomLeft + 1
                indices[pointer++] = topLeft
                indices[pointer++] = bottomLeft
                indices[pointer++] = topRight
                indices[pointer++] = topRight
                indices[pointer++] = bottomLeft
                indices[pointer++] = bottomRight
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices)
    }
}