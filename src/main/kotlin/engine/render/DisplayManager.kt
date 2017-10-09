package engine.render

import org.lwjgl.Sys
import org.lwjgl.opengl.*

object DisplayManager {

    const val WIDTH = 1280
    const val HEIGHT = 720
    const val FPS_CAP = 120

    private var lastFrameTime: Long = 0L
    var delta: Float = 0f
        private set

    fun createDisplay() {
        val attribs = ContextAttribs(3, 3)
                .withForwardCompatible(true)
                .withProfileCore(true)

        Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
        Display.create(PixelFormat(), attribs)

        GL11.glViewport(0, 0, WIDTH, HEIGHT)
    }

    fun updateDisplay() {
        Display.sync(FPS_CAP)
        Display.update()
        val currentFrameTime = getCurrentTime()
        delta = (currentFrameTime - lastFrameTime)/1000f
        lastFrameTime = currentFrameTime
    }

    fun closeDisplay() {
        Display.destroy()
    }

    fun getCurrentTime(): Long {
        return Sys.getTime() * 1000 / Sys.getTimerResolution()
    }

}