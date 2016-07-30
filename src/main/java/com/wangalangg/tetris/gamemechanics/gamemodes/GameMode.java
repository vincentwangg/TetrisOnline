package com.wangalangg.tetris.gamemechanics.gamemodes;

public interface GameMode {

	void update();

	float getStepTime();

	void reset();

	void softDrop();

	void hardDrop(int lines);

	void single();

	void duhble();

	void triple();

	void tetris();

	// Tspin that clears no lines
	void tSpin();

	void tSpinSingle();

	void tSpinDouble();

	void tSpinTriple();

	void tSpinMini();

	void tSpinMiniSingle();

	void backToBack();
}
