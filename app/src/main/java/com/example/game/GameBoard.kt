package com.example.game

class GameBoard {
  val width: Int = GameConstants.BOARD_WIDTH
  val height: Int = GameConstants.BOARD_HEIGHT

  // 0: Empty, 1: Blue block, 2: Cyan block
  val grid: Array<IntArray> = Array(height) { IntArray(width) { GameConstants.CELL_EMPTY } }

  fun reset() {
    for (y in 0 until height) {
      for (x in 0 until width) {
        grid[y][x] = GameConstants.CELL_EMPTY
      }
    }
  }

  fun isValid(points: List<Point>): Boolean {
    for (p in points) {
      if (p.x !in 0 until width || p.y !in 0 until height) {
        return false
      }
      if (grid[p.y][p.x] != GameConstants.CELL_EMPTY) {
        return false
      }
    }
    return true
  }

  fun canPlace(piece: ActivePiece, newX: Int, newY: Int, newRot: Int): Boolean {
    val blocks = piece.getAbsoluteBlocks(atX = newX, atY = newY, atRot = newRot)
    return isValid(blocks)
  }

  fun getGhostY(piece: ActivePiece): Int {
    var ghostY = piece.y
    while (canPlace(piece, piece.x, ghostY + 1, piece.rotation)) {
      ghostY++
    }
    return ghostY
  }

  /**
   * Locks the active piece onto the board.
   * Returns true if piece was successfully locked, false if locked out of bounds (Game Over).
   */
  fun lockPiece(piece: ActivePiece): Boolean {
    val blocks = piece.getAbsoluteBlocks()
    for (p in blocks) {
      if (p.y < 0 || p.y >= height || p.x < 0 || p.x >= width) {
        return false
      }
      grid[p.y][p.x] = piece.cellType
    }
    return true
  }

  fun getCompletedLines(): List<Int> {
    val completed = mutableListOf<Int>()
    for (y in 0 until height) {
      var isFull = true
      for (x in 0 until width) {
        if (grid[y][x] == GameConstants.CELL_EMPTY) {
          isFull = false
          break
        }
      }
      if (isFull) {
        completed.add(y)
      }
    }
    return completed
  }

  fun clearLines(linesToClear: List<Int>) {
    if (linesToClear.isEmpty()) return
    // Remove completed rows and push new empty rows from the top
    val sortedLines = linesToClear.sorted()
    for (line in sortedLines) {
      for (y in line downTo 1) {
        for (x in 0 until width) {
          grid[y][x] = grid[y - 1][x]
        }
      }
      // Top row is cleared
      for (x in 0 until width) {
        grid[0][x] = GameConstants.CELL_EMPTY
      }
    }
  }

  fun copyGrid(): Array<IntArray> {
    return Array(height) { y -> grid[y].clone() }
  }
}
