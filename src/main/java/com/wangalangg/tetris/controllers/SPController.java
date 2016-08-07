package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.SPGame;
import com.wangalangg.tetris.gamemechanics.gamemodes.Marathon;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.ui.UIManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class SPController implements Controller {

	protected SPGame spGame;
	protected UIManager uiManager;
	protected Scene scene;
	protected ImageLoader images;
	protected Runnable quitGameRunnable;

	@FXML
	protected GridPane tetrisGrid;
	@FXML
	protected StackPane pauseGroup, gameOverScreen;
	@FXML
	protected ImageView holdBlock, block1, block2, block3, block4, block5;
	@FXML
	protected Text pointsText, levelText, linesLeftText;
	@FXML
	protected Button menuButton;

	public SPController() {
		images = new ImageLoader();
	}

	@FXML
	public void initialize() {
		ImageView[] blocks = {block1, block2, block3, block4, block5};

		Marathon marathon = new Marathon.MarathonFactory()
				.points(pointsText)
				.level(levelText)
				.linesLeft(linesLeftText)
				.build();

		spGame = new SPGame(tetrisGrid, holdBlock, images, blocks, marathon) {
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

			@Override
			public void onGameOver() {
				showGameOverScreen();
			}
		};
		spGame.start();
	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public void configScene(Scene scene) {
		this.scene = scene;
		scene.getRoot().requestFocus();
		menuButton.setOnMouseExited(evt -> scene.getRoot().requestFocus());
		scene.setOnKeyPressed(evt -> spGame.onPressed(evt.getCode()));
		scene.setOnKeyReleased(evt -> spGame.onReleased(evt.getCode()));
	}

	public void setQuitGameRunnable(Runnable quitGameRunnable) {
		this.quitGameRunnable = quitGameRunnable;
	}

	public void openMenu(ActionEvent event) {
		// Remove focus from button
		scene.getRoot().requestFocus();
		pauseGroup.setVisible(true);
		spGame.pause();
	}

	public void closeMenu(ActionEvent event) {
		// Remove focus from button
		scene.getRoot().requestFocus();
		pauseGroup.setVisible(false);
		spGame.unpause();
	}

	public void quitGame(ActionEvent event) {
		quitGameRunnable.run();
	}

	private void showGameOverScreen() {
		scene.getRoot().requestFocus();
		gameOverScreen.setVisible(true);
		spGame.pause();
	}

	public void playAgainFromGameOver(ActionEvent event) {
		spGame.unpause();
		spGame.restart();
		gameOverScreen.setVisible(false);
		scene.getRoot().requestFocus();
	}

	public void restartGame(ActionEvent event) {
		spGame.unpause();
		spGame.restart();
		pauseGroup.setVisible(false);
		scene.getRoot().requestFocus();
	}
}
