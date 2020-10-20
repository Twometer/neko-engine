package de.twometer.orion.render.filter;

import de.twometer.orion.core.OrionApp;
import de.twometer.orion.render.Camera;
import de.twometer.orion.render.model.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FrustumCullingFilter implements IModelFilter {

    private final Matrix4f projViewMatrix = new Matrix4f();
    private final Vector4f[] frustumPlanes = new Vector4f[6];

    public FrustumCullingFilter() {
        for (int i = 0; i < 6; i++) {
            frustumPlanes[i] = new Vector4f();
        }
    }

    @Override
    public boolean shouldRender(ModelPart part) {
        return insideFrustum(part.getCenter(), part.getSize().length());
    }

    @Override
    public void update() {
        var camera = OrionApp.get().getCamera();
        if (!camera.ready())
            return;

        projViewMatrix.set(camera.getProjectionMatrix());
        projViewMatrix.mul(camera.getViewMatrix());

        for (int i = 0; i < 6; i++)
            projViewMatrix.frustumPlane(i, frustumPlanes[i]);
    }

    private boolean insideFrustum(Vector3f vec, float radius) {
        float x0 = vec.x;
        float y0 = vec.y;
        float z0 = vec.z;
        for (int i = 0; i < 6; i++) {
            Vector4f plane = frustumPlanes[i];
            if (plane.x * x0 + plane.y * y0 + plane.z * z0 + plane.w <= -radius) {
                return false;
            }
        }
        return true;
    }
}
