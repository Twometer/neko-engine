package de.twometer.neko.gui

import de.twometer.neko.core.Window
import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw

object ImGuiHandler {

    private val glfw = ImGuiImplGlfw()
    private val gl3 = ImGuiImplGl3()

    fun setup(window: Window) {
        ImGui.createContext()

        val io = ImGui.getIO()
        io.fonts.addFontDefault()

        glfw.init(window.handle, true)
        gl3.init("#version 330")

        ImGuiTheme.apply()
    }

    fun wantsControl() = ImGui.getIO().wantCaptureMouse

    fun newFrame() {
        glfw.newFrame()
        ImGui.newFrame()
    }

    fun render() {
        ImGui.render()
        gl3.renderDrawData(ImGui.getDrawData())
    }

    fun shutdown() {
        glfw.dispose()
        gl3.dispose()
        ImGui.destroyContext()
    }

}