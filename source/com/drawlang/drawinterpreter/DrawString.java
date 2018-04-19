package com.drawlang.drawinterpreter;

import java.util.*;

class DrawString extends DrawInstance {
	
	private String str;

	DrawString(String str) {
		super(null);
		this.str = str;
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "length": 
				return (double)str.length();
			case "substring":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (!(arguments.get(0) instanceof Double && arguments.get(1) instanceof Double)) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number).");
						}
						int start = Integer.valueOf(((Double)arguments.get(0)).intValue());
						int end = Integer.valueOf(((Double)arguments.get(1)).intValue());
						return new DrawString(str.substring(start, end));
					}
				};
			case "charAt":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (!(arguments.get(0) instanceof Double)) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number).");
						}
						int index = Integer.valueOf(((Double)arguments.get(0)).intValue());
						return new DrawChar(str.charAt(index));
					}
				};
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

	@Override
	void set(Token name, Object value) {
		throw new RuntimeError(name, "Cannot add properties to strings.");
	}

	@Override
	public String toString() {
		return str;
	}

}