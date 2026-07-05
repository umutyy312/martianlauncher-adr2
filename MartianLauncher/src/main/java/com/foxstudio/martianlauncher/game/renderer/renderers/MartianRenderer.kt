package com.foxstudio.martianlauncher.game.renderer.renderers

import com.foxstudio.martianlauncher.game.renderer.RendererInterface

object MartianRenderer : RendererInterface {
    override fun getRendererId(): String = "martian"

    override fun getUniqueIdentifier(): String = "m4rt1an-0000-0000-0000-000000000000"

    override fun getRendererName(): String = "Martian Renderer (Beta)"

    override fun getRendererSummary(): String? = "Render Engine Martian Launcher"

    override fun getMinMCVersion(): String? = null

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        buildMap {
            put("LIBGL_USE_MC_COLOR", "1")
            put("LIBGL_GL", "31")
            put("LIBGL_ES", "3")
            put("LIBGL_NORMALIZE", "1")
            put("LIBGL_NOERROR", "1")
        }
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libmartian.so"
}
