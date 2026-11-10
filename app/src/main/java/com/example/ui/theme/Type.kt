package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val LilitaOneFontFamily = FontFamily(
  Font(R.font.lilita_one, FontWeight.Normal)
)

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 52.sp,
    lineHeight = 58.sp,
    letterSpacing = 1.sp
  ),
  displayMedium = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 38.sp,
    lineHeight = 44.sp,
    letterSpacing = 0.5.sp
  ),
  headlineLarge = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 30.sp,
    lineHeight = 36.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 30.sp
  ),
  titleLarge = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 26.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp
  ),
  labelLarge = TextStyle(
    fontFamily = LilitaOneFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 20.sp
  )
)
