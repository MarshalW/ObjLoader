package com.example.ObjLoader;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-28
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class Vertex {
    float x, y, z;

    public float[] getPosition() {
        return new float[]{
                x, y, z
        };
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
