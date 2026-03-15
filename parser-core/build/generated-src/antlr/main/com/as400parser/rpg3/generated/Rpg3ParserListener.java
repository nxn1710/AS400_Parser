// Generated from D:/Code/AS400_Parser/parser-core/src/main/antlr/com/as400parser/rpg3/generated/Rpg3Parser.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Rpg3Parser}.
 */
public interface Rpg3ParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#rpg3Program}.
	 * @param ctx the parse tree
	 */
	void enterRpg3Program(Rpg3Parser.Rpg3ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#rpg3Program}.
	 * @param ctx the parse tree
	 */
	void exitRpg3Program(Rpg3Parser.Rpg3ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#headerSpec}.
	 * @param ctx the parse tree
	 */
	void enterHeaderSpec(Rpg3Parser.HeaderSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#headerSpec}.
	 * @param ctx the parse tree
	 */
	void exitHeaderSpec(Rpg3Parser.HeaderSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#fileSpec}.
	 * @param ctx the parse tree
	 */
	void enterFileSpec(Rpg3Parser.FileSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#fileSpec}.
	 * @param ctx the parse tree
	 */
	void exitFileSpec(Rpg3Parser.FileSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#extensionSpec}.
	 * @param ctx the parse tree
	 */
	void enterExtensionSpec(Rpg3Parser.ExtensionSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#extensionSpec}.
	 * @param ctx the parse tree
	 */
	void exitExtensionSpec(Rpg3Parser.ExtensionSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#alternatingArray}.
	 * @param ctx the parse tree
	 */
	void enterAlternatingArray(Rpg3Parser.AlternatingArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#alternatingArray}.
	 * @param ctx the parse tree
	 */
	void exitAlternatingArray(Rpg3Parser.AlternatingArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#inputSpec}.
	 * @param ctx the parse tree
	 */
	void enterInputSpec(Rpg3Parser.InputSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#inputSpec}.
	 * @param ctx the parse tree
	 */
	void exitInputSpec(Rpg3Parser.InputSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#inputFieldDetail}.
	 * @param ctx the parse tree
	 */
	void enterInputFieldDetail(Rpg3Parser.InputFieldDetailContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#inputFieldDetail}.
	 * @param ctx the parse tree
	 */
	void exitInputFieldDetail(Rpg3Parser.InputFieldDetailContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#calculationSpec}.
	 * @param ctx the parse tree
	 */
	void enterCalculationSpec(Rpg3Parser.CalculationSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#calculationSpec}.
	 * @param ctx the parse tree
	 */
	void exitCalculationSpec(Rpg3Parser.CalculationSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#subroutine}.
	 * @param ctx the parse tree
	 */
	void enterSubroutine(Rpg3Parser.SubroutineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#subroutine}.
	 * @param ctx the parse tree
	 */
	void exitSubroutine(Rpg3Parser.SubroutineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#calcStatement}.
	 * @param ctx the parse tree
	 */
	void enterCalcStatement(Rpg3Parser.CalcStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#calcStatement}.
	 * @param ctx the parse tree
	 */
	void exitCalcStatement(Rpg3Parser.CalcStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#commentLine}.
	 * @param ctx the parse tree
	 */
	void enterCommentLine(Rpg3Parser.CommentLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#commentLine}.
	 * @param ctx the parse tree
	 */
	void exitCommentLine(Rpg3Parser.CommentLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void enterIfBlock(Rpg3Parser.IfBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void exitIfBlock(Rpg3Parser.IfBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#doBlock}.
	 * @param ctx the parse tree
	 */
	void enterDoBlock(Rpg3Parser.DoBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#doBlock}.
	 * @param ctx the parse tree
	 */
	void exitDoBlock(Rpg3Parser.DoBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dowBlock}.
	 * @param ctx the parse tree
	 */
	void enterDowBlock(Rpg3Parser.DowBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dowBlock}.
	 * @param ctx the parse tree
	 */
	void exitDowBlock(Rpg3Parser.DowBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#douBlock}.
	 * @param ctx the parse tree
	 */
	void enterDouBlock(Rpg3Parser.DouBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#douBlock}.
	 * @param ctx the parse tree
	 */
	void exitDouBlock(Rpg3Parser.DouBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void enterCaseBlock(Rpg3Parser.CaseBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#caseBlock}.
	 * @param ctx the parse tree
	 */
	void exitCaseBlock(Rpg3Parser.CaseBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecPrefix}.
	 * @param ctx the parse tree
	 */
	void enterCspecPrefix(Rpg3Parser.CspecPrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecPrefix}.
	 * @param ctx the parse tree
	 */
	void exitCspecPrefix(Rpg3Parser.CspecPrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecSuffix}.
	 * @param ctx the parse tree
	 */
	void enterCspecSuffix(Rpg3Parser.CspecSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecSuffix}.
	 * @param ctx the parse tree
	 */
	void exitCspecSuffix(Rpg3Parser.CspecSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#calcLine}.
	 * @param ctx the parse tree
	 */
	void enterCalcLine(Rpg3Parser.CalcLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#calcLine}.
	 * @param ctx the parse tree
	 */
	void exitCalcLine(Rpg3Parser.CalcLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecIFxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecIFxx(Rpg3Parser.CspecIFxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecIFxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecIFxx(Rpg3Parser.CspecIFxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecELSE}.
	 * @param ctx the parse tree
	 */
	void enterCspecELSE(Rpg3Parser.CspecELSEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecELSE}.
	 * @param ctx the parse tree
	 */
	void exitCspecELSE(Rpg3Parser.CspecELSEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecENDIF}.
	 * @param ctx the parse tree
	 */
	void enterCspecENDIF(Rpg3Parser.CspecENDIFContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecENDIF}.
	 * @param ctx the parse tree
	 */
	void exitCspecENDIF(Rpg3Parser.CspecENDIFContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecDO}.
	 * @param ctx the parse tree
	 */
	void enterCspecDO(Rpg3Parser.CspecDOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecDO}.
	 * @param ctx the parse tree
	 */
	void exitCspecDO(Rpg3Parser.CspecDOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecDOWxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecDOWxx(Rpg3Parser.CspecDOWxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecDOWxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecDOWxx(Rpg3Parser.CspecDOWxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecDOUxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecDOUxx(Rpg3Parser.CspecDOUxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecDOUxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecDOUxx(Rpg3Parser.CspecDOUxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecENDDO}.
	 * @param ctx the parse tree
	 */
	void enterCspecENDDO(Rpg3Parser.CspecENDDOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecENDDO}.
	 * @param ctx the parse tree
	 */
	void exitCspecENDDO(Rpg3Parser.CspecENDDOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecCASxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecCASxx(Rpg3Parser.CspecCASxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecCASxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecCASxx(Rpg3Parser.CspecCASxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecCAS}.
	 * @param ctx the parse tree
	 */
	void enterCspecCAS(Rpg3Parser.CspecCASContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecCAS}.
	 * @param ctx the parse tree
	 */
	void exitCspecCAS(Rpg3Parser.CspecCASContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecENDCS}.
	 * @param ctx the parse tree
	 */
	void enterCspecENDCS(Rpg3Parser.CspecENDCSContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecENDCS}.
	 * @param ctx the parse tree
	 */
	void exitCspecENDCS(Rpg3Parser.CspecENDCSContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecANDxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecANDxx(Rpg3Parser.CspecANDxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecANDxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecANDxx(Rpg3Parser.CspecANDxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecORxx}.
	 * @param ctx the parse tree
	 */
	void enterCspecORxx(Rpg3Parser.CspecORxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecORxx}.
	 * @param ctx the parse tree
	 */
	void exitCspecORxx(Rpg3Parser.CspecORxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecBEGSR}.
	 * @param ctx the parse tree
	 */
	void enterCspecBEGSR(Rpg3Parser.CspecBEGSRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecBEGSR}.
	 * @param ctx the parse tree
	 */
	void exitCspecBEGSR(Rpg3Parser.CspecBEGSRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspecENDSR}.
	 * @param ctx the parse tree
	 */
	void enterCspecENDSR(Rpg3Parser.CspecENDSRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspecENDSR}.
	 * @param ctx the parse tree
	 */
	void exitCspecENDSR(Rpg3Parser.CspecENDSRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csOperation}.
	 * @param ctx the parse tree
	 */
	void enterCsOperation(Rpg3Parser.CsOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csOperation}.
	 * @param ctx the parse tree
	 */
	void exitCsOperation(Rpg3Parser.CsOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#onOffFlag}.
	 * @param ctx the parse tree
	 */
	void enterOnOffFlag(Rpg3Parser.OnOffFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#onOffFlag}.
	 * @param ctx the parse tree
	 */
	void exitOnOffFlag(Rpg3Parser.OnOffFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIndicator}.
	 * @param ctx the parse tree
	 */
	void enterCsIndicator(Rpg3Parser.CsIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIndicator}.
	 * @param ctx the parse tree
	 */
	void exitCsIndicator(Rpg3Parser.CsIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csResultIndicators}.
	 * @param ctx the parse tree
	 */
	void enterCsResultIndicators(Rpg3Parser.CsResultIndicatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csResultIndicators}.
	 * @param ctx the parse tree
	 */
	void exitCsResultIndicators(Rpg3Parser.CsResultIndicatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputSpec}.
	 * @param ctx the parse tree
	 */
	void enterOutputSpec(Rpg3Parser.OutputSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputSpec}.
	 * @param ctx the parse tree
	 */
	void exitOutputSpec(Rpg3Parser.OutputSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputRecordSpec}.
	 * @param ctx the parse tree
	 */
	void enterOutputRecordSpec(Rpg3Parser.OutputRecordSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputRecordSpec}.
	 * @param ctx the parse tree
	 */
	void exitOutputRecordSpec(Rpg3Parser.OutputRecordSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputFieldSpec}.
	 * @param ctx the parse tree
	 */
	void enterOutputFieldSpec(Rpg3Parser.OutputFieldSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputFieldSpec}.
	 * @param ctx the parse tree
	 */
	void exitOutputFieldSpec(Rpg3Parser.OutputFieldSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputCondIndicators}.
	 * @param ctx the parse tree
	 */
	void enterOutputCondIndicators(Rpg3Parser.OutputCondIndicatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputCondIndicators}.
	 * @param ctx the parse tree
	 */
	void exitOutputCondIndicators(Rpg3Parser.OutputCondIndicatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#endSource}.
	 * @param ctx the parse tree
	 */
	void enterEndSource(Rpg3Parser.EndSourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#endSource}.
	 * @param ctx the parse tree
	 */
	void exitEndSource(Rpg3Parser.EndSourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#endSourceLine}.
	 * @param ctx the parse tree
	 */
	void enterEndSourceLine(Rpg3Parser.EndSourceLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#endSourceLine}.
	 * @param ctx the parse tree
	 */
	void exitEndSourceLine(Rpg3Parser.EndSourceLineContext ctx);
}