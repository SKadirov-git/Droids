package com.example.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

class SoundManager(private val context: Context) {
  companion object {
    private const val TAG = "SoundManager"
    private const val PREFS_NAME = "droids_sound_prefs"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_SFX_ENABLED = "sfx_enabled"
  }

  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  var isMusicEnabled: Boolean = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
    private set

  var isSfxEnabled: Boolean = prefs.getBoolean(KEY_SFX_ENABLED, true)
    private set

  private var mediaPlayer: MediaPlayer? = null
  private val soundPool: SoundPool
  private var clickSoundId: Int = 0
  private var isMusicLoaded: Boolean = false
  private var isPlaybackRequested: Boolean = false

  init {
    val audioAttributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_GAME)
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
      .build()

    soundPool = SoundPool.Builder()
      .setMaxStreams(8)
      .setAudioAttributes(audioAttributes)
      .build()

    loadSounds()
  }

  private fun loadSounds() {
    // 1. Load click.ogg into SoundPool
    try {
      val afd: AssetFileDescriptor = context.assets.openFd("click.ogg")
      clickSoundId = soundPool.load(afd, 1)
      Log.d(TAG, "Loaded click.ogg from assets into SoundPool")
    } catch (e: Exception) {
      Log.e(TAG, "Failed loading click.ogg from assets, trying fallback resource", e)
      try {
        clickSoundId = soundPool.load(context, com.example.R.raw.click, 1)
      } catch (err: Exception) {
        Log.e(TAG, "Failed loading click fallback", err)
      }
    }

    // 2. Prepare MediaPlayer for Korobeiniki.ogg
    try {
      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        isLooping = true
        val afd = context.assets.openFd("Korobeiniki.ogg")
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        prepareAsync()
        setOnPreparedListener {
          isMusicLoaded = true
          Log.d(TAG, "Korobeiniki.ogg prepared successfully")
          if (isPlaybackRequested && isMusicEnabled) {
            it.start()
          }
        }
        setOnErrorListener { _, what, extra ->
          Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
          true
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed loading Korobeiniki.ogg from assets", e)
    }
  }

  fun playClick() {
    if (!isSfxEnabled || clickSoundId == 0) return
    soundPool.play(clickSoundId, 0.8f, 0.8f, 1, 0, 1.0f)
  }

  fun playLineClearSound() {
    if (!isSfxEnabled || clickSoundId == 0) return
    // Play with higher pitch for rewarding line clear feedback
    soundPool.play(clickSoundId, 1.0f, 1.0f, 2, 0, 1.5f)
  }

  fun playTetrisDropSound() {
    if (!isSfxEnabled || clickSoundId == 0) return
    soundPool.play(clickSoundId, 1.0f, 1.0f, 2, 0, 0.7f)
  }

  fun startMusic() {
    isPlaybackRequested = true
    if (!isMusicEnabled) return
    try {
      mediaPlayer?.let {
        if (isMusicLoaded && !it.isPlaying) {
          it.start()
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error starting music", e)
    }
  }

  fun pauseMusic() {
    isPlaybackRequested = false
    try {
      mediaPlayer?.let {
        if (it.isPlaying) {
          it.pause()
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error pausing music", e)
    }
  }

  fun resumeMusic() {
    if (isMusicEnabled) {
      startMusic()
    }
  }

  fun toggleMusic(): Boolean {
    isMusicEnabled = !isMusicEnabled
    prefs.edit().putBoolean(KEY_MUSIC_ENABLED, isMusicEnabled).apply()
    if (isMusicEnabled) {
      startMusic()
    } else {
      pauseMusic()
    }
    return isMusicEnabled
  }

  fun toggleSfx(): Boolean {
    isSfxEnabled = !isSfxEnabled
    prefs.edit().putBoolean(KEY_SFX_ENABLED, isSfxEnabled).apply()
    if (isSfxEnabled) {
      playClick()
    }
    return isSfxEnabled
  }

  fun release() {
    try {
      mediaPlayer?.release()
      mediaPlayer = null
      soundPool.release()
    } catch (e: Exception) {
      Log.e(TAG, "Error releasing audio", e)
    }
  }
}
