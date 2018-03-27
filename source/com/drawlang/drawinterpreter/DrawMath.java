package com.drawlang.drawinterpreter;

import java.util.*;

class DrawMath extends DrawClass {

	DrawMath() {
		super(null, null, null, null);
		
	}

	@Override
	Object get(Token name) {
		if (name.lexeme.equals("floor")) {
			return new DrawCallable() {
				@Override
				public int arity() {
					return 1;
				}

				@Override
				public Object call(Interpreter interpreter, List<Object> arguments) {
					double num = (double)arguments.get(0);
					return Math.floor(num);
				}
			};
		}

		if (name.lexeme.equals("random")) {
			return new DrawCallable() {
				@Override
				public int arity() {
					return 0;
				}

				@Override
				public Object call(Interpreter interpreter, List<Object> arguments) {
					return Math.random();
				}
			};
		}

		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}

}

