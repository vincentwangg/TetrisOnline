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

	}

	@Override
	public void reset() {

	}

	@Override
	public void softDrop() {

	}

	@Override
	public void hardDrop(int lines) {

	}

	@Override
	public void single() {

	}

	@Override
	public void duhble() {

	}

	@Override
	public void triple() {

	}

	@Override
	public void tetris() {

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
