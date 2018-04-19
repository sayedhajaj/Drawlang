package com.drawlang.drawinterpreter;

import java.util.*;

class DrawMath extends DrawClass {

	DrawMath() {
		super(null, null, null, null);
		
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "abs":
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
						double num = (double)arguments.get(0);
						return Math.abs(num);
					}
				};

			case "acos":
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
						double num = (double)arguments.get(0);
						return Math.acos(num);
					}
				};

			case "asin":
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
						double num = (double)arguments.get(0);
						return Math.asin(num);
					}
				};
			
			case "cbrt":
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
						double num = (double)arguments.get(0);
						return Math.cbrt(num);
					}
				};

			case "ceil":
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
						double num = (double)arguments.get(0);
						return Math.ceil(num);
					}
				};

			case "cos":
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
						double num = (double)arguments.get(0);
						return Math.cos(num);
					}
				};

			case "exp":
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
						double num = (double)arguments.get(0);
						return Math.exp(num);
					}
				};

			case "floor": 
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
						double num = (double)arguments.get(0);
						return Math.floor(num);
					}
				};

			case "hypot": 
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
						double x = (double)arguments.get(0), y = (double)arguments.get(1);
						return Math.hypot(x, y);
					}
				};

			case "ln": 
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
						double num = (double)arguments.get(0);
						return Math.log(num);
					}
				};

			case "log": 
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						if (!(arguments.get(0) instanceof Double)) {
							throw new RuntimeError(name, "Expected " + name.lexeme + "(number, number).");
						}
						double base = (double)arguments.get(0), num = (double)arguments.get(1);
						// uses log maths to return log of any bases by dividing with log10
						return Math.log10(num) / Math.log10(base);
					}
				};

			case "log10": 
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
						double num = (double)arguments.get(0);
						return Math.log10(num);
					}
				};

			case "max": 
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
						double x = (double)arguments.get(0), y = (double)arguments.get(1);
						return Math.max(x, y);
					}
				};

			case "min": 
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
						double x = (double)arguments.get(0), y = (double)arguments.get(1);
						return Math.min(x, y);
					}
				};

			case "pow": 
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
						double x = (double)arguments.get(0), y = (double)arguments.get(1);
						return Math.pow(x, y);
					}
				};

			case "random":
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

			case "round": 
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
						double num = (double)arguments.get(0);
						return (double) Math.round(num);
					}
				};

			case "sin": 
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
						double num = (double)arguments.get(0);
						return Math.sin(num);
					}
				};

			case "sqrt": 
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
						double num = (double)arguments.get(0);
						return Math.sqrt(num);
					}
				};

			case "tan": 
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
						double num = (double)arguments.get(0);
						return Math.tan(num);
					}
				};

			case "toDegrees": 
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
						double num = (double)arguments.get(0);
						return Math.toDegrees(num);
					}
				};

			case "toRadians": 
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
						double num = (double)arguments.get(0);
						return Math.toRadians(num);
					}
				};

			case "E":
				return Math.E;

			case "PI":
				return Math.PI;

			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

}

