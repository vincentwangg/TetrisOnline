package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.SPGame;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.ui.UIManager;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class SPController implements ScreenChangeable {

	public static final boolean DEBUG = false;
	protected SPGame spGame;
	protected UIManager uiManager;
	@FXML protected GridPane tetrisGrid;
	@FXML protected ImageView holdBlock, block1, block2, block3, block4, block5;
	@FXML protected Text points, level, linesLeft;

	public SPController() {
	}

	@FXML
	public void initialize() {
		ImageView[] blocks = {block1, block2, block3, block4, block5};

		spGame = new SPGame(tetrisGrid, holdBlock, new ImageLoader(), blocks, points, level, linesLeft) {
			@Override
			public void onBlockMoved() {
				// Do nothing since it's singleplayer. Maybe add sound effects later
			}

			@Override
			public void onNewBlock() {
				// Do nothing once again
			}

			@Override
			public void onBlockLanded() {
				// And anotha one
			}
		};
	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public void setupKeyboardInput(Scene scene) {
		scene.setOnKeyPressed(evt -> {
			if (DEBUG) {
				System.out.println(evt.getCode());
				//printMatrices();
			}
			spGame.onPressed(evt.getCode());
		});

		scene.setOnKeyReleased(event -> {
			spGame.onReleased(event.getCode());
		});
	}
}
