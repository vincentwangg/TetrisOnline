package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.CycleManager;
import com.wangalangg.tetris.gamemechanics.blocks.BlockManager;
import com.wangalangg.tetris.gamemechanics.blocks.Blocks;
import com.wangalangg.tetris.gamemechanics.gamemodes.GameMode;
import com.wangalangg.tetris.gamemechanics.matrix.BlockInfo;
import com.wangalangg.tetris.gamemechanics.matrix.Matrix;
import com.wangalangg.tetris.gamemechanics.ui.UIHandler;
import com.wangalangg.tetris.gamemechanics.ui.UIPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;

/**
 * Responsibilities:
 * - Generating the next blocks
 * - Keep track of current holding block
 * - Send the next block to the matrix object
 * - Keep track of game time
 * - UI control
 */
public abstract class SPGame {

	private boolean holdUsed = false;
	private Matrix matrix;
	private BlockInfo currentBlock;
	private CycleManager gameCycle;
	private BlockManager blockManager;
	private AnimationTimer rotateLeftTimer, rotateRightTimer, shiftLeftTimer, shiftRightTimer;
	private AnimationTimer softDropTimer, hardDropTimer, gameTimer;
	private RestingBlockTimer restingBlockTimer;
	private List<CycleManager> allCyclers = new ArrayList<>();
	private GameMode gameMode;
	protected UIHandler uiHandler;

	public SPGame(UIPackage uiPackage, GameMode gameMode) {
		gameCycle = new CycleManager();
		allCyclers.add(gameCycle);
		blockManager = new BlockManager();
		currentBlock = new BlockInfo() {
			@Override
			public void blockChanged() {
				onBlockMoved();
				restingBlockTimer.blockMoved();
			}
		};
		this.gameMode = gameMode;
		matrix = new Matrix(currentBlock, gameCycle, gameMode);
		uiHandler = new UIHandler(uiPackage, matrix, blockManager, this.gameMode);

		softDropTimer = createSoftDropTimer(() -> matrix.handleSoftDrop());
		hardDropTimer = createOneClickTimer(() -> {
			matrix.handleHardDrop();
			onBlockFallen();
		});
		rotateLeftTimer = createOneClickTimer(() -> matrix.handleRotateLeft());
		rotateRightTimer = createOneClickTimer(() -> matrix.handleRotateRight());
		shiftLeftTimer = createShiftTimer(() -> matrix.handleShiftLeft());
		shiftRightTimer = createShiftTimer(() -> matrix.handleShiftRight());
		restingBlockTimer = createRestingBlockTimer();
		createGameTimer();
	}

	private void createGameTimer() {
		gameTimer = new AnimationTimer() {
			private boolean canStepFrame = true;
			private boolean isFirstCycle = true;

			@Override
			public void handle(long now) {
				// Keep track of game cycle
				gameCycle.setCurrentTime(now);

				if (!isFirstCycle && gameCycle.didTimeExceed(gameMode.getStepTime()) && !gameCycle.isPaused()) {
					gameCycle.restartTimer();

					if (matrix.doesCurrentBlockExist()) {
						canStepFrame = matrix.stepFrame();
					}

					// If block hit bottom, give 2 cycles for the player to finalize their decision
					//  or move the piece under other pieces
					if (matrix.doesCurrentBlockExist()
							&& !canStepFrame
							&& !restingBlockTimer.started()) {
						restingBlockTimer.start();
					}
				}

				if (isFirstCycle) {
					addBlockToMatrix(blockManager.getCurrentBlock());
					isFirstCycle = false;
					gameCycle.restartTimer();
				}

				uiHandler.update();
			}
		};
	}

	private AnimationTimer createSoftDropTimer(Runnable action) {
		CycleManager shiftCycle = new CycleManager();
		allCyclers.add(shiftCycle);
		return new AnimationTimer() {
			boolean isFirstShift = true;

			@Override
			public void handle(long now) {
				shiftCycle.setCurrentTime(now);

				if (!shiftCycle.isPaused()) {
					// Process is done backwards to ensure everything runs once
					//  e.g. if shifted first, isFirstShift will become false, making delay2Secs
					//       method run, making isSecondShift true, making the shiftEvery1Sec run,
					//       shifting the block twice

					// Shift every 0.05 sec
					if (!isFirstShift && shiftCycle.didTimeExceed(0.05)) {
						shiftDown();
					}

					// Shift first
					if (isFirstShift) {
						isFirstShift = false;
						shiftDown();
					}
				}
			}

			private void shiftDown() {
				shiftCycle.restartTimer();
				action.run();
				gameCycle.restartTimer();
			}
		};
	}

	private AnimationTimer createShiftTimer(Runnable shiftAction) {
		CycleManager shiftCycle = new CycleManager();
		allCyclers.add(shiftCycle);
		return new AnimationTimer() {
			boolean isFirstShift = true;
			boolean isSecondShift = false;

			@Override
			public void handle(long now) {
				shiftCycle.setCurrentTime(now);

				if (!shiftCycle.isPaused()) {
					// Process is done backwards to ensure everything runs once
					//  e.g. if shifted first, isFirstShift will become false, making delay2Secs
					//       method run, making isSecondShift true, making the shiftEvery1Sec run,
					//       shifting the block twice

					// Shift every 1 sec
					if (isSecondShift && shiftCycle.didTimeExceed(0.05)) {
						shiftCycle.restartTimer();
						shiftAction.run();
					}

					// Delay 2 secs
					if (!isFirstShift && shiftCycle.didTimeExceed(0.2)) {
						shiftCycle.restartTimer();
						isSecondShift = true;
					}

					// Shift first
					if (isFirstShift) {
						isFirstShift = false;
						shiftCycle.restartTimer();
						shiftAction.run();
					}
				}
			}

			@Override
			public void stop() {
				super.stop();
				isFirstShift = true;
				isSecondShift = false;
			}
		};
	}

	/**
	 * Creates a timer for when a key can only be clicked once and won't continue to be pressed
	 * after being held down
	 */
	private AnimationTimer createOneClickTimer(Runnable action) {
		CycleManager cycleMgr = new CycleManager();
		allCyclers.add(cycleMgr);
		return new AnimationTimer() {
			boolean isFirstRotation = true;

			@Override
			public void handle(long now) {
				cycleMgr.setCurrentTime(now);

				if (!cycleMgr.isPaused()) {
					// Rotate first and don't rotate again
					if (isFirstRotation) {
						action.run();
						cycleMgr.restartTimer();
						isFirstRotation = false;
					}
				}
			}

			@Override
			public void stop() {
				super.stop();
				isFirstRotation = true;
			}
		};
	}

	private RestingBlockTimer createRestingBlockTimer() {
		CycleManager cycleMgr = new CycleManager();
		allCyclers.add(cycleMgr);
		return new RestingBlockTimer(cycleMgr, this::onBlockFallen, matrix);
	}

	private void onBlockFallen() {
		onBlockLanded();
		matrix.onBlockFallen();
		holdUsed = false;
		blockManager.nextBlock();
		addBlockToMatrix(blockManager.getCurrentBlock());
		matrix.updateMatrix();
		uiHandler.update();
		restingBlockTimer.stop();
	}

	public void onPressed(KeyCode input) {
		if (matrix.doesCurrentBlockExist() && !gameCycle.isPaused()) {
			switch (input) {
				case DOWN:
					softDropTimer.start();
					break;
				case Z:
				case CONTROL:
					rotateLeftTimer.start();
					break;
				case X:
				case UP:
					rotateRightTimer.start();
					break;
				case RIGHT:
					shiftRightTimer.start();
					break;
				case LEFT:
					shiftLeftTimer.start();
					break;
				case SPACE:
					hardDropTimer.start();
					break;
				case C:
				case SHIFT:
					holdBlock();
					break;
				default:
					break;
			}
		}
	}

	public void onReleased(KeyCode input) {
		if (matrix.doesCurrentBlockExist()) {
			switch (input) {
				case DOWN:
					softDropTimer.stop();
					break;
				case Z:
				case CONTROL:
					rotateLeftTimer.stop();
					break;
				case X:
				case UP:
					rotateRightTimer.stop();
					break;
				case LEFT:
					shiftLeftTimer.stop();
					break;
				case RIGHT:
					shiftRightTimer.stop();
					break;
				case SPACE:
					hardDropTimer.stop();
					break;
				default:
					break;
			}
		}
	}

	private void holdBlock() {
		if (!holdUsed) {
			currentBlock.takeOutOfPlay();
			blockManager.holdBlock();
			holdUsed = true;
			addBlockToMatrix(blockManager.getCurrentBlock());
			gameCycle.restartTimer();
		}
	}

	public void addBlockToMatrix(Blocks block) {
		currentBlock.setBlock(block);
		matrix.getGhostBlock().update();

		// Check to see if player lost
		if (matrix.willNewBlockCollide()) {
			onGameOver();
		} else {
			onNewBlock();
		}
	}

	public void start() {
		gameTimer.start();
	}

	public void pause() {
		for (CycleManager cycleManager : allCyclers) {
			cycleManager.pause();
		}
	}

	public void unpause() {
		for (CycleManager cycleManager : allCyclers) {
			cycleManager.unpause();
		}
	}

	public void restart() {
		matrix.reset();
		blockManager.reset();
		gameCycle.restartTimer();
		addBlockToMatrix(blockManager.getCurrentBlock());
		gameMode.reset();
		matrix.updateMatrix();
		uiHandler.update();
	}

	public JSONObject getMoveBlockDataJson() {
		// Returns block data in following json format:
		// 		{
		//			"row": currentBlock.row(),
		//			"col": currentBlock.col(),
		//			"rotation": currentBlock.getRotation()
		//		}

		JSONObject currentBlockData = new JSONObject();
		try {
			currentBlockData.put("row", currentBlock.row());
			currentBlockData.put("col", currentBlock.col());
			currentBlockData.put("rotation", currentBlock.getRotation());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return currentBlockData;
	}

	public JSONObject getNewBlockDataJson() {
		// Returns new block data in the following format:
		//	{
		//		"block": currentBlock.getBlock()
		//	}

		JSONObject newBlockData = new JSONObject();
		try {
			newBlockData.put("block", currentBlock.getBlock());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return newBlockData;
	}

	// Methods that should only be implemented in multiplayer.
	public abstract void onBlockMoved();

	public abstract void onNewBlock();

	public abstract void onBlockLanded();

	public abstract void onGameOver();
}
