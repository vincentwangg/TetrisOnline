package com.wangalangg.tetris.ui;

import com.wangalangg.tetris.controllers.MPController;
import com.wangalangg.tetris.controllers.MainController;
import com.wangalangg.tetris.controllers.RoomSelectionCtrller;
import com.wangalangg.tetris.controllers.SPController;
import com.wangalangg.tetris.controllers.ScreenChangeable;

import java.io.IOException;

import io.socket.client.Socket;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UIManager {

	private Scene scene;
	private Stage stage;
	private ScreenChangeable currentController;

	public UIManager(Scene scene, Stage stage) {
		this.scene = scene;
		this.stage = stage;
	}

	public void showSinglePlayer() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/singleplayer.fxml"));
			scene.setRoot(loader.load());
			SPController controller = loader.getController();
			controller.configScene(scene);
			controller.setQuitGameRunnable(this::showMainMenu);
			currentController = controller;

			stage.setWidth(600);
			stage.setHeight(800);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMultiPlayer() {
		// Make sure currentController is room selection
		if (currentController instanceof RoomSelectionCtrller) {
			Socket socket = ((RoomSelectionCtrller) currentController).getSocket();
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/multiplayer.fxml"));
				scene.setRoot(loader.load());
				MPController controller = loader.getController();
				controller.configScene(scene);
				controller.setQuitGameRunnable(this::showMainMenu);
				controller.setSocket(socket);
				currentController = controller;

				stage.setWidth(1200);
				stage.setHeight(800);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void showMainMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
			scene.setRoot(loader.load());
			MainController controller = loader.getController();
			controller.setUIManager(this);
			currentController = controller;

			stage.setWidth(600);
			stage.setHeight(600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showRoomSelection() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/room_selection.fxml"));
			scene.setRoot(loader.load());
			RoomSelectionCtrller controller = loader.getController();
			controller.setUIManager(this);
			controller.configScene(scene);
			currentController = controller;

			stage.setWidth(600);
			stage.setHeight(600);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
