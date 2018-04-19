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
						Object key = arguments.get(0);
						return map.get(key);
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
						Object key = arguments.get(0);
						Object value = arguments.get(1);
						return map.put(key, value);
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
						Object key = arguments.get(0);
						return map.containsKey(key);
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
						Object key = arguments.get(0);
						return map.remove(key);
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