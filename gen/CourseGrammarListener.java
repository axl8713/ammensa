// Generated from /home/aleric/repos/ammensa/src/main/antlr4/CourseGrammar.g4 by ANTLR 4.7

	package net.ammensa.parse.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CourseGrammarParser}.
 */
public interface CourseGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CourseGrammarParser#courses}.
	 * @param ctx the parse tree
	 */
	void enterCourses(CourseGrammarParser.CoursesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CourseGrammarParser#courses}.
	 * @param ctx the parse tree
	 */
	void exitCourses(CourseGrammarParser.CoursesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CourseGrammarParser#course}.
	 * @param ctx the parse tree
	 */
	void enterCourse(CourseGrammarParser.CourseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CourseGrammarParser#course}.
	 * @param ctx the parse tree
	 */
	void exitCourse(CourseGrammarParser.CourseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CourseGrammarParser#ingredients}.
	 * @param ctx the parse tree
	 */
	void enterIngredients(CourseGrammarParser.IngredientsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CourseGrammarParser#ingredients}.
	 * @param ctx the parse tree
	 */
	void exitIngredients(CourseGrammarParser.IngredientsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CourseGrammarParser#content}.
	 * @param ctx the parse tree
	 */
	void enterContent(CourseGrammarParser.ContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CourseGrammarParser#content}.
	 * @param ctx the parse tree
	 */
	void exitContent(CourseGrammarParser.ContentContext ctx);
}