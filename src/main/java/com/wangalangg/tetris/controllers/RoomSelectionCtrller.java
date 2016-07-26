package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.ui.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public class RoomSelectionCtrller implements ScreenChangeable {

	private UIManager uiManager;
	private Scene scene;
	private Socket socket;

	@FXML
	private TextField roomNumField;

	public RoomSelectionCtrller() {
		connectSocket();
		configSocket();
	}

	@FXML
	public void initialize() {

	}

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public Socket getSocket() {
		return socket;
	}

	public void configScene(Scene scene) {
		this.scene = scene;
		scene.getRoot().requestFocus();
		scene.getRoot().setOnMouseClicked(event -> scene.getRoot().requestFocus());
	}

	public void createRoom(ActionEvent event) {
		socket.emit("createRoom");
		uiManager.showMultiPlayer();
	}

	public void joinRoom(ActionEvent event) {
		// todo popup for when room num field is empty or incorrect
		JSONObject room = new JSONObject();
		try {
			room.put("roomID", Integer.parseInt(roomNumField.getCharacters().toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject[] data = {room};
		socket.emit("joinRoom", data, args -> Platform.runLater(() -> uiManager.showMultiPlayer()));
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void configSocket() {
		// todo finish this
		socket.on("playerJoinedRoom", args -> socket.emit("playerJoinedRoom", (JSONObject) args[0]))
				.on("roomFull", args -> System.out.println("room full"))
				.on("roomNull", args -> System.out.println("room null"));
	}
}
