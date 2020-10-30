package de.twometer.neko.render.model;

import de.twometer.neko.render.shading.IShadingStrategy;
import org.joml.Vector3f;

public class ModelInstance extends ModelBase {

    private final ModelBase base;

    private IShadingStrategy shadingStrategy;

    public ModelInstance(ModelBase base) {
        super(base.getName());
        this.base = base;
    }

    @Override
    public void render() {
        base.overwriteShadingStrategy(shadingStrategy);
        base.getTransform().set(getTransform());
        base.render();
    }

    @Override
    public Vector3f getCenter() {
        return base.getCenter();
    }

    @Override
    public Vector3f getMinimum() {
        return base.getMinimum();
    }

    @Override
    public Vector3f getMaximum() {
        return base.getMaximum();
    }

    @Override
    public void overwriteShadingStrategy(IShadingStrategy shadingStrategy) {
        this.shadingStrategy = shadingStrategy;
    }

    @Override
    public Vector3f getTransformedCenter() {
        return base.getTransformedCenter();
    }

    @Override
    public Vector3f getSize() {
        return base.getSize();
    }


}
