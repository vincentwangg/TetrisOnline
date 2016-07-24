package com.wangalangg.tetris.gamemechanics.blocks;

public class LBlock implements Block {

	private static final int[][][] fullMatrix =
			{
					// Rotation 0
					{
							{0, 0, 3, 0},
							{3, 3, 3, 0},
							{0, 0, 0, 0},
							{0, 0, 0, 0}
					},

					// Rotation 1
					{
							{0, 3, 0, 0},
							{0, 3, 0, 0},
							{0, 3, 3, 0},
							{0, 0, 0, 0}
					},

					// Rotation 2
					{
							{0, 0, 0, 0},
							{3, 3, 3, 0},
							{3, 0, 0, 0},
							{0, 0, 0, 0}
					},

					// Rotation 3
					{
							{3, 3, 0, 0},
							{0, 3, 0, 0},
							{0, 3, 0, 0},
							{0, 0, 0, 0}
					}
			};

	private static final int[][][] ghostMatrix =
			{
					// Rotation 0
					{
							{0, 0, -1, 0},
							{-1, -1, -1, 0},
							{0, 0, 0, 0},
							{0, 0, 0, 0}
					},

					// Rotation 1
					{
							{0, -1, 0, 0},
							{0, -1, 0, 0},
							{0, -1, -1, 0},
							{0, 0, 0, 0}
					},

					// Rotation 2
					{
							{0, 0, 0, 0},
							{-1, -1, -1, 0},
							{-1, 0, 0, 0},
							{0, 0, 0, 0}
					},

					// Rotation 3
					{
							{-1, -1, 0, 0},
							{0, -1, 0, 0},
							{0, -1, 0, 0},
							{0, 0, 0, 0}
					}
			};

	@Override
	public int[][] getRotatedMatrix(int rotation) {
		return fullMatrix[rotation];
	}

	@Override
	public int[][] getGhostMatrix(int rotation) {
		return ghostMatrix[rotation];
	}
}
