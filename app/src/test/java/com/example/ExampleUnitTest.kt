package com.example

import com.example.game.ActivePiece
import com.example.game.GameBoard
import com.example.game.GameConstants
import com.example.game.PieceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun gameBoard_initialStateIsEmpty() {
    val board = GameBoard()
    assertEquals(GameConstants.BOARD_WIDTH, board.width)
    assertEquals(GameConstants.BOARD_HEIGHT, board.height)
    for (y in 0 until board.height) {
      for (x in 0 until board.width) {
        assertEquals(GameConstants.CELL_EMPTY, board.grid[y][x])
      }
    }
  }

  @Test
  fun gameBoard_collisionDetection() {
    val board = GameBoard()
    val piece = ActivePiece(type = PieceType.O, x = 4, y = 0)
    assertTrue(board.canPlace(piece, piece.x, piece.y, piece.rotation))

    // Horizontal bounds
    assertFalse(board.canPlace(piece, -3, 0, 0))
    assertFalse(board.canPlace(piece, 9, 0, 0))

    // Vertical bounds
    assertFalse(board.canPlace(piece, 4, 20, 0))
  }

  @Test
  fun gameBoard_lineClearAndShift() {
    val board = GameBoard()
    val bottomRow = GameConstants.BOARD_HEIGHT - 1
    for (x in 0 until GameConstants.BOARD_WIDTH) {
      board.grid[bottomRow][x] = GameConstants.CELL_CYAN
    }

    val completed = board.getCompletedLines()
    assertEquals(listOf(bottomRow), completed)

    board.clearLines(completed)
    for (x in 0 until GameConstants.BOARD_WIDTH) {
      assertEquals(GameConstants.CELL_EMPTY, board.grid[bottomRow][x])
    }
  }

  @Test
  fun gameConstants_scoringAndFallSpeed() {
    val speedLvl1 = GameConstants.getFallIntervalMs(1)
    val speedLvl10 = GameConstants.getFallIntervalMs(10)
    assertTrue(speedLvl1 > speedLvl10)
    assertEquals(100L, GameConstants.getFallIntervalMs(20))
  }
}
