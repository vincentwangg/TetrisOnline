package com.wangalangg.tetris.ui;

import com.wangalangg.tetris.controllers.MPController;
import com.wangalangg.tetris.controllers.MainController;
import com.wangalangg.tetris.controllers.SPController;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UIManager {

	private Scene scene;
	private Stage stage;

	public UIManager(Scene scene, Stage stage) {
		this.scene = scene;
		this.stage = stage;
	}

	public void showSinglePlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tetris.fxml"));
			scene.setRoot(loader.load());
			SPController controller = loader.getController();
			controller.configScene(scene);
			controller.setQuitGameRunnable(this::showMainMenu);

			stage.setWidth(600);
			stage.setHeight(800);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMultiPlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/multiplayer.fxml"));
			scene.setRoot(loader.load());
			MPController controller = loader.getController();
			controller.setupKeyboardInput(scene);

			stage.setWidth(1000);
			stage.setHeight(800);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMainMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
			scene.setRoot(loader.load());
			MainController controller = loader.getController();
			controller.setUIManager(this);

			stage.setWidth(600);
			stage.setHeight(600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
