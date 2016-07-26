package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.gamemechanics.blocks.Blocks;
import com.wangalangg.tetris.gamemechanics.matrix.BlockInfo;
import com.wangalangg.tetris.gamemechanics.matrix.RMatrix;
import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;
import com.wangalangg.tetris.gamemechanics.ui.UIHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MPGame {

	private SPGame spGame;
	private RMatrix p2Matrix;
	private BlockInfo p2CurrentBlock;
	private UIHandler p2UIHandler;
	private Socket socket;

	public MPGame(GridPane p1Grid, GridPane p2Grid, ImageView holdBlock, ImageLoader imageLoader,
				  ImageView[] blocks, Text points, Text level, Text linesLeft) {
		connectSocket();
		configSocket();
		spGame = new SPGame(p1Grid, holdBlock, imageLoader, blocks, points, level, linesLeft) {
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
		};
		spGame.pause();
		p2CurrentBlock = new BlockInfo() {
			@Override
			public void blockChanged() {
				// Do nothing
			}
		};
		p2Matrix = new RMatrix(p2CurrentBlock);
		p2UIHandler = new UIHandler(p2Grid, p2Matrix);
	}

	public void onPressed(KeyCode input) {
		spGame.onPressed(input);
	}

	public void onReleased(KeyCode input) {
		spGame.onReleased(input);
	}

	private void configSocket() {
		socket.on("playerJoinedRoom", args -> {

		}).on("start", args -> {
			spGame.unpause();
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
		});
	}

	private void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
