package com.drawlang.drawinterpreter;

import java.util.*;

class DrawFunction implements DrawCallable {
	private final String name;
	private final Expr.Function declaration;
	private final Environment closure;
	private final boolean isInitializer;
	
	DrawFunction(String name, Expr.Function declaration, Environment closure, boolean isInitializer) {
		this.isInitializer = isInitializer;
		this.name = name;
		this.closure = closure;
		this.declaration = declaration;
	}

	DrawFunction bind(DrawInstance instance) {
		Environment environment = new Environment(closure);
		// associates this to instance - allows instance to
		// refer to itself
		environment.define("this", instance);
		return new DrawFunction(name, declaration, environment, false);
	}

	@Override
	public String toString() {
		return name == null ? "<fn>" : "<fn " + name + ">";
	}

	@Override
	public int arity() {
		// returns number of parameters in function
		return declaration.parameters.size();
	}

	// calls user defined function
	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(closure);
		// adds parameters to scope
		for (int i = 0; i < declaration.parameters.size(); i++) {
			environment.define(declaration.parameters.get(i).lexeme, arguments.get(i));
		}

		try {
			// executes function body
			interpreter.executeBlock(declaration.body, environment);
		} catch (Return returnValue) {
			return returnValue.value;
		}
		// returns instance if constructor
		if (isInitializer) return closure.getAt(0, "this");
		return null;
	}



}


