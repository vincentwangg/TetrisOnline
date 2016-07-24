package com.wangalangg.tetris.gamemechanics.blocks;

public interface Block {

	int[][] getRotatedMatrix(int rotation);

	int[][] getGhostMatrix(int rotation);
}
