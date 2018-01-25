package com.drawlang.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

import com.drawlang.drawinterpreter.*;

public class DrawTab extends Tab {

	private Console console;
	private Canvas canvas;

	public DrawTab(String title) {
		super();
		setText(title);
		BorderPane tabLayout = new BorderPane();
		Button drawButton = new Button("Draw!");
		drawButton.setMaxWidth(Double.MAX_VALUE);
		TextArea textArea = new TextArea();
		drawButton.setOnAction(e -> Draw.run(textArea.getText()));
		console = new Console();
		canvas = new Canvas(640, 480);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		BorderPane right = new BorderPane();
		right.setCenter(textArea);
		right.setBottom(drawButton);
		tabLayout.setCenter(right);
		tabLayout.setLeft(canvas);
		tabLayout.setBottom(console);
		setContent(tabLayout);
	}

	public Console getConsole() {
		return console;
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
}