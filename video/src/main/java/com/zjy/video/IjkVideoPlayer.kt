package com.zjy.video

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_video.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


/**
 * @author zhengjy
 * @since 2020/11/25
 * Description:
 */
internal class IjkVideoPlayer : FrameLayout {

    private val mContext: Context
    private lateinit var surfaceView: SurfaceView
    private lateinit var textureView: TextureView
    lateinit var mediaPlayer: IjkMediaPlayer
    private var listener: VideoPlayerListener? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var mPath: String? = null

    private val mHandler = Handler()

    private var touchX = 0f
    private var touchY = 0f

    private var duration = 0L
    private var originPosition = 0L
    private var seekPosition = 0L

    private var videoHeight = 0
    private var videoWidth = 0

    private var mRotation = 0f

    private var maxHeight = 500
    private var maxWidth = 0

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (duration != 0L) {
                seekBar?.apply {
                    progress = ((mediaPlayer.currentPosition * 1f / duration) * max).toInt()
                }
            }
            if (mediaPlayer.currentPosition != mediaPlayer.duration) {
                mHandler.postDelayed(this, 1000)
            }
        }
    }

    constructor(context: Context) : super(context) {
        mContext = context
        initVideoView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initVideoView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        mContext = context
        initVideoView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        initVideoView()
    }

    private fun initVideoView() {
        maxWidth = context.resources.displayMetrics.widthPixels
//        createSurfaceView()
        createTextureView()
    }

    private fun createSurfaceView() {
        //生成一个新的surface view
        surfaceView = SurfaceView(mContext)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                load()
                mHandler.removeCallbacks(progressRunnable)
                mHandler.postDelayed(progressRunnable, 1000)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)
        surfaceView.layoutParams = layoutParams
        addView(surfaceView)
    }

    private fun createTextureView() {
        //生成一个新的surface view
        textureView = TextureView(mContext)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
//                videoWidth = width
//                videoHeight = height
                surfaceTexture = surface
                load()
                mHandler.removeCallbacks(progressRunnable)
                mHandler.postDelayed(progressRunnable, 1000)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                surfaceTexture?.release()
                surfaceTexture = null
                mHandler.removeCallbacks(progressRunnable)
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                
            }

        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)
        textureView.layoutParams = layoutParams
        addView(textureView)
    }

    fun setVideoPath(path: String?) {
        mPath = path
        load()
    }

    /**
     * 加载视频
     */
    private fun load() {
        if (mPath == null || surfaceTexture == null) {
            return
        }
        //每次都要重新创建IMediaPlayer
        createPlayer()
        try {
            mediaPlayer.dataSource = mPath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //给mediaPlayer设置视图
//        mediaPlayer.setDisplay(surfaceView.holder)
        mediaPlayer.setSurface(Surface(surfaceTexture))
        mediaPlayer.prepareAsync()
    }

    /**
     * 创建一个新的player
     */
    private fun createPlayer() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.setDisplay(null)
            mediaPlayer.release()
        }
        mediaPlayer = IjkMediaPlayer()
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)
        //开启硬解码
        // ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        setListenerInternal()
    }

    fun setVideoRotation(rotation: Float) {
//        val canvas = surfaceView.holder.lockCanvas()
//        canvas.rotate(rotation)
//        surfaceView.holder.unlockCanvasAndPost(canvas)

        textureView.rotation = rotation

        val matrix = Matrix()
        textureView.getTransform(matrix)
        val src = RectF(0f, 0f, textureView.height.toFloat(), textureView.width.toFloat())
        val dst = RectF(0f, 0f, textureView.width.toFloat(), textureView.height.toFloat())
        val screen = RectF(dst)
        matrix.postRotate(rotation, screen.centerX(), screen.centerY())

        matrix.mapRect(dst)
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)

        matrix.mapRect(src)
        matrix.setRectToRect(screen, src, Matrix.ScaleToFit.FILL)

        matrix.postRotate(rotation, screen.centerX(), screen.centerY())

        textureView.setTransform(matrix)
    }

    fun setListener(listener: VideoPlayerListener) {
        this.listener = listener
        if (this::mediaPlayer.isInitialized) {
            setListenerInternal()
        }
    }

    private var seekBar: SeekBar? = null

    fun setSeekBar(seekBar: SeekBar) {
        this.seekBar = seekBar
    }

    private fun setListenerInternal() {
        mediaPlayer.setOnPreparedListener { player ->
            duration = player.duration
            videoWidth = player.videoWidth
            videoHeight = player.videoHeight
            requestLayout()
            listener?.onPrepared(player)
        }
        mediaPlayer.setOnTimedTextListener(listener)
        mediaPlayer.setOnInfoListener {player, what, extra ->
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                //这里返回了视频旋转的角度，根据角度旋转视频到正确的画面
                mRotation = extra.toFloat()
            }
            return@setOnInfoListener listener?.onInfo(player, what, extra) ?: false
        }
        mediaPlayer.setOnSeekCompleteListener(listener)
        mediaPlayer.setOnBufferingUpdateListener(listener)
        mediaPlayer.setOnErrorListener(listener)
        mediaPlayer.setOnVideoSizeChangedListener(listener)
        mediaPlayer.setOnCompletionListener {
            mHandler.removeCallbacks(progressRunnable)
            listener?.onCompletion(it)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                originPosition = mediaPlayer.currentPosition
            }
            MotionEvent.ACTION_MOVE -> {
                val percent = (event.x - touchX) / measuredWidth
                val offset = ((duration / 5) * percent).toLong()
                seekPosition = if (offset > 0) {
                    // 快进不超过最大长度
                    min(originPosition + offset, duration)
                } else {
                    // 快退不小于0
                    max(originPosition + offset, 0)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mediaPlayer.seekTo(seekPosition)
                seekBar?.apply {
                    progress = ((seekPosition * 1f / duration) * max).toInt()
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mRotation == 90f || mRotation == 270f) {
            doMeasure(heightMeasureSpec, widthMeasureSpec)
        } else {
            doMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    @SuppressLint("WrongCall")
    private fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val wSpec = MeasureSpec.getMode(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val hSpec = MeasureSpec.getMode(heightMeasureSpec)


        val videoRatio = if (videoWidth != 0 && videoHeight != 0) {
            videoWidth * 1f / videoHeight
        } else -1f
        if (videoRatio == -1f) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (wSpec == MeasureSpec.AT_MOST && hSpec == MeasureSpec.AT_MOST) {
            if (height > maxHeight && width <= maxWidth) {
                val newWidth = maxHeight * videoRatio
                setMeasuredDimension(
                    MeasureSpec.makeMeasureSpec(newWidth.toInt(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
                )

                children.forEach {
                    it.measure(
                        MeasureSpec.makeMeasureSpec(newWidth.toInt(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
                    )
                }
            } else if (height <= maxHeight && width > maxWidth) {
                val newHeight = maxWidth / videoRatio
                setMeasuredDimension(
                    MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(newHeight.toInt(), MeasureSpec.EXACTLY)
                )
            } else {
                val w = min(width, maxWidth)
                val h = min(height, maxHeight)
                val ratio = w * 1f / min(height, maxHeight)
                if (ratio < videoRatio) {
                    // 视频更宽，优先满足视频宽度
                    val newHeight = w / videoRatio
                    setMeasuredDimension(
                        MeasureSpec.makeMeasureSpec(w, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(newHeight.toInt(), MeasureSpec.AT_MOST)
                    )
                } else {
                    val newWidth = h * videoRatio
                    setMeasuredDimension(
                        MeasureSpec.makeMeasureSpec(newWidth.toInt(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.AT_MOST)
                    )
                }
            }
        } else if (wSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        } else if (hSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun onPause() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.pause()
        }
    }

    fun onResume() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.start()
        }
    }

    fun release() {
        if (this::mediaPlayer.isInitialized) {
            mHandler.removeCallbacks(progressRunnable)
            mediaPlayer.stop()
            mediaPlayer.setDisplay(null)
            mediaPlayer.release()
        }
    }
}