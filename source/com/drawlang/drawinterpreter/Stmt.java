package com.drawlang.drawinterpreter;

import java.util.List;

// similar to Expr.java - creates a syntax tree of statements

abstract class Stmt {
	interface Visitor <R> {
		R visitBlockStmt(Block Stmt);
		R visitBreakStmt(Break Stmt);
		R visitClassStmt(Class Stmt);
		R visitContinueStmt(Continue Stmt);
		R visitExpressionStmt(Expression stmt);
		R visitFunctionStmt(Function stmt);
		R visitIfStmt(If stmt);
		R visitReturnStmt(Return stmt);
		R visitVarStmt(Var stmt);
		R visitWhileStmt(While stmt);
	}

	static class Block extends Stmt {
		Block(List<Stmt> statements) {
			this.statements = statements;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

		final List<Stmt> statements;
	}

	static class Break extends Stmt {
		Break() {
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStmt(this);
		}
	}

	static class Class extends Stmt {
		Class(Token name, Expr superclass, List<Stmt.Function> methods, List<Stmt.Function> classMethods) {
			this.name = name;
			this.superclass = superclass;
			this.methods = methods;
			this.classMethods = classMethods;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitClassStmt(this);
		}

		final Token name;
		final Expr superclass;
		final List<Stmt.Function> methods;
		final List<Stmt.Function> classMethods;
	}

	static class Continue extends Stmt {
		Continue() {
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitContinueStmt(this);
		}
	}

	static class Expression extends Stmt {
		Expression(Expr expression) {
			this.expression = expression;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

		final Expr expression;
	}

	static class Function extends Stmt {
		Function(Token name, Expr.Function function) {
			this.name = name;
			this.function = function;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}

		final Token name;
		final Expr.Function function;
	}

	static class If extends Stmt {
		If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

		final Expr condition;
		final Stmt thenBranch;
		final Stmt elseBranch;
	}

	static class Return extends Stmt {
		Return(Token keyword, Expr value) {
			this.keyword = keyword;
			this.value = value;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}

		final Token keyword;
		final Expr value;
	}

	static class Var extends Stmt {
		Var(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}

		final Token name;
		final Expr initializer;
	}

	static class While extends Stmt {
		While(Expr condition, Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

		final Expr condition;
		final Stmt body;
	}

	abstract <R> R accept(Visitor<R> visitor);

}