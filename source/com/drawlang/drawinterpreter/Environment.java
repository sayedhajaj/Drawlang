package com.drawlang.drawinterpreter;

import java.util.*;

class Environment {
	// parent environment
	final Environment enclosing;
	// string is variable name, object is value associated with
	// it
	private final Map<String, Object> values = new HashMap<>();

	// for global scope
	Environment() {
		enclosing = null;
	}

	// for non-global scope with parent
	Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	void define(String name, Object value) {
		values.put(name, value);
	}

	Object get(Token name) {
		// checks if variable is defined
		// if so, look up name and return value associated with it
		if (values.containsKey(name.lexeme)) {
			return values.get(name.lexeme);
		}

		// if not in this environment check parent one (this is recursive
		// and will repeat until global)
		if (enclosing != null) return enclosing.get(name);

		// if undefined throw an error

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	void assign(Token name, Object value) {
		// checks if variable exists
		if (values.containsKey(name.lexeme)) {
			// if so update value then return
			values.put(name.lexeme, value);
			return;
		}

		// if not in current scope check parent scope
		if (enclosing != null) {
			enclosing.assign(name, value);
			return;
		}

		//otherwise raise an error

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
}