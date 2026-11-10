package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.assets.AssetManagerHelper
import com.example.assets.GameTheme
import com.example.audio.SoundManager
import com.example.game.GameEngine
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.LilitaOneFontFamily
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan

@Composable
fun MainMenuScreen(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  soundManager: SoundManager,
  modifier: Modifier = Modifier
) {
  var showHelpDialog by remember { mutableStateOf(false) }
  val currentTheme = gameEngine.currentTheme

  // Pulsing animation for logo and title
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    // Dynamic themed arcade background
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

    // Dark semi-transparent overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.4f),
              Color.Black.copy(alpha = 0.65f)
            )
          )
        )
    )

    // Top Action Bar (Audio toggles & Help)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 40.dp, start = 20.dp, end = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Audio controls
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        IconButton(
          onClick = { soundManager.toggleMusic() },
          modifier = Modifier
            .size(48.dp)
            .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
            .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
            .testTag("toggle_music_button")
        ) {
          Icon(
            imageVector = if (soundManager.isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
            contentDescription = "Toggle Music",
            tint = if (soundManager.isMusicEnabled) NeonCyan else Color.Gray
          )
        }

        IconButton(
          onClick = { soundManager.toggleSfx() },
          modifier = Modifier
            .size(48.dp)
            .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
            .border(1.5.dp, NeonAmber.copy(alpha = 0.6f), CircleShape)
            .testTag("toggle_sfx_button")
        ) {
          Icon(
            imageVector = if (soundManager.isSfxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
            contentDescription = "Toggle SFX",
            tint = if (soundManager.isSfxEnabled) NeonAmber else Color.Gray
          )
        }
      }

      // Help button
      IconButton(
        onClick = {
          soundManager.playClick()
          showHelpDialog = true
        },
        modifier = Modifier
          .size(48.dp)
          .background(CyberDarkSurface.copy(alpha = 0.8f), CircleShape)
          .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), CircleShape)
          .testTag("help_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.HelpOutline,
          contentDescription = "How To Play",
          tint = NeonCyan
        )
      }
    }

    // Main Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(60.dp))

      // Droids Icon
      Image(
        painter = painterResource(id = R.drawable.img_droids_icon),
        contentDescription = "Droids Logo",
        modifier = Modifier
          .size(110.dp)
          .scale(pulseScale)
          .clip(RoundedCornerShape(24.dp))
          .border(3.dp, NeonCyan, RoundedCornerShape(24.dp))
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Title in LilitaOne Font
      Text(
        text = "DROIDS",
        fontFamily = LilitaOneFontFamily,
        fontSize = 58.sp,
        fontWeight = FontWeight.Bold,
        color = NeonCyan,
        letterSpacing = 2.sp,
        modifier = Modifier.scale(pulseScale)
      )

      Text(
        text = "ARCADE BLOCK PUZZLE • V1.0.0",
        fontFamily = LilitaOneFontFamily,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.85f),
        letterSpacing = 1.5.sp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // High Score Trophy Banner
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = CyberDarkSurface.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ArcadeGold.copy(alpha = 0.7f)),
        modifier = Modifier
          .widthIn(max = 320.dp)
          .fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Trophy",
            tint = ArcadeGold,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "HIGH SCORE",
              fontFamily = LilitaOneFontFamily,
              fontSize = 12.sp,
              color = ArcadeGold
            )
            Text(
              text = "${gameEngine.highScore}",
              fontFamily = LilitaOneFontFamily,
              fontSize = 26.sp,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Primary PLAY Button using button_play.png asset!
      val playBitmap = assetHelper.buttonPlayBitmap
      Box(
        modifier = Modifier
          .widthIn(max = 300.dp)
          .fillMaxWidth()
          .height(72.dp)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            gameEngine.startNewGame()
          }
          .testTag("play_button"),
        contentAlignment = Alignment.Center
      ) {
        if (playBitmap != null) {
          Image(
            bitmap = playBitmap,
            contentDescription = "Play Button Texture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
          )
        } else {
          // Fallback styled play button
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF14B8A6))),
                RoundedCornerShape(20.dp)
              )
              .border(3.dp, Color(0xFF5EEAD4), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "▶  PLAY",
              fontFamily = LilitaOneFontFamily,
              fontSize = 28.sp,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Theme Selector Button using button_blue.png asset!
      val blueBtnBitmap = assetHelper.buttonBlueBitmap
      Box(
        modifier = Modifier
          .widthIn(max = 300.dp)
          .fillMaxWidth()
          .height(64.dp)
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            gameEngine.toggleTheme()
          }
          .testTag("theme_selector_button"),
        contentAlignment = Alignment.Center
      ) {
        if (blueBtnBitmap != null) {
          Image(
            bitmap = blueBtnBitmap,
            contentDescription = "Theme Selector Texture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(listOf(NeonBlue, Color(0xFF2563EB))),
                RoundedCornerShape(20.dp)
              )
              .border(2.dp, Color(0xFF93C5FD), RoundedCornerShape(20.dp))
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = "Theme",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "THEME: ${currentTheme.displayName.uppercase()}",
            fontFamily = LilitaOneFontFamily,
            fontSize = 17.sp,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }

  // How to Play Dialog
  if (showHelpDialog) {
    AlertDialog(
      onDismissRequest = { showHelpDialog = false },
      containerColor = CyberDarkSurface,
      shape = RoundedCornerShape(20.dp),
      title = {
        Text(
          text = "HOW TO PLAY DROIDS",
          fontFamily = LilitaOneFontFamily,
          fontSize = 22.sp,
          color = NeonCyan
        )
      },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "• Arrange falling polyomino blocks to form complete horizontal lines.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
          )
          Text(
            text = "• Clearing 4 lines at once awards 800 × Level DROID TETRIS bonus!",
            style = MaterialTheme.typography.bodyLarge,
            color = ArcadeGold,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "• Use On-Screen Arcade controls or Gestures (Swipe left/right, tap to rotate, flick down to hard drop).",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
          )
          Text(
            text = "• Switch between Amber Arcade, Neon Cobalt, and Cyber Crimson themes at any time!",
            style = MaterialTheme.typography.bodyLarge,
            color = NeonAmber
          )
          Text(
            text = "• Enjoy classic Korobeiniki 8-bit chiptune audio & retro sound effects.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            soundManager.playClick()
            showHelpDialog = false
          }
        ) {
          Text(
            text = "GOT IT!",
            fontFamily = LilitaOneFontFamily,
            fontSize = 18.sp,
            color = NeonCyan
          )
        }
      }
    )
  }
}
