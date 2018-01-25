package com.drawlang.drawinterpreter;

import java.util.List;

import static com.drawlang.drawinterpreter.TokenType.*;

public class Parser {

	private static class ParseError	extends RuntimeException {}

	private final List<Token> tokens;
	private int current = 0;

	// takes in token generated from scanner
	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	Expr parse() {
		try {
			return expression();
		} catch (ParseError error) {
			return null;
		}
	}

	private Expr expression() {
		return equality();
	}

	private Expr equality() {
		// set expression result to lower precendence expression
		Expr expr = comparison();

		// check if there are equality operator tokens
		while (match(BANG_EQUAL, EQUAL_EQUAL)) {
			// if there are return binary eqaulity expression
			Token operator = previous();
			Expr right = comparison();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr comparison() {
		Expr expr = addition();

		// similar to above method but goes to the next precedence level down
		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = addition();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr addition() {
		Expr expr = multiplication();

		// similar to above method but goes to the next precedence level down
		while (match(MINUS, PLUS)) {
			Token operator = previous();
			Expr right = multiplication();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr multiplication() {
		Expr expr = unary();

		// similar to above method but goes to the next precedence level
		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		// checks if unary
		if (match(BANG, MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}

		// if not then it returns the lowest precedence
		return primary();
	}

	// the lowest precedence - returns literals and groups
	private Expr primary() {
		if (match(FALSE)) return new Expr.Literal(false);
		if (match(TRUE)) return new Expr.Literal(true);
		if (match(NULL)) return new Expr.Literal(null);

		if (match(NUMBER, STRING)) {
			return new Expr.Literal(previous().literal);
		}

		// check groups '(' and ')', check if there is a closing
		// bracket and raise an error if not
		// return grouping expression
		if (match(LEFT_PAREN)) {
			Expr expr = expression();
			consume(RIGHT_PAREN, "Expect ')' after expression.");
			return new Expr.Grouping(expr);
		}

		// if no expression was returned then there must be a syntax error

		throw error(peek(), "Expect expression.");
	}

	// checks if current token is any of the types passed in
	// if the type matches then the token is consumed
	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;
	}

	// checks if the current token type is equal to a given type
	// and consume. If not then raise an error
	private Token consume(TokenType type, String message) {
		if (check(type)) return advance();
		throw error(peek(), message);
	}

	// checks if current token type is equal to token type passed in
	private boolean check(TokenType tokenType) {
		if (isAtEnd()) return false;
		return peek().type == tokenType;
	}

	private Token advance() {
		current++;
		return previous();
	}

	// checks if token is end of file token
	private boolean isAtEnd() {
		return peek().type == EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current-1);
	}

	private ParseError error(Token token, String message) {
		Draw.error(token, message);
		return new ParseError();	
	}

	// go to the next line (after ';') if there is an error so the parser can uncover
	// as many errors as possible
	private void synchronize() {
		advance();

		while (!isAtEnd()) {
			if (previous().type == SEMICOLON) return;

			switch (peek().type) {
				case CLASS:
				case FUNCTION:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}

			advance();
		}
	}

}