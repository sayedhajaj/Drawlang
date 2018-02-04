package com.drawlang.drawinterpreter;

import static com.drawlang.drawinterpreter.TokenType.*;
import com.drawlang.gui.*;

import java.util.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	
	// allows interpreter to define things in global scope
	final Environment globals = new Environment();
	private Environment environment = globals;
	// associates expressions with distance from current scope
	// and scope where variable is defined
	private final Map<Expr, Integer> locals = new HashMap<>();

	Interpreter() {
		globals.define("clock", new DrawCallable() {
			@Override
			public int arity() {
				return 0;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return (double)System.currentTimeMillis();
			}
		});

		globals.define("print", new DrawCallable() {
			@Override
			public int arity() {
				return 1;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				Main.getConsole().print(stringify(arguments.get(0)));
				return null;
			}
		});

		globals.define("println", new DrawCallable() {
			@Override
			public int arity() {
				return 1;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				Main.getConsole().println(stringify(arguments.get(0)));
				return null;
			}
		});
	}

	void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				execute(statement);
			}
		} catch (RuntimeError error) {
			Draw.runtimeError(error);
		}
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitLogicalExpr(Expr.Logical expr) {
		Object left = evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			// if the operator is or and the left expression
			// is truthy then return the left expression
			if (isTruthy(left)) return left;
		} else {
			// if it is an and and not truthy then return left
			if (!isTruthy(left)) return left;
		}

		// otherwise evaluate the right expression because the conditionals
		// have not short-circuited

		return evaluate(expr.right);
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
			case BANG:
				return !isTruthy(right);
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double)right;
		}

		return null;
	}

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		// returns the value associated with a variable name
		return lookUpVariable(expr.name, expr);
	}

	private Object lookUpVariable(Token name, Expr expr) {
		Integer distance = locals.get(expr);
		// if distance is null then assume global, else return resolved value
		return distance == null ? globals.get(name) : environment.getAt(distance, name.lexeme);
	}

	// checks to see if operand is a number
	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number.");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (left instanceof Double && right instanceof Double) return;
	
		throw new RuntimeError(operator, "Operands must be numbers.");
	}

	// returns false if value is null or false, otherwise returns true
	private boolean isTruthy(Object object) {
		if (object == null) return false;
		if (object instanceof Boolean) return (boolean)object;
		return true;
	}

	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null) return false;

		return a.equals(b);
	}

	// converts java value representation to draw lang 
	private String stringify(Object object) {
		if (object == null) return "null";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		return object.toString();
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}


	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}

	void resolve(Expr expr, int depth) {
		locals.put(expr, depth);
	}

	void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;
		try {
			// sets current environment to environment of block
			this.environment = environment;

			// loops through statements and executes them
			for (Stmt statement : statements) {
				execute(statement);
			}
		} finally {
			// restores environment to previous one
			this.environment = previous;
		}
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		executeBlock(stmt.statements, new Environment(environment));
		return null;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		DrawFunction function = new DrawFunction(stmt, environment);
		// associates function name with function in environment
		environment.define(stmt.name.lexeme, function);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		if (isTruthy(evaluate(stmt.condition))) {
			execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if (stmt.value != null) value = evaluate(stmt.value);

		throw new Return(value);
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		// defines a variable, checks if there is an initializer
		// if not then it gets initialized to null
		Object value = null;
		if (stmt.initializer != null) {
			value = evaluate(stmt.initializer);
		}

		// adds the variable name and value to environment
		environment.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		// keep executing statement body while statement
		// condition evaluates to a non falsey value
		while (isTruthy(evaluate(stmt.condition))) {
			execute(stmt.body);
		}

		return null;
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		// assigns new value to variable
		// obtains value
		Object value = evaluate(expr.value);

		Integer distance = locals.get(expr);
		// if is local then assigns in current environment
		if (distance != null) {
			environment.assignAt(distance, expr.name, value);
		} else {
			// updates it in global scope
			globals.assign(expr.name, value);
		}

		return value;
	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
			case GREATER:
				checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;
			case LESS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)right;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;
			case BANG_EQUAL: return !isEqual(left, right);
			case EQUAL_EQUAL: return isEqual(left, right);
			case MINUS:
				checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			// special case because '+' can be used for adding numbers
			// or string concatenation
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				}

				if (left instanceof String && right instanceof String) {
					return (String)left + (String)right;
				}

				throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");

			case SLASH:
				checkNumberOperands(expr.operator, left, right);
				return (double)left / (double)right;
			case STAR:
				checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right; 
		}

		return null;
	}

	@Override
	public Object visitCallExpr(Expr.Call expr) {
		Object callee = evaluate(expr.callee);

		List<Object> arguments = new ArrayList<>();
		for (Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}

		// throws error if calling something that is not a function
		if (!(callee instanceof DrawCallable))
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");

		DrawCallable function = (DrawCallable)callee;

		// checks if call has same number of arguments as function definition
		if (arguments.size() != function.arity()) {
			throw new RuntimeError(
				expr.paren, 
				"Expected " + function.arity() + 
				" arguments but got " + arguments.size() + "."
				);
		}

		return function.call(this, arguments);

	}

}
