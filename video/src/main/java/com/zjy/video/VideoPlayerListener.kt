package com.zjy.video

import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * @author zhengjy
 * @since 2020/11/25
 * Description:
 */
interface VideoPlayerListener
    : IMediaPlayer.OnPreparedListener, IMediaPlayer.OnTimedTextListener, IMediaPlayer.OnInfoListener,
    IMediaPlayer.OnErrorListener, IMediaPlayer.OnVideoSizeChangedListener,
    IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener,
    IMediaPlayer.OnCompletionListener