package engine.render

import engine.models.RawModel
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

object ObjLoader {

    fun loadObjModel(fileName: String, loader: Loader): RawModel {
        val fileReader = FileReader(File("src/main/resources/models/$fileName.obj"))
        val reader = BufferedReader(fileReader)
        val vertices = ArrayList<Vector3f>()
        val textures = ArrayList<Vector2f>()
        val normals = ArrayList<Vector3f>()
        val indices = ArrayList<Int>()

        var texturesArray : Array<Float>? = null
        var normalsArray : Array<Float>? = null

        var line = reader.readLine()
        while (line != null) {
            val currentLine = line.split(" ")
            when (currentLine[0]) {
                "v" -> {
                    val vertex = Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat())
                    vertices.add(vertex)
                }
                "vt" -> {
                    val texture = Vector2f(currentLine[1].toFloat(), currentLine[2].toFloat())
                    textures.add(texture)
                }
                "vn" -> {
                    val vertex = Vector3f(currentLine[1].toFloat(), currentLine[2].toFloat(), currentLine[3].toFloat())
                    normals.add(vertex)
                }
                "f" -> {
                    if (texturesArray == null) {
                        texturesArray = Array(vertices.size * 2, { i -> 0f })
                    }
                    if (normalsArray == null) {
                        normalsArray = Array(vertices.size * 3, { i -> 0f })
                    }
                    if (textures.isEmpty()) {
                        textures.add(Vector2f(0f, 0f))
                        textures.add(Vector2f(0f, 1f))
                        textures.add(Vector2f(1f, 0f))
                    }

                    val vertex1 = currentLine[1].split("/")
                    val vertex2 = currentLine[2].split("/")
                    val vertex3 = currentLine[3].split("/")

                    val cleanVertex1 = Array(vertex1.size, { idx -> if (vertex1[idx].isEmpty()) "1" else vertex1[idx] })
                    val cleanVertex2 = Array(vertex2.size, { idx -> if (vertex2[idx].isEmpty()) "2" else vertex2[idx] })
                    val cleanVertex3 = Array(vertex3.size, { idx -> if (vertex3[idx].isEmpty()) "3" else vertex3[idx] })

                    processVertex(cleanVertex1, indices, textures, normals, texturesArray, normalsArray)
                    processVertex(cleanVertex2, indices, textures, normals, texturesArray, normalsArray)
                    processVertex(cleanVertex3, indices, textures, normals, texturesArray, normalsArray)
                }
                else -> System.out.println("Command <${currentLine[0]}> not supported for obj format.")
            }
            line = reader.readLine()
        }
        reader.close()

        val verticesArray = Array(vertices.size * 3, { 0f })
        val indicesArray = Array(indices.size, { idx -> indices[idx] })

        var vertexPointer = 0
        vertices.forEach {
            verticesArray[vertexPointer++] = it.x
            verticesArray[vertexPointer++] = it.y
            verticesArray[vertexPointer++] = it.z
        }

        if (texturesArray != null && normalsArray != null)
            return loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray)
        throw UnsupportedOperationException("Haven't built support for obj without textures or normals!")
    }

    fun processVertex(vertexData: Array<String>, indices: ArrayList<Int>, textures: ArrayList<Vector2f>, normals: ArrayList<Vector3f>, textureArray: Array<Float>, normalsArray: Array<Float>) {
        val currentVertexPointer = vertexData[0].toInt() - 1
        indices.add(currentVertexPointer)

        val texData = if (vertexData[1].isEmpty()) 0 else vertexData[1].toInt() - 1
        val currentTex = textures[texData]
        textureArray[currentVertexPointer*2] = currentTex.x
        textureArray[currentVertexPointer*2+1] = 1 - currentTex.y

        val currentNorm = normals[vertexData[2].toInt() - 1]
        normalsArray[currentVertexPointer*3] = currentNorm.x
        normalsArray[currentVertexPointer*3+1] = currentNorm.y
        normalsArray[currentVertexPointer*3+2] = currentNorm.z
    }
}