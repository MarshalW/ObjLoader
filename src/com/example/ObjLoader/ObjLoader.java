package com.example.ObjLoader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-28
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class ObjLoader {

    Map<String, PrefixCallback> callbackMap = new HashMap<String, PrefixCallback>();

    PrefixCallback[] prefixCallbacks = new PrefixCallback[]{
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "#";
                }

                @Override
                public void callback(String[] content) {
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "v";
                }

                @Override
                public void callback(String[] content) {
                    Vertex vertex = new Vertex();
                    vertex.x = Float.parseFloat(content[1]);
                    vertex.y = Float.parseFloat(content[2]);
                    vertex.z = Float.parseFloat(content[3]);
                    vertexs.add(vertex);
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "vt";
                }

                @Override
                public void callback(String[] content) {
                    Vertex vertex = new Vertex();
                    vertex.x = Float.parseFloat(content[1]);
                    vertex.y = Float.parseFloat(content[2]);
                    textureCoods.add(vertex);
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "f";
                }

                @Override
                public void callback(String[] content) {
                    for (int i = 1; i < content.length; i++) {
                        String s = content[i];
                        String[] data = s.split("/");
                        if (data.length > 0) {
                            vertexIndexes.add((short) (Short.parseShort(data[0]) - 1));
                        }
                        if (data.length > 1) {
                            textureIndexes.add((short) (Short.parseShort(data[1]) - 1));
                        }

                    }
                }
            },
    };

    Context context;

    String filePrefix;

    List<Vertex> vertexs = new ArrayList<Vertex>();

    List<Vertex> textureCoods = new ArrayList<Vertex>();

    List<Short> vertexIndexes = new ArrayList<Short>();

    List<Short> textureIndexes = new ArrayList<Short>();

    public ObjLoader(Context context, String filePrefix) {
        this.context = context;
        this.filePrefix = filePrefix;

        for (PrefixCallback prefixCallback : prefixCallbacks) {
            callbackMap.put(prefixCallback.getPrefix(), prefixCallback);
        }
    }

    public void load() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filePrefix + ".obj")));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] s = line.split("[ ]");
                doCallback(s);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vertex[] getVertexes() {
        return vertexs.toArray(new Vertex[]{});
    }

    public byte[] getDrawOrderIndex() {
        byte[] value = new byte[vertexIndexes.size()];

        for (int i = 0; i < value.length; i++) {
            value[i] = vertexIndexes.get(i).byteValue();
        }

        return value;
    }

    private void doCallback(String[] content) {
        PrefixCallback callback = callbackMap.get(content[0]);
        if (callback != null) {
            callback.callback(content);
        }
    }

    interface PrefixCallback {
        String getPrefix();

        void callback(String[] content);
    }
}
