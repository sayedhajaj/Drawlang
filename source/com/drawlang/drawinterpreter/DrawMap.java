package com.drawlang.drawinterpreter;

import java.util.*;

public class DrawMap extends DrawInstance {
	
	private final Map map;

	DrawMap() {
		super(null);
		map = new HashMap();
	}

	@Override
	Object get(Token name) {
		switch (name.lexeme) {
			case "get":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						DrawString key = (DrawString) arguments.get(0);
						return map.get(key.toString());
					}
				};
			case "put":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 2;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						DrawString key = (DrawString) arguments.get(0);
						Object value = arguments.get(1);
						return map.put(key.toString(), value);
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
						map.clear();
						return null;
					}
				};
			case "containsKey":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 1;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						DrawString key = (DrawString) arguments.get(0);
						return map.containsKey(key.toString());
					}
				};
			case "entries":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						return new DrawArray(map.entrySet().toArray());
					}
				};
			case "keys":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						return new DrawArray(map.keySet().toArray());
					}
				};
			case "values":
				return new DrawCallable() {
					@Override
					public int arity() {
						return 0;
					}

					@Override
					public Object call(Interpreter interpreter, List<Object> arguments) {
						return new DrawArray(map.values().toArray());
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
						DrawString key = (DrawString) arguments.get(0);
						return map.remove(key.toString());
					}
				};
			default:
				throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
		}
	}

	@Override
	void set(Token name, Object value) {
		throw new RuntimeError(name, "Cannot add properties to maps.");
	}

}