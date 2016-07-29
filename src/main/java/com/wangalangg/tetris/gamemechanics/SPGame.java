package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.CycleManager;
import com.wangalangg.tetris.gamemechanics.blocks.BlockManager;
import com.wangalangg.tetris.gamemechanics.blocks.Blocks;
import com.wangalangg.tetris.gamemechanics.matrix.BlockInfo;
import com.wangalangg.tetris.gamemechanics.matrix.Matrix;
import com.wangalangg.tetris.gamemechanics.matrix.Score;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.gamemechanics.ui.UIHandler;
import com.wangalangg.tetris.gamemechanics.utils.LevelInfo;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * Responsibilities:
 * - Generating the next blocks
 * - Keep track of current holding block
 * - Send the next block to the matrix object
 * - Keep track of game time
 * - UI control
 */
public abstract class SPGame {

	private Matrix matrix;
	private BlockInfo currentBlock;
	private CycleManager gameCycle;
	private boolean holdUsed = false, isPaused = false;
	private BlockManager blockManager;
	private UIHandler uiHandler;
	private AnimationTimer rotateLeftTimer, rotateRightTimer, shiftLeftTimer, shiftRightTimer, softDropTimer;
	private int restingBlockCounter = 0;
	private Score score;

	public SPGame(GridPane tetrisGrid, ImageView holdBlockImage, ImageLoader images,
				  ImageView[] nextBlocksImages, Text points, Text level, Text linesLeft) {
		gameCycle = new CycleManager();
		blockManager = new BlockManager();
		currentBlock = new BlockInfo() {
			@Override
			public void blockChanged() {
				onBlockMoved();
			}
		};
		matrix = new Matrix(currentBlock, gameCycle);
		this.score = new Score(points, level, linesLeft);
		uiHandler = new UIHandler(tetrisGrid, holdBlockImage, images, nextBlocksImages,
				matrix, blockManager, this.score);

		softDropTimer = createSoftDropTimer(() -> matrix.handleSoftDrop());
		rotateLeftTimer = createRotateTimer(() -> matrix.handleRotateLeft());
		rotateRightTimer = createRotateTimer(() -> matrix.handleRotateRight());
		shiftLeftTimer = createShiftTimer(() -> matrix.handleShiftLeft());
		shiftRightTimer = createShiftTimer(() -> matrix.handleShiftRight());
		createGameTimer();
	}

	private void createGameTimer() {
		new AnimationTimer() {
			private boolean canStepFrame = true;
			private boolean isFirstCycle = true;

			@Override
			public void handle(long now) {
				if (!isPaused) {
					// Keep track of game cycle
					gameCycle.setCurrentTime(now);

					if (!isFirstCycle && gameCycle.didTimeExceed(LevelInfo.STEP_TIME[score.getLevel() - 1])) {
						gameCycle.restartTimer();

						if (matrix.doesCurrentBlockExist()) {
							canStepFrame = matrix.stepFrame();
						}

						// If block hit bottom, give 2 cycles for the player to finalize their decision
						//  or move the piece under other pieces
						if (matrix.doesCurrentBlockExist()
								&& !canStepFrame
								&& restingBlockCounter < 1) {
							restingBlockCounter++;
						}

						// Block has fallen
						else if (matrix.doesCurrentBlockExist()
								&& !canStepFrame) {
							onBlockFallen();
						}
					}

					if (isFirstCycle) {
						addBlockToMatrix(blockManager.getCurrentBlock());
						isFirstCycle = false;
						gameCycle.restartTimer();
					}

					uiHandler.update();
				}
			}
		}.start();
	}

	private AnimationTimer createSoftDropTimer(Runnable action) {
		CycleManager shiftCycle = new CycleManager();
		return new AnimationTimer() {
			boolean isFirstShift = true;

			@Override
			public void handle(long now) {
				if (isPaused) stop();

				shiftCycle.setCurrentTime(now);

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

			private void shiftDown() {
				shiftCycle.restartTimer();
				action.run();
				gameCycle.restartTimer();
				// todo if user holds down, next cycle will never come and points will keep going up
				score.softDrop();
			}
		};
	}

	private AnimationTimer createShiftTimer(Runnable shiftAction) {
		CycleManager shiftCycle = new CycleManager();
		return new AnimationTimer() {
			boolean isFirstShift = true;
			boolean isSecondShift = false;

			@Override
			public void handle(long now) {
				if (isPaused) stop();

				shiftCycle.setCurrentTime(now);
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

			@Override
			public void stop() {
				super.stop();
				isFirstShift = true;
				isSecondShift = false;
			}
		};
	}

	private AnimationTimer createRotateTimer(Runnable rotate) {
		CycleManager rotateCycle = new CycleManager();
		return new AnimationTimer() {
			boolean isFirstRotation = true;

			@Override
			public void handle(long now) {
				if (isPaused) stop();

				rotateCycle.setCurrentTime(now);

				// Rotate first and don't rotate again
				if (isFirstRotation) {
					rotate.run();
					rotateCycle.restartTimer();
					isFirstRotation = false;
				}
			}

			@Override
			public void stop() {
				super.stop();
				isFirstRotation = true;
			}
		};
	}

	private void onBlockFallen() {
		onBlockLanded();
		determinePoints(matrix.onBlockFallen());
		restingBlockCounter = 0;
		holdUsed = false;
		blockManager.nextBlock();
		addBlockToMatrix(blockManager.getCurrentBlock());
	}

	private void determinePoints(int linesCleared) {
		switch (linesCleared) {
			case 1:
				score.single();
				break;
			case 2:
				score.duhble();
				break;
			case 3:
				score.triple();
				break;
			case 4:
				score.tetris();
				break;
			default:
				break;
		}
	}

	public void onPressed(KeyCode input) {
		if (input == KeyCode.P) {
			isPaused = !isPaused;
		}
		if (matrix.doesCurrentBlockExist() && !isPaused) {
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
					score.hardDrop(matrix.handleHardDrop());
					onBlockFallen();
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

	public void pause() {
		isPaused = true;
	}

	public void unpause() {
		isPaused = false;
	}

	public void restart() {
		matrix.reset();
		blockManager.reset();
		gameCycle.restartTimer();
		matrix.updateMatrix();
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
