package com.wangalangg.tetris.gamemechanics.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class UIPackage {

	public final GridPane p1Grid, p2Grid;
	public final ImageLoader images;
	public final ImageView holdBlockImage, p1GameOverMask, p2GameOverMask;
	public final ImageView[] nextBlocksImages;

	// Singleplayer UI package
	public UIPackage(GridPane tetrisGrid, ImageView holdBlockImage, ImageLoader images,
					 ImageView[] nextBlocksImages) {
		this(tetrisGrid, null, holdBlockImage, images, nextBlocksImages, null, null);
	}

	// 2P - Main Player UI package
	public UIPackage(GridPane p1Grid, GridPane p2Grid, ImageView holdBlock, ImageLoader images,
					 ImageView[] nextBlocksImages, ImageView p1GameOverMask, ImageView p2GameOverMask) {
		this.p1Grid = p1Grid;
		this.p2Grid = p2Grid;
		this.images = images;
		this.holdBlockImage = holdBlock;
		this.nextBlocksImages = nextBlocksImages;
		this.p1GameOverMask = p1GameOverMask;
		this.p2GameOverMask = p2GameOverMask;
	}
}
