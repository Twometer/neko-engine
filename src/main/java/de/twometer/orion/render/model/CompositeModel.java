package de.twometer.orion.render.model;

import org.joml.Vector3f;

import java.util.List;

public class CompositeModel extends BaseModel {

    private final List<BaseModel> children;

    private Vector3f minimum;
    private Vector3f maximum;
    private Vector3f center;

    public CompositeModel(String name, List<BaseModel> children) {
        super(name);
        this.children = children;
    }

    @Override
    public void render() {
        Transform parentTransform = getTransform();
        for (BaseModel child : children) {
            child.getTransform().set(parentTransform);
            child.render();
        }
    }

    @Override
    public Vector3f getCenter() {
        if (center == null) {
            float x = 0;
            float y = 0;
            float z = 0;

            for (BaseModel model : children) {
                Vector3f com = model.getCenter();
                x += com.x;
                y += com.y;
                z += com.z;
            }

            float num = children.size();
            center = new Vector3f(x / num, y / num, z / num);
        }
        return center;
    }

    @Override
    public Vector3f getMinimum() {
        if (minimum == null) {
            minimum = new Vector3f(9999999, 9999999, 9999999);
            for (BaseModel model : children) {
                var childMinimum = model.getMinimum();
                if (childMinimum.x < minimum.x) minimum.x = childMinimum.x;
                if (childMinimum.y < minimum.y) minimum.y = childMinimum.y;
                if (childMinimum.y < minimum.z) minimum.z = childMinimum.z;
            }
        }
        return minimum;
    }

    @Override
    public Vector3f getMaximum() {
        if (maximum == null) {
            maximum = new Vector3f(-9999999, -9999999, -9999999);
            for (BaseModel model : children) {
                var childMaximum = model.getMaximum();
                if (childMaximum.x > maximum.x) maximum.x = childMaximum.x;
                if (childMaximum.y > maximum.y) maximum.y = childMaximum.y;
                if (childMaximum.z > maximum.z) maximum.z = childMaximum.z;
            }
        }
        return maximum;
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        for (BaseModel child : children)
            child.setTag(tag);
    }

    public List<BaseModel> getChildren() {
        return children;
    }
}
