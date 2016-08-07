package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.MPGame;
import com.wangalangg.tetris.gamemechanics.gamemodes.Sprint;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.gamemechanics.ui.UIPackage;
import com.wangalangg.tetris.ui.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MPController implements Controller, Online {

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
	protected ImageView holdBlock, block1, block2, block3, block4, block5, p1GameOverMask, p2GameOverMask;
	@FXML
	protected Text linesLeftText, roomIDText, timeLeftText;

	public MPController() {
	}

	@FXML
	public void initialize() {
		ImageView[] blocks = {block1, block2, block3, block4, block5};
		UIPackage uiPackage = new UIPackage(player1Grid, player2Grid, holdBlock,
				new ImageLoader(), blocks, p1GameOverMask, p2GameOverMask);
		mpGame = new MPGame(uiPackage, new Sprint(linesLeftText));
	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	public void configScene(Scene scene) {
		this.scene = scene;
		scene.getRoot().requestFocus();
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

	public void configSocket(Socket socket) {
		this.socket = socket;
		socket.on("roomID", args -> {
			Platform.runLater(() -> {
				try {
					roomIDText.setText(String.format("%03d", ((JSONObject) args[0]).getInt("roomID")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		});
		mpGame.setSocket(socket);
	}
}
