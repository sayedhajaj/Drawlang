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

	public void print(Object obj) {
		setText(getText() + (String)obj);
	}

	public void println(Object obj) {
		print(((String)obj)+"\n");
	}
	
}