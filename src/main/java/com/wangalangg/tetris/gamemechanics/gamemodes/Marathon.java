package com.wangalangg.tetris.gamemechanics.gamemodes;

import javafx.scene.text.Text;

public class Marathon implements GameMode {

	private static final float[] STEP_TIME = {
			1, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.09f, 0.08f, 0.07f, 0.06f, 0.05f
	};
	private int points, level, linesLeft;
	private Text pointsDisplay, levelDisplay, linesLeftDisplay;

	public Marathon(Text pointsDisplay, Text levelDisplay, Text linesLeftDisplay) {
		this.pointsDisplay = pointsDisplay;
		this.levelDisplay = levelDisplay;
		this.linesLeftDisplay = linesLeftDisplay;
		initValues();
	}

	private void initValues() {
		points = 0;
		level = 1;
		linesLeft = 5;
	}

	@Override
	public void update() {
		linesLeftDisplay.setText(Integer.toString(linesLeft));
		pointsDisplay.setText(Integer.toString(points));
		levelDisplay.setText(Integer.toString(level));
	}

	@Override
	public float getStepTime() {
		return STEP_TIME[level - 1];
	}

	public int getLevel() {
		return level;
	}

	@Override
	public void softDrop() {
		points++;
	}

	@Override
	public void hardDrop(int lines) {
		points += lines * 2;
	}

	@Override
	public void single() {
		linesLeft--;
		points += 100 * level;
		checkLevelUp();
	}

	@Override
	public void duhble() {
		linesLeft -= 3;
		points += 300 * level;
		checkLevelUp();
	}

	@Override
	public void triple() {
		linesLeft -= 4;
		points += 500 * level;
		checkLevelUp();
	}

	@Override
	public void tetris() {
		linesLeft -= 5;
		points += 800 * level;
		checkLevelUp();
	}

	@Override
	public void tSpin() {
		linesLeft -= 4;
		points += 400 * level;
		checkLevelUp();
	}

	@Override
	public void tSpinSingle() {
		linesLeft -= 8;
		points += 800 * level;
		checkLevelUp();
	}

	@Override
	public void tSpinDouble() {
		linesLeft -= 12;
		points += 1200 * level;
		checkLevelUp();
	}

	@Override
	public void tSpinTriple() {
		linesLeft -= 16;
		points += 1600 * level;
		checkLevelUp();
	}

	@Override
	public void tSpinMini() {
		linesLeft -= 1;
		points += 100 * level;
		checkLevelUp();
	}

	@Override
	public void tSpinMiniSingle() {
		linesLeft -= 2;
		points += 200 * level;
		checkLevelUp();
	}

	@Override
	public void backToBack() {
		// todo stub
	}

	private void checkLevelUp() {
		if (linesLeft <= 0) {
			level++;
			linesLeft = level * 5;
		}
	}

	@Override
	public void reset() {
		initValues();
		update();
	}

	public static class MarathonFactory {

		private Text pointsDisplay, levelDisplay, linesLeftDisplay;

		public MarathonFactory() {
		}

		public MarathonFactory points(Text points) {
			pointsDisplay = points;
			return this;
		}

		public MarathonFactory level(Text level) {
			levelDisplay = level;
			return this;
		}

		public MarathonFactory linesLeft(Text linesLeft) {
			linesLeftDisplay = linesLeft;
			return this;
		}

		public Marathon build() {
			return new Marathon(pointsDisplay, levelDisplay, linesLeftDisplay);
		}
	}
}
