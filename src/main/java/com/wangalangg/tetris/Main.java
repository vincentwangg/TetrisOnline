package com.wangalangg.tetris;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.wangalangg.tetris.ui.UIManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Play-Regular.ttf"), 14);
        primaryStage.setTitle("Tetris");
        primaryStage.setResizable(false);

        Scene scene = new Scene(new StackPane());
        UIManager uiManager = new UIManager(scene, primaryStage);
        uiManager.showMainMenu();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
