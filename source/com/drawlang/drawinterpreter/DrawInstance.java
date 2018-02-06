package com.drawlang.drawinterpreter;

import java.util.*;

class DrawInstance {
	private DrawClass drawClass;
	private final Map<String, Object> fields = new HashMap<>();

	DrawInstance(DrawClass drawClass) {
		this.drawClass = drawClass;
	}

	Object get(Token name) {
		// if the field name is a key in the fields map
		// return value associated with it
		if (fields.containsKey(name.lexeme)) {
			return fields.get(name.lexeme);
		}

		// if field is not a variable then assume it is a method
		// and search

		DrawFunction method = drawClass.findMethod(this, name.lexeme);
		if (method != null) return method;

		// otherwise throw error
		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}

	void set(Token name, Object value) {
		fields.put(name.lexeme, value);
	}

	@Override
	public String toString() {
		return drawClass.name + " instance";
	}
}

