package com.drawlang.drawinterpreter;

import java.util.*;

class DrawFunction implements DrawCallable {
	private final Stmt.Function declaration;
	private final Environment closure;
	
	DrawFunction(Stmt.Function declaration, Environment closure) {
		this.closure = closure;
		this.declaration = declaration;
	}

	@Override
	public String toString() {
		return "<fn " + declaration.name.lexeme + ">";
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
		return null;
	}



}


