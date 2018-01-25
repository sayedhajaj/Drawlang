package com.drawlang.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public class Main extends Application {
	
	private static Stage window;
	private static TabPane tabPane;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		window.setTitle("Draw");

		MenuBar menuBar = createMenuBar();

		tabPane = new TabPane();
		for (int i = 0; i < 5; i++)
			tabPane.getTabs().add(new DrawTab("untitled"));

		BorderPane layout = new BorderPane();
		layout.setTop(menuBar);
		layout.setCenter(tabPane);

		Scene scene = new Scene(layout, 640, 480);
		// adds stylesheet to override default styles
		scene.getStylesheets().add("css/draw_default.css");
		window.setScene(scene);
		window.show();
		window.setMaximized(true);
	}


	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu preferencesMenu = new Menu("Preferences");
		Menu helpMenu = new Menu("Help");
		menuBar.getMenus().addAll(fileMenu, editMenu, preferencesMenu, helpMenu);
		return menuBar;
	}

	public static Console getConsole() {
		return ((DrawTab)tabPane.getSelectionModel().getSelectedItem()).getConsole();
	}

	public static Canvas getCanvas() {
		return ((DrawTab)tabPane.getSelectionModel().getSelectedItem()).getCanvas();
	}

}