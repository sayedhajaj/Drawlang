package com.drawlang.drawinterpreter;

import java.util.*;

public class DrawChar extends DrawInstance {
	
	private char val;

	DrawChar(char val) {
		super(null);
		this.val = val;
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "toAscii":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						return (double) (int) val;
					}
				};
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

	@Override
	void set(Token name, Object value) {
		throw new RuntimeError(name, "Cannot add properties to characters.");
	}

	@Override
	public String toString() {
		return val + "";
	}
}

