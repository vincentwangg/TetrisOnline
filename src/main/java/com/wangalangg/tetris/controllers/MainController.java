package com.wangalangg.tetris.controllers;

import com.wangalangg.tetris.ui.UIManager;

import javafx.event.ActionEvent;

public class MainController implements ScreenChangeable {

	private UIManager uiManager;

	@Override
	public void setUIManager(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	public void showSinglePlayer(ActionEvent event) {
		uiManager.showSinglePlayer();
	}

	public void showMultiPlayer(ActionEvent event) {
		uiManager.showMultiPlayer();
	}
}
