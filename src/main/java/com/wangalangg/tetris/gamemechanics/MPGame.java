package com.wangalangg.tetris.gamemechanics;

import com.wangalangg.tetris.gamemechanics.ui.ImageLoader;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class MPGame extends SPGame {

	public MPGame(GridPane tetrisGrid, GridPane p2Grid, ImageView holdBlockImage, ImageLoader images,
				  ImageView[] nextBlocksImages, Text points, Text level, Text linesLeft) {
		super(tetrisGrid, holdBlockImage, images, nextBlocksImages, points, level, linesLeft);
	}
}
