// Generated from com/as400parser/rpg3/generated/Rpg3Placeholder.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Rpg3PlaceholderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Rpg3PlaceholderVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Rpg3PlaceholderParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(Rpg3PlaceholderParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3PlaceholderParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(Rpg3PlaceholderParser.LineContext ctx);
}