package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.MPGame;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.ui.UIManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MPController implements ScreenChangeable {

	private MPGame mpGame;
	private Scene scene;
	private Runnable quitGameRunnable;
	private Socket socket;
	protected UIManager uiManager;

	@FXML
	private GridPane player1Grid, player2Grid;
	@FXML
	protected StackPane pauseGroup;
	@FXML
	protected ImageView holdBlock, block1, block2, block3, block4, block5;
	@FXML
	protected Text points, level, linesLeft;

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

	public void configScene(Scene scene) {
		this.scene = scene;
		scene.setOnKeyPressed(evt -> {
			mpGame.onPressed(evt.getCode());
		});

		scene.setOnKeyReleased(event -> {
			mpGame.onReleased(event.getCode());
		});
	}

	public void setQuitGameRunnable(Runnable quitGameRunnable) {
		this.quitGameRunnable = quitGameRunnable;
	}

	public void openMenu(ActionEvent event) {
		// Remove focus from button
		scene.getRoot().requestFocus();
		pauseGroup.setVisible(true);
	}

	public void closeMenu(ActionEvent event) {
		// Remove focus from button
		scene.getRoot().requestFocus();
		pauseGroup.setVisible(false);
	}

	public void quitGame(ActionEvent event) {
		quitGameRunnable.run();
		socket.disconnect();
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		mpGame.setSocket(socket);
	}
}
