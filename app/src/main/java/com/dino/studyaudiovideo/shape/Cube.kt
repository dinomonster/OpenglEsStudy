package com.dino.studyaudiovideo.shape

import android.opengl.GLES32
import android.opengl.Matrix
import android.os.SystemClock
import android.view.View
import com.dino.studyaudiovideo.base.Shape
import com.dino.studyaudiovideo.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Cube(mView: View) : Shape(mView) {
    companion object {
        // 每个顶点的坐标数
        internal val COORDS_PER_VERTEX = 3
        //立方体每个顶点坐标
        internal val cubePositions = floatArrayOf(
                -1.0f, 1.0f, 1.0f, //正面左上0
                -1.0f, -1.0f, 1.0f, //正面左下1
                1.0f, -1.0f, 1.0f, //正面右下2
                1.0f, 1.0f, 1.0f, //正面右上3
                -1.0f, 1.0f, -1.0f, //反面左上4
                -1.0f, -1.0f, -1.0f, //反面左下5
                1.0f, -1.0f, -1.0f, //反面右下6
                1.0f, 1.0f, -1.0f //反面右上7
        )
        // 顶点的绘制顺序
        internal val index = shortArrayOf(
                6, 7, 4, 6, 4, 5, //后面
                6, 3, 7, 6, 2, 3, //右面
                6, 5, 1, 6, 1, 2, //下面
                0, 3, 2, 0, 2, 1, //正面
                0, 1, 5, 0, 5, 4, //左面
                0, 7, 3, 0, 4, 7  //上面
        )
        //设置颜色，依次为红绿蓝和透明通道
        internal var color = floatArrayOf(
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f
        )
    }

    var mVertexShader = ShaderUtil.loadFromAssetsFile("colorfultriangleCamera.glsl", mView.context.resources)//顶点着色器
    var mFragmentShader = ShaderUtil.loadFromAssetsFile("colorfulfrag.glsl", mView.context.resources)//片元着色器
    var mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader)//自定义渲染管线程序id

    private var mPositionHandle: Int = 0  //顶点位置属性引用id
    private var mColorHandle: Int = 0  //顶点颜色属性引用id
    private var mMVPMatrixHandle: Int = 0  //变换矩阵

    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

    //顶点个数
    private val vertexCount = cubePositions.size / COORDS_PER_VERTEX
    //顶点之间的偏移量
    private val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节

    private val mVertexBuffer: FloatBuffer//顶点坐标数据缓冲
    private val colorBuffer: FloatBuffer
    private val mIndexBuffer: ShortBuffer

    init {
        //分配新的直接字节缓冲区
        val bb = ByteBuffer.allocateDirect(
                cubePositions.size * 4)//float占四个字节
        // 修改ButyBuffer的字节顺序,使用JVM运行的硬件平台的固有字节顺序
        bb.order(ByteOrder.nativeOrder())
        //获取FloatBuffer缓冲
        mVertexBuffer = bb.asFloatBuffer()
        //写入floatArray的顶点坐标
        mVertexBuffer.put(cubePositions)
        //指向可读数据的首位
        mVertexBuffer.position(0)

        val dd = ByteBuffer.allocateDirect(
                color.size * 4)
        dd.order(ByteOrder.nativeOrder())
        colorBuffer = dd.asFloatBuffer()
        colorBuffer.put(color)
        colorBuffer.position(0)

        //分配新的直接字节缓冲区
        val cc = ByteBuffer.allocateDirect(index.size * 2)//short占两个字节
        cc.order(ByteOrder.nativeOrder())
        mIndexBuffer = cc.asShortBuffer()
        mIndexBuffer.put(index)
        mIndexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //将程序加入到OpenGLES3.2环境
        GLES32.glUseProgram(mProgram)
        // 创建旋转矩阵
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
//        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, -1f, 0.0f)
//        val scratch = FloatArray(16)
//        // 两个矩阵相乘  合并旋转矩阵和之前的合并矩阵
//        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // 获取转换矩阵
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 将投影和视图转换传递给着色器
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition")
        //启用三角形顶点的句柄
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形的坐标数据
        GLES32.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES32.GL_FLOAT, false,
                vertexStride, mVertexBuffer)
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aColor")
        //设置绘制三角形的颜色
        GLES32.glEnableVertexAttribArray(mColorHandle)
        GLES32.glVertexAttribPointer(mColorHandle, 4,
                GLES32.GL_FLOAT, false,
                0, colorBuffer)

        /**
         * 绘制正方形,使用GL_TRIANGLES模式，由两个三角形拼接
         * mode 绘图模式
         *      GL_TRIANGLES - 这个参数意味着OpenGL使用三个顶点来组成图形。
         *                所以，在开始的三个顶点，将用顶点1，顶点2，顶点3来组成一个三角形。
         *                完成后，在用下一组的三个顶点(顶点4，5，6)来组成三角形，直到数组结束
         * count 依次从索引数组中读取count个顶点来进行绘制
         * type 索引数组中存放的元素的类型
         * indices 指向索引数组的指针
         */
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, index.size, GLES32.GL_UNSIGNED_SHORT, mIndexBuffer)
        //禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height
        /**
         * 设置透视投影
         * float left, //near面的left
         * float right, //near面的right
         * float bottom, //near面的bottom
         * float top, //near面的top
         * float near, //near面距离
         * float far //far面距离
         *
         * 先是left，right和bottom,top，这4个参数会影响图像左右和上下缩放比，
         * 所以往往会设置的值分别-(float) width / height和(float) width / height，
         * top和bottom和top会影响上下缩放比，如果left和right已经设置好缩放，则bottom只需要设置为-1，top设置为1，这样就能保持图像不变形。
         * 也可以将left，right 与bottom，top交换比例，即bottom和top设置为 -height/width 和 height/width, left和right设置为-1和1
         *
         * near和far参数稍抽象一点，就是一个立方体的前面和后面，near和far需要结合拍摄相机即观察者眼睛的位置来设置，
         * 例如setLookAtM中设置cx = 0, cy = 0, cz = 10，near设置的范围需要是大于10才可以看得到绘制的图像，
         * 如果小于10，图像就会处于了观察这眼睛的后面，这样绘制的图像就会消失在镜头前，far参数，far参数影响的是立体图形的背面，
         * far一定比near大，一般会设置得比较大，如果设置的比较小，一旦3D图形尺寸很大，这时候由于far太小，这个投影矩阵没法容纳图形全部的背面，
         * 这样3D图形的背面会有部分隐藏掉的
         */
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        /**
         * 设置相机位置
         * float eyeX, //摄像机位置x
         * float eyeY, //摄像机位置y
         * float eyeZ, //摄像机位置z
         * float centerX, //摄像机目标点x
         * float centerY, //摄像机目标点y
         * float centerZ, //摄像机目标点z
         * float upX, //摄像机UP向量X分量
         * float upY, //摄像机UP向量Y分量
         * float upZ //摄像机UP向量Z分量
         */
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //开启深度测试
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
    }
}