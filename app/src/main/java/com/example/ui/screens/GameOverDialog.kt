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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import com.example.ui.theme.NeonCrimson

@Composable
fun GameOverDialog(
  gameEngine: GameEngine,
  assetHelper: AssetManagerHelper,
  onDismiss: () -> Unit
) {
  val isNewHighScore = gameEngine.score > 0 && gameEngine.score >= gameEngine.highScore

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CyberDarkSurface,
      border = androidx.compose.foundation.BorderStroke(2.dp, NeonCrimson),
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
          text = "GAME OVER",
          fontFamily = LilitaOneFontFamily,
          fontSize = 34.sp,
          color = NeonCrimson,
          fontWeight = FontWeight.Bold
        )

        if (isNewHighScore) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
              .background(ArcadeGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
              .border(1.dp, ArcadeGold, RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "New Record",
              tint = ArcadeGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "NEW HIGH SCORE!",
              fontFamily = LilitaOneFontFamily,
              fontSize = 15.sp,
              color = ArcadeGold
            )
          }
        }

        // Score display
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
        ) {
          Text(
            text = "FINAL SCORE",
            fontFamily = LilitaOneFontFamily,
            fontSize = 13.sp,
            color = Color.LightGray
          )
          Text(
            text = "${gameEngine.score}",
            fontFamily = LilitaOneFontFamily,
            fontSize = 40.sp,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "LINES CLEARED",
                fontFamily = LilitaOneFontFamily,
                fontSize = 11.sp,
                color = Color.Gray
              )
              Text(
                text = "${gameEngine.linesCleared}",
                fontFamily = LilitaOneFontFamily,
                fontSize = 20.sp,
                color = NeonBlue
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "LEVEL REACHED",
                fontFamily = LilitaOneFontFamily,
                fontSize = 11.sp,
                color = Color.Gray
              )
              Text(
                text = "${gameEngine.level}",
                fontFamily = LilitaOneFontFamily,
                fontSize = 20.sp,
                color = NeonCrimson
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play Again Button (with button_play.png)
        val playBitmap = assetHelper.buttonPlayBitmap
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.startNewGame()
            }
            .testTag("game_over_play_again_button"),
          contentAlignment = Alignment.Center
        ) {
          if (playBitmap != null) {
            Image(
              bitmap = playBitmap,
              contentDescription = "Play Again Button",
              modifier = Modifier.fillMaxWidth(),
              contentScale = ContentScale.FillBounds
            )
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D9488), RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "PLAY AGAIN",
                fontFamily = LilitaOneFontFamily,
                fontSize = 22.sp,
                color = Color.White
              )
            }
          }
        }

        // Main Menu Button
        val blueBitmap = assetHelper.buttonBlueBitmap
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              gameEngine.returnToMenu()
            }
            .testTag("game_over_menu_button"),
          contentAlignment = Alignment.Center
        ) {
          if (blueBitmap != null) {
            Image(
              bitmap = blueBitmap,
              contentDescription = "Main Menu Button",
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
            text = "MAIN MENU",
            fontFamily = LilitaOneFontFamily,
            fontSize = 18.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
