package com.wangalangg.tetris;

public class CycleManager {

	private double time;
	private double timePassed;
	private boolean isPaused;

	public CycleManager() {
		time = System.nanoTime();
	}

	public void setCurrentTime(long currentTime) {
		if (!isPaused) {
			timePassed = (currentTime - time) / 1000000000.0;
		} else {
			time = currentTime - timePassed * 1000000000.0;
		}
	}

	public void restartTimer() {
		time = System.nanoTime();
	}

	public boolean didTimeExceed(double time) {
		return timePassed > time;
	}

	public void pause() {
		isPaused = true;
	}

	public void unpause() {
		isPaused = false;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public double getTimePassed() {
		return timePassed;
	}
}
