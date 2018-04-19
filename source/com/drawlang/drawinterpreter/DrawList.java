package com.drawlang.drawinterpreter;

import java.util.*;

public class DrawList extends DrawInstance {

	private final ArrayList elements;

	DrawList() {
		super(null);
		elements = new ArrayList();
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "add":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						elements.add(arguments.get(0));
						return null;
					}
				};
			case "addTo":
				return new DrawCallable() {
						@Override
						public int arity() {
							return 2;
						}

						@Override
						public Object call(Interpreter interpreter, List<Object> arguments) {
							if (!(arguments.get(0) instanceof Double)) {
								throw new RuntimeError(name, "Expected " + name.lexeme + "(number).");
							}
							elements.add((int)(double) arguments.get(0), arguments.get(1));
							return null;
						}
					};
			case "get":
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
							return elements.get((int)(double) arguments.get(0));
						}
					};
			case "clear":
				return new DrawCallable() {
						@Override
						public int arity() {
							return 0;
						}

						@Override
						public Object call(Interpreter interpreter, List<Object> arguments) {
							elements.clear();
							return null;
						}
					};

			case "remove":
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
							return elements.remove((int)(double) arguments.get(0));
						}
					};

			case "size":
				return new DrawCallable() {
						@Override
						public int arity() {
							return 0;
						}

						@Override
						public Object call(Interpreter interpreter, List<Object> arguments) {
							return (double)elements.size();
						}
					};

			case "toArray":
				return new DrawCallable() {
						@Override
						public int arity() {
							return 0;
						}

						@Override
						public Object call(Interpreter interpreter, List<Object> arguments) {
							return new DrawArray(elements.toArray());
						}
					};
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

	@Override
	void set(Token name, Object value) {
		throw new RuntimeError(name, "Cannot add properties to lists.");
	}

	// prints list in the form [element, element, ...]
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < elements.size(); i++) {
			if (i != 0) buffer.append(", ");
			buffer.append(elements.get(i));
		}
		buffer.append("]");
		return buffer.toString();
	}

}