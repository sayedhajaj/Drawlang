package com.drawlang.drawinterpreter;

import java.util.*;

import static com.drawlang.drawinterpreter.TokenType.*;

public class Parser {

	private static class ParseError	extends RuntimeException {}

	private final List<Token> tokens;
	private int current = 0;
	private int loops = 0;

	// takes in token generated from scanner
	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	// returns a list of statements to be executed
	List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();
		while (!isAtEnd()) {
			statements.add(declaration());
		}

		return statements;
	}

	private Expr expression() {
		return comma();
	}

	private Expr comma() {
		Expr expr = assignment();

		while (match(COMMA)) {
			Token operator = previous();
			Expr right = assignment();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Stmt declaration() {
		try {
			// checks if class declaration
			if (match(CLASS)) return classDeclaration();
			// checks if token is function declaration
			if (check(FUNCTION) && checkNext(IDENTIFIER)) {
				consume(FUNCTION, null);
				return function("function");
			}
			// checks if token is a variable declaration
			if (match(VAR)) return varDeclaration();

			// if not then return previous statment code
			return statement();
		} catch (ParseError error) {
			// error recovery implemented earlier
			synchronize();
			return null;
		}
	}

	private Stmt classDeclaration() {
		// checks for class name
		Token name = consume(IDENTIFIER, "Expect class name.");

		// checks if extends another class
		Expr superclass = null;
		if (match(EXTENDS)) {
			// checks for superclass name
			consume(IDENTIFIER, "Expect superclass name.");
			superclass = new Expr.Variable(previous());
		}

		// checks for {
		consume(LEFT_BRACE, "Expect '{' before class body.");

		// adds methods
		List<Stmt.Function> methods = new ArrayList<>();
		List<Stmt.Function> classMethods = new ArrayList<>();

		while (!check(RIGHT_BRACE) && !isAtEnd()) {

			// check if static method
			boolean isClassMethod = match(STATIC);
			if(isClassMethod) classMethods.add(function("method"));
			else methods.add(function("method"));
		}

		consume(RIGHT_BRACE, "Expect '}' after class body.");

		return new Stmt.Class(name, superclass, methods, classMethods);
	}

	private Stmt statement() {
		// check for for statement, if so return while statment
		// from desugaring for
		if (match(FOR)) return forStatement();
		// check for if statement, if so return if statement
		if (match(IF)) return ifStatement();
		// check for return
		if (match(RETURN)) return returnStatement();
		// check for do while
		if (match(DO)) return doWhileStatement();
		// check for "while"
		if (match(WHILE)) return whileStatement();
		// checks for break
		if (match(BREAK)) return breakStatement();
		// checks for continue
		if (match(CONTINUE)) return continueStatement();
		// checks for {, if so return block statment
		if (match(LEFT_BRACE)) return new Stmt.Block(block());
		// otherwise return expression statement
		return expressionStatement();
	}

	private Stmt forStatement() {
		consume(LEFT_PAREN, "Expect '(' after 'for'.");

		// checks the initializer, e.g for('var i = 0') or for('i = 0')
		// this also allows for no initializer - e.g for(;)
		Stmt initializer;
		if (match(SEMICOLON)) {
			initializer = null;
		} else if (match(VAR)) {
			initializer = varDeclaration();
		} else {
			initializer = expressionStatement();
		}

		// checks for a condition - for(var i = 0; 'i < 5')
		Expr condition = null;
		if (!check(SEMICOLON)) {
			condition = expression();
		}
		consume(SEMICOLON, "Expect ';' after loop condition.");

		// check for incrementer expression - for(var i = 0; i < 5; 'i = i + 1')
		Expr increment = null;
		if (!check(RIGHT_PAREN)) {
			increment = expression();
		}
		consume(RIGHT_PAREN, "Expect ')' after for clauses.");
		loops++;
		Stmt body = statement();

		// if there is an incrementer add it to the end of the body
		if (increment != null) 
			body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
		
		// if there is no condition it will loop infinitely
		if (condition == null) condition = new Expr.Literal(true);
		body = new Stmt.While(condition, body);

		// if there is an initializer then add it to the start of the body
		if (initializer != null)
			body = new Stmt.Block(Arrays.asList(initializer, body));
		loops--;
		return body;

	}

	private Stmt ifStatement() {
		consume(LEFT_PAREN, "Expect '(' after 'if'.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after if condition.");

		// branch to be executed if condition is true
		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		// checks for else branch, if there is an else token then add it
		if (match(ELSE)) {
			elseBranch = statement();
		}

		return new Stmt.If(condition, thenBranch, elseBranch);
	}

	private Stmt returnStatement() {
		Token keyword = previous();
		Expr value = null;
		// return value is optional
		if (!check(SEMICOLON)) {
			value = expression();
		}

		consume(SEMICOLON, "Expect ';' after return value.");
		return new Stmt.Return(keyword, value);
	}

	private Stmt varDeclaration() {
		// check for variable name and raise error if not
		// available
		Token name = consume(IDENTIFIER, "Expect variable name.");

		// optionally checks for initializer. This allows code like:
		// var message; and var message = "hello";
		Expr initializer = null;
		if (match(EQUAL)) {
			initializer = expression();
		}

		consume(SEMICOLON, "Expect ';' after variable declaration.");
		return new Stmt.Var(name, initializer);
	}

	private Stmt doWhileStatement() {
		// syntactic sugar around a while statement
		loops++;
		Stmt body = statement();
		consume(WHILE, "Expect 'while' in a do-while loop.");
		consume(LEFT_PAREN, "Expect '(' after 'while'.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after condition.");
		consume(SEMICOLON, "Expect ';' after do-while statement");
		loops--;
		return new Stmt.Block(Arrays.asList(body, new Stmt.While(condition, body)));
	}

	private Stmt whileStatement() {
		// after 'while' it needs to parse a '(', then a condition
		// then a ')' then a statement
		consume(LEFT_PAREN, "Expect '(' after 'while'.");
		Expr condition = expression();
		consume(RIGHT_PAREN, "Expect ')' after condition.");
		loops++;
		Stmt body = statement();
		loops--;
		return new Stmt.While(condition, body);
	}

	private Stmt breakStatement() {
		// throws error if not in loop
		if (!(loops > 0)) throw error(previous(), "Break statement must be inside a loop.");
		consume(SEMICOLON, "Expect ';' after 'break' statement.");
		return new Stmt.Break();
	}

	private Stmt continueStatement() {
		// throws error if not in loop
		if (!(loops > 0)) throw error(previous(), "Continue statement must be inside a loop.");
		consume(SEMICOLON, "Expect ';' after 'continue' statement.");
		return new Stmt.Continue();
	}

	//similar to above but returns expression statement instead
	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(SEMICOLON, "Expect ';' after expression.");
		return new Stmt.Expression(expr);
	}

	private Stmt.Function function(String kind) {
		// looks for function or method name
		Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
		return new Stmt.Function(name, functionBody(kind));
	}

	private Expr.Function functionBody(String kind) {
		consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
		// looks for parameters
		List<Token> parameters = new ArrayList<>();
		if (!check(RIGHT_PAREN)) {
			do {
				if (parameters.size() >= 8) error(peek(), "Cannot have more than 8 parameters.");

				parameters.add(consume(IDENTIFIER, "Expect parameter name."));
			} while (match(COMMA));
		}

		consume(RIGHT_PAREN, "Expect ')' after parameters.");

		// looks for function body
		consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
		List<Stmt> body = block();
		return new Expr.Function(parameters, body);
	}

	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<Stmt>();

		// keeps adding statement whilst token is not equal to } or end of file
		while (!check(RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}

		// raises error if } is missing
		consume(RIGHT_BRACE, "Expect '}' after block.");
		return statements;
	}

	private Expr assignment() {
		// defaults to ternary expression
		Expr expr = ternary();

		if(check(EQUAL) && expr instanceof Expr.Get) {
			match(EQUAL);
			Expr.Get get = (Expr.Get)expr;
			return new Expr.Set(get.object, get.name, get.index, assignment());
		}


		// checks if is an assinment
		if (match(EQUAL, PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL, STAR_STAR_EQUAL, MODULOS_EQUAL)) {
			Token equals = previous();
			Expr value = assignment();

			// checks if left expression is variable name
			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable)expr).name;
				return new Expr.Assign(name, value, equals);
				// checks if left expression is a get instance
			} else if (expr instanceof Expr.Get) {
				Expr.Get get = (Expr.Get)expr;
				return new Expr.Set(get.object, get.name, get.index, value);
			}

			// if not a variable then raise an error

			error(equals, "Invalid assignment target.");
		}

		return expr;
	}

	private Expr ternary() {
		// defaults to or if no ?
		// this is the condition
		Expr expr = or();
		if (match(QUESTION)) {
			// checks for branch if condition is met
			Expr thenBranch = expression();
			consume(COLON, "Expect ':' after then branch of ternary expression.");
			// checks for branch if condition is not met
			Expr elseBranch = ternary();
			expr = new Expr.Ternary(expr, thenBranch, elseBranch);
		}

		return expr;
	}

	private Expr or() {
		// defaults to and if no OR
		Expr expr = and();

		// loop allows chaining e.g if(1 or 2 or 0 or 2);
		while (match(OR)) {
			Token operator = previous();
			Expr right = and();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr and() {
		// defaults to and
		Expr expr = equality();

		while (match(AND)) {
			Token operator = previous();
			Expr right = equality();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
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
		Expr expr = term();

		// similar to above method but goes to the next precedence level down
		while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = factor();

		while (match(MINUS, PLUS, MODULOS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = exponent();

		while (match(SLASH, STAR)) {
			Token operator = previous();
			Expr right = exponent();
			expr = new Expr.Binary(expr, operator, right);
		}
		
		return expr;
	}

	private Expr exponent() {
		Expr expr = unary();

		while (match(STAR_STAR)) {
			Token operator = previous();
			Expr right = unary();
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
		if (match(BANG, MINUS, PLUS_PLUS, MINUS_MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right, false);
		}

		// if not then it returns the lowest precedence
		return postfix();
	}

	private Expr postfix() {
		if (matchNext(PLUS_PLUS, MINUS_MINUS)) {
			Token operator = peek();
			current--;
			Expr left = primary();
			advance();
			return new Expr.Unary(operator, left, true);
		}

		return call();
	}

	private Expr finishCall(Expr callee) {
		List<Expr> arguments = new ArrayList<>();
		 if (!check(RIGHT_PAREN)) {
		 	// keep adding an argument whilst the current token
		 	// is a ,
		 	do {
		 		// limits number of arguments to 8
		 		if (arguments.size() >= 8) {
		 			error(peek(), "Cannot have more than 8 arguments.");
		 		}
		 		arguments.add(assignment());
		 	} while (match(COMMA));
		 }

		 	// returns parentheses token for use in error handling
		 	Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

		 	return new Expr.Call(callee, paren, arguments);
	}

	private Expr call() {
		Expr expr = primary();

		while (true) {
			if (match(LEFT_PAREN)) {
				// gathers all the arguments
				expr = finishCall(expr);
			} else if (match(DOT)) {
				Token name = consume(IDENTIFIER,"Expect property name after '.'.");
				expr = new Expr.Get(expr, name, null);
			} else if (match(LEFT_SUB)) {
				// if token is '[' parse an expression then check for ']'
				// then return get expression with index but no name
				Expr index = expression();
				expr = new Expr.Get(expr, null, index);
				consume(RIGHT_SUB, "Expect closing ']'");
			} else {
				break;
			}
		}

		return expr;
	}

	// the lowest precedence - returns literals and groups
	private Expr primary() {
		if (match(FALSE)) return new Expr.Literal(false);
		if (match(TRUE)) return new Expr.Literal(true);
		if (match(NULL)) return new Expr.Literal(null);

		if (match(NUMBER, STRING)) {
			return new Expr.Literal(previous().literal);
		}

		// checks if array literal, e.g [3, 5, e, x, y]
		if (match(LEFT_SUB)) {
			List<Expr> elements = new ArrayList<>();
			// keeps adding elements to list whilst the next token is a ","
			if (!check(RIGHT_SUB)) {
				do {
					elements.add(assignment());
				} while(match(COMMA));
			}
			consume(RIGHT_SUB, "Expect closing ']'");
			// creates array and sets elements to expressions
			DrawArray array = new DrawArray(elements.size());
			for(int i = 0; i < elements.size(); i++) array.set(i, elements.get(i));
			// returns it as literal
			return new Expr.Literal(array);
		}

		// checks if token is an identifier e.g a variable name
		if (match(IDENTIFIER)) {
			return new Expr.Variable(previous());
		}

		// checks for "super" token plus method name
		if (match(SUPER)) {
			Token keyword = previous();
			consume(DOT, "Expect '.' after 'super'.");
			Token method = consume(IDENTIFIER, "Expect superclass method name.");
			return new Expr.Super(keyword, method);
		}

		// check for "this" token
		if (match(THIS)) return new Expr.This(previous());

		// check groups '(' and ')', check if there is a closing
		// bracket and raise an error if not
		// return grouping expression
		if (match(LEFT_PAREN)) {
			Expr expr = expression();
			consume(RIGHT_PAREN, "Expect ')' after expression.");
			return new Expr.Grouping(expr);
		}

		if (match(FUNCTION)) return functionBody("function");

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

	// similar to above but next token instead
	private boolean matchNext(TokenType... types) {
		for (TokenType type : types) {
			if (checkNext(type)) {
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

	// checks if next token type is equal to token type passed in
	private boolean checkNext(TokenType tokenType) {
		if (isAtEnd() || tokens.get(current + 1).type == EOF) return false;
		return tokens.get(current + 1).type == tokenType;
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
				case RETURN:
					return;
			}
			advance();
		}
	}

}