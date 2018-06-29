package com.drawlang.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.stage.*;

import java.awt.image.*;
import javafx.embed.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.*;

import com.drawlang.drawinterpreter.*;

public class DrawTab extends Tab {

	private Console console;
	private Canvas canvas;
	private TextArea textArea;
	private File file;

	public DrawTab(File file, String contents) {
		this(file.getName(), contents);
		this.file = file;
	}

	public DrawTab(String title, String contents) {
		super();
		setText(title);
		BorderPane tabLayout = new BorderPane();
		Button drawButton = new Button("Draw!");
		drawButton.setMaxWidth(Double.MAX_VALUE);
		textArea = new TextArea();
		// set contents of text area to source code if provided
		textArea.setText(contents);
		// applies class to text area so that it can be selected in css
		textArea.getStyleClass().add("code-text-area");
		drawButton.setOnAction(e -> Draw.run(textArea.getText()));

		console = new Console();
		canvas = new Canvas(640, 480);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		ContextMenu contextMenu = createContextMenu();
		canvas.setOnContextMenuRequested(
			e -> contextMenu.show(canvas, e.getScreenX(), e.getScreenY())
		);
		
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

	// creates context menu with copy and save functionality
	private ContextMenu createContextMenu() {
		// creates context menu
		ContextMenu contextMenu = new ContextMenu();
		// creates menu items
		MenuItem saveItem = new MenuItem("Save Image");
		MenuItem copyItem = new MenuItem("Copy Image");

		// adds functionality to menu items
		saveItem.setOnAction(e -> saveCanvas());
		copyItem.setOnAction(e -> copyCanvasToClipboard());

		// adds menu items to context menu
		contextMenu.getItems().addAll(saveItem, copyItem);

		return contextMenu;
	}

	private void saveCanvas() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("PNG", "*.png")
		);
		File file = fileChooser.showSaveDialog(Main.getWindow());
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(getImage(), null), "png", file);
		} catch (IOException ex) {}
	}

	private void copyCanvasToClipboard() {
		// converts canvas to image and puts in clipboard
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		WritableImage image = getImage();

		content.putImage(image);
		clipboard.setContent(content);
	}

	private WritableImage getImage() {
		SnapshotParameters sp = new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);
		WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
		canvas.snapshot(sp, image);
		return image;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getDrawText() {
		return textArea.getText();
	}

	public File getFile() {
		return file;
	}

	public TextArea getTextArea() {
		return textArea;
	}
	
}