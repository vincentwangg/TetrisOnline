package com.wangalangg.tetris.gamemechanics.matrix;

public class GhostBlock {

	private MatrixCollisionHandler collisionHandler;
	private BlockInfo currentBlock, ghostBlock;

	public GhostBlock(BlockInfo currentBlock, MatrixCollisionHandler collisionHandler) {
		ghostBlock = new BlockInfo();
		this.currentBlock = currentBlock;
		this.collisionHandler = collisionHandler;
	}

	public BlockInfo blockInfo() {
		return ghostBlock;
	}

	// Called when is rotated or shifted
	public void update() {
		if (currentBlock.doesExist()) {
			ghostBlock.copyBlockAsGhost(currentBlock);
		} else {
			ghostBlock.takeOutOfPlay();
		}
		do {
			ghostBlock.shiftDown();
		} while (!collisionHandler.willGhostBlockCollide());
		ghostBlock.shiftUp();
	}
}
