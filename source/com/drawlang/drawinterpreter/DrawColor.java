package com.drawlang.drawinterpreter;

import javafx.scene.paint.*;

public class DrawColor extends DrawInstance {

	public Color color;

	DrawColor(Color color) {
		super(null);
		this.color = color; 
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "r": return color.getRed()*255;
			case "g": return color.getGreen()*255;
			case "b": return color.getBlue()*255;
			case "a": return color.getOpacity()*255;

			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}
	
}


