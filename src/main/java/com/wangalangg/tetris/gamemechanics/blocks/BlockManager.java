package com.wangalangg.tetris.gamemechanics.blocks;

public class BlockManager {

	private Blocks holdBlock, currentBlock;
	private Blocks[] queued;
	private RandomBlockGenerator generator;

	public BlockManager() {
		generator = new RandomBlockGenerator();
		queued = new Blocks[5];
		init();
	}

	public void init() {
		for (int i = 0; i < queued.length; i++) {
			queued[i] = generator.nextBlock();
		}
		nextBlock();
	}

	public Blocks getHoldBlock() {
		return holdBlock;
	}

	public Blocks getCurrentBlock() {
		return currentBlock;
	}

	public Blocks getQueuedBlock(int pos) {
		return queued[pos];
	}

	public void holdBlock() {
		if (holdBlock == null) {
			holdBlock = currentBlock;
			currentBlock = queued[0];
			proceedQueue();
		}
		else {
			Blocks tmp = currentBlock;
			currentBlock = holdBlock;
			holdBlock = tmp;
		}
	}

	public void nextBlock() {
		currentBlock = queued[0];
		proceedQueue();
	}

	private void proceedQueue() {
		System.arraycopy(queued, 1, queued, 0, 4);
		queued[4] = generator.nextBlock();
	}

	public void reset() {
		generator.reset();
		holdBlock = null;
		init();
	}
}
