package com.reelsapp.reels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

@OptIn(UnstableApi::class)
object ReelPlayerManager {

    private const val TAG = "ReelPlayerManager"
    private val playerPool = mutableListOf<ExoPlayer>()

    fun acquirePlayer(context: Context): ExoPlayer {
        val player = if (playerPool.isNotEmpty()) {
            Log.d(TAG, "Acquiring player from pool")
            playerPool.removeAt(0)
        } else {
            Log.d(TAG, "Creating HD ExoPlayer instance")
            val trackSelector = DefaultTrackSelector(context.applicationContext).apply {
                setParameters(
                    buildUponParameters()
                        .setMaxVideoBitrate(20_000_000)
                        .setForceHighestSupportedBitrate(true)
                )
            }
            ExoPlayer.Builder(context.applicationContext)
                .setTrackSelector(trackSelector)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_ONE
                    playWhenReady = true
                }
        }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                val stateName = when (state) {
                    Player.STATE_IDLE -> "STATE_IDLE"
                    Player.STATE_BUFFERING -> "STATE_BUFFERING"
                    Player.STATE_READY -> "STATE_READY"
                    Player.STATE_ENDED -> "STATE_ENDED"
                    else -> "UNKNOWN"
                }
                Log.d(TAG, "[PlayerState] Changed to: $stateName")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "[PlayerState] isPlaying: $isPlaying")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "[PlayerError] Code: ${error.errorCodeName}, Message: ${error.message}", error)
            }

            override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                Log.d(TAG, "🎥 [VideoResolution] Width: ${videoSize.width}px, Height: ${videoSize.height}px")
            }

            override fun onRenderedFirstFrame() {
                Log.d(TAG, "[PlayerState] Rendered FIRST FRAME successfully! 🎬")
            }
        })

        return player
    }

    fun prepareMedia(context: Context, player: ExoPlayer, url: String) {
        Log.d(TAG, "Preparing media URL: $url")
        val uri = Uri.parse(url)

        if (url.startsWith("android.resource://")) {
            // Explicitly specify VIDEO_MP4 MimeType for raw android.resource:// media items
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setMimeType(MimeTypes.VIDEO_MP4)
                .build()
            player.setMediaItem(mediaItem)
        } else {
            val mediaItem = MediaItem.fromUri(uri)
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

            val dataSourceFactory = DefaultDataSource.Factory(context.applicationContext, httpDataSourceFactory)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            player.setMediaSource(mediaSource)
        }

        player.prepare()
        player.playWhenReady = true
    }

    fun releasePlayer(player: ExoPlayer) {
        Log.d(TAG, "Releasing player to pool")
        player.stop()
        player.clearMediaItems()
        if (playerPool.size < 3) {
            playerPool.add(player)
        } else {
            player.release()
        }
    }
}
