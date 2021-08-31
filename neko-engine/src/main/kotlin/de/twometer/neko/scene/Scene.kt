package de.twometer.neko.scene

import de.twometer.neko.scene.nodes.Node

class Scene {

    val backgroundColor = Color(0.25f, 0.25f, 0.25f, 1.0f)
    var ambientStrength = 0.25f
    val camera = Camera()
    val rootNode = Node(name = "SceneRoot")

}