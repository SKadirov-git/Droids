package com.example.game

object GameConstants {
  const val BOARD_WIDTH = 10
  const val BOARD_HEIGHT = 20

  // Scoring
  const val SCORE_SINGLE_LINE = 100
  const val SCORE_DOUBLE_LINE = 300
  const val SCORE_TRIPLE_LINE = 500
  const val SCORE_DROID_TETRIS = 800
  const val SCORE_SOFT_DROP = 1
  const val SCORE_HARD_DROP = 2

  // Lines per level
  const val LINES_PER_LEVEL = 10

  // Gravity fall speed in milliseconds per level
  fun getFallIntervalMs(level: Int): Long {
    val ms = 850L - ((level - 1) * 50L)
    return ms.coerceAtLeast(100L)
  }

  // Cell types for rendering block textures
  const val CELL_EMPTY = 0
  const val CELL_BLUE = 1
  const val CELL_CYAN = 2
}
