package com.wangalangg.tetris;

import com.wangalangg.tetris.controllers.Controller;
import com.wangalangg.tetris.controllers.Online;
import com.wangalangg.tetris.ui.UIManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	private UIManager uiManager;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("/fonts/Play-Regular.ttf"), 14);
		primaryStage.setTitle("Tetris");
		primaryStage.setResizable(false);

		Scene scene = new Scene(new StackPane());
		uiManager = new UIManager(scene, primaryStage);
		uiManager.showMainMenu();

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		// Disconnect socket if connected
		Controller currentController = uiManager.getCurrentController();
		if (currentController instanceof Online
				&& ((Online) currentController).getSocket() != null
				&& ((Online) currentController).getSocket().connected()) {
			((Online) currentController).getSocket().disconnect();
		}
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
