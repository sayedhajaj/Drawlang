package com.drawlang.drawinterpreter;

import java.util.*;

import static com.drawlang.drawinterpreter.TokenType.*;

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0, current = 0, line = 1;

	private static final Map<String, TokenType> keywords;

	static {
		// associates key words with tokens
		keywords = new HashMap<>();
		keywords.put("and",			AND);
		keywords.put("class",		CLASS);
		keywords.put("else",		ELSE);
		keywords.put("false",		FALSE);
		keywords.put("for",			FOR);
		keywords.put("function",    FUNCTION);
		keywords.put("if",			IF);
		keywords.put("null",		NULL);
		keywords.put("or",			OR);
		keywords.put("return",		RETURN);
		keywords.put("super",		SUPER);
		keywords.put("this",		THIS);
		keywords.put("true",		TRUE);
		keywords.put("var",			VAR);
		keywords.put("do",          DO);
		keywords.put("while",		WHILE);
		keywords.put("extends",     EXTENDS);
		keywords.put("static",      STATIC);
		keywords.put("break",       BREAK);
		keywords.put("continue",    CONTINUE);
	}

	Scanner(String source) {
		this.source = source;
	}

	// This is the main part of this class. It scans all the characters and adds the tokens to a list.
	List<Token> scanTokens() {
		// Loops through the characters until the last character is reached.
		while (!isAtEnd()) {
			// sets starting index to the current position, this allows checking of multiple characters.
			// by measuring distance from start.
			start = current;
			// scans the characters for the next token
			scanToken();
		}

		// Adds an end of file token to the end. This is useful for the next stage - parsing.
		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}


	private void scanToken() {
		char c = advance();
		// conditional on all the characters, adds a token associated with that character
		switch (c) {
			case '(': addToken(LEFT_PAREN); break;
			case ')': addToken(RIGHT_PAREN); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case '[': addToken(LEFT_SUB); break;
			case ']': addToken(RIGHT_SUB); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': 
				// check for -- or -= or just -
				addToken(match('-') ? MINUS_MINUS : match('=') ? MINUS_EQUAL : MINUS); 
				break;
			case '+': 
				// same with +
				addToken(match('+') ? PLUS_PLUS : match('=') ? PLUS_EQUAL : PLUS); 
				break;
			case ';': addToken(SEMICOLON); break;
			case ':': addToken(COLON); break;
			case '?': addToken(QUESTION); break;
			case '%': 
				addToken(match('=') ? MODULOS_EQUAL : MODULOS); 
				break;
			case '*': 
				addToken(match('*') ? match('=') ? STAR_STAR_EQUAL : STAR_STAR : match('=') ? STAR_EQUAL : STAR);
				break;
			// if the current character is ! and the next is = then the token is != - it checks if two things
			// are not equal, if it's just ! then it negates the next value
			// ! then it negates
			case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
			// similar to above but with == and equal, the next are also similar
			case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
			case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
			case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
			case '/':
				if (match('/')) {
					// if it's a single
					// line  comment then consume each character until the end of the line
					while (peek() != '\n' && ! isAtEnd()) advance();
				} else if (match('*')) {
					// allows nesting by counting
					// number of comments
					int comments = 1;
					while (!isAtEnd()) {
						// decreases comments when closed
						if(peek() == '*' && peekNext() == '/') comments--;
						// increases when new one
						if(peek() == '/' && peekNext() == '*') comments++;
						// skips to next line
						if(peekNext() == '\n') line++;
						if(comments == 0) break;
						advance();
					}
					// closes comment
					if(peek() == '*' && peekNext() == '/') {
						advance();
						advance();
					} else {
						// raises error if not closed
						Draw.error(line, "Unclosed block comment.");
					}
				} else if (match('=')) {
					addToken(SLASH_EQUAL);
				} else {
					// otherwise add a slash token - most likely for division
					addToken(SLASH);
				}
				break;
			// ignore tabs, new lines and whitespace
			case ' ':
			case '\r':
			case '\t':
				break;
			case '\n':
				line++;
				break;
			case '"':
				string(); break;
			default:
				// checks if the character is numeric
				if (isDigit(c)) {
					number();
				} else if (isAlpha(c)) { 
					identifier();
				} else {
					// any characters that are not used in the interpreter will raise an error
					// for validation purposes
					// this consumes the character so the scanner can go through the rest and
					// check for more characters
					Draw.error(line, "Unexpected character");
				}
		}
	}

	private void identifier() {
		while(isAlphaNumeric(peek())) advance();

		String text = source.substring(start, current);

		// checks if the identifieris a reserved word, if it isn't
		// the get will return null
		TokenType type = keywords.get(text);
		if(type == null) type = IDENTIFIER;
		addToken(type);
	}

	private void number() {
		// advance if the next character is a digit
		while (isDigit(peek())) advance();

		// looks for decimal point
		if (peek() == '.' && isDigit(peekNext())) {
			// consume the . then the numerical characters after
			advance();

			while (isDigit(peek())) advance();
		}

		// converts string representation of number to double
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	private void string(){
		while (peek() != '"' && !isAtEnd()) {
			// this allows multiline strings because if the next character is a new line
			// it increments the line variable and keeps scanning
			if (peek() == '\n') line++;
			advance();
		}

		// if there is no closing " and it has reached the end of the file
		// then it raises an error
		if (isAtEnd()) {
			Draw.error(line, "Unterminated string.");
			return;
		}

		// this consumes the "
		advance();

		// this trims the surrounding quotes and uses the start and current
		// variables to add multiple characters
		String value = source.substring(start+1, current-1);
		// this adds a wrapper around the string
		// which is useful for letting all strings have
		// string methods by default
		addToken(STRING, new DrawString(value));
	}

	// this is similar to advance but it only advances if the next character in the source
	// matches the expected character
	private boolean match(char expected) {
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	// returns the next character in the source
	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	// similar to above but checks the character after
	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current+1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	// checks if a character is between 0 and 9
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9'; 
	}

	// returns the next character in the source
	private char advance() {
		current++;
		return source.charAt(current - 1);
	}

	// gets current lexeme (string representation) and adds it to list of tokens
	private void addToken(TokenType type) {
		addToken(type, null);
	}


	// same as above but token has a literal value - e.g an identifier, string, or number
	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}


}