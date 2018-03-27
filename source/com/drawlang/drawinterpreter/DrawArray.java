package com.drawlang.drawinterpreter;

import java.util.*;

class DrawArray extends DrawInstance {
	public final Object[] elements;

	DrawArray(int size) {
		super(null);
		elements = new Object[size];
	}

	DrawArray(Object[] arr) {
		super(null);
		elements = arr;
	}

	Object get(int index) {
		return elements[index];
	}

	Object set(int index, Object value) {
		return elements[index] = value;
	}

	@Override
	Object get(Token name) {
		if (name.lexeme.equals("length")) {
			return (double)elements.length;
		} else if (name.lexeme.equals("map")) {
			return new DrawCallable() {
				@Override
				public int arity() {
					return 1;
				}

				@Override
				public Object call(Interpreter interpreter, List<Object> arguments) {
					DrawFunction function = (DrawFunction) arguments.get(0);
					Object[] newList = new Object[elements.length];
					for (int i = 0; i < elements.length; i++) {
						List<Object> args = new ArrayList();
						args.add(elements[i]);
						newList[i] = function.call(interpreter, args);
					}
					return new DrawArray(newList);
				}
			};
		}

		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}

	@Override
	void set(Token name, Object value) {
		throw new RuntimeError(name, "Cannot add properties to arrays.");
	}

	// prints array in the form [element, element, ...]
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < elements.length; i++) {
			if (i != 0) buffer.append(", ");
			buffer.append(elements[i]);
		}
		buffer.append("]");
		return buffer.toString();
	}
}

