package com.wangalangg.tetris.gamemechanics.matrix;

class MatrixCollisionHandler {

	private Matrix matrix;

	MatrixCollisionHandler(Matrix matrix) {
		this.matrix = matrix;
	}

	/**
	 * Checks for block collision. Used when block is spawned or rotating
	 *
	 * @return true if collision will occur, false if not
	 */
	boolean willCurrentBlockCollide() {
		// Loop from row up (Works by comparing the matrix coords to the block coords, hence the
		//   weird for loop values
		for (int row = matrix.currentBlock.row(); row < matrix.currentBlock.row() + 4; row++) {
			for (int col = matrix.currentBlock.col(); col < matrix.currentBlock.col() + 4; col++) {
				// Check to make sure the coordinates aren't out of bounds and check for collision
				if (isInBounds(row, col) && willCollideAtPoint(matrix.getActualBaseValue(row, col),
						matrix.currentBlock.value(row - matrix.currentBlock.row(), col - matrix.currentBlock.col()))) {
					return true;
				}
			}
		}
		return false;
	}

	boolean willGhostBlockCollide() {
		// Loop from row up (Works by comparing the matrix coords to the block coords, hence the
		//   weird for loop values
		for (int row = matrix.ghostBlock.blockInfo().row(); row < matrix.ghostBlock.blockInfo().row() + 4; row++) {
			for (int col = matrix.ghostBlock.blockInfo().col(); col < matrix.ghostBlock.blockInfo().col() + 4; col++) {
				// Check to make sure the coordinates aren't out of bounds and check for collision
				if (isInBounds(row, col) && willGhostCollideAtPoint(matrix.getActualBaseValue(row, col),
						matrix.ghostBlock.blockInfo()
								.value(row - matrix.ghostBlock.blockInfo().row(),
										col - matrix.ghostBlock.blockInfo().col()))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < Matrix.ACTUAL_ROWS && col >= 0 && col < Matrix.ACTUAL_COLS;
	}

	/**
	 * Checks for collision at a single point
	 *
	 * @param gameMatrixPointValue game matrix block value at that point (0-6)
	 * @param blockMatrixPointValue block matrix block value at that point (0-6)
	 * @return true if collision will occur, false if not
	 */
	private boolean willCollideAtPoint(int gameMatrixPointValue, int blockMatrixPointValue) {
		boolean gameMatrixBlockExists = gameMatrixPointValue > 0;
		boolean blockMatrixBlockExists = blockMatrixPointValue > 0;
		return gameMatrixBlockExists && blockMatrixBlockExists;
	}

	private boolean willGhostCollideAtPoint(int gameMatrixPointValue, int blockMatrixPointValue) {
		boolean gameMatrixBlockExists = gameMatrixPointValue > 0;
		boolean blockMatrixBlockExists = blockMatrixPointValue < 0;
		return gameMatrixBlockExists && blockMatrixBlockExists;
	}
}
