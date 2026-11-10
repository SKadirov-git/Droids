package com.example.assets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.R

enum class GameTheme(
  val displayName: String,
  val assetFileName: String,
  val drawableResId: Int,
  val accentColorHex: Long
) {
  BLUE("Neon Cobalt", "gamebg_blue.png", R.drawable.gamebg_blue, 0xFF00E5FF),
  AMBER("Amber Arcade", "gamebg_amber.png", R.drawable.gamebg_amber, 0xFFF59E0B),
  CRIMSON("Cyber Crimson", "gamebg_crimson.png", R.drawable.gamebg_crimson, 0xFFF43F5E);

  fun next(): GameTheme {
    val values = entries
    return values[(ordinal + 1) % values.size]
  }
}

class AssetManagerHelper(private val context: Context) {
  companion object {
    private const val TAG = "AssetManagerHelper"
  }

  // Cached Bitmaps
  var blueBlockBitmap: ImageBitmap? = null
    private set
  var cyanBlockBitmap: ImageBitmap? = null
    private set
  var buttonBlueBitmap: ImageBitmap? = null
    private set
  var buttonPlayBitmap: ImageBitmap? = null
    private set

  // Cached Theme Backgrounds
  private val themeBackgrounds = mutableMapOf<GameTheme, ImageBitmap>()

  init {
    loadAllAssets()
  }

  private fun loadBitmapFromAsset(filename: String, fallbackResId: Int? = null): Bitmap? {
    return try {
      context.assets.open(filename).use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
      }
    } catch (e: Exception) {
      Log.w(TAG, "Could not load $filename from assets, trying drawable fallback: ${e.message}")
      if (fallbackResId != null) {
        try {
          BitmapFactory.decodeResource(context.resources, fallbackResId)
        } catch (resErr: Exception) {
          Log.e(TAG, "Failed to load fallback resource for $filename", resErr)
          null
        }
      } else null
    }
  }

  fun loadAllAssets() {
    // 1. Blocks
    loadBitmapFromAsset("blueblock.png", R.drawable.blueblock)?.let {
      blueBlockBitmap = it.asImageBitmap()
    }
    loadBitmapFromAsset("cyanblock.png", R.drawable.cyanblock)?.let {
      cyanBlockBitmap = it.asImageBitmap()
    }

    // 2. UI Buttons
    loadBitmapFromAsset("button_blue.png", R.drawable.button_blue)?.let {
      buttonBlueBitmap = it.asImageBitmap()
    }
    loadBitmapFromAsset("button_play.png", R.drawable.button_play)?.let {
      buttonPlayBitmap = it.asImageBitmap()
    }

    // 3. Backgrounds for all themes
    for (theme in GameTheme.entries) {
      loadBitmapFromAsset(theme.assetFileName, theme.drawableResId)?.let {
        themeBackgrounds[theme] = it.asImageBitmap()
      }
    }
    Log.d(TAG, "Asset preloading complete")
  }

  fun getBackgroundForTheme(theme: GameTheme): ImageBitmap? {
    return themeBackgrounds[theme]
  }
}
