// Generated from D:/Code/AS400_Parser/parser-core/src/main/antlr/com/as400parser/rpg3/generated/Rpg3Parser.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Rpg3Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Rpg3ParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#rpg3Program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRpg3Program(Rpg3Parser.Rpg3ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#headerSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeaderSpec(Rpg3Parser.HeaderSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#fileSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileSpec(Rpg3Parser.FileSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#extensionSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtensionSpec(Rpg3Parser.ExtensionSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#alternatingArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternatingArray(Rpg3Parser.AlternatingArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#inputSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputSpec(Rpg3Parser.InputSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#inputFieldDetail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputFieldDetail(Rpg3Parser.InputFieldDetailContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#calculationSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalculationSpec(Rpg3Parser.CalculationSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#subroutine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubroutine(Rpg3Parser.SubroutineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#calcStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalcStatement(Rpg3Parser.CalcStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#commentLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommentLine(Rpg3Parser.CommentLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#ifBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfBlock(Rpg3Parser.IfBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#doBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoBlock(Rpg3Parser.DoBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dowBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDowBlock(Rpg3Parser.DowBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#douBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDouBlock(Rpg3Parser.DouBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#caseBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseBlock(Rpg3Parser.CaseBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecPrefix(Rpg3Parser.CspecPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecSuffix(Rpg3Parser.CspecSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#calcLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalcLine(Rpg3Parser.CalcLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecIFxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecIFxx(Rpg3Parser.CspecIFxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecELSE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecELSE(Rpg3Parser.CspecELSEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecENDIF}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecENDIF(Rpg3Parser.CspecENDIFContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecDO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecDO(Rpg3Parser.CspecDOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecDOWxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecDOWxx(Rpg3Parser.CspecDOWxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecDOUxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecDOUxx(Rpg3Parser.CspecDOUxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecENDDO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecENDDO(Rpg3Parser.CspecENDDOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecCASxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecCASxx(Rpg3Parser.CspecCASxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecCAS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecCAS(Rpg3Parser.CspecCASContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecENDCS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecENDCS(Rpg3Parser.CspecENDCSContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecANDxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecANDxx(Rpg3Parser.CspecANDxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecORxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecORxx(Rpg3Parser.CspecORxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecBEGSR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecBEGSR(Rpg3Parser.CspecBEGSRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspecENDSR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspecENDSR(Rpg3Parser.CspecENDSRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsOperation(Rpg3Parser.CsOperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#onOffFlag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnOffFlag(Rpg3Parser.OnOffFlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIndicator(Rpg3Parser.CsIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csResultIndicators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsResultIndicators(Rpg3Parser.CsResultIndicatorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputSpec(Rpg3Parser.OutputSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputRecordSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputRecordSpec(Rpg3Parser.OutputRecordSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputFieldSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputFieldSpec(Rpg3Parser.OutputFieldSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputCondIndicators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputCondIndicators(Rpg3Parser.OutputCondIndicatorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endSource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndSource(Rpg3Parser.EndSourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endSourceLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndSourceLine(Rpg3Parser.EndSourceLineContext ctx);
}