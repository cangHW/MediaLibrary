package simcpux.sourceforge.net.testaudiomv.util;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by cangHX
 * on 2019/01/21  15:49
 */
public class Triangle {

    private final int mProgram;
    private FloatBuffer mVertexBuffer;

    private static final int VERTEX_COORDS = 3;
    private static final float[] TRIANGLE_POINTS = {
             0,     0.5f, 0,
            -0.5f, -0.5f, 0,
             0.5f, -0.5f, 0
    };

    private float color[] = {255,0,0,1};

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMvpMatrix;"+
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMvpMatrix * vPosition;" +
            "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private static final int VERTEX_COUNT = TRIANGLE_POINTS.length / VERTEX_COORDS;
    private static final int VERTEX_STRIDE = VERTEX_COORDS * 4;

    public Triangle() {
        ByteBuffer byteBuffer=ByteBuffer.allocateDirect(TRIANGLE_POINTS.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer=byteBuffer.asFloatBuffer();
        mVertexBuffer.put(TRIANGLE_POINTS);
        mVertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER_CODE);

        mProgram=GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void onDraw(float[] floats){
        GLES20.glUseProgram(mProgram);
        int vPositionLocation = GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(vPositionLocation);
        GLES20.glVertexAttribPointer(vPositionLocation,VERTEX_COORDS,GLES20.GL_FLOAT,false,VERTEX_STRIDE,mVertexBuffer);

        int uMvpMatrix = GLES20.glGetUniformLocation(mProgram,"uMvpMatrix");
        GLES20.glUniformMatrix4fv(uMvpMatrix,1,false,floats,0);

        int vColorLocation = GLES20.glGetUniformLocation(mProgram,"vColor");
        GLES20.glUniform4fv(vColorLocation,1,color,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,VERTEX_COUNT);
        GLES20.glDisableVertexAttribArray(vPositionLocation);
    }

    private int loadShader(int type,String shaderCode){
        int shader=GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
