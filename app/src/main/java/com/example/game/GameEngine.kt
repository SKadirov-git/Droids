package com.example.game

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assets.GameTheme
import com.example.audio.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class GameState {
  MENU,
  PLAYING,
  PAUSED,
  GAME_OVER
}

data class ScorePopup(
  val text: String,
  val points: Int,
  val timestamp: Long = System.currentTimeMillis()
)

class GameEngine(
  private val context: Context,
  val soundManager: SoundManager
) {
  companion object {
    private const val PREFS_NAME = "droids_game_prefs"
    private const val KEY_HIGH_SCORE = "high_score"
    private const val KEY_SELECTED_THEME = "selected_theme"
  }

  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  val board = GameBoard()

  var gameState by mutableStateOf(GameState.MENU)
    private set

  var activePiece by mutableStateOf<ActivePiece?>(null)
    private set

  var nextPiece by mutableStateOf<PieceType?>(null)
    private set

  var holdPiece by mutableStateOf<PieceType?>(null)
    private set

  var canHold by mutableStateOf(true)
    private set

  var score by mutableIntStateOf(0)
    private set

  var highScore by mutableIntStateOf(prefs.getInt(KEY_HIGH_SCORE, 0))
    private set

  var linesCleared by mutableIntStateOf(0)
    private set

  var level by mutableIntStateOf(1)
    private set

  var currentTheme by mutableStateOf(
    try {
      val savedThemeName = prefs.getString(KEY_SELECTED_THEME, GameTheme.BLUE.name)
      GameTheme.valueOf(savedThemeName ?: GameTheme.BLUE.name)
    } catch (_: Exception) {
      GameTheme.BLUE
    }
  )
    private set

  var clearingLines by mutableStateOf<List<Int>>(emptyList())
    private set

  var scorePopup by mutableStateOf<ScorePopup?>(null)
    private set

  private val pieceBag = mutableListOf<PieceType>()
  private var lastFallTime = 0L
  private var lockDelayCounter = 0

  init {
    refillBag()
    nextPiece = takeFromBag()
  }

  fun setTheme(theme: GameTheme) {
    currentTheme = theme
    prefs.edit().putString(KEY_SELECTED_THEME, theme.name).apply()
    soundManager.playClick()
  }

  fun toggleTheme() {
    setTheme(currentTheme.next())
  }

  private fun refillBag() {
    val all = PieceType.entries.shuffled()
    pieceBag.addAll(all)
  }

  private fun takeFromBag(): PieceType {
    if (pieceBag.isEmpty()) {
      refillBag()
    }
    return pieceBag.removeAt(0)
  }

  fun startNewGame() {
    board.reset()
    score = 0
    linesCleared = 0
    level = 1
    holdPiece = null
    canHold = true
    clearingLines = emptyList()
    scorePopup = null

    pieceBag.clear()
    refillBag()
    nextPiece = takeFromBag()
    spawnNextPiece()

    gameState = GameState.PLAYING
    soundManager.playClick()
    soundManager.startMusic()
  }

  fun pauseGame() {
    if (gameState == GameState.PLAYING) {
      gameState = GameState.PAUSED
      soundManager.pauseMusic()
    }
  }

  fun resumeGame() {
    if (gameState == GameState.PAUSED) {
      gameState = GameState.PLAYING
      soundManager.resumeMusic()
    }
  }

  fun returnToMenu() {
    gameState = GameState.MENU
    soundManager.playClick()
  }

  private fun spawnNextPiece() {
    val type = nextPiece ?: takeFromBag()
    nextPiece = takeFromBag()
    val initialPiece = ActivePiece(
      type = type,
      x = (board.width / 2) - 2,
      y = 0,
      rotation = 0
    )

    if (!board.canPlace(initialPiece, initialPiece.x, initialPiece.y, initialPiece.rotation)) {
      // Game Over!
      triggerGameOver()
      return
    }

    activePiece = initialPiece
    canHold = true
    lockDelayCounter = 0
  }

  private fun triggerGameOver() {
    gameState = GameState.GAME_OVER
    activePiece = null
    soundManager.pauseMusic()
    if (score > highScore) {
      highScore = score
      prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply()
    }
  }

  fun moveLeft(): Boolean {
    if (gameState != GameState.PLAYING) return false
    val piece = activePiece ?: return false
    if (board.canPlace(piece, piece.x - 1, piece.y, piece.rotation)) {
      activePiece = piece.copy(x = piece.x - 1)
      soundManager.playClick()
      return true
    }
    return false
  }

  fun moveRight(): Boolean {
    if (gameState != GameState.PLAYING) return false
    val piece = activePiece ?: return false
    if (board.canPlace(piece, piece.x + 1, piece.y, piece.rotation)) {
      activePiece = piece.copy(x = piece.x + 1)
      soundManager.playClick()
      return true
    }
    return false
  }

  fun rotateClockwise(): Boolean {
    if (gameState != GameState.PLAYING) return false
    val piece = activePiece ?: return false
    val newRot = (piece.rotation + 1) % 4
    return tryRotationWithKicks(piece, newRot)
  }

  fun rotateCounterClockwise(): Boolean {
    if (gameState != GameState.PLAYING) return false
    val piece = activePiece ?: return false
    val newRot = ((piece.rotation - 1) + 4) % 4
    return tryRotationWithKicks(piece, newRot)
  }

  private fun tryRotationWithKicks(piece: ActivePiece, newRot: Int): Boolean {
    // Wall-kick offsets: (0,0), (-1,0), (1,0), (0,-1), (-2,0), (2,0)
    val kicks = listOf(
      Pair(0, 0), Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(-2, 0), Pair(2, 0)
    )
    for (kick in kicks) {
      val testX = piece.x + kick.first
      val testY = piece.y + kick.second
      if (board.canPlace(piece, testX, testY, newRot)) {
        activePiece = piece.copy(x = testX, y = testY, rotation = newRot)
        soundManager.playClick()
        return true
      }
    }
    return false
  }

  fun softDrop() {
    if (gameState != GameState.PLAYING) return
    val piece = activePiece ?: return
    if (board.canPlace(piece, piece.x, piece.y + 1, piece.rotation)) {
      activePiece = piece.copy(y = piece.y + 1)
      addScore(GameConstants.SCORE_SOFT_DROP)
      soundManager.playClick()
    } else {
      lockActivePiece()
    }
  }

  fun hardDrop() {
    if (gameState != GameState.PLAYING) return
    val piece = activePiece ?: return
    val ghostY = board.getGhostY(piece)
    val dropDistance = ghostY - piece.y
    activePiece = piece.copy(y = ghostY)
    addScore(dropDistance * GameConstants.SCORE_HARD_DROP)
    soundManager.playTetrisDropSound()
    lockActivePiece()
  }

  fun holdCurrentPiece() {
    if (gameState != GameState.PLAYING || !canHold) return
    val piece = activePiece ?: return
    val currentType = piece.type

    if (holdPiece == null) {
      holdPiece = currentType
      spawnNextPiece()
    } else {
      val heldType = holdPiece!!
      holdPiece = currentType
      activePiece = ActivePiece(
        type = heldType,
        x = (board.width / 2) - 2,
        y = 0,
        rotation = 0
      )
    }
    canHold = false
    soundManager.playClick()
  }

  private fun lockActivePiece() {
    val piece = activePiece ?: return
    val success = board.lockPiece(piece)
    activePiece = null

    if (!success) {
      triggerGameOver()
      return
    }

    val completedLines = board.getCompletedLines()
    if (completedLines.isNotEmpty()) {
      clearingLines = completedLines
      handleLineClears(completedLines.size)
    } else {
      spawnNextPiece()
    }
  }

  private fun handleLineClears(count: Int) {
    soundManager.playLineClearSound()
    val basePoints = when (count) {
      1 -> GameConstants.SCORE_SINGLE_LINE
      2 -> GameConstants.SCORE_DOUBLE_LINE
      3 -> GameConstants.SCORE_TRIPLE_LINE
      4 -> GameConstants.SCORE_DROID_TETRIS
      else -> count * 200
    }
    val pointsAwarded = basePoints * level
    addScore(pointsAwarded)

    val label = when (count) {
      1 -> "SINGLE +$pointsAwarded"
      2 -> "DOUBLE +$pointsAwarded"
      3 -> "TRIPLE +$pointsAwarded"
      4 -> "DROID TETRIS! +$pointsAwarded"
      else -> "COMBO +$pointsAwarded"
    }
    scorePopup = ScorePopup(label, pointsAwarded)

    linesCleared += count
    val newLevel = (linesCleared / GameConstants.LINES_PER_LEVEL) + 1
    if (newLevel > level) {
      level = newLevel
    }
  }

  fun onLineClearAnimationFinished() {
    board.clearLines(clearingLines)
    clearingLines = emptyList()
    spawnNextPiece()
  }

  private fun addScore(points: Int) {
    score += points
    if (score > highScore) {
      highScore = score
      prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply()
    }
  }

  /**
   * Main game loop tick called regularly (every frame / delta).
   */
  fun update(currentTimeMs: Long) {
    if (gameState != GameState.PLAYING) return

    // If clearing lines, let animation complete
    if (clearingLines.isNotEmpty()) {
      return
    }

    val piece = activePiece ?: return
    val interval = GameConstants.getFallIntervalMs(level)

    if (currentTimeMs - lastFallTime >= interval) {
      lastFallTime = currentTimeMs
      if (board.canPlace(piece, piece.x, piece.y + 1, piece.rotation)) {
        activePiece = piece.copy(y = piece.y + 1)
        lockDelayCounter = 0
      } else {
        // Touched bottom, check lock delay
        lockDelayCounter++
        if (lockDelayCounter >= 2) {
          lockActivePiece()
        }
      }
    }
  }
}
