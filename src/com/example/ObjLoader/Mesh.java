package com.example.ObjLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-2
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class Mesh {

    private Vertex[] vertexes;

    private Shader shader;

    private FloatBuffer vertexBuffer;

    ByteBuffer indexArray;

    public Mesh(Context context, String vertexShaderFileName, String fragmentShaderFileName) {
        shader = new Shader();
        shader.setProgram(getShaderString(context, vertexShaderFileName), getShaderString(context, fragmentShaderFileName));
    }

    public void setDrawOrderIndex(byte[] drawOrderIndex) {
        indexArray = ByteBuffer.allocateDirect(drawOrderIndex.length).put(drawOrderIndex);
    }

    private String getShaderString(Context context, String name) {
        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(name)));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    public void draw(float[] projectionMatrix) {
        if (vertexes == null) {
            return;
        }

        initBuffer();

        this.shader.useProgram();

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                0, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        indexArray.position(0);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, indexArray);
    }

    public void setVertexes(Vertex[] vertexes) {
        this.vertexes = vertexes;
        vertexBuffer = ByteBuffer.allocateDirect(3 * vertexes.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public void clear() {
        vertexes = null;
    }

    public boolean isClear() {
        return vertexes == null;
    }

    private void initBuffer() {
        this.vertexBuffer.clear();

        for (Vertex v : vertexes) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);
    }
}
