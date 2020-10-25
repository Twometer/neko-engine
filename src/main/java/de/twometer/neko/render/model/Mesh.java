package de.twometer.neko.render.model;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Mesh {

    private final int vertexCapacity;

    private final int dimensions;

    private final FloatBuffer vertices;

    private FloatBuffer colors;

    private FloatBuffer normals;

    private FloatBuffer texCoords;

    private int vertexCount;

    private int colorCount;

    private int normalCount;

    private int texCoordCount;

    private Mesh(int vertexCapacity, int dimensions) {
        this.vertexCapacity = vertexCapacity;
        this.dimensions = dimensions;
        vertices = MemoryUtil.memAllocFloat(vertexCapacity * dimensions);
    }

    public static Mesh create(int vertexCapacity, int dimensions) {
        return new Mesh(vertexCapacity, dimensions);
    }

    public Mesh withColors() {
        colors = MemoryUtil.memAllocFloat(vertexCapacity * 3);
        return this;
    }

    public Mesh withNormals() {
        normals = MemoryUtil.memAllocFloat(vertexCapacity * dimensions);
        return this;
    }

    public Mesh withTexCoords() {
        texCoords = MemoryUtil.memAllocFloat(vertexCapacity * 2);
        return this;
    }

    public Mesh putVertex(float x, float y, float z) {
        vertices.put(x);
        vertices.put(y);
        vertices.put(z);
        vertexCount++;
        return this;
    }

    public Mesh putVertex(float x, float y) {
        vertices.put(x);
        vertices.put(y);
        vertexCount++;
        return this;
    }

    public Mesh putColor(float r, float g, float b) {
        colors.put(r);
        colors.put(g);
        colors.put(b);
        colorCount++;
        return this;
    }

    public Mesh putNormal(float x, float y, float z) {
        normals.put(x);
        normals.put(y);
        normals.put(z);
        normalCount++;
        return this;
    }

    public Mesh putNormal(float x, float y) {
        normals.put(x);
        normals.put(y);
        normalCount++;
        return this;
    }

    public Mesh putTexCoord(float u, float v) {
        texCoords.put(u);
        texCoords.put(v);
        texCoordCount++;
        return this;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    int getColorCount() {
        return colorCount;
    }

    int getTexCoordCount() {
        return texCoordCount;
    }

    int getNormalCount() {
        return normalCount;
    }

    FloatBuffer getVertices() {
        return vertices;
    }

    FloatBuffer getColors() {
        return colors;
    }

    FloatBuffer getTexCoords() {
        return texCoords;
    }

    FloatBuffer getNormals() {
        return normals;
    }

    public void destroy() {
        MemoryUtil.memFree(vertices);
        if (colors != null) MemoryUtil.memFree(colors);
        if (normals != null) MemoryUtil.memFree(normals);
        if (texCoords != null) MemoryUtil.memFree(texCoords);
    }

    int getDimensions() {
        return dimensions;
    }

    public ModelPart bake(String modelName, int primitiveType) {
        ModelPart model = ModelPart.create(modelName, this, primitiveType);
        destroy();
        return model;
    }

}
