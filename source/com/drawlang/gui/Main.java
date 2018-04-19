package com.drawlang.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.control.ButtonBar.*;

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Main extends Application {
	
	private static Stage window;
	private static TabPane tabPane;
	private Scene scene;

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
			tabPane.getTabs().add(new DrawTab("untitled", ""));

		BorderPane layout = new BorderPane();
		layout.setTop(menuBar);
		layout.setCenter(tabPane);

		scene = new Scene(layout, 640, 480);
		// adds stylesheet to override default styles
		scene.getStylesheets().add("css/draw_default.css");
		// adds light stylesheet by default
		scene.getStylesheets().add("css/light.css");
		window.setScene(scene);
		window.show();
		window.setMaximized(true);
	}


	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		// creates file menu items
		Menu fileMenu = new Menu("_File");
		MenuItem newFile = new MenuItem("New File");
		MenuItem openFile = new MenuItem("Open File");
		MenuItem saveFile = new MenuItem("Save");
		MenuItem saveFileAs = new MenuItem("Save As");
		MenuItem exitProgramItem = new MenuItem("Exit");

		// adds new untitled tab when new file selected
		newFile.setOnAction(e -> tabPane.getTabs().add(new DrawTab("untitled", "")));

		// opens a source file in a new tab if open is selected
		openFile.setOnAction(e -> {
			// retrieves source file from open dialog window
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open");
			File file = fileChooser.showOpenDialog(Main.getWindow());
			// exits action handler if the user exited the dialog
			// without choosing a file
			if (file == null) return;
			try {
				// gets string contents from file
				String source = new String(Files.readAllBytes(file.toPath()));
				// creates a new tab, passes in the file to set the name of the file
				// and text area contents to the text of the file
				tabPane.getTabs().add(new DrawTab(file, source));
			} catch(IOException ex) {

			}
			
		});

		// saves a source file if it is not untilted, otherwise saves as new file
		saveFile.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				if (tab.getFile() != null) {
					save(tab.getFile(), tab.getDrawText());
				} else {
					saveAs(tab);
				}
			}
		});

		// saves source as new file
		saveFileAs.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				saveAs(tab);
			}
		});

		// exits program
		exitProgramItem.setOnAction(e -> System.exit(0));

		// adds file menu items to file menu, adds a ____ above the exit option
		fileMenu.getItems().addAll(
			newFile, openFile, saveFile, 
			saveFileAs, new SeparatorMenuItem(), exitProgramItem
		);


		// adds keyboard shortcuts to file menu items
		newFile.setAccelerator(KeyCombination.keyCombination("SHORTCUT+N"));
		openFile.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
		saveFile.setAccelerator(KeyCombination.keyCombination("SHORTCUT+S"));
		saveFileAs.setAccelerator(
			new KeyCodeCombination(
				KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN
			)
		);

		// creates edit menu items
		Menu editMenu = new Menu("_Edit");
		MenuItem undoItem = new MenuItem("Undo");
		MenuItem redoItem = new MenuItem("Redo");
		MenuItem cutItem = new MenuItem("Cut");
		MenuItem copyItem = new MenuItem("Copy");
		MenuItem pasteItem = new MenuItem("Paste");
		MenuItem findItem = new MenuItem("Find");
		MenuItem goToItem = new MenuItem("Go To");

		// adds functionality to edit menu items
		undoItem.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				tab.getTextArea().undo();
			}
		});

		redoItem.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				tab.getTextArea().redo();
			}
		});

		cutItem.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				tab.getTextArea().cut();
			}
		});

		copyItem.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				tab.getTextArea().copy();
			}
		});

		pasteItem.setOnAction(e -> {
			if (tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab) {
				DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
				tab.getTextArea().paste();
			}
		});

		findItem.setOnAction(e -> {
			// exits if current tab is not a draw tab
			if (!(tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab))
				return;

			// gets tab and text
			DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
			String source = tab.getDrawText();

			// creates dialog
			Dialog<Void> dialog = new Dialog<>();
			dialog.setHeaderText("Find What:");
			dialog.setTitle("Find");

			// adds find and cancel button to dialog
			Button findButton = new Button("Find Next");
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

			// creates text input
			TextField input = new TextField();

			// adds functionality to find button
			findButton.setOnAction(event -> {
				int index = source.indexOf(input.getText(), tab.getTextArea().getCaretPosition());
				tab.getTextArea().positionCaret(index);
				tab.getTextArea().selectPositionCaret(index+input.getText().length());
			});

			// binds text input to find button - so button is fired
			// when enter is pressed
			input.setOnAction(event -> findButton.fire());

			// creates grid, adds input to the far left of the first row
			// and find button the the right of it
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));
			grid.add(input, 0, 0);
			grid.add(findButton, 1, 0);

			// adds grid to dialog then displays
			dialog.getDialogPane().setContent(grid);
			dialog.showAndWait();
		});

		goToItem.setOnAction(e -> {
			// exits if current tab is not a draw tab
			if (!(tabPane.getSelectionModel().getSelectedItem() instanceof DrawTab))
				return;

			// gets current tab
			DrawTab tab = (DrawTab) tabPane.getSelectionModel().getSelectedItem();
			String text = tab.getDrawText();
			// counts number of new lines in text area
			int numLines = text.split("\n").length;
			// gets current line by counting new lines between start and selection
			int currentLine = text.substring(
					0, tab.getTextArea().getCaretPosition()).split("\n").length;

			currentLine = currentLine == 1 ? 1 : currentLine+1;
			currentLine = currentLine > numLines ? numLines - 1 : currentLine;

			// creates dialog
			Dialog<Integer> dialog = new Dialog<>();
			dialog.setHeaderText("Line Number:");
			dialog.setTitle("Go To Line");

			// adds buttons
			ButtonType goButtonType = new ButtonType("Go To", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(goButtonType, ButtonType.CANCEL);
			// adds numeric text field
			Spinner<Integer> spinner = new Spinner<>(1, numLines, currentLine);
			spinner.setEditable(true);

			// creates grid pane to position numeric text field
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));
			grid.add(spinner, 0, 0);

			// adds grid pane to dialog
			dialog.getDialogPane().setContent(grid);
			// returns numeric text field value when button clicked
			dialog.setResultConverter(button -> {
				return button == goButtonType ? spinner.getValue() : null;
			});

			// gets result from dialog
			Optional<Integer> result = dialog.showAndWait();
			result.ifPresent(line -> {
				// gets index in string of the selected line
				int index = line == 1 ? 0 : getNthOccurence(tab.getDrawText(), "\n", line-1)+1;
				tab.getTextArea().positionCaret(index);
			});
		});

		// adds edit menu items to edit menu
		editMenu.getItems().addAll(
			undoItem, redoItem, new SeparatorMenuItem(),
			cutItem, copyItem, pasteItem, new SeparatorMenuItem(),
			findItem, goToItem
		);

		// adds keyboard shortcuts to file menu items
		undoItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+Z"));
		redoItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+Y"));
		cutItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+X"));
		copyItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+C"));
		pasteItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+P"));
		findItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+F"));
		goToItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT+G"));
		

		Menu preferencesMenu = new Menu("_Preferences");
		Menu theme = new Menu("Theme");

		// creates toggle for light and dark menu items
		// which means only one of them can be selected at a time
		ToggleGroup themeToggle = new ToggleGroup();
		RadioMenuItem light = new RadioMenuItem("Light");
		RadioMenuItem dark = new RadioMenuItem("Dark");
		light.setToggleGroup(themeToggle);
		dark.setToggleGroup(themeToggle);

		// adds functionality to theme menu items
		light.setOnAction(e -> {
			scene.getStylesheets().add("css/light.css");
			scene.getStylesheets().remove("css/dark.css");
		});

		dark.setOnAction(e -> {
			scene.getStylesheets().add("css/dark.css");
			scene.getStylesheets().remove("css/light.css");
		});

		// selects light by default
		light.setSelected(true);
		theme.getItems().addAll(light, dark); // adds both to theme menu

		preferencesMenu.getItems().add(theme);

		Menu helpMenu = new Menu("_Help");
		MenuItem welcomeItem = new MenuItem("Welcome");
		helpMenu.getItems().add(welcomeItem);

		menuBar.getMenus().addAll(fileMenu, editMenu, preferencesMenu, helpMenu);
		return menuBar;
	}

	public static Console getConsole() {
		return ((DrawTab)tabPane.getSelectionModel().getSelectedItem()).getConsole();
	}

	public static Canvas getCanvas() {
		return ((DrawTab)tabPane.getSelectionModel().getSelectedItem()).getCanvas();
	}

	public static Stage getWindow() {
		return window;
	}

	// writes text to file
	private void save(File file, String text) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(text);
			out.close();
		} catch (IOException e) {
			
		}
	}

	// obtains file from save dialog then writes text
	private void saveAs(DrawTab tab) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save As");
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Draw (*.draw)", "*.draw")
		);
		File file = fileChooser.showSaveDialog(Main.getWindow());
		// changes tab name to name of file, and sets the file of the tab
		// so that the tab can be saved without using the dialog
		tab.setText(file.getName());
		tab.setFile(file);
		save(file, tab.getDrawText());
	}

	private int getNthOccurence(String string, String substring, int n) {
		int index = string.indexOf(substring);
		while (--n > 0 && index != -1)
			index = string.indexOf(substring, index + 1);
		return index;
	}
}