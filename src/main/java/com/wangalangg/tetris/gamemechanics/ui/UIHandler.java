package com.wangalangg.tetris.gamemechanics.ui;

import com.wangalangg.tetris.gamemechanics.blocks.BlockManager;
import com.wangalangg.tetris.gamemechanics.matrix.Matrix;
import com.wangalangg.tetris.gamemechanics.matrix.Score;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UIHandler {

	private GridPane tetrisGrid;
	private ImageView holdBlock;
	private ImageView[] nextBlocks;
	private ImageLoader imageLoader;
	private Matrix matrix;
	private BlockManager blockManager;
	private Score score;

	public UIHandler(GridPane tetrisGrid, ImageView holdBlock, ImageLoader imageLoader,
					 ImageView[] nextBlocks, Matrix matrix, BlockManager blockManager, Score score) {
		this.tetrisGrid = tetrisGrid;
		this.holdBlock = holdBlock;
		this.nextBlocks = nextBlocks;
		this.imageLoader = imageLoader;
		this.matrix = matrix;
		this.blockManager = blockManager;
		this.score = score;

		// Create Tetris area
		for (int row = 0; row < Matrix.HEIGHT; row++) {
			for (int col = 0; col < Matrix.WIDTH; col++) {
				tetrisGrid.add(new Rectangle(30, 30), col, row);
			}
		}
	}

	public void update() {
		updateImageViews();
		updateGridColors();
		score.update();
	}

	private void updateImageViews() {
		if (blockManager.getHoldBlock() != null) {
			holdBlock.setImage(imageLoader.getImage(blockManager.getHoldBlock()));
		}
		for (int i = 0; i < 5; i++) {
			nextBlocks[i].setImage(imageLoader.getImage(blockManager.getQueuedBlock(i)));
		}
	}

	private void updateGridColors() {
		for (int row = 0; row < Matrix.HEIGHT; row++) {
			for (int col = 0; col < Matrix.WIDTH; col++) {
				int color = matrix.getVisualActiveValue(row, col);
				Rectangle rectangle = getRectangleFromGrid(col, row);
				if (rectangle != null) {
					rectangle.setFill(getColorFromInt(color));
				}
			}
		}
	}

	private Rectangle getRectangleFromGrid(int col, int row) {
		for (Node node : tetrisGrid.getChildren()) {
			if (node instanceof Rectangle &&
					GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
				return (Rectangle) node;
			}
		}
		return null;
	}

	public Color getColorFromInt(int color) {
		switch (color) {
			case -1:
				return Color.DARKGRAY;
			case 0:
				return Color.LIGHTGRAY;
			case 1:
				return Color.DEEPSKYBLUE;
			case 2:
				return Color.DARKBLUE;
			case 3:
				return Color.ORANGE;
			case 4:
				return Color.YELLOW;
			case 5:
				return Color.GREEN;
			case 6:
				return Color.PURPLE;
			case 7:
				return Color.ORANGERED;
			default:
				throw new IllegalStateException("Controller: Weird color number - " + color);
		}
	}
}
