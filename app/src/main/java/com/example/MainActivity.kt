package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.assets.AssetManagerHelper
import com.example.audio.SoundManager
import com.example.game.GameEngine
import com.example.game.GameState
import com.example.ui.screens.GameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.DroidsTheme

class MainActivity : ComponentActivity() {

  private lateinit var soundManager: SoundManager
  private lateinit var assetHelper: AssetManagerHelper
  private lateinit var gameEngine: GameEngine

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    soundManager = SoundManager(applicationContext)
    assetHelper = AssetManagerHelper(applicationContext)
    gameEngine = GameEngine(applicationContext, soundManager)

    setContent {
      DroidsTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
        ) {
          DroidsApp(
            gameEngine = gameEngine,
            assetHelper = assetHelper,
            soundManager = soundManager
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (::gameEngine.isInitialized && gameEngine.gameState == GameState.PLAYING) {
      soundManager.resumeMusic()
    }
  }

  override fun onPause() {
    super.onPause()
    if (::soundManager.isInitialized) {
      soundManager.pauseMusic()
    }
    if (::gameEngine.isInitialized && gameEngine.gameState == GameState.PLAYING) {
      gameEngine.pauseGame()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::soundManager.isInitialized) {
      soundManager.release()
    }
  }
}

@Composable
fun DroidsApp(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  soundManager: SoundManager
) {
  Crossfade(
    targetState = gameEngine.gameState == GameState.MENU,
    label = "screen_transition"
  ) { isMenu ->
    if (isMenu) {
      MainMenuScreen(
        gameEngine = gameEngine,
        assetHelper = assetHelper,
        soundManager = soundManager
      )
    } else {
      GameScreen(
        gameEngine = gameEngine,
        assetHelper = assetHelper,
        soundManager = soundManager
      )
    }
  }
}
