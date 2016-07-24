package com.wangalangg.tetris.gamemechanics.blocks;

public enum Blocks {

	I(new IBlock(), "I"), // 0
	J(new JBlock(), "J"), // 1
	L(new LBlock(), "L"), // 2
	O(new OBlock(), "O"), // 3
	S(new SBlock(), "S"), // 4
	T(new TBlock(), "T"), // 5
	Z(new ZBlock(), "Z"); // 6

	private Block block;
	private String name;

	Blocks(Block block, String name) {
		this.block = block;
		this.name = name;
	}

	public int[][] getRotatedMatrix(int rotation) {
		return block.getRotatedMatrix(rotation);
	}

	public int[][] getGhostMatrix(int rotation) {
		return block.getGhostMatrix(rotation);
	}

	public String toString() {
		return name;
	}
}
