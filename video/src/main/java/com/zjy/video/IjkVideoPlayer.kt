package com.zjy.video

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.SeekBar
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
    lateinit var mediaPlayer: IjkMediaPlayer
    private var listener: VideoPlayerListener? = null
    private var mPath: String? = null

    private val mHandler = Handler()

    private var touchX = 0f
    private var touchY = 0f

    private var duration = 0L
    private var originPosition = 0L
    private var seekPosition = 0L

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
        createSurfaceView()
    }

    private fun createSurfaceView() {
        //生成一个新的surface view
        surfaceView = SurfaceView(mContext)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {

            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                load()
                mHandler.removeCallbacks(progressRunnable)
                mHandler.postDelayed(progressRunnable, 1000)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)
        surfaceView.layoutParams = layoutParams
        addView(surfaceView)
    }

    fun setVideoPath(path: String?) {
        mPath = path
        load()
    }

    /**
     * 加载视频
     */
    private fun load() {
        if (mPath == null) {
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
        mediaPlayer.setDisplay(surfaceView.holder)
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
        surfaceView.rotation = rotation
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
            duration = player?.duration ?: 0L
            listener?.onPrepared(player)
        }
        mediaPlayer.setOnInfoListener(listener)
        mediaPlayer.setOnSeekCompleteListener(listener)
        mediaPlayer.setOnBufferingUpdateListener(listener)
        mediaPlayer.setOnErrorListener(listener)
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