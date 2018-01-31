package com.drawlang.drawinterpreter;

import java.util.*;

import com.drawlang.gui.*;

public class Draw {

	static boolean hadError = false;
	static boolean hadRuntimeError = false;

	public static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		Interpreter interpreter = new Interpreter();

		if (hadError) return;

		interpreter.interpret(statements);
	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		Main.getConsole().println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			report(token.line, " at end", message);
		} else {
			report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error) {
		Main.getConsole().println(error.getMessage() + "\n[line " + error.token.line + "]");
		hadRuntimeError = true;
	}

}