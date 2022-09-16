package com.zjy.video

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkTimedText

/**
 * @author zhengjy
 * @since 2020/11/25
 * Description:
 */
class VideoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }

//        ivp_video.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20201114_005546.mp4")
//        ivp_video.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20201117_112359.mp4")
        ivp_video.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20220429_132811_0_COMPRESSED.mp4")
        ivp_video.setSeekBar(sb_progress)
        ivp_video.setListener(object : VideoPlayerListener {
            override fun onPrepared(p0: IMediaPlayer?) {
                p0?.start()
            }

            override fun onTimedText(p0: IMediaPlayer?, p1: IjkTimedText?) {

            }

            override fun onInfo(p0: IMediaPlayer?, what: Int, extra: Int): Boolean {
                if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                    //这里返回了视频旋转的角度，根据角度旋转视频到正确的画面
                    ivp_video.setVideoRotation(extra.toFloat())
                }
                return true
            }

            override fun onError(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
                return true
            }

            override fun onVideoSizeChanged(p0: IMediaPlayer?, p1: Int, p2: Int, p3: Int, p4: Int) {

            }

            override fun onSeekComplete(p0: IMediaPlayer?) {

            }

            override fun onBufferingUpdate(p0: IMediaPlayer?, p1: Int) {

            }

            override fun onCompletion(p0: IMediaPlayer?) {

            }
        })
        sb_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    ivp_video.mediaPlayer.seekTo((ivp_video.mediaPlayer.duration * progress / 100.0f).toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        ivp_video.onResume()
    }

    override fun onPause() {
        super.onPause()
        ivp_video.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ivp_video.release()
    }
}