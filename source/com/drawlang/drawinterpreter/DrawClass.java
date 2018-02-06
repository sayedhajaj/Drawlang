package com.drawlang.drawinterpreter;

import java.util.*;

class DrawClass implements DrawCallable {
	final String name;
	private final Map<String, DrawFunction> methods;

	DrawClass(String name, Map<String, DrawFunction> methods) {
		this.name = name;
		this.methods = methods;
	}

	DrawFunction findMethod(DrawInstance instance, String name) {
		// if method is defined return in and bind it to instance
		// in local scope
		return methods.containsKey(name) ? methods.get(name).bind(instance) : null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		DrawInstance instance = new DrawInstance(this);
		// gets constructor
		DrawFunction initializer = methods.get("init");
		if (initializer != null) {
			// calls constructor on object creation with arguments
			initializer.bind(instance).call(interpreter, arguments);
		}

		return instance;
	}

	@Override
	public int arity() {
		DrawFunction initializer = methods.get("init");
		// returns number of arguments constructor takes or 0
		// if it doesn't exist
		return initializer == null ? 0 : initializer.arity();
	}
}



