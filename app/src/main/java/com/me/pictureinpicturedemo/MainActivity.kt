package com.me.pictureinpicturedemo

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.me.pictureinpicturedemo.databinding.ActivityMainBinding

private const val ACTION_STOPWATCH_CONTROL = "stopwatch_control"
private const val EXTRA_CONTROL_TYPE = "control_type"
private const val CONTROL_TYPE_CLEAR = 1
private const val CONTROL_TYPE_START_OR_PAUSE = 2
private const val REQUEST_CLEAR = 3
private const val REQUEST_START_OR_PAUSE = 4


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    val TEST_VIDEO_URL =
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }
    private val pipBuilder by lazy { PictureInPictureParams.Builder() }
    private var player: ExoPlayer? = null

    private val broadcastReceiver = object : BroadcastReceiver() {

        // Called when an item is clicked.
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_STOPWATCH_CONTROL) {
                return
            }
            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_TYPE_START_OR_PAUSE -> player?.also {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.play()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding.root
        setContentView(view)
        Log.d(TAG, "onCreate: ")
        if (!checkPIP()) {
            Toast.makeText(
                this,
                "this device does not support picture in picture mode",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.button.setOnClickListener {
            enterPIP()
        }
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d(TAG, "onUserLeaveHint: ")
        //Monitor home button
        enterPIP()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        Log.d(
            TAG,
            "onPictureInPictureModeChanged: isInPictureInPictureMode:　${isInPictureInPictureMode}"
        )
        if (isInPictureInPictureMode) {
            // Hide in-app buttons. They cannot be interacted in the picture-in-picture mode, and
            // their features are provided as the action icons.
            binding.button.visibility = View.GONE
            binding.videoView.useController = false
        } else {
            binding.button.visibility = View.VISIBLE
            binding.videoView.useController = true

        }
    }

    fun setPIPParameter(w: Int = 16, h: Int = 9) {
        pipBuilder.setAspectRatio(Rational(w, h))
        // Sets whether the system will automatically put the activity in picture-in-picture mode without needing/waiting for the activity to call

        pipBuilder.setActions(
            listOf(
                createRemoteAction(
                    R.drawable.baseline_play_circle_24,
                    "Start/Pause",
                    REQUEST_START_OR_PAUSE,
                    CONTROL_TYPE_START_OR_PAUSE
                )
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pipBuilder.setAutoEnterEnabled(true)
            pipBuilder.setSeamlessResizeEnabled(false)
        }

        enterPictureInPictureMode(pipBuilder.build())
    }

    private fun checkPIP(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    private fun enterPIP() {
//        enterPictureInPictureMode()   //if don't need parameter
        setPIPParameter()
    }

    private fun initializePlayer() {
        Log.d(TAG, "initializePlayer: ")
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(TEST_VIDEO_URL)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true

                //PIP using broadcast to control action
                registerReceiver(broadcastReceiver, IntentFilter(ACTION_STOPWATCH_CONTROL))
            }
    }

    /**
     * Creates a [RemoteAction]. It is used as an action icon on the overlay of the
     * picture-in-picture mode.
     */
    private fun createRemoteAction(
        @DrawableRes iconResId: Int,
        title: String,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        return RemoteAction(
            Icon.createWithResource(this, iconResId),
            title,
            title,
            PendingIntent.getBroadcast(
                this,
                requestCode,
                Intent(ACTION_STOPWATCH_CONTROL)
                    .putExtra(EXTRA_CONTROL_TYPE, controlType),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}