package com.drawlang.drawinterpreter;

import java.util.List;

// these are all for creating a syntax tree of expressions
// where each expression can contain multiple expressions

abstract class Expr {
	interface Visitor<R> {
		R visitBinaryExpr(Binary expr);
		R visitGroupingExpr(Grouping expr);
		R visitLiteralExpr(Literal expr);
		R visitUnaryExpr(Unary expr);
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

	abstract <R> R accept(Visitor<R> visitor);

}