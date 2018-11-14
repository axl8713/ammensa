// Generated from /home/aleric/repos/ammensa/src/main/antlr4/CourseGrammar.g4 by ANTLR 4.7

	package net.ammensa.parse.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CourseGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CourseGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CourseGrammarParser#courses}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCourses(CourseGrammarParser.CoursesContext ctx);
	/**
	 * Visit a parse tree produced by {@link CourseGrammarParser#course}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCourse(CourseGrammarParser.CourseContext ctx);
	/**
	 * Visit a parse tree produced by {@link CourseGrammarParser#ingredients}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIngredients(CourseGrammarParser.IngredientsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CourseGrammarParser#content}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContent(CourseGrammarParser.ContentContext ctx);
}