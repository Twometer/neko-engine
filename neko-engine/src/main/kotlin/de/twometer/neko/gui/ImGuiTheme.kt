package de.twometer.neko.gui

import imgui.ImGui
import imgui.flag.ImGuiCol

object ImGuiTheme {

    // "Corporate Gray" theme from https://github.com/ocornut/imgui/issues/707#issuecomment-468798935

    fun apply() {
        val style = ImGui.getStyle()

        val colors = style.colors
        colors[ImGuiCol.Text]                   = floatArrayOf(1.00f, 1.00f, 1.00f, 1.00f)
        colors[ImGuiCol.TextDisabled]           = floatArrayOf(0.40f, 0.40f, 0.40f, 1.00f)
        colors[ImGuiCol.ChildBg]                = floatArrayOf(0.25f, 0.25f, 0.25f, 1.00f)
        colors[ImGuiCol.WindowBg]               = floatArrayOf(0.25f, 0.25f, 0.25f, 1.00f)
        colors[ImGuiCol.PopupBg]                = floatArrayOf(0.25f, 0.25f, 0.25f, 1.00f)
        colors[ImGuiCol.Border]                 = floatArrayOf(0.12f, 0.12f, 0.12f, 0.71f)
        colors[ImGuiCol.BorderShadow]           = floatArrayOf(1.00f, 1.00f, 1.00f, 0.06f)
        colors[ImGuiCol.FrameBg]                = floatArrayOf(0.42f, 0.42f, 0.42f, 0.54f)
        colors[ImGuiCol.FrameBgHovered]         = floatArrayOf(0.42f, 0.42f, 0.42f, 0.40f)
        colors[ImGuiCol.FrameBgActive]          = floatArrayOf(0.56f, 0.56f, 0.56f, 0.67f)
        colors[ImGuiCol.TitleBg]                = floatArrayOf(0.19f, 0.19f, 0.19f, 1.00f)
        colors[ImGuiCol.TitleBgActive]          = floatArrayOf(0.22f, 0.22f, 0.22f, 1.00f)
        colors[ImGuiCol.TitleBgCollapsed]       = floatArrayOf(0.17f, 0.17f, 0.17f, 0.90f)
        colors[ImGuiCol.MenuBarBg]              = floatArrayOf(0.335f, 0.335f, 0.335f, 1.000f)
        colors[ImGuiCol.ScrollbarBg]            = floatArrayOf(0.24f, 0.24f, 0.24f, 0.53f)
        colors[ImGuiCol.ScrollbarGrab]          = floatArrayOf(0.41f, 0.41f, 0.41f, 1.00f)
        colors[ImGuiCol.ScrollbarGrabHovered]   = floatArrayOf(0.52f, 0.52f, 0.52f, 1.00f)
        colors[ImGuiCol.ScrollbarGrabActive]    = floatArrayOf(0.76f, 0.76f, 0.76f, 1.00f)
        colors[ImGuiCol.CheckMark]              = floatArrayOf(0.65f, 0.65f, 0.65f, 1.00f)
        colors[ImGuiCol.SliderGrab]             = floatArrayOf(0.52f, 0.52f, 0.52f, 1.00f)
        colors[ImGuiCol.SliderGrabActive]       = floatArrayOf(0.64f, 0.64f, 0.64f, 1.00f)
        colors[ImGuiCol.Button]                 = floatArrayOf(0.54f, 0.54f, 0.54f, 0.35f)
        colors[ImGuiCol.ButtonHovered]          = floatArrayOf(0.52f, 0.52f, 0.52f, 0.59f)
        colors[ImGuiCol.ButtonActive]           = floatArrayOf(0.76f, 0.76f, 0.76f, 1.00f)
        colors[ImGuiCol.Header]                 = floatArrayOf(0.38f, 0.38f, 0.38f, 1.00f)
        colors[ImGuiCol.HeaderHovered]          = floatArrayOf(0.47f, 0.47f, 0.47f, 1.00f)
        colors[ImGuiCol.HeaderActive]           = floatArrayOf(0.76f, 0.76f, 0.76f, 0.77f)
        colors[ImGuiCol.Separator]              = floatArrayOf(0.000f, 0.000f, 0.000f, 0.137f)
        colors[ImGuiCol.SeparatorHovered]       = floatArrayOf(0.700f, 0.671f, 0.600f, 0.290f)
        colors[ImGuiCol.SeparatorActive]        = floatArrayOf(0.702f, 0.671f, 0.600f, 0.674f)
        colors[ImGuiCol.ResizeGrip]             = floatArrayOf(0.26f, 0.59f, 0.98f, 0.25f)
        colors[ImGuiCol.ResizeGripHovered]      = floatArrayOf(0.26f, 0.59f, 0.98f, 0.67f)
        colors[ImGuiCol.ResizeGripActive]       = floatArrayOf(0.26f, 0.59f, 0.98f, 0.95f)
        colors[ImGuiCol.PlotLines]              = floatArrayOf(0.61f, 0.61f, 0.61f, 1.00f)
        colors[ImGuiCol.PlotLinesHovered]       = floatArrayOf(1.00f, 0.43f, 0.35f, 1.00f)
        colors[ImGuiCol.PlotHistogram]          = floatArrayOf(0.90f, 0.70f, 0.00f, 1.00f)
        colors[ImGuiCol.PlotHistogramHovered]   = floatArrayOf(1.00f, 0.60f, 0.00f, 1.00f)
        colors[ImGuiCol.TextSelectedBg]         = floatArrayOf(0.73f, 0.73f, 0.73f, 0.35f)
        colors[ImGuiCol.ModalWindowDimBg]       = floatArrayOf(0.80f, 0.80f, 0.80f, 0.35f)
        colors[ImGuiCol.DragDropTarget]         = floatArrayOf(1.00f, 1.00f, 0.00f, 0.90f)
        colors[ImGuiCol.NavHighlight]           = floatArrayOf(0.26f, 0.59f, 0.98f, 1.00f)
        colors[ImGuiCol.NavWindowingHighlight]  = floatArrayOf(1.00f, 1.00f, 1.00f, 0.70f)
        colors[ImGuiCol.NavWindowingDimBg]      = floatArrayOf(0.80f, 0.80f, 0.80f, 0.20f)
        ImGui.getStyle().colors = colors

        style.setWindowPadding(4.0f, 4.0f)
        style.setFramePadding(6.0f, 4.0f)
        style.setItemSpacing(6.0f, 2.0f)
        style.popupRounding = 3f
        style.scrollbarSize = 18f
        style.windowBorderSize = 1f
        style.childBorderSize  = 1f
        style.popupBorderSize  = 1f
        style.frameBorderSize  = 1f
        style.windowRounding    = 3f
        style.childRounding     = 3f
        style.frameRounding     = 3f
        style.scrollbarRounding = 2f
        style.grabRounding      = 3f
    }

}