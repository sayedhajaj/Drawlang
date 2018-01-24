package com.drawlang.drawinterpreter;

public class Token {
	final TokenType type;
	// this is the actual string representation - e.g an OR token will have an "or" lexeme and an IDENTIFIER token
	// can have a lexeme like "name", this is useful for things like error handling
	final String lexeme; 
	final Object literal;
	final int line; //the line number

	Token(TokenType type, String lexeme, Object literal, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line; 
	}

	public String toString() {
		return type + " " + lexeme + " " + literal;
	}
		
	
}