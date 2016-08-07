package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.gamemechanics.blocks.Blocks;
import com.wangalangg.tetris.gamemechanics.gamemodes.GameMode;
import com.wangalangg.tetris.gamemechanics.matrix.BlockInfo;
import com.wangalangg.tetris.gamemechanics.matrix.VisualMatrix;
import com.wangalangg.tetris.gamemechanics.ui.UIHandler;
import com.wangalangg.tetris.gamemechanics.ui.UIPackage;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public class MPGame {

	private SPGame spGame;
	private VisualMatrix p2Matrix;
	private BlockInfo p2CurrentBlock;
	private UIHandler p2UIHandler;
	private Socket socket;

	public MPGame(UIPackage uiPackage, GameMode gameMode) {
		spGame = new SPGame(uiPackage, gameMode) {
			@Override
			public void onBlockMoved() {
				socket.emit("moveBlock", getMoveBlockDataJson());
			}

			@Override
			public void onNewBlock() {
				socket.emit("newBlock", getNewBlockDataJson());
			}

			@Override
			public void onBlockLanded() {
				socket.emit("blockLanded", getMoveBlockDataJson());
			}

			@Override
			public void onGameOver() {
				// Show game over mask
				socket.emit("gameOver");
				uiHandler.gameOver();
				spGame.pause();
			}
		};
		p2CurrentBlock = new BlockInfo() {
			@Override
			public void blockChanged() {
				// Do nothing
			}
		};
		p2Matrix = new VisualMatrix(p2CurrentBlock);
		p2UIHandler = new UIHandler(uiPackage.p2Grid, uiPackage.p2GameOverMask, p2Matrix);
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		configSocket();
		socket.emit("ready");
	}

	public void onPressed(KeyCode input) {
		spGame.onPressed(input);
	}

	public void onReleased(KeyCode input) {
		spGame.onReleased(input);
	}

	public void pause() {
		spGame.pause();
	}

	public void unpause() {
		spGame.unpause();
	}

	private void configSocket() {
		socket.on("playerJoinedRoom", args -> {

		}).on("start", args -> {
			spGame.start();
		}).on("blockMoved", args -> {
			JSONObject data = (JSONObject) args[0];
			try {
				p2CurrentBlock.moveTo(data.getInt("row"), data.getInt("col"));
				p2CurrentBlock.setRotation(data.getInt("rotation"));
				p2CurrentBlock.updateMatrix();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			p2Matrix.getGhostBlock().update();
			p2Matrix.updateMatrix();
			p2UIHandler.updateFromSocket();
		}).on("newBlock", args -> {
			JSONObject data = (JSONObject) args[0];
			try {
				p2CurrentBlock.setBlock(Blocks.getBlock(data.getString("block")));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			p2Matrix.getGhostBlock().update();
			p2Matrix.updateMatrix();
			p2UIHandler.updateFromSocket();
		}).on("blockLanded", args -> {
			JSONObject data = (JSONObject) args[0];
			try {
				p2CurrentBlock.moveTo(data.getInt("row"), data.getInt("col"));
				p2CurrentBlock.setRotation(data.getInt("rotation"));
				p2Matrix.mergeBaseMatrix();
				p2Matrix.clearLines();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			p2Matrix.updateMatrix();
			p2UIHandler.updateFromSocket();
		}).on("playerDied", args -> {
			Platform.runLater(() -> p2UIHandler.gameOver());
		});
	}
}
