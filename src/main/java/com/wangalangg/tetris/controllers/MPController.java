package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.gamemechanics.MPGame;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.ui.UIManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MPController extends SPController implements ScreenChangeable {

	private Socket socket;
	@FXML
	private GridPane player2Grid;

	public MPController() {
		configSocket();
	}

	@FXML
	public void initialize() {
		ImageView[] blocks = {block1, block2, block3, block4, block5};

		MPGame mpGame = new MPGame(tetrisGrid, player2Grid, holdBlock, new ImageLoader(), blocks,
				points, level, linesLeft);
	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	private void configSocket() {
		try {
			socket = IO.socket("http://localhost:8080/");
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		socket.on("playerJoinedRoom", args -> {

		}).on("blockMoved", args1 -> {

		});
	}
}
