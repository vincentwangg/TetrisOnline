package com.wangalangg.tetris;

public class CycleManager {

	private long time;
	private double timePassed;

	public CycleManager() {
		time = System.nanoTime();
	}

	public void setCurrentTime(long currentTime) {
		timePassed = (currentTime - time) / 1000000000.0;
	}

	public void restartTimer() {
		time = System.nanoTime();
	}

	public double getTimePassed() {
		return timePassed;
	}

	public boolean didTimeExceed(double time) {
		return timePassed > time;
	}
}
