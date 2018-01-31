package com.drawlang.drawinterpreter;

import java.util.*;

// interface for creating classes and functions

interface DrawCallable {
	int arity();
	Object call(Interpreter interpreter, List<Object> arguments);
}

