package com.wangalangg.tetris.gamemechanics.blocks;

public class OBlock implements Block {

	private static final int[][] matrix =
			{
					{0, 4, 4, 0},
					{0, 4, 4, 0},
					{0, 0, 0, 0},
					{0, 0, 0, 0}
			};

	private static final int[][] ghostMatrix =
			{
					{0, -1, -1, 0},
					{0, -1, -1, 0},
					{0, 0, 0, 0},
					{0, 0, 0, 0}
			};

	@Override
	public int[][] getRotatedMatrix(int rotation) {
		return matrix;
	}

	@Override
	public int[][] getGhostMatrix(int rotation) {
		return ghostMatrix;
	}
}
