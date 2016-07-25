package com.wangalangg.tetris.gamemechanics.matrix;

import com.wangalangg.tetris.CycleManager;

/**
 * Responsibilities:
 * - Keep track of the game matrix
 * - Handles gravity for blocks
 * - Handles user input
 * - Add blocks into the matrix
 */
public class Matrix extends RMatrix {

	private CycleManager cycleManager;

	public Matrix(BlockInfo currentBlock, CycleManager cycleManager) {
		super(currentBlock);
		this.cycleManager = cycleManager;
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

	public void handleShiftLeft() {
		currentBlock.shiftLeft();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftRight();
		} else {
			currentBlock.blockChanged();
		}
		ghostBlock.update();
		updateMatrix();
	}

	public void handleShiftRight() {
		currentBlock.shiftRight();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftLeft();
		} else {
			currentBlock.blockChanged();
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
			} else {
				currentBlock.blockChanged();
			}
		} else {
			currentBlock.blockChanged();
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
			} else {
				currentBlock.blockChanged();
			}
		} else {
			currentBlock.blockChanged();
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
		currentBlock.blockChanged();
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
