import engine.entities.Camera
import engine.entities.Entity
import engine.entities.Light
import engine.models.TextureModel
import engine.render.DisplayManager
import engine.render.Loader
import engine.render.MasterRenderer
import engine.render.ObjLoader
import engine.terrian.Terrain
import engine.textures.ModelTexture
import engine.textures.TerrainTexture
import engine.textures.TerrainTexturePack
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import java.util.*


object Application {

    @JvmStatic
    fun main(args : Array<String>) {
        DisplayManager.createDisplay()

        val loader = Loader()
        val renderer = MasterRenderer()

        val light = Light(Vector3f(10f, 10f, 10f), Vector3f(1f, 1f, 1f))
        val camera = Camera()

        val backgroundTexture = TerrainTexture(loader.loadTexture("grassy2"))
        val rTexture = TerrainTexture(loader.loadTexture("mud"))
        val gTexture = TerrainTexture(loader.loadTexture("grassFlowers"))
        val bTexture = TerrainTexture(loader.loadTexture("path"))

        val texturePack = TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture)
        val blendMap = TerrainTexture(loader.loadTexture("blendMap"))

        val terrain = Terrain(0, -1, loader, texturePack, blendMap)
        val terrain2 = Terrain(-1, -1, loader, texturePack, blendMap)


        val entities = ArrayList<Entity>()
        val random = Random()
        val model = ObjLoader.loadObjModel("grassModel", loader)
        val staticModel = TextureModel(model, ModelTexture(loader.loadTexture("grassTexture")))
        staticModel.texture.hasTransparency = true
        staticModel.texture.useFakeLighting = true
        for (i in 0..99) {
            entities.add(Entity(staticModel, Vector3f(random.nextFloat() * 800 - 400, 0f, random.nextFloat() * -600), 0f, 0f, 0f, 3f))
        }

        val model2 = ObjLoader.loadObjModel("fern", loader)
        val staticModel2 = TextureModel(model2, ModelTexture(loader.loadTexture("fern")))
        staticModel2.texture.hasTransparency = true
        for (i in 0..20) {
            entities.add(Entity(staticModel2, Vector3f(random.nextFloat() * 800 - 400, 0f, random.nextFloat() * -600), 0f, 0f, 0f, 3f))
        }

        val staticModel3 = TextureModel(model, ModelTexture(loader.loadTexture("flower")))
        staticModel.texture.hasTransparency = true
        staticModel.texture.useFakeLighting = true
        for (i in 0..99) {
            entities.add(Entity(staticModel3, Vector3f(random.nextFloat() * 800 - 400, 0f, random.nextFloat() * -600), 0f, 0f, 0f, 3f))
        }

        while (!Display.isCloseRequested()) {
            camera.move()
            renderer.processTerrain(terrain)
            renderer.processTerrain(terrain2)
            entities.forEach { renderer.processEntity(it) }
            renderer.render(light, camera)
            DisplayManager.updateDisplay()
        }

        renderer.cleanUp()
        loader.cleanUp()
        DisplayManager.closeDisplay()
    }

}