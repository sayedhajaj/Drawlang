package com.drawlang.drawinterpreter;

import java.util.*;

import com.drawlang.gui.*;

public class Draw {

	static boolean hadError = false;

	public static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		for (Token token : tokens) {
			Main.getConsole().println(token.toString());
		}

	}

	static void error(int line, String message) {
		report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		Main.getConsole().println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}

}