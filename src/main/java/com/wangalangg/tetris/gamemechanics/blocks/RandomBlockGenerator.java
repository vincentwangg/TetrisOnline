package com.wangalangg.tetris.gamemechanics.blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class RandomBlockGenerator {

	public Random random;
	private Deque<Blocks> nextBlocks;

	public RandomBlockGenerator() {
		random = new Random();
		nextBlocks = new ArrayDeque<>();
	}

	private void fillQueue() {
		List<Integer> nextBlocksInt = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			nextBlocksInt.add(i);
		}
		Collections.shuffle(nextBlocksInt);
		for (int i : nextBlocksInt) {
			nextBlocks.add(getBlockFromInt(i));
		}
	}

	private Blocks getBlockFromInt(int i) {
		switch (i) {
			case 0:
				return Blocks.I;
			case 1:
				return Blocks.J;
			case 2:
				return Blocks.L;
			case 3:
				return Blocks.O;
			case 4:
				return Blocks.S;
			case 5:
				return Blocks.T;
			case 6:
				return Blocks.Z;
			default:
				throw new AssertionError("RandomBlockGenerator: number not in 0-6");
		}
	}

	public Blocks nextBlock() {
		// If queue is empty, generate more blocks
		if (nextBlocks.size() == 0) {
			fillQueue();
		}
		return nextBlocks.pop();
	}
}
