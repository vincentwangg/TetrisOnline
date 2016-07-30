package com.wangalangg.tetris.gamemechanics.gamemodes;

import javafx.scene.text.Text;

public class Sprint implements GameMode {

	private int linesLeft;
	private Text linesLeftText;

	public Sprint(Text linesLeftText) {
		this.linesLeftText = linesLeftText;
		initValues();
	}

	public void initValues() {
		linesLeft = 40;
	}

	@Override
	public void update() {
		linesLeftText.setText(Integer.toString(linesLeft));
	}

	@Override
	public float getStepTime() {
		return 1;
	}

	@Override
	public void reset() {
		initValues();
		update();
	}

	@Override
	public void softDrop() {

	}

	@Override
	public void hardDrop(int lines) {

	}

	@Override
	public void single() {
		linesLeft--;
	}

	@Override
	public void duhble() {
		linesLeft -= 2;
	}

	@Override
	public void triple() {
		linesLeft -= 3;
	}

	@Override
	public void tetris() {
		linesLeft -= 4;
	}

	@Override
	public void tSpin() {

	}

	@Override
	public void tSpinSingle() {

	}

	@Override
	public void tSpinDouble() {

	}

	@Override
	public void tSpinTriple() {

	}

	@Override
	public void tSpinMini() {

	}

	@Override
	public void tSpinMiniSingle() {

	}

	@Override
	public void backToBack() {

	}
}
