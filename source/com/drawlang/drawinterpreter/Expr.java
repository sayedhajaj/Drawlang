package com.drawlang.drawinterpreter;

import java.util.List;

// these are all for creating a syntax tree of expressions
// where each expression can contain multiple expressions

abstract class Expr {
	interface Visitor<R> {
		R visitArrayLiteralExpr(ArrayLiteral expr);

		R visitBinaryExpr(Binary expr);

		R visitCallExpr(Call expr);

		R visitGetExpr(Get expr);

		R visitGroupingExpr(Grouping expr);

		R visitLiteralExpr(Literal expr);

		R visitLogicalExpr(Logical expr);

		R visitSetExpr(Set expr);

		R visitSuperExpr(Super expr);

		R visitTernaryExpr(Ternary expr);

		R visitThisExpr(This expr);

		R visitUnaryExpr(Unary expr);

		R visitVariableExpr(Variable expr);

		R visitAssignExpr(Assign expr);

		R visitFunctionExpr(Function expr);
	}

	static class ArrayLiteral extends Expr {

		ArrayLiteral(List<Expr> values) {
			this.values = values;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitArrayLiteralExpr(this);
		}

		final List<Expr> values;
	}

	static class Binary extends Expr {
		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		final Expr left;
		final Token operator;
		final Expr right;
	}

	static class Call extends Expr {
		Call(Expr callee, Token paren, List<Expr> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpr(this);
		}

		final Expr callee;
		final Token paren;
		final List<Expr> arguments;
	}

	static class Get extends Expr {
		Get(Expr object, Token name, Expr index) {
			this.object = object;
			this.name = name;
			this.index = index;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGetExpr(this);
		}

		final Expr object;
		final Token name;
		final Expr index;
	}

	static class Grouping extends Expr {
		Grouping(Expr expression) {
			this.expression = expression;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		final Expr expression;
	}

	static class Literal extends Expr {
		Literal(Object value) {
			this.value = value;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		final Object value;
	}

	static class Logical extends Expr {
		Logical(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpr(this);
		}

		final Expr left;
		final Token operator;
		final Expr right;
	}

	static class Set extends Expr {
		Set(Expr object, Token name, Expr index, Expr value) {
			this.object = object;
			this.name = name;
			this.index = index;
			this.value = value;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitSetExpr(this);
		}

		final Expr object;
		final Token name;
		final Expr index;
		final Expr value;
	}

	static class Super extends Expr {
		Super(Token keyword, Token method) {
			this.keyword = keyword;
			this.method = method;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitSuperExpr(this);
		}

		final Token keyword;
		final Token method;
	}

	static class Ternary extends Expr {
		Ternary(Expr expr, Expr thenBranch, Expr elseBranch) {
			this.expr = expr;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitTernaryExpr(this);
		}

		final Expr expr;
		final Expr thenBranch;
		final Expr elseBranch;
	}

	static class This extends Expr {
		This(Token keyword) {
			this.keyword = keyword;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitThisExpr(this);
		}

		final Token keyword;
	}

	static class Unary extends Expr {
		Unary(Token operator, Expr right, Boolean postfix) {
			this.operator = operator;
			this.right = right;
			this.postfix = postfix;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		final Token operator;
		final Expr right;
		final Boolean postfix;
	}

	static class Variable extends Expr {
		Variable(Token name) {
			this.name = name;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpr(this);
		}

		final Token name;
	}

	static class Assign extends Expr {
		Assign(Token name, Expr value, Token equals) {
			this.name = name;
			this.value = value;
			this.equals = equals;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpr(this);
		}

		final Token name;
		final Expr value;
		final Token equals;
	}

	static class Function extends Expr {
		Function(List<Token> parameters, List<Stmt> body) {
			this.parameters = parameters;
			this.body = body;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionExpr(this);
		}

		final List<Token> parameters;
		final List<Stmt> body;
	}

	abstract <R> R accept(Visitor<R> visitor);

}