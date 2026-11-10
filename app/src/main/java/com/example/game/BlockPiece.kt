package com.example.game

data class Point(val x: Int, val y: Int)

enum class PieceType(
  val defaultCellType: Int,
  val orientations: List<List<Point>>
) {
  // I Piece (Straight Line)
  I(
    defaultCellType = GameConstants.CELL_CYAN,
    orientations = listOf(
      listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1)),
      listOf(Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3)),
      listOf(Point(0, 2), Point(1, 2), Point(2, 2), Point(3, 2)),
      listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))
    )
  ),

  // J Piece
  J(
    defaultCellType = GameConstants.CELL_BLUE,
    orientations = listOf(
      listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(1, 2)),
      listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2)),
      listOf(Point(1, 0), Point(1, 1), Point(0, 2), Point(1, 2))
    )
  ),

  // L Piece
  L(
    defaultCellType = GameConstants.CELL_BLUE,
    orientations = listOf(
      listOf(Point(2, 0), Point(0, 1), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2)),
      listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 2)),
      listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(1, 2))
    )
  ),

  // O Piece (Square)
  O(
    defaultCellType = GameConstants.CELL_CYAN,
    orientations = listOf(
      listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(2, 0), Point(1, 1), Point(2, 1))
    )
  ),

  // S Piece
  S(
    defaultCellType = GameConstants.CELL_CYAN,
    orientations = listOf(
      listOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1)),
      listOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(2, 2)),
      listOf(Point(1, 1), Point(2, 1), Point(0, 2), Point(1, 2)),
      listOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))
    )
  ),

  // T Piece
  T(
    defaultCellType = GameConstants.CELL_BLUE,
    orientations = listOf(
      listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1)),
      listOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(1, 2)),
      listOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2)),
      listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(1, 2))
    )
  ),

  // Z Piece
  Z(
    defaultCellType = GameConstants.CELL_BLUE,
    orientations = listOf(
      listOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1)),
      listOf(Point(2, 0), Point(1, 1), Point(2, 1), Point(1, 2)),
      listOf(Point(0, 1), Point(1, 1), Point(1, 2), Point(2, 2)),
      listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2))
    )
  );

  fun getBlocks(rotation: Int): List<Point> {
    val norm = ((rotation % 4) + 4) % 4
    return orientations[norm]
  }
}

data class ActivePiece(
  val type: PieceType,
  val x: Int = 3,
  val y: Int = 0,
  val rotation: Int = 0,
  val cellType: Int = type.defaultCellType
) {
  fun getAbsoluteBlocks(atX: Int = x, atY: Int = y, atRot: Int = rotation): List<Point> {
    return type.getBlocks(atRot).map { Point(it.x + atX, it.y + atY) }
  }
}
