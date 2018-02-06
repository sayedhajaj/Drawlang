package com.drawlang.drawinterpreter;

import java.util.*;

class DrawClass extends DrawInstance implements DrawCallable {
	final String name;
	final DrawClass superclass;
	private final Map<String, DrawFunction> methods;

	DrawClass(DrawClass metaclass, String name, DrawClass superclass, Map<String, DrawFunction> methods) {
		super(metaclass);
		this.superclass = superclass;
		this.name = name;
		this.methods = methods;
	}

	DrawFunction findMethod(DrawInstance instance, String name) {
		// if method is defined return it and bind it to instance
		// in local scope
		if (methods.containsKey(name))
			return methods.get(name).bind(instance);

		// if not found and superclass exists then search superclass
		if (superclass != null) 
			return superclass.findMethod(instance, name);

		return null;
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



