package de.twometer.orion.render.model;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class SimpleModel extends BaseModel {

    private final int vao;

    private final int vertexBuffer;
    private final int colorBuffer;
    private final int normalBuffer;
    private final int texCoordBuffer;

    private final int dimensions;

    private final int vertices;

    private final int primitiveType;

    private final Vector3f minimum;
    private final Vector3f maximum;
    private final Vector3f centerOfMass;

    private Material material;

    private SimpleModel(String name, int vao, int vertexBuffer, int colorBuffer, int normalBuffer, int texCoordBuffer, int dimensions, int vertices, int primitiveType, Vector3f min, Vector3f max, Vector3f com) {
        super(name);
        this.vao = vao;
        this.vertexBuffer = vertexBuffer;
        this.colorBuffer = colorBuffer;
        this.normalBuffer = normalBuffer;
        this.texCoordBuffer = texCoordBuffer;
        this.dimensions = dimensions;
        this.vertices = vertices;
        this.primitiveType = primitiveType;
        this.minimum = min;
        this.maximum = max;
        this.centerOfMass = com;
    }

    public static BaseModel create(String name, Mesh mesh, int primitiveType) {
        int dimensions = mesh.getDimensions();

        mesh.getVertices().flip();
        if (mesh.getColorCount() > 0) mesh.getColors().flip();
        if (mesh.getNormalCount() > 0) mesh.getNormals().flip();
        if (mesh.getTexCoordCount() > 0) mesh.getTexCoords().flip();

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.getVertices(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, dimensions, GL_FLOAT, false, 0, 0);

        int colorBuffer = -1;
        if (mesh.getColorCount() > 0) {
            colorBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getColors(), GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        }

        int normalBuffer = -1;
        if (mesh.getNormalCount() > 0) {
            normalBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getNormals(), GL_STATIC_DRAW);
            glVertexAttribPointer(2, dimensions, GL_FLOAT, false, 0, 0);
        }

        int texCoordBuffer = -1;
        if (mesh.getTexCoordCount() > 0) {
            texCoordBuffer = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
            glBufferData(GL_ARRAY_BUFFER, mesh.getTexCoords(), GL_STATIC_DRAW);
            glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);


        Vector3f min = new Vector3f(9999999, 9999999, 9999999);
        Vector3f max = new Vector3f(-9999999, -9999999, -9999999);
        Vector3f com = new Vector3f();

        if (dimensions == 3) {
            for (int i = 0; i < mesh.getVertexCount(); i++) {
                int bi = i * 3;
                float x = mesh.getVertices().get(bi);
                float y = mesh.getVertices().get(bi + 1);
                float z = mesh.getVertices().get(bi + 2);

                if (x < min.x) min.x = x;
                if (y < min.y) min.y = y;
                if (z < min.z) min.z = z;

                if (x > max.x) max.x = x;
                if (y > max.y) max.y = y;
                if (z > max.z) max.z = z;
            }


            com.x = (min.x + max.x) / 2;
            com.y = (min.y + max.y) / 2;
            com.z = (min.z + max.z) / 2;

        }

        return new SimpleModel(name, vao, vertexBuffer, colorBuffer, normalBuffer, texCoordBuffer, dimensions, mesh.getVertexCount(), primitiveType, min, max, com);
    }

    public void destroy() {
        glDeleteBuffers(vertexBuffer);
        glDeleteBuffers(colorBuffer);
        glDeleteBuffers(normalBuffer);
        glDeleteBuffers(texCoordBuffer);
        glDeleteVertexArrays(vao);
    }

    @Override
    public void render() {
        if (dimensions == 3)
            return;

        boolean hasColors = colorBuffer != -1;
        boolean hasNormals = normalBuffer != -1;
        boolean hasTextures = texCoordBuffer != -1;

        glBindVertexArray(vao);

        glEnableVertexAttribArray(0);
        if (hasColors) glEnableVertexAttribArray(1);
        if (hasNormals) glEnableVertexAttribArray(2);
        if (hasTextures) glEnableVertexAttribArray(3);

        glDrawArrays(primitiveType, 0, vertices);

        if (hasTextures) glDisableVertexAttribArray(3);
        if (hasNormals) glDisableVertexAttribArray(2);
        if (hasColors) glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    @Override
    public Vector3f getCenter() {
        return centerOfMass;
    }

    @Override
    public Vector3f getMinimum() {
        return minimum;
    }

    @Override
    public Vector3f getMaximum() {
        return maximum;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
