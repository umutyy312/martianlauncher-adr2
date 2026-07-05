package com.foxstudio.martianlauncher.game.renderer.renderers

import com.foxstudio.martianlauncher.game.renderer.RendererInterface

object LTWRenderer : RendererInterface {
    override fun getRendererId(): String = "ltw"

    override fun getUniqueIdentifier(): String = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

    override fun getRendererName(): String = "LTW (Large Thin Wrapper)"

    override fun getRendererSummary(): String? = "A thin OpenGL core-to-OpenGL ES wrapper, optimized for Minecraft 1.17+"

    override fun getMinMCVersion(): String? = "1.17"

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy { emptyMap() }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libltw.so"
}
