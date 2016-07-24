package com.wangalangg.tetris.gamemechanics.matrix;

import com.wangalangg.tetris.CycleManager;

/**
 * Responsibilities:
 * - Keep track of the game matrix
 * - Handles gravity for blocks
 * - Handles user input
 * - Add blocks into the matrix
 */
public class Matrix {

	public static final int WIDTH = 10, HEIGHT = 20;
	protected static final int ACTUAL_ROWS = 22, ACTUAL_COLS = 12;
	protected BlockInfo currentBlock;
	protected GhostBlock ghostBlock;
	protected int[][] baseMatrix, activeMatrix;
	protected MatrixCollisionHandler collisionHandler;
	private CycleManager cycleManager;

	public Matrix(BlockInfo currentBlock, CycleManager cycleManager) {
		baseMatrix = new int[ACTUAL_ROWS][ACTUAL_COLS];
		activeMatrix = new int[ACTUAL_ROWS][ACTUAL_COLS];
		collisionHandler = new MatrixCollisionHandler(this);
		padMatrices(activeMatrix, baseMatrix);
		this.currentBlock = currentBlock;
		this.cycleManager = cycleManager;
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

	public boolean checkNewBlockInMatrix() {
		// todo If there is a collision, the player has essentially lost
		if (!collisionHandler.willCurrentBlockCollide()) {
			updateMatrix();
			return false;
		}
		return true;
	}

	/**
	 * Updates values for where the block should be in the matrix
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

	/**
	 * One step for gravity
	 *
	 * @return if the block is still in play
	 */
	public boolean stepFrame() {
		// Shift block down
		currentBlock.shiftDown();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftUp();
			return false;
		}
		updateMatrix();
		return true;
	}

	public int onBlockFallen() {
		mergeBaseMatrix();
		return clearLines();
	}

	public boolean doesCurrentBlockExist() {
		return currentBlock.doesExist();
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

	public void handleShiftLeft() {
		currentBlock.shiftLeft();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftRight();
		}
		ghostBlock.update();
		updateMatrix();
	}

	public void handleShiftRight() {
		currentBlock.shiftRight();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftLeft();
		}
		ghostBlock.update();
		updateMatrix();
	}

	public void handleRotateLeft() {
		currentBlock.rotateLeft();
		if (collisionHandler.willCurrentBlockCollide()) {
			boolean testsDidPass;
			if (currentBlock.isIBlock()) {
				testsDidPass = IWallKickLeftTest();
			} else {
				testsDidPass = nonIWallKickLeftTest();
			}
			if (!testsDidPass) {
				currentBlock.rotateRight();
			}
		}
		ghostBlock.update();
		updateMatrix();
	}

	public void handleRotateRight() {
		currentBlock.rotateRight();
		if (collisionHandler.willCurrentBlockCollide()) {
			boolean testsDidPass;
			if (currentBlock.isIBlock()) {
				testsDidPass = IWallKickRightTest();
			} else {
				testsDidPass = nonIWallKickRightTest();
			}
			if (!testsDidPass) {
				currentBlock.rotateLeft();
			}
		}
		ghostBlock.update();
		updateMatrix();
	}

	public int handleHardDrop() {
		int lines = 0;
		do {
			currentBlock.shiftDown();
			lines++;
		} while (!collisionHandler.willCurrentBlockCollide());
		currentBlock.shiftUp();
		lines--;
		updateMatrix();
		cycleManager.requestNextCycle();
		return lines;
	}

	public void handleSoftDrop() {
		currentBlock.shiftDown();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftUp();
		}
		updateMatrix();
	}

	private boolean nonIWallKickRightTest() {
		for (int[] i : WallKickTest.nonIRightTests[currentBlock.getRotation()]) {
			if (attemptShift(i[0], i[1])) {
				return true;
			}
		}
		return false;
	}

	private boolean nonIWallKickLeftTest() {
		for (int[] i : WallKickTest.nonILeftTests[currentBlock.getRotation()]) {
			if (attemptShift(i[0], i[1])) {
				return true;
			}
		}
		return false;
	}

	private boolean IWallKickLeftTest() {
		for (int[] i : WallKickTest.ILeftTests[currentBlock.getRotation()]) {
			if (attemptShift(i[0], i[1])) {
				return true;
			}
		}
		return false;
	}

	private boolean IWallKickRightTest() {
		for (int[] i : WallKickTest.IRightTests[currentBlock.getRotation()]) {
			if (attemptShift(i[0], i[1])) {
				return true;
			}
		}
		return false;
	}

	private boolean attemptShift(int cols, int rows) {
		currentBlock.save();
		currentBlock.shiftBy(rows, cols);
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.rollBack();
			return false;
		}
		return true;
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
