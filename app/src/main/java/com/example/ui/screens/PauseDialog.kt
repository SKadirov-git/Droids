package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.assets.AssetManagerHelper
import com.example.game.GameEngine
import com.example.ui.theme.ArcadeGold
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.LilitaOneFontFamily
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan

@Composable
fun PauseDialog(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CyberDarkSurface,
      border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "GAME PAUSED",
          fontFamily = LilitaOneFontFamily,
          fontSize = 32.sp,
          color = NeonCyan,
          fontWeight = FontWeight.Bold
        )

        // Score summary
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "SCORE",
              fontFamily = LilitaOneFontFamily,
              fontSize = 13.sp,
              color = ArcadeGold
            )
            Text(
              text = "${gameEngine.score}",
              fontFamily = LilitaOneFontFamily,
              fontSize = 24.sp,
              color = Color.White
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "LEVEL",
              fontFamily = LilitaOneFontFamily,
              fontSize = 13.sp,
              color = NeonCyan
            )
            Text(
              text = "${gameEngine.level}",
              fontFamily = LilitaOneFontFamily,
              fontSize = 24.sp,
              color = Color.White
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "LINES",
              fontFamily = LilitaOneFontFamily,
              fontSize = 13.sp,
              color = NeonBlue
            )
            Text(
              text = "${gameEngine.linesCleared}",
              fontFamily = LilitaOneFontFamily,
              fontSize = 24.sp,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Resume Button
        val playBitmap = assetHelper.buttonPlayBitmap
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.resumeGame()
            }
            .testTag("pause_resume_button"),
          contentAlignment = Alignment.Center
        ) {
          if (playBitmap != null) {
            Image(
              bitmap = playBitmap,
              contentDescription = "Resume Button",
              modifier = Modifier.fillMaxWidth(),
              contentScale = ContentScale.FillBounds
            )
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(NeonCyan, RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "RESUME",
                fontFamily = LilitaOneFontFamily,
                fontSize = 22.sp,
                color = Color.Black
              )
            }
          }
        }

        // Restart Button
        val blueBitmap = assetHelper.buttonBlueBitmap
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.startNewGame()
            }
            .testTag("pause_restart_button"),
          contentAlignment = Alignment.Center
        ) {
          if (blueBitmap != null) {
            Image(
              bitmap = blueBitmap,
              contentDescription = "Restart Button",
              modifier = Modifier.fillMaxWidth(),
              contentScale = ContentScale.FillBounds
            )
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(NeonBlue, RoundedCornerShape(16.dp))
            )
          }
          Text(
            text = "RESTART",
            fontFamily = LilitaOneFontFamily,
            fontSize = 20.sp,
            color = Color.White
          )
        }

        // Change Theme Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.toggleTheme()
            }
            .testTag("pause_theme_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "THEME: ${gameEngine.currentTheme.displayName.uppercase()}",
            fontFamily = LilitaOneFontFamily,
            fontSize = 16.sp,
            color = Color.White
          )
        }

        // Main Menu Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .border(1.5.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.returnToMenu()
            }
            .testTag("pause_menu_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "MAIN MENU",
            fontFamily = LilitaOneFontFamily,
            fontSize = 16.sp,
            color = Color(0xFFFF6B6B)
          )
        }
      }
    }
  }
}
