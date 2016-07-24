package com.wangalangg.tetris.gamemechanics.matrix;

import javafx.scene.text.Text;

public class Score {

	private int points, level;
	private int linesLeft;
	private Text pointsDisplay, levelDisplay, linesLeftDisplay;

	public Score(Text pointsDisplay, Text levelDisplay, Text linesLeftDisplay) {
		points = 0;
		level = 1;
		linesLeft = 5;
		this.pointsDisplay = pointsDisplay;
		this.levelDisplay = levelDisplay;
		this.linesLeftDisplay = linesLeftDisplay;
	}

	public void update() {
		linesLeftDisplay.setText(Integer.toString(linesLeft));
		pointsDisplay.setText(Integer.toString(points));
		levelDisplay.setText(Integer.toString(level));
	}

	public int getLevel() {
		return level;
	}

	public void softDrop() {
		points++;
	}

	public void hardDrop(int lines) {
		points += lines * 2;
	}

	public void single() {
		linesLeft--;
		points += 100 * level;
		checkLevelUp();
	}

	public void duhble() {
		linesLeft -= 3;
		points += 300 * level;
		checkLevelUp();
	}

	public void triple() {
		linesLeft -= 4;
		points += 500 * level;
		checkLevelUp();
	}

	public void tetris() {
		linesLeft -= 5;
		points += 800 * level;
		checkLevelUp();
	}

	// Tspin that clears no lines
	public void tSpin() {
		linesLeft -= 4;
		points += 400 * level;
		checkLevelUp();
	}

	public void tSpinSingle() {
		linesLeft -= 8;
		points += 800 * level;
		checkLevelUp();
	}

	public void tSpinDouble() {
		linesLeft -= 12;
		points += 1200 * level;
		checkLevelUp();
	}

	public void tSpinTriple() {
		linesLeft -= 16;
		points += 1600 * level;
		checkLevelUp();
	}

	public void tSpinMini() {
		linesLeft -= 1;
		points += 100 * level;
		checkLevelUp();
	}

	public void tSpinMiniSingle() {
		linesLeft -= 2;
		points += 200 * level;
		checkLevelUp();
	}

	public void backToBack() {
		// todo stub
	}

	private void checkLevelUp() {
		if (linesLeft <= 0) {
			level++;
			linesLeft = level * 5;
		}
	}
}
