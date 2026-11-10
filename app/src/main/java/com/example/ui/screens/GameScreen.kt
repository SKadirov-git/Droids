package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assets.AssetManagerHelper
import com.example.assets.GameTheme
import com.example.audio.SoundManager
import com.example.game.ActivePiece
import com.example.game.GameConstants
import com.example.game.GameEngine
import com.example.game.GameState
import com.example.game.PieceType
import com.example.game.Point
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.LilitaOneFontFamily
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCrimson
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GameScreen(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  soundManager: SoundManager,
  modifier: Modifier = Modifier
) {
  val currentTheme = gameEngine.currentTheme

  // Intercept back button to pause game
  BackHandler {
    if (gameEngine.gameState == GameState.PLAYING) {
      gameEngine.pauseGame()
    } else {
      gameEngine.returnToMenu()
    }
  }

  // Main 60 FPS Game Loop
  LaunchedEffect(gameEngine.gameState) {
    while (gameEngine.gameState == GameState.PLAYING) {
      gameEngine.update(System.currentTimeMillis())
      delay(16L) // ~60 FPS
    }
  }

  // Line Clear Animation handler
  LaunchedEffect(gameEngine.clearingLines) {
    if (gameEngine.clearingLines.isNotEmpty()) {
      delay(220L) // Flash duration
      gameEngine.onLineClearAnimationFinished()
    }
  }

  // Clear score popup after duration
  LaunchedEffect(gameEngine.scorePopup) {
    if (gameEngine.scorePopup != null) {
      delay(1500L)
      // will naturally expire or update
    }
  }

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    // 1. Full-screen themed background
    val bgBitmap = assetHelper.getBackgroundForTheme(currentTheme)
    if (bgBitmap != null) {
      Image(
        bitmap = bgBitmap,
        contentDescription = "Theme Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else {
      Image(
        painter = painterResource(id = currentTheme.drawableResId),
        contentDescription = "Theme Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    // Vignette overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.5f),
              Color.Black.copy(alpha = 0.25f),
              Color.Black.copy(alpha = 0.65f)
            )
          )
        )
    )

    // 2. Main Game Layout
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 28.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top HUD Bar
      GameTopBar(
        gameEngine = gameEngine,
        soundManager = soundManager
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Center Arena: Hold Panel + Matrix Board + Next Panel
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left Side Panel: HOLD Piece
          SidePanel(
            title = "HOLD",
            pieceType = gameEngine.holdPiece,
            assetHelper = assetHelper,
            modifier = Modifier
              .width(72.dp)
              .clickable { gameEngine.holdCurrentPiece() }
              .testTag("hold_panel")
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Center Playing Field (Matrix)
          BoxWithConstraints(
            modifier = Modifier
              .weight(1f, fill = false)
              .fillMaxHeight(),
            contentAlignment = Alignment.Center
          ) {
            val boardW = GameConstants.BOARD_WIDTH
            val boardH = GameConstants.BOARD_HEIGHT
            val maxH = maxHeight
            val maxW = maxWidth

            // Target aspect ratio is 10 / 20 = 0.5
            val calculatedW = (maxH * 0.5f).coerceAtMost(maxW)
            val calculatedH = calculatedW * 2.0f

            Box(
              modifier = Modifier
                .size(calculatedW, calculatedH)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .border(2.5.dp, Color(currentTheme.accentColorHex), RoundedCornerShape(8.dp))
                .testTag("game_matrix")
            ) {
              // Canvas Board Renderer
              GameBoardCanvas(
                gameEngine = gameEngine,
                assetHelper = assetHelper,
                modifier = Modifier
                  .fillMaxSize()
                  .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                      onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                      },
                      onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        // Horizontal cell step
                        val cellPx = size.width / GameConstants.BOARD_WIDTH.toFloat()
                        if (abs(totalDragX) >= cellPx * 0.8f) {
                          if (totalDragX > 0) {
                            gameEngine.moveRight()
                          } else {
                            gameEngine.moveLeft()
                          }
                          totalDragX = 0f
                        }

                        // Vertical soft drop
                        if (totalDragY >= cellPx) {
                          gameEngine.softDrop()
                          totalDragY = 0f
                        }
                      }
                    )
                  }
                  .pointerInput(Unit) {
                    detectTapGestures(
                      onTap = {
                        gameEngine.rotateClockwise()
                      },
                      onDoubleTap = {
                        gameEngine.hardDrop()
                      }
                    )
                  }
              )

              // Floating Score Popup
              val popup = gameEngine.scorePopup
              if (popup != null) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = popup.text,
                    fontFamily = LilitaOneFontFamily,
                    fontSize = 22.sp,
                    color = ArcadeGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                      .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                      .padding(horizontal = 12.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Right Side Panel: NEXT Piece + Stats
          SidePanel(
            title = "NEXT",
            pieceType = gameEngine.nextPiece,
            assetHelper = assetHelper,
            modifier = Modifier.width(72.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Ergonomic Arcade Controls
      ArcadeControllerBar(
        gameEngine = gameEngine,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // In-Game Overlays
    if (gameEngine.gameState == GameState.PAUSED) {
      PauseDialog(
        gameEngine = gameEngine,
        assetHelper = assetHelper,
        onDismiss = { gameEngine.resumeGame() }
      )
    }

    if (gameEngine.gameState == GameState.GAME_OVER) {
      GameOverDialog(
        gameEngine = gameEngine,
        assetHelper = assetHelper,
        onDismiss = { gameEngine.startNewGame() }
      )
    }
  }
}

@Composable
private fun GameTopBar(
  gameEngine: GameEngine,
  soundManager: SoundManager,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Back / Pause Button
    IconButton(
      onClick = { gameEngine.pauseGame() },
      modifier = Modifier
        .size(44.dp)
        .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
        .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
        .testTag("pause_button")
    ) {
      Icon(
        imageVector = Icons.Default.Pause,
        contentDescription = "Pause Game",
        tint = NeonCyan
      )
    }

    // Score & Stats Center Badge
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "SCORE",
          fontFamily = LilitaOneFontFamily,
          fontSize = 11.sp,
          color = ArcadeGold
        )
        Text(
          text = "${gameEngine.score}",
          fontFamily = LilitaOneFontFamily,
          fontSize = 20.sp,
          color = Color.White
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "LEVEL",
          fontFamily = LilitaOneFontFamily,
          fontSize = 11.sp,
          color = NeonCyan
        )
        Text(
          text = "${gameEngine.level}",
          fontFamily = LilitaOneFontFamily,
          fontSize = 20.sp,
          color = Color.White
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "LINES",
          fontFamily = LilitaOneFontFamily,
          fontSize = 11.sp,
          color = NeonAmber
        )
        Text(
          text = "${gameEngine.linesCleared}",
          fontFamily = LilitaOneFontFamily,
          fontSize = 20.sp,
          color = Color.White
        )
      }
    }

    // Theme & Audio Controls
    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      IconButton(
        onClick = { gameEngine.toggleTheme() },
        modifier = Modifier
          .size(40.dp)
          .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
          .border(1.dp, Color(gameEngine.currentTheme.accentColorHex), CircleShape)
          .testTag("in_game_theme_button")
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = "Toggle Theme",
          tint = Color(gameEngine.currentTheme.accentColorHex)
        )
      }

      IconButton(
        onClick = { soundManager.toggleMusic() },
        modifier = Modifier
          .size(40.dp)
          .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
          .testTag("in_game_music_button")
      ) {
        Icon(
          imageVector = if (soundManager.isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
          contentDescription = "Toggle Music",
          tint = if (soundManager.isMusicEnabled) NeonCyan else Color.Gray,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
private fun SidePanel(
  title: String,
  pieceType: PieceType?,
  assetHelper: AssetManagerHelper,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = CyberDarkSurface.copy(alpha = 0.8f),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.2f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = title,
        fontFamily = LilitaOneFontFamily,
        fontSize = 12.sp,
        color = NeonCyan,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Piece Preview Mini Grid (4x4)
      Box(
        modifier = Modifier
          .size(56.dp)
          .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        if (pieceType != null) {
          Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val blocks = pieceType.getBlocks(0)
            val minX = blocks.minOf { it.x }
            val maxX = blocks.maxOf { it.x }
            val minY = blocks.minOf { it.y }
            val maxY = blocks.maxOf { it.y }
            val spanX = maxX - minX + 1
            val spanY = maxY - minY + 1

            val cellSize = (size.width / 4f).coerceAtMost(size.height / 4f)
            val offsetX = (size.width - spanX * cellSize) / 2f
            val offsetY = (size.height - spanY * cellSize) / 2f

            val blockBitmap = if (pieceType.defaultCellType == GameConstants.CELL_CYAN) {
              assetHelper.cyanBlockBitmap
            } else {
              assetHelper.blueBlockBitmap
            }

            for (p in blocks) {
              val px = offsetX + (p.x - minX) * cellSize
              val py = offsetY + (p.y - minY) * cellSize

              if (blockBitmap != null) {
                drawImage(
                  image = blockBitmap,
                  dstOffset = IntOffset(px.toInt(), py.toInt()),
                  dstSize = IntSize(cellSize.toInt(), cellSize.toInt())
                )
              } else {
                drawRect(
                  color = if (pieceType.defaultCellType == GameConstants.CELL_CYAN) NeonCyan else NeonBlue,
                  topLeft = Offset(px, py),
                  size = Size(cellSize - 1f, cellSize - 1f)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun GameBoardCanvas(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  modifier: Modifier = Modifier
) {
  val board = gameEngine.board
  val activePiece = gameEngine.activePiece
  val clearingLines = gameEngine.clearingLines
  val blueBitmap = assetHelper.blueBlockBitmap
  val cyanBitmap = assetHelper.cyanBlockBitmap

  Canvas(modifier = modifier) {
    val cellW = size.width / GameConstants.BOARD_WIDTH.toFloat()
    val cellH = size.height / GameConstants.BOARD_HEIGHT.toFloat()

    // 1. Draw subtle background grid lines
    for (x in 0..GameConstants.BOARD_WIDTH) {
      val px = x * cellW
      drawLine(
        color = Color.White.copy(alpha = 0.06f),
        start = Offset(px, 0f),
        end = Offset(px, size.height),
        strokeWidth = 1f
      )
    }
    for (y in 0..GameConstants.BOARD_HEIGHT) {
      val py = y * cellH
      drawLine(
        color = Color.White.copy(alpha = 0.06f),
        start = Offset(0f, py),
        end = Offset(size.width, py),
        strokeWidth = 1f
      )
    }

    // 2. Draw locked blocks on the board
    for (y in 0 until GameConstants.BOARD_HEIGHT) {
      val isRowClearing = clearingLines.contains(y)
      for (x in 0 until GameConstants.BOARD_WIDTH) {
        val cell = board.grid[y][x]
        if (cell != GameConstants.CELL_EMPTY) {
          val px = x * cellW
          val py = y * cellH

          if (isRowClearing) {
            // Flash white on line clear
            drawRect(
              color = Color.White,
              topLeft = Offset(px, py),
              size = Size(cellW, cellH)
            )
          } else {
            drawBlockCell(cell, blueBitmap, cyanBitmap, px, py, cellW, cellH)
          }
        }
      }
    }

    // 3. Draw Ghost Piece projection
    if (activePiece != null && clearingLines.isEmpty()) {
      val ghostY = board.getGhostY(activePiece)
      val ghostBlocks = activePiece.getAbsoluteBlocks(atY = ghostY)
      for (p in ghostBlocks) {
        val px = p.x * cellW
        val py = p.y * cellH
        drawRect(
          color = Color.White.copy(alpha = 0.25f),
          topLeft = Offset(px + 1f, py + 1f),
          size = Size(cellW - 2f, cellH - 2f),
          style = Stroke(width = 2f)
        )
      }

      // 4. Draw Active Piece using blueblock.png / cyanblock.png
      val activeBlocks = activePiece.getAbsoluteBlocks()
      for (p in activeBlocks) {
        val px = p.x * cellW
        val py = p.y * cellH
        drawBlockCell(activePiece.cellType, blueBitmap, cyanBitmap, px, py, cellW, cellH)
      }
    }
  }
}

private fun DrawScope.drawBlockCell(
  cellType: Int,
  blueBitmap: ImageBitmap?,
  cyanBitmap: ImageBitmap?,
  px: Float,
  py: Float,
  cellW: Float,
  cellH: Float
) {
  val targetBitmap = if (cellType == GameConstants.CELL_CYAN) cyanBitmap else blueBitmap
  if (targetBitmap != null) {
    drawImage(
      image = targetBitmap,
      dstOffset = IntOffset(px.toInt(), py.toInt()),
      dstSize = IntSize(cellW.toInt(), cellH.toInt())
    )
  } else {
    // Fallback vector drawing with bevel
    val baseColor = if (cellType == GameConstants.CELL_CYAN) NeonCyan else NeonBlue
    drawRect(
      color = baseColor,
      topLeft = Offset(px + 1f, py + 1f),
      size = Size(cellW - 2f, cellH - 2f)
    )
    drawRect(
      color = Color.White.copy(alpha = 0.4f),
      topLeft = Offset(px + 2f, py + 2f),
      size = Size(cellW * 0.35f, cellH * 0.35f)
    )
  }
}

@Composable
private fun ArcadeControllerBar(
  gameEngine: GameEngine,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Row 1: Rotation & Utility Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Rotate Counter-Clockwise
      ControllerButton(
        onClick = { gameEngine.rotateCounterClockwise() },
        label = "⟲ ROT",
        color = NeonCyan,
        icon = Icons.AutoMirrored.Filled.RotateLeft,
        testTag = "btn_rotate_ccw"
      )

      // Hold Piece Button
      ControllerButton(
        onClick = { gameEngine.holdCurrentPiece() },
        label = "HOLD",
        color = NeonAmber,
        icon = Icons.Default.Lock,
        testTag = "btn_hold"
      )

      // Hard Drop (Lightning Fast Drop)
      ControllerButton(
        onClick = { gameEngine.hardDrop() },
        label = "⚡ DROP",
        color = ArcadeGold,
        icon = Icons.Default.Bolt,
        testTag = "btn_hard_drop"
      )

      // Rotate Clockwise
      ControllerButton(
        onClick = { gameEngine.rotateClockwise() },
        label = "ROT ⟳",
        color = NeonCyan,
        icon = Icons.AutoMirrored.Filled.RotateRight,
        testTag = "btn_rotate_cw"
      )
    }

    // Row 2: Directional D-Pad Controls
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Button
      DirectionalButton(
        onClick = { gameEngine.moveLeft() },
        icon = Icons.AutoMirrored.Filled.ArrowLeft,
        contentDesc = "Move Left",
        testTag = "btn_left"
      )

      // Soft Down Button
      DirectionalButton(
        onClick = { gameEngine.softDrop() },
        icon = Icons.Default.ArrowDownward,
        contentDesc = "Soft Drop",
        testTag = "btn_down"
      )

      // Right Button
      DirectionalButton(
        onClick = { gameEngine.moveRight() },
        icon = Icons.AutoMirrored.Filled.ArrowRight,
        contentDesc = "Move Right",
        testTag = "btn_right"
      )
    }
  }
}

@Composable
private fun ControllerButton(
  onClick: () -> Unit,
  label: String,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  testTag: String
) {
  Box(
    modifier = Modifier
      .height(48.dp)
      .width(76.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(CyberDarkSurface.copy(alpha = 0.9f))
      .border(1.5.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = color,
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = label,
        fontFamily = LilitaOneFontFamily,
        fontSize = 11.sp,
        color = Color.White
      )
    }
  }
}

@Composable
private fun DirectionalButton(
  onClick: () -> Unit,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDesc: String,
  testTag: String
) {
  Box(
    modifier = Modifier
      .height(52.dp)
      .width(96.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.verticalGradient(
          listOf(CyberDarkSurface, Color(0xFF1E293B))
        )
      )
      .border(2.dp, NeonBlue.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDesc,
      tint = Color.White,
      modifier = Modifier.size(32.dp)
    )
  }
}
