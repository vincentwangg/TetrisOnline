package com.wangalangg.tetris.gamemechanics.matrix;

import com.wangalangg.tetris.CycleManager;
import com.wangalangg.tetris.gamemechanics.gamemodes.GameMode;

/**
 * Responsibilities:
 * - Keep track of the game matrix
 * - Handles gravity for blocks
 * - Handles user input
 * - Add blocks into the matrix
 */
public class Matrix extends VisualMatrix {

	private CycleManager cycleManager;
	private GameMode gameMode;

	public Matrix(BlockInfo currentBlock, CycleManager cycleManager, GameMode gameMode) {
		super(currentBlock);
		this.cycleManager = cycleManager;
		this.gameMode = gameMode;
	}

	public boolean willNewBlockCollide() {
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
		currentBlock.blockChanged();
		updateMatrix();
		return true;
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

	public void handleHardDrop() {
		int lines = 0;
		do {
			currentBlock.shiftDown();
			lines++;
		} while (!collisionHandler.willCurrentBlockCollide());
		currentBlock.shiftUp();
		currentBlock.blockChanged();
		lines--;
		gameMode.hardDrop(lines);
		updateMatrix();
		cycleManager.restartTimer();
	}

	public void handleSoftDrop() {
		currentBlock.shiftDown();
		if (collisionHandler.willCurrentBlockCollide()) {
			currentBlock.shiftUp();
			return;
		} else {
			gameMode.softDrop();
		}
		currentBlock.blockChanged();
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

	@Override
	public int clearLines() {
		int clearedLines = super.clearLines();
		determinePoints(clearedLines);
		return clearedLines;
	}

	private void determinePoints(int linesCleared) {
		switch (linesCleared) {
			case 1:
				gameMode.single();
				break;
			case 2:
				gameMode.duhble();
				break;
			case 3:
				gameMode.triple();
				break;
			case 4:
				gameMode.tetris();
				break;
			default:
				break;
		}
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
