package com.wangalangg.tetris.gamemechanics.matrix;

/**
 * Receiving matrix. Receives current block positions and updates the matrix. This matrix is
 * 	purely visual
 */
public class RMatrix {

	public static final int WIDTH = 10, HEIGHT = 20;
	protected static final int ACTUAL_ROWS = 22, ACTUAL_COLS = 12;
	protected BlockInfo currentBlock;
	protected GhostBlock ghostBlock;
	protected int[][] baseMatrix, activeMatrix;
	protected MatrixCollisionHandler collisionHandler;

	public RMatrix(BlockInfo currentBlock) {
		baseMatrix = new int[ACTUAL_ROWS][ACTUAL_COLS];
		activeMatrix = new int[ACTUAL_ROWS][ACTUAL_COLS];
		collisionHandler = new MatrixCollisionHandler(this);
		padMatrices(activeMatrix, baseMatrix);
		this.currentBlock = currentBlock;
		ghostBlock = new GhostBlock(currentBlock, collisionHandler);
	}

	private void padMatrices(int[][]... matrix) {
		for (int[][] m : matrix) {
			for (int row = 0; row < ACTUAL_ROWS; row++) {
				m[row][0] = m[row][ACTUAL_COLS - 1] = 99;
			}
			for (int col = 0; col < ACTUAL_COLS; col++) {
				m[ACTUAL_ROWS - 1][col] = 99;
			}
		}
	}

	/**
	 * Updates matrix visually
	 */
	public void updateMatrix() {
		for (int row = 1; row < ACTUAL_ROWS - 1; row++) {
			for (int col = 1; col < ACTUAL_COLS - 1; col++) {
				activeMatrix[row][col] = baseMatrix[row][col];
				// Set active value = base value + block value when in the current block matrix
				if (currentBlock.doesExist() && isInGhostBlock(row, col)) {
					activeMatrix[row][col] += ghostBlock.blockInfo()
							.value(row - ghostBlock.blockInfo().row(), col - ghostBlock.blockInfo().col());
				}
				if (currentBlock.doesExist()
						&& isInCurrentBlock(row, col)
						&& currentBlock.value(row - currentBlock.row(), col - currentBlock.col()) > 0) {
					activeMatrix[row][col] = currentBlock.value(row - currentBlock.row(), col - currentBlock.col());
				}
			}
		}
	}

	private boolean isInCurrentBlock(int row, int col) {
		return row < currentBlock.row() + 4
				&& row >= currentBlock.row()
				&& col < currentBlock.col() + 4
				&& col >= currentBlock.col();
	}

	private boolean isInGhostBlock(int row, int col) {
		return row < ghostBlock.blockInfo().row() + 4
				&& row >= ghostBlock.blockInfo().row()
				&& col < ghostBlock.blockInfo().col() + 4
				&& col >= ghostBlock.blockInfo().col();
	}

	public boolean doesCurrentBlockExist() {
		return currentBlock.doesExist();
	}

	public int onBlockFallen() {
		mergeBaseMatrix();
		return clearLines();
	}

	/**
	 * Clears lines if possible
	 *
	 * @return number of cleared lines
	 */
	public int clearLines() {
		boolean clearable = true;
		int clearedLines = 0;
		// Loop through lines from top to bottom
		for (int row = 0; row < ACTUAL_ROWS - 1; row++) {
			// Loop through line
			for (int col = 1; col < ACTUAL_COLS - 1; col++) {
				// If the line has a hole in it, set clearable to false
				if (baseMatrix[row][col] == 0) {
					clearable = false;
					break;
				}
			}
			// Clear line if possible and increment clearedLines
			if (clearable) {
				for (int i = row; i > 0; i--) {
					for (int j = ACTUAL_COLS - 2; j > 0; j--) {
						baseMatrix[i][j] = baseMatrix[i - 1][j];
					}
				}
				clearedLines++;
			}
			clearable = true;
		}
		return clearedLines;
	}

	public void mergeBaseMatrix() {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				// Merge current block into base matrix
				if (currentBlock.value(row, col) != 0) {
					baseMatrix[row + currentBlock.row()][col + currentBlock.col()]
							= currentBlock.value(row, col);
				}
			}
		}
		currentBlock.takeOutOfPlay();
		updateMatrix();
	}

	public int currentBlockVal(int row, int col) {
		return currentBlock.value(row, col);
	}

	public GhostBlock getGhostBlock() {
		return ghostBlock;
	}

	public int getVisualActiveValue(int row, int col) {
		return activeMatrix[row + 1][col + 1];
	}

	public int getVisualBaseValue(int row, int col) {
		return baseMatrix[row + 1][col + 1];
	}

	public int getActualActiveValue(int row, int col) {
		return activeMatrix[row][col];
	}

	public int getActualBaseValue(int row, int col) {
		return baseMatrix[row][col];
	}

	public void printMatrices() {
		System.out.println("Active Matrix   Base Matrix\t\tCurrent Block");
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.print(getVisualActiveValue(i, j));
			}
			System.out.print("\t\t");
			for (int j = 0; j < 10; j++) {
				System.out.print(getVisualBaseValue(i, j));
			}
			System.out.print("\t\t");
			if (i < 4) {
				for (int j = 0; j < 4; j++) {
					System.out.print(currentBlockVal(i, j));
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
