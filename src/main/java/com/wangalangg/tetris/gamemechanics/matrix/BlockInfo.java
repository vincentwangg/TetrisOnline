package com.wangalangg.tetris.gamemechanics.matrix;

import com.wangalangg.tetris.gamemechanics.blocks.Blocks;

/**
 * Row and Column denotes top left coordinate
 */
public abstract class BlockInfo {

	private Blocks block;
	private int[][] matrix;
	private int rotation, row, col;
	private int savedRow, savedCol;
	private boolean doesExist;

	public BlockInfo() {
		doesExist = false;
	}

	public void copyBlockAsGhost(BlockInfo blockInfo) {
		block = blockInfo.block;
		rotation = blockInfo.rotation;
		row = blockInfo.row;
		col = blockInfo.col;
		savedRow = blockInfo.savedRow;
		savedCol = blockInfo.savedCol;
		doesExist = blockInfo.doesExist;
		matrix = block.getGhostMatrix(rotation);
	}

	public void setBlock(Blocks block) {
		this.block = block;
		rotation = 0;
		matrix = block.getRotatedMatrix(rotation);
		row = 0;
		col = 4;
		save();
		doesExist = true;
	}

	public int row() {
		return row;
	}

	public int col() {
		return col;
	}

	public void save() {
		savedRow = row;
		savedCol = col;
	}

	public void rollBack() {
		row = savedRow;
		col = savedCol;
	}

	public void moveTo(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void shiftBy(int rows, int cols) {
		row += rows;
		col += cols;
	}

	public void shiftLeft() {
		col--;
	}

	public void shiftRight() {
		col++;
	}

	public void shiftUp() {
		row--;
	}

	public void shiftDown() {
		row++;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void rotateLeft() {
		rotation = (rotation + 3) % 4;
		updateMatrix();
	}

	public void rotateRight() {
		rotation = (rotation + 1) % 4;
		updateMatrix();
	}

	public void updateMatrix() {
		matrix = block.getRotatedMatrix(rotation);
	}

	public int value(int row, int col) {
		return matrix[row][col];
	}

	public boolean isIBlock() {
		return block == Blocks.I;
	}

	public Blocks getBlock() {
		return block;
	}

	/**
	 * Removes the current block from the matrix. e.g. when the block is placed and has to be
	 * removed so the next block can be placed in the matrix, takeOutOfPlay() will be called
	 */
	public void takeOutOfPlay() {
		doesExist = false;
	}

	/**
	 * Resets orientation of the block
	 */
	public void reset() {
		setBlock(block);
	}

	public boolean doesExist() {
		return doesExist;
	}

	public abstract void blockChanged();
}
