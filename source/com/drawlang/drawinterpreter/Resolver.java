package com.drawlang.drawinterpreter;

import java.util.*;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
	private final Interpreter interpreter;
	// keeps track of scopes that are currently in scope, the string is
	// the variable name and the boolean is to see if it is given a value
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();

	private FunctionType currentFunction = FunctionType.NONE;

	Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	// enum to handle methods later
	private enum FunctionType {
		NONE,
		FUNCTION
	}

	void resolve(List<Stmt> statements) {
		for (Stmt statement : statements) {
			resolve(statement);
		}
	}

	public void resolveFunction(Stmt.Function stmt, FunctionType type) {
		// because of local functions, I can't simply set the current function
		// type to none when finished, I set it to it's previous value
		FunctionType enclosingFunction = currentFunction;
		currentFunction = type;
		// introduces new scope for function
		beginScope();
		for (Token param : stmt.parameters) {
			// binds all of the functions parameters
			// so they can be used like variables
			declare(param);
			define(param);
		}

		resolve(stmt.body);
		endScope();
		currentFunction = enclosingFunction;
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		// {} introduces new scope for statement inside
		beginScope();
		resolve(stmt.statements);
		endScope();
		return null;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		resolve(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		// declares and defines function name
		declare(stmt.name);
		define(stmt.name);

		resolveFunction(stmt, FunctionType.FUNCTION);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		// analyses all branches and condition
		resolve(stmt.condition);
		resolve(stmt.thenBranch);
		// checks if else branch exists
		if (stmt.elseBranch != null) resolve(stmt.elseBranch);
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		// raise error if returning out of function
		if (currentFunction == FunctionType.NONE)
			Draw.error(stmt.keyword, "Cannot return from top-level code.");
		// check if there is a value to return
		if (stmt.value != null) resolve(stmt.value);
		return null;
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		declare(stmt.name);
		if (stmt.initializer != null) {
			resolve(stmt.initializer);
		}
		define(stmt.name);
		return null;
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		// similar to if
		resolve(stmt.condition);
		resolve(stmt.body);
		return null;
	}

	@Override
	public Void visitAssignExpr(Expr.Assign expr) {
		// resolves any variables the value could refer to
		resolve(expr.value);
		// resolves variable value is being assigned to
		resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitBinaryExpr(Expr.Binary expr) {
		// resolves both operands
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitCallExpr(Expr.Call expr) {
		// callee could be variable or function name
		resolve(expr.callee);

		for (Expr argument : expr.arguments) {
			resolve(argument);
		}

		return null;
	}

	@Override
	public Void visitGroupingExpr(Expr.Grouping expr) {
		resolve(expr.expression);
		return null;
	}

	@Override
	public Void visitLiteralExpr(Expr.Literal expr) {
		// literals contain no expressions
		return null;
	}

	@Override
	public Void visitLogicalExpr(Expr.Logical expr) {
		// same as binary because no control flow is being done
		resolve(expr.left);
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitUnaryExpr(Expr.Unary expr) {
		resolve(expr.right);
		return null;
	}

	@Override
	public Void visitVariableExpr(Expr.Variable expr) {
		// if variable is declared but not inialized - e.g var a; a = a;
		if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
			Draw.error(expr.name,  "Cannot read local variable in its own initializer.");
		}

		resolveLocal(expr, expr.name);
		return null;
	}

	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	private void beginScope() {
		// adds new scope, allows nesting
		scopes.push(new HashMap<String, Boolean>());
	}

	private void endScope() {
		scopes.pop(); // exits scope
	}

	private void declare(Token name) {
		// if it's global then there is no need to resolve this
		if (scopes.isEmpty()) return;

		// adds to innermost scope to shadow outer variables
		Map<String, Boolean> scope = scopes.peek();
		// raises error if redeclaring variable with same name in
		// same scope
		if (scope.containsKey(name.lexeme)) 
			Draw.error(name,"Variable with this name already declared in this scope.");
		scope.put(name.lexeme, false);
	}

	private void define(Token name) {
		if (scopes.isEmpty()) return;
		scopes.peek().put(name.lexeme, true); // true marks it as initialized
	}

	private void resolveLocal(Expr expr, Token name) {
		// starts at inntermost scope and goes outwards
		// if not found then assumes it is global
		for (int i = scopes.size() - 1; i >= 0; i--) {
			if (scopes.get(i).containsKey(name.lexeme)) {
				// if found passes in distance between scope the variable is 
				// located in and the innermost scope to the interpreter
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
	}

}

