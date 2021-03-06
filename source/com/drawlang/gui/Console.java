package com.drawlang.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public class Console extends TextArea {

	public Console() {
		super();
		setEditable(false);
		getStyleClass().add("console");
		// setFocusTraversable(false);
		// setMouseTransparent(true);
	}

	public void print(Object obj) {
		appendText((String)obj);
	}

	public void println(Object obj) {
		print(((String)obj)+"\n");
	}

	public void clear() {
		setText("");
	}
	
}