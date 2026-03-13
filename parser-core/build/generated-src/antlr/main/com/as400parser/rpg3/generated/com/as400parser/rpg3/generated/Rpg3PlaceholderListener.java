// Generated from com/as400parser/rpg3/generated/Rpg3Placeholder.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Rpg3PlaceholderParser}.
 */
public interface Rpg3PlaceholderListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Rpg3PlaceholderParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(Rpg3PlaceholderParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3PlaceholderParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(Rpg3PlaceholderParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3PlaceholderParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(Rpg3PlaceholderParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3PlaceholderParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(Rpg3PlaceholderParser.LineContext ctx);
}