package com.example.ObjLoader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glVertexAttribPointer;

public class MyActivity extends Activity implements GLSurfaceView.Renderer {

    private ObjLoader objLoader;

    private GLSurfaceView surfaceView;

    private int width, height;

    private float ratio;

    private Mesh mesh;

    //投影矩阵
    private float[] projectionMatrix = new float[16];

    //模型矩阵
    private float[] modelMatrix = new float[16];

    //视图矩阵
    private float[] viewMatrix = new float[16];

    //模型视图投影矩阵
    private float[] mvpMatrix = new float[16];

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        objLoader = new ObjLoader(this, "simple2");
        objLoader.load();

        surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);

        //设置背景透明
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(surfaceView);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        //照相机位置
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 37;

        //照相机拍照方向
        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -1.0f;

        //照相机的垂直方向
        float upX = 0f;
        float upY = 1f;
        float upZ = 0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        mesh = new Mesh(this, "vertex_shader.glsl", "fragment_shader.glsl");

        mesh.setVertexes(objLoader.getVertexes());
        mesh.setDrawOrderIndex(objLoader.getDrawOrderIndex());


    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);
        ratio = width / (float) height;

        float left = -ratio;
        float right = ratio;
        float top = 1;
        float bottom = -1;
        float near = 7;
        float far = 100;
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        try {
            Bitmap texture= BitmapFactory.decodeStream(getAssets().open("h.png"));
            mesh.loadTexture(texture);
            texture.recycle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        glClear(GL_COLOR_BUFFER_BIT);

        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        mesh.setTextureCoord(objLoader.getTextureCoodsArray());

        mesh.draw(mvpMatrix);
    }
}
