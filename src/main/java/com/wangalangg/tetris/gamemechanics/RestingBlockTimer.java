package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.CycleManager;
import com.wangalangg.tetris.gamemechanics.matrix.Matrix;

import javafx.animation.AnimationTimer;

public class RestingBlockTimer extends AnimationTimer {

	private CycleManager cycleManager;
	private Runnable landBlock;
	private Matrix matrix;
	private boolean started;
	private int chancesLeft = 16;

	public RestingBlockTimer(CycleManager cycleManager, Runnable landBlock, Matrix matrix) {
		super();
		this.cycleManager = cycleManager;
		this.landBlock = landBlock;
		this.matrix = matrix;
	}

	@Override
	public void start() {
		super.start();
		chancesLeft = 16;
		started = true;
	}

	@Override
	public void stop() {
		super.stop();
		started = false;
	}

	@Override
	public void handle(long now) {
		cycleManager.setCurrentTime(now);

		if (!cycleManager.isPaused()) {
			// If a block moves within 0.65 seconds, dec chancesLeft
			if ((cycleManager.didTimeExceed(0.65) && matrix.canShiftDown()) || chancesLeft < 0) {
				// Else land block
				landBlock.run();
				started = false;
				stop();
			}
		}
	}

	public void blockMoved() {
		if (matrix.canShiftDown()) {
			chancesLeft--;
			cycleManager.restartTimer();
		}
	}

	public boolean started() {
		return started;
	}
}
