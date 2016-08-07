package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.ui.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class RoomSelectionCtrller implements Controller, Online {

	private UIManager uiManager;
	private Socket socket;
	private boolean isServerOnline;

	@FXML
	private TextField roomNumField;
	@FXML
	private Text errorText;
	@FXML
	private Button createButton, joinButton;

	@FXML
	public void initialize() {
		// TODO show different screen if server isn't available
		if (connectSocket()) {
			configSocket();
		}
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
		scene.getRoot().requestFocus();
		scene.getRoot().setOnMousePressed(event -> scene.getRoot().requestFocus());
	}

	public void createRoom(ActionEvent event) {
		socket.emit("createRoom");
		uiManager.showMultiPlayer();
	}

	public void joinRoom(ActionEvent event) {
		JSONObject room = new JSONObject();
		try {
			room.put("roomID", Integer.parseInt(roomNumField.getText()) - 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject[] data = {room};
		socket.emit("joinRoom", data, args -> Platform.runLater(() -> uiManager.showMultiPlayer()));
	}

	private boolean connectSocket() {
		isServerOnline = true;

		try {
			URL url = new URL("http://localhost:8080");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		} catch (Exception e) {
			isServerOnline = false;
		}

		if (isServerOnline) {
			try {
				socket = IO.socket("http://localhost:8080");
				socket.connect();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			joinButton.setDisable(true);
			createButton.setDisable(true);
			roomNumField.setDisable(true);
			errorText.setText("Could not connect to server.");
		}
		return isServerOnline;
	}

	private void configSocket() {
		socket.on("playerJoinedRoom", args -> socket.emit("playerJoinedRoom", (JSONObject) args[0]))
				.on("roomFull", args -> Platform.runLater(() -> errorText.setText("Couldn't join room because it was full.")))
				.on("roomNull", args -> Platform.runLater(() -> errorText.setText("Room doesn't exist.")));
	}

	public void backToMenu(ActionEvent event) {
		if (isServerOnline) {
			socket.off();
			socket.disconnect();
		}
		uiManager.showMainMenu();
	}
}
