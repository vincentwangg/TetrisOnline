package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.MPGame;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.ui.UIManager;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MPController implements ScreenChangeable {

	private MPGame mpGame;
	protected UIManager uiManager;
	@FXML private GridPane player1Grid, player2Grid;
	@FXML protected ImageView holdBlock, block1, block2, block3, block4, block5;
	@FXML protected Text points, level, linesLeft;

	public MPController() {
	}

	@FXML
	public void initialize() {
		ImageView[] blocks = {block1, block2, block3, block4, block5};
		mpGame = new MPGame(player1Grid, player2Grid, holdBlock, new ImageLoader(),
				blocks, points, level, linesLeft);
	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public void setupKeyboardInput(Scene scene) {
		scene.setOnKeyPressed(evt -> {
			mpGame.onPressed(evt.getCode());
		});

		scene.setOnKeyReleased(event -> {
			mpGame.onReleased(event.getCode());
		});
	}
}
