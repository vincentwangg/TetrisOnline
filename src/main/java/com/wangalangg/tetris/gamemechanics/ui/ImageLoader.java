package com.wangalangg.tetris.gamemechanics.ui;

import com.wangalangg.tetris.gamemechanics.blocks.Blocks;

import javafx.scene.image.Image;

public class ImageLoader {

	public final Image iBlockImage, jBlockImage, lBlockImage;
	public final Image oBlockImage, sBlockImage, tBlockImage, zBlockImage;

	public ImageLoader() {
		// Load images of block previews
		iBlockImage = new Image("/blocks/i_block.png");
		jBlockImage = new Image("/blocks/j_block.png");
		lBlockImage = new Image("/blocks/l_block.png");
		oBlockImage = new Image("/blocks/o_block.png");
		sBlockImage = new Image("/blocks/s_block.png");
		tBlockImage = new Image("/blocks/t_block.png");
		zBlockImage = new Image("/blocks/z_block.png");
	}

	public Image getImage(Blocks block) {
		switch (block) {
			case I:
				return iBlockImage;
			case J:
				return jBlockImage;
			case L:
				return lBlockImage;
			case O:
				return oBlockImage;
			case S:
				return sBlockImage;
			case T:
				return tBlockImage;
			case Z:
				return zBlockImage;
			default:
				throw new AssertionError("ImageLoader::getImage(): block type doesn't exist");
		}
	}
}
