package de.twometer.orion.render.model;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BaseModel {

    private final String name;

    private final Transform transform;

    private boolean ignoreFilters;

    private Object tag;

    public BaseModel(String name) {
        this.name = name;
        this.transform = new Transform();
    }

    public abstract void render();

    public abstract Vector3f getCenter();

    public abstract Vector3f getMinimum();

    public abstract Vector3f getMaximum();

    public Vector3f getTransformedCenter() {
        return transform.transform(getCenter());
    }

    public Vector3f getSize() {
        Vector3f maximum = getMaximum();
        Vector3f minimum = getMinimum();
        return new Vector3f(maximum.x - minimum.x, maximum.y - minimum.y, maximum.z - minimum.z);
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Stream<BaseModel> streamTree() {
        var tree = new ArrayList<BaseModel>();
        traverseTree(tree::add);
        return tree.stream();
    }

    public void traverseTree(Consumer<BaseModel> consumer) {
        if (this instanceof CompositeModel) {
            var model = (CompositeModel) this;
            for (var child : model.getChildren())
                child.traverseTree(consumer);
        } else {
            consumer.accept(this);
        }
    }

    public boolean isIgnoreFilters() {
        return ignoreFilters;
    }

    public void setIgnoreFilters(boolean ignoreFilters) {
        this.ignoreFilters = ignoreFilters;
    }
}
