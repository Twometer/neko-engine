package de.twometer.neko.player

import de.twometer.neko.scene.AABB
import de.twometer.neko.scene.Scene
import de.twometer.neko.scene.nodes.Geometry
import de.twometer.neko.scene.nodes.Node
import de.twometer.neko.util.MathF.max
import de.twometer.neko.util.MathF.min
import org.joml.Vector3f

class PickEngine(private val scene: Scene) {

    var maxDistance = 3.5f
    private val maxDistanceSq get() = maxDistance * maxDistance

    fun pick(): Node? {
        val ray = Vector3f(scene.camera.position)
        val direction = Vector3f(scene.camera.direction)
        val nodes = gatherNodes()

        return getIntersectingNode(nodes, ray, direction)?.geom
    }

    private fun gatherNodes(): List<NodeReference> {
        val nodes = ArrayList<NodeReference>()
        scene.rootNode.scanTree {
            if (it is Geometry && it.aabb != null && it.canPick) {
                val transformedAABB = it.aabb!!.transform(it.compositeTransform.matrix)
                val distSq = scene.camera.position.distanceSquared(transformedAABB.center)
                if (distSq <= maxDistanceSq)
                    nodes.add(NodeReference(it, transformedAABB, distSq))
            }
        }
        return nodes.sortedBy { it.distSq }
    }

    private fun getIntersectingNode(nodes: List<NodeReference>, ray: Vector3f, direction: Vector3f): NodeReference? {
        // "Slab" ray intersection test
        return nodes.firstOrNull {
            val box = it.transformedAABB
            var tMin = -1E9f
            var tMax = 1E9f
            if (direction.x != 0f) {
                val tx1 = (box.min.x - ray.x) / direction.x
                val tx2 = (box.max.x - ray.x) / direction.x
                tMin = max(tMin, min(tx1, tx2))
                tMax = min(tMax, max(tx1, tx2))
            }
            if (direction.y != 0f) {
                val ty1 = (box.min.y - ray.y) / direction.y
                val ty2 = (box.max.y - ray.y) / direction.y
                tMin = max(tMin, min(ty1, ty2))
                tMax = min(tMax, max(ty1, ty2))
            }
            if (direction.z != 0f) {
                val tz1 = (box.min.z - ray.z) / direction.z
                val tz2 = (box.max.z - ray.z) / direction.z
                tMin = max(tMin, min(tz1, tz2))
                tMax = min(tMax, max(tz1, tz2))
            }

            tMax >= tMin
        }
    }

    private class NodeReference(val geom: Geometry, val transformedAABB: AABB, val distSq: Float)

}