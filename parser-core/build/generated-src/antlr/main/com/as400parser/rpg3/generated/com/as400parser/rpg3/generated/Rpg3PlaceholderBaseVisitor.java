// Generated from com/as400parser/rpg3/generated/Rpg3Placeholder.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link Rpg3PlaceholderVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
@SuppressWarnings("CheckReturnValue")
public class Rpg3PlaceholderBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements Rpg3PlaceholderVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitProgram(Rpg3PlaceholderParser.ProgramContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitLine(Rpg3PlaceholderParser.LineContext ctx) { return visitChildren(ctx); }
}