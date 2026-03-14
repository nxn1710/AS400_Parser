// Generated from d:/Code/AS400_Parser/grammar/rpg3/Rpg3Parser.g4 by ANTLR 4.13.2
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
	 * Enter a parse tree produced by {@link Rpg3Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(Rpg3Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(Rpg3Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(Rpg3Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(Rpg3Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#ifstatement}.
	 * @param ctx the parse tree
	 */
	void enterIfstatement(Rpg3Parser.IfstatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#ifstatement}.
	 * @param ctx the parse tree
	 */
	void exitIfstatement(Rpg3Parser.IfstatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#elsestmt}.
	 * @param ctx the parse tree
	 */
	void enterElsestmt(Rpg3Parser.ElsestmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#elsestmt}.
	 * @param ctx the parse tree
	 */
	void exitElsestmt(Rpg3Parser.ElsestmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#beginif}.
	 * @param ctx the parse tree
	 */
	void enterBeginif(Rpg3Parser.BeginifContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#beginif}.
	 * @param ctx the parse tree
	 */
	void exitBeginif(Rpg3Parser.BeginifContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#begsr}.
	 * @param ctx the parse tree
	 */
	void enterBegsr(Rpg3Parser.BegsrContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#begsr}.
	 * @param ctx the parse tree
	 */
	void exitBegsr(Rpg3Parser.BegsrContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#endsr}.
	 * @param ctx the parse tree
	 */
	void enterEndsr(Rpg3Parser.EndsrContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#endsr}.
	 * @param ctx the parse tree
	 */
	void exitEndsr(Rpg3Parser.EndsrContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#endif}.
	 * @param ctx the parse tree
	 */
	void enterEndif(Rpg3Parser.EndifContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#endif}.
	 * @param ctx the parse tree
	 */
	void exitEndif(Rpg3Parser.EndifContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csENDIF_rule}.
	 * @param ctx the parse tree
	 */
	void enterCsENDIF_rule(Rpg3Parser.CsENDIF_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csENDIF_rule}.
	 * @param ctx the parse tree
	 */
	void exitCsENDIF_rule(Rpg3Parser.CsENDIF_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#enddo}.
	 * @param ctx the parse tree
	 */
	void enterEnddo(Rpg3Parser.EnddoContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#enddo}.
	 * @param ctx the parse tree
	 */
	void exitEnddo(Rpg3Parser.EnddoContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard}.
	 * @param ctx the parse tree
	 */
	void enterCspec_fixed_standard(Rpg3Parser.Cspec_fixed_standardContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard}.
	 * @param ctx the parse tree
	 */
	void exitCspec_fixed_standard(Rpg3Parser.Cspec_fixed_standardContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csRETURN_FIXED}.
	 * @param ctx the parse tree
	 */
	void enterCsRETURN_FIXED(Rpg3Parser.CsRETURN_FIXEDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csRETURN_FIXED}.
	 * @param ctx the parse tree
	 */
	void exitCsRETURN_FIXED(Rpg3Parser.CsRETURN_FIXEDContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSORTA_FIXED}.
	 * @param ctx the parse tree
	 */
	void enterCsSORTA_FIXED(Rpg3Parser.CsSORTA_FIXEDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSORTA_FIXED}.
	 * @param ctx the parse tree
	 */
	void exitCsSORTA_FIXED(Rpg3Parser.CsSORTA_FIXEDContext ctx);
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
	 * Enter a parse tree produced by {@link Rpg3Parser#lineCounterSpec}.
	 * @param ctx the parse tree
	 */
	void enterLineCounterSpec(Rpg3Parser.LineCounterSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#lineCounterSpec}.
	 * @param ctx the parse tree
	 */
	void exitLineCounterSpec(Rpg3Parser.LineCounterSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#compileTimeData}.
	 * @param ctx the parse tree
	 */
	void enterCompileTimeData(Rpg3Parser.CompileTimeDataContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#compileTimeData}.
	 * @param ctx the parse tree
	 */
	void exitCompileTimeData(Rpg3Parser.CompileTimeDataContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirective(Rpg3Parser.DirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirective(Rpg3Parser.DirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#symbolicConstants}.
	 * @param ctx the parse tree
	 */
	void enterSymbolicConstants(Rpg3Parser.SymbolicConstantsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#symbolicConstants}.
	 * @param ctx the parse tree
	 */
	void exitSymbolicConstants(Rpg3Parser.SymbolicConstantsContext ctx);
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
	 * Enter a parse tree produced by {@link Rpg3Parser#endSourceHead}.
	 * @param ctx the parse tree
	 */
	void enterEndSourceHead(Rpg3Parser.EndSourceHeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#endSourceHead}.
	 * @param ctx the parse tree
	 */
	void exitEndSourceHead(Rpg3Parser.EndSourceHeadContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#star_comments}.
	 * @param ctx the parse tree
	 */
	void enterStar_comments(Rpg3Parser.Star_commentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#star_comments}.
	 * @param ctx the parse tree
	 */
	void exitStar_comments(Rpg3Parser.Star_commentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#comments}.
	 * @param ctx the parse tree
	 */
	void enterComments(Rpg3Parser.CommentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#comments}.
	 * @param ctx the parse tree
	 */
	void exitComments(Rpg3Parser.CommentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#casestatement}.
	 * @param ctx the parse tree
	 */
	void enterCasestatement(Rpg3Parser.CasestatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#casestatement}.
	 * @param ctx the parse tree
	 */
	void exitCasestatement(Rpg3Parser.CasestatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#casestatementend}.
	 * @param ctx the parse tree
	 */
	void enterCasestatementend(Rpg3Parser.CasestatementendContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#casestatementend}.
	 * @param ctx the parse tree
	 */
	void exitCasestatementend(Rpg3Parser.CasestatementendContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFxx}.
	 * @param ctx the parse tree
	 */
	void enterCsIFxx(Rpg3Parser.CsIFxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFxx}.
	 * @param ctx the parse tree
	 */
	void exitCsIFxx(Rpg3Parser.CsIFxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOUxx}.
	 * @param ctx the parse tree
	 */
	void enterCsDOUxx(Rpg3Parser.CsDOUxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOUxx}.
	 * @param ctx the parse tree
	 */
	void exitCsDOUxx(Rpg3Parser.CsDOUxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWxx}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWxx(Rpg3Parser.CsDOWxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWxx}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWxx(Rpg3Parser.CsDOWxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#complexCondxx}.
	 * @param ctx the parse tree
	 */
	void enterComplexCondxx(Rpg3Parser.ComplexCondxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#complexCondxx}.
	 * @param ctx the parse tree
	 */
	void exitComplexCondxx(Rpg3Parser.ComplexCondxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDxx}.
	 * @param ctx the parse tree
	 */
	void enterCsANDxx(Rpg3Parser.CsANDxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDxx}.
	 * @param ctx the parse tree
	 */
	void exitCsANDxx(Rpg3Parser.CsANDxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORxx}.
	 * @param ctx the parse tree
	 */
	void enterCsORxx(Rpg3Parser.CsORxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORxx}.
	 * @param ctx the parse tree
	 */
	void exitCsORxx(Rpg3Parser.CsORxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#ospec_fixed}.
	 * @param ctx the parse tree
	 */
	void enterOspec_fixed(Rpg3Parser.Ospec_fixedContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#ospec_fixed}.
	 * @param ctx the parse tree
	 */
	void exitOspec_fixed(Rpg3Parser.Ospec_fixedContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc1}.
	 * @param ctx the parse tree
	 */
	void enterOs_fixed_pgmdesc1(Rpg3Parser.Os_fixed_pgmdesc1Context ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc1}.
	 * @param ctx the parse tree
	 */
	void exitOs_fixed_pgmdesc1(Rpg3Parser.Os_fixed_pgmdesc1Context ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputConditioningOnOffIndicator}.
	 * @param ctx the parse tree
	 */
	void enterOutputConditioningOnOffIndicator(Rpg3Parser.OutputConditioningOnOffIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputConditioningOnOffIndicator}.
	 * @param ctx the parse tree
	 */
	void exitOutputConditioningOnOffIndicator(Rpg3Parser.OutputConditioningOnOffIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#outputConditioningIndicator}.
	 * @param ctx the parse tree
	 */
	void enterOutputConditioningIndicator(Rpg3Parser.OutputConditioningIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#outputConditioningIndicator}.
	 * @param ctx the parse tree
	 */
	void exitOutputConditioningIndicator(Rpg3Parser.OutputConditioningIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc_compound}.
	 * @param ctx the parse tree
	 */
	void enterOs_fixed_pgmdesc_compound(Rpg3Parser.Os_fixed_pgmdesc_compoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc_compound}.
	 * @param ctx the parse tree
	 */
	void exitOs_fixed_pgmdesc_compound(Rpg3Parser.Os_fixed_pgmdesc_compoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc2}.
	 * @param ctx the parse tree
	 */
	void enterOs_fixed_pgmdesc2(Rpg3Parser.Os_fixed_pgmdesc2Context ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc2}.
	 * @param ctx the parse tree
	 */
	void exitOs_fixed_pgmdesc2(Rpg3Parser.Os_fixed_pgmdesc2Context ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#os_fixed_pgmfield}.
	 * @param ctx the parse tree
	 */
	void enterOs_fixed_pgmfield(Rpg3Parser.Os_fixed_pgmfieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmfield}.
	 * @param ctx the parse tree
	 */
	void exitOs_fixed_pgmfield(Rpg3Parser.Os_fixed_pgmfieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#fspec_fixed}.
	 * @param ctx the parse tree
	 */
	void enterFspec_fixed(Rpg3Parser.Fspec_fixedContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#fspec_fixed}.
	 * @param ctx the parse tree
	 */
	void exitFspec_fixed(Rpg3Parser.Fspec_fixedContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspec_fixed}.
	 * @param ctx the parse tree
	 */
	void enterCspec_fixed(Rpg3Parser.Cspec_fixedContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspec_fixed}.
	 * @param ctx the parse tree
	 */
	void exitCspec_fixed(Rpg3Parser.Cspec_fixedContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspec_continuedIndicators}.
	 * @param ctx the parse tree
	 */
	void enterCspec_continuedIndicators(Rpg3Parser.Cspec_continuedIndicatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspec_continuedIndicators}.
	 * @param ctx the parse tree
	 */
	void exitCspec_continuedIndicators(Rpg3Parser.Cspec_continuedIndicatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspec_blank}.
	 * @param ctx the parse tree
	 */
	void enterCspec_blank(Rpg3Parser.Cspec_blankContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspec_blank}.
	 * @param ctx the parse tree
	 */
	void exitCspec_blank(Rpg3Parser.Cspec_blankContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#blank_spec}.
	 * @param ctx the parse tree
	 */
	void enterBlank_spec(Rpg3Parser.Blank_specContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#blank_spec}.
	 * @param ctx the parse tree
	 */
	void exitBlank_spec(Rpg3Parser.Blank_specContext ctx);
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
	 * Enter a parse tree produced by {@link Rpg3Parser#csBEGSR}.
	 * @param ctx the parse tree
	 */
	void enterCsBEGSR(Rpg3Parser.CsBEGSRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csBEGSR}.
	 * @param ctx the parse tree
	 */
	void exitCsBEGSR(Rpg3Parser.CsBEGSRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csENDSR}.
	 * @param ctx the parse tree
	 */
	void enterCsENDSR(Rpg3Parser.CsENDSRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csENDSR}.
	 * @param ctx the parse tree
	 */
	void exitCsENDSR(Rpg3Parser.CsENDSRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#onOffIndicatorsFlag}.
	 * @param ctx the parse tree
	 */
	void enterOnOffIndicatorsFlag(Rpg3Parser.OnOffIndicatorsFlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#onOffIndicatorsFlag}.
	 * @param ctx the parse tree
	 */
	void exitOnOffIndicatorsFlag(Rpg3Parser.OnOffIndicatorsFlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cs_controlLevel}.
	 * @param ctx the parse tree
	 */
	void enterCs_controlLevel(Rpg3Parser.Cs_controlLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cs_controlLevel}.
	 * @param ctx the parse tree
	 */
	void exitCs_controlLevel(Rpg3Parser.Cs_controlLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cs_indicators}.
	 * @param ctx the parse tree
	 */
	void enterCs_indicators(Rpg3Parser.Cs_indicatorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cs_indicators}.
	 * @param ctx the parse tree
	 */
	void exitCs_indicators(Rpg3Parser.Cs_indicatorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#resultIndicator}.
	 * @param ctx the parse tree
	 */
	void enterResultIndicator(Rpg3Parser.ResultIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#resultIndicator}.
	 * @param ctx the parse tree
	 */
	void exitResultIndicator(Rpg3Parser.ResultIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard_parts}.
	 * @param ctx the parse tree
	 */
	void enterCspec_fixed_standard_parts(Rpg3Parser.Cspec_fixed_standard_partsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard_parts}.
	 * @param ctx the parse tree
	 */
	void exitCspec_fixed_standard_parts(Rpg3Parser.Cspec_fixed_standard_partsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csACQ}.
	 * @param ctx the parse tree
	 */
	void enterCsACQ(Rpg3Parser.CsACQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csACQ}.
	 * @param ctx the parse tree
	 */
	void exitCsACQ(Rpg3Parser.CsACQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csADD}.
	 * @param ctx the parse tree
	 */
	void enterCsADD(Rpg3Parser.CsADDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csADD}.
	 * @param ctx the parse tree
	 */
	void exitCsADD(Rpg3Parser.CsADDContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csADDDUR}.
	 * @param ctx the parse tree
	 */
	void enterCsADDDUR(Rpg3Parser.CsADDDURContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csADDDUR}.
	 * @param ctx the parse tree
	 */
	void exitCsADDDUR(Rpg3Parser.CsADDDURContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsANDEQ(Rpg3Parser.CsANDEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsANDEQ(Rpg3Parser.CsANDEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDNE}.
	 * @param ctx the parse tree
	 */
	void enterCsANDNE(Rpg3Parser.CsANDNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDNE}.
	 * @param ctx the parse tree
	 */
	void exitCsANDNE(Rpg3Parser.CsANDNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDLE}.
	 * @param ctx the parse tree
	 */
	void enterCsANDLE(Rpg3Parser.CsANDLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDLE}.
	 * @param ctx the parse tree
	 */
	void exitCsANDLE(Rpg3Parser.CsANDLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDLT}.
	 * @param ctx the parse tree
	 */
	void enterCsANDLT(Rpg3Parser.CsANDLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDLT}.
	 * @param ctx the parse tree
	 */
	void exitCsANDLT(Rpg3Parser.CsANDLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDGE}.
	 * @param ctx the parse tree
	 */
	void enterCsANDGE(Rpg3Parser.CsANDGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDGE}.
	 * @param ctx the parse tree
	 */
	void exitCsANDGE(Rpg3Parser.CsANDGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csANDGT}.
	 * @param ctx the parse tree
	 */
	void enterCsANDGT(Rpg3Parser.CsANDGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csANDGT}.
	 * @param ctx the parse tree
	 */
	void exitCsANDGT(Rpg3Parser.CsANDGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csBITOFF}.
	 * @param ctx the parse tree
	 */
	void enterCsBITOFF(Rpg3Parser.CsBITOFFContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csBITOFF}.
	 * @param ctx the parse tree
	 */
	void exitCsBITOFF(Rpg3Parser.CsBITOFFContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csBITON}.
	 * @param ctx the parse tree
	 */
	void enterCsBITON(Rpg3Parser.CsBITONContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csBITON}.
	 * @param ctx the parse tree
	 */
	void exitCsBITON(Rpg3Parser.CsBITONContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABxx}.
	 * @param ctx the parse tree
	 */
	void enterCsCABxx(Rpg3Parser.CsCABxxContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABxx}.
	 * @param ctx the parse tree
	 */
	void exitCsCABxx(Rpg3Parser.CsCABxxContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsCABEQ(Rpg3Parser.CsCABEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsCABEQ(Rpg3Parser.CsCABEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABNE}.
	 * @param ctx the parse tree
	 */
	void enterCsCABNE(Rpg3Parser.CsCABNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABNE}.
	 * @param ctx the parse tree
	 */
	void exitCsCABNE(Rpg3Parser.CsCABNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABLE}.
	 * @param ctx the parse tree
	 */
	void enterCsCABLE(Rpg3Parser.CsCABLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABLE}.
	 * @param ctx the parse tree
	 */
	void exitCsCABLE(Rpg3Parser.CsCABLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABLT}.
	 * @param ctx the parse tree
	 */
	void enterCsCABLT(Rpg3Parser.CsCABLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABLT}.
	 * @param ctx the parse tree
	 */
	void exitCsCABLT(Rpg3Parser.CsCABLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABGE}.
	 * @param ctx the parse tree
	 */
	void enterCsCABGE(Rpg3Parser.CsCABGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABGE}.
	 * @param ctx the parse tree
	 */
	void exitCsCABGE(Rpg3Parser.CsCABGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCABGT}.
	 * @param ctx the parse tree
	 */
	void enterCsCABGT(Rpg3Parser.CsCABGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCABGT}.
	 * @param ctx the parse tree
	 */
	void exitCsCABGT(Rpg3Parser.CsCABGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCALL}.
	 * @param ctx the parse tree
	 */
	void enterCsCALL(Rpg3Parser.CsCALLContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCALL}.
	 * @param ctx the parse tree
	 */
	void exitCsCALL(Rpg3Parser.CsCALLContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsCASEQ(Rpg3Parser.CsCASEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsCASEQ(Rpg3Parser.CsCASEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASNE}.
	 * @param ctx the parse tree
	 */
	void enterCsCASNE(Rpg3Parser.CsCASNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASNE}.
	 * @param ctx the parse tree
	 */
	void exitCsCASNE(Rpg3Parser.CsCASNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASLE}.
	 * @param ctx the parse tree
	 */
	void enterCsCASLE(Rpg3Parser.CsCASLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASLE}.
	 * @param ctx the parse tree
	 */
	void exitCsCASLE(Rpg3Parser.CsCASLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASLT}.
	 * @param ctx the parse tree
	 */
	void enterCsCASLT(Rpg3Parser.CsCASLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASLT}.
	 * @param ctx the parse tree
	 */
	void exitCsCASLT(Rpg3Parser.CsCASLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASGE}.
	 * @param ctx the parse tree
	 */
	void enterCsCASGE(Rpg3Parser.CsCASGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASGE}.
	 * @param ctx the parse tree
	 */
	void exitCsCASGE(Rpg3Parser.CsCASGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCASGT}.
	 * @param ctx the parse tree
	 */
	void enterCsCASGT(Rpg3Parser.CsCASGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCASGT}.
	 * @param ctx the parse tree
	 */
	void exitCsCASGT(Rpg3Parser.CsCASGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCAS}.
	 * @param ctx the parse tree
	 */
	void enterCsCAS(Rpg3Parser.CsCASContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCAS}.
	 * @param ctx the parse tree
	 */
	void exitCsCAS(Rpg3Parser.CsCASContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCAT}.
	 * @param ctx the parse tree
	 */
	void enterCsCAT(Rpg3Parser.CsCATContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCAT}.
	 * @param ctx the parse tree
	 */
	void exitCsCAT(Rpg3Parser.CsCATContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCHAIN}.
	 * @param ctx the parse tree
	 */
	void enterCsCHAIN(Rpg3Parser.CsCHAINContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCHAIN}.
	 * @param ctx the parse tree
	 */
	void exitCsCHAIN(Rpg3Parser.CsCHAINContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCHECK}.
	 * @param ctx the parse tree
	 */
	void enterCsCHECK(Rpg3Parser.CsCHECKContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCHECK}.
	 * @param ctx the parse tree
	 */
	void exitCsCHECK(Rpg3Parser.CsCHECKContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCHECKR}.
	 * @param ctx the parse tree
	 */
	void enterCsCHECKR(Rpg3Parser.CsCHECKRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCHECKR}.
	 * @param ctx the parse tree
	 */
	void exitCsCHECKR(Rpg3Parser.CsCHECKRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCLEAR}.
	 * @param ctx the parse tree
	 */
	void enterCsCLEAR(Rpg3Parser.CsCLEARContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCLEAR}.
	 * @param ctx the parse tree
	 */
	void exitCsCLEAR(Rpg3Parser.CsCLEARContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCLOSE}.
	 * @param ctx the parse tree
	 */
	void enterCsCLOSE(Rpg3Parser.CsCLOSEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCLOSE}.
	 * @param ctx the parse tree
	 */
	void exitCsCLOSE(Rpg3Parser.CsCLOSEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCOMMIT}.
	 * @param ctx the parse tree
	 */
	void enterCsCOMMIT(Rpg3Parser.CsCOMMITContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCOMMIT}.
	 * @param ctx the parse tree
	 */
	void exitCsCOMMIT(Rpg3Parser.CsCOMMITContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csCOMP}.
	 * @param ctx the parse tree
	 */
	void enterCsCOMP(Rpg3Parser.CsCOMPContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csCOMP}.
	 * @param ctx the parse tree
	 */
	void exitCsCOMP(Rpg3Parser.CsCOMPContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDEFINE}.
	 * @param ctx the parse tree
	 */
	void enterCsDEFINE(Rpg3Parser.CsDEFINEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDEFINE}.
	 * @param ctx the parse tree
	 */
	void exitCsDEFINE(Rpg3Parser.CsDEFINEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDELETE}.
	 * @param ctx the parse tree
	 */
	void enterCsDELETE(Rpg3Parser.CsDELETEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDELETE}.
	 * @param ctx the parse tree
	 */
	void exitCsDELETE(Rpg3Parser.CsDELETEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDIV}.
	 * @param ctx the parse tree
	 */
	void enterCsDIV(Rpg3Parser.CsDIVContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDIV}.
	 * @param ctx the parse tree
	 */
	void exitCsDIV(Rpg3Parser.CsDIVContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDO}.
	 * @param ctx the parse tree
	 */
	void enterCsDO(Rpg3Parser.CsDOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDO}.
	 * @param ctx the parse tree
	 */
	void exitCsDO(Rpg3Parser.CsDOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOUEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsDOUEQ(Rpg3Parser.CsDOUEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOUEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsDOUEQ(Rpg3Parser.CsDOUEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOUNE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOUNE(Rpg3Parser.CsDOUNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOUNE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOUNE(Rpg3Parser.CsDOUNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOULE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOULE(Rpg3Parser.CsDOULEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOULE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOULE(Rpg3Parser.CsDOULEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOULT}.
	 * @param ctx the parse tree
	 */
	void enterCsDOULT(Rpg3Parser.CsDOULTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOULT}.
	 * @param ctx the parse tree
	 */
	void exitCsDOULT(Rpg3Parser.CsDOULTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOUGE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOUGE(Rpg3Parser.CsDOUGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOUGE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOUGE(Rpg3Parser.CsDOUGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOUGT}.
	 * @param ctx the parse tree
	 */
	void enterCsDOUGT(Rpg3Parser.CsDOUGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOUGT}.
	 * @param ctx the parse tree
	 */
	void exitCsDOUGT(Rpg3Parser.CsDOUGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWEQ(Rpg3Parser.CsDOWEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWEQ(Rpg3Parser.CsDOWEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWNE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWNE(Rpg3Parser.CsDOWNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWNE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWNE(Rpg3Parser.CsDOWNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWLE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWLE(Rpg3Parser.CsDOWLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWLE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWLE(Rpg3Parser.CsDOWLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWLT}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWLT(Rpg3Parser.CsDOWLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWLT}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWLT(Rpg3Parser.CsDOWLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWGE}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWGE(Rpg3Parser.CsDOWGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWGE}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWGE(Rpg3Parser.CsDOWGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDOWGT}.
	 * @param ctx the parse tree
	 */
	void enterCsDOWGT(Rpg3Parser.CsDOWGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDOWGT}.
	 * @param ctx the parse tree
	 */
	void exitCsDOWGT(Rpg3Parser.CsDOWGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDSPLY}.
	 * @param ctx the parse tree
	 */
	void enterCsDSPLY(Rpg3Parser.CsDSPLYContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDSPLY}.
	 * @param ctx the parse tree
	 */
	void exitCsDSPLY(Rpg3Parser.CsDSPLYContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csDUMP}.
	 * @param ctx the parse tree
	 */
	void enterCsDUMP(Rpg3Parser.CsDUMPContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csDUMP}.
	 * @param ctx the parse tree
	 */
	void exitCsDUMP(Rpg3Parser.CsDUMPContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csELSE}.
	 * @param ctx the parse tree
	 */
	void enterCsELSE(Rpg3Parser.CsELSEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csELSE}.
	 * @param ctx the parse tree
	 */
	void exitCsELSE(Rpg3Parser.CsELSEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csEND}.
	 * @param ctx the parse tree
	 */
	void enterCsEND(Rpg3Parser.CsENDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csEND}.
	 * @param ctx the parse tree
	 */
	void exitCsEND(Rpg3Parser.CsENDContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csENDCS}.
	 * @param ctx the parse tree
	 */
	void enterCsENDCS(Rpg3Parser.CsENDCSContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csENDCS}.
	 * @param ctx the parse tree
	 */
	void exitCsENDCS(Rpg3Parser.CsENDCSContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csENDDO}.
	 * @param ctx the parse tree
	 */
	void enterCsENDDO(Rpg3Parser.CsENDDOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csENDDO}.
	 * @param ctx the parse tree
	 */
	void exitCsENDDO(Rpg3Parser.CsENDDOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csEXCEPT}.
	 * @param ctx the parse tree
	 */
	void enterCsEXCEPT(Rpg3Parser.CsEXCEPTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csEXCEPT}.
	 * @param ctx the parse tree
	 */
	void exitCsEXCEPT(Rpg3Parser.CsEXCEPTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csEXFMT}.
	 * @param ctx the parse tree
	 */
	void enterCsEXFMT(Rpg3Parser.CsEXFMTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csEXFMT}.
	 * @param ctx the parse tree
	 */
	void exitCsEXFMT(Rpg3Parser.CsEXFMTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csEXSR}.
	 * @param ctx the parse tree
	 */
	void enterCsEXSR(Rpg3Parser.CsEXSRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csEXSR}.
	 * @param ctx the parse tree
	 */
	void exitCsEXSR(Rpg3Parser.CsEXSRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csEXTRCT}.
	 * @param ctx the parse tree
	 */
	void enterCsEXTRCT(Rpg3Parser.CsEXTRCTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csEXTRCT}.
	 * @param ctx the parse tree
	 */
	void exitCsEXTRCT(Rpg3Parser.CsEXTRCTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csFEOD}.
	 * @param ctx the parse tree
	 */
	void enterCsFEOD(Rpg3Parser.CsFEODContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csFEOD}.
	 * @param ctx the parse tree
	 */
	void exitCsFEOD(Rpg3Parser.CsFEODContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csFORCE}.
	 * @param ctx the parse tree
	 */
	void enterCsFORCE(Rpg3Parser.CsFORCEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csFORCE}.
	 * @param ctx the parse tree
	 */
	void exitCsFORCE(Rpg3Parser.CsFORCEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csGOTO}.
	 * @param ctx the parse tree
	 */
	void enterCsGOTO(Rpg3Parser.CsGOTOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csGOTO}.
	 * @param ctx the parse tree
	 */
	void exitCsGOTO(Rpg3Parser.CsGOTOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsIFEQ(Rpg3Parser.CsIFEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsIFEQ(Rpg3Parser.CsIFEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFNE}.
	 * @param ctx the parse tree
	 */
	void enterCsIFNE(Rpg3Parser.CsIFNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFNE}.
	 * @param ctx the parse tree
	 */
	void exitCsIFNE(Rpg3Parser.CsIFNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFLE}.
	 * @param ctx the parse tree
	 */
	void enterCsIFLE(Rpg3Parser.CsIFLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFLE}.
	 * @param ctx the parse tree
	 */
	void exitCsIFLE(Rpg3Parser.CsIFLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFLT}.
	 * @param ctx the parse tree
	 */
	void enterCsIFLT(Rpg3Parser.CsIFLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFLT}.
	 * @param ctx the parse tree
	 */
	void exitCsIFLT(Rpg3Parser.CsIFLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFGE}.
	 * @param ctx the parse tree
	 */
	void enterCsIFGE(Rpg3Parser.CsIFGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFGE}.
	 * @param ctx the parse tree
	 */
	void exitCsIFGE(Rpg3Parser.CsIFGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIFGT}.
	 * @param ctx the parse tree
	 */
	void enterCsIFGT(Rpg3Parser.CsIFGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIFGT}.
	 * @param ctx the parse tree
	 */
	void exitCsIFGT(Rpg3Parser.CsIFGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csIN}.
	 * @param ctx the parse tree
	 */
	void enterCsIN(Rpg3Parser.CsINContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csIN}.
	 * @param ctx the parse tree
	 */
	void exitCsIN(Rpg3Parser.CsINContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csITER}.
	 * @param ctx the parse tree
	 */
	void enterCsITER(Rpg3Parser.CsITERContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csITER}.
	 * @param ctx the parse tree
	 */
	void exitCsITER(Rpg3Parser.CsITERContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csKLIST}.
	 * @param ctx the parse tree
	 */
	void enterCsKLIST(Rpg3Parser.CsKLISTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csKLIST}.
	 * @param ctx the parse tree
	 */
	void exitCsKLIST(Rpg3Parser.CsKLISTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csKFLD}.
	 * @param ctx the parse tree
	 */
	void enterCsKFLD(Rpg3Parser.CsKFLDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csKFLD}.
	 * @param ctx the parse tree
	 */
	void exitCsKFLD(Rpg3Parser.CsKFLDContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csLEAVE}.
	 * @param ctx the parse tree
	 */
	void enterCsLEAVE(Rpg3Parser.CsLEAVEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csLEAVE}.
	 * @param ctx the parse tree
	 */
	void exitCsLEAVE(Rpg3Parser.CsLEAVEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csLOOKUP}.
	 * @param ctx the parse tree
	 */
	void enterCsLOOKUP(Rpg3Parser.CsLOOKUPContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csLOOKUP}.
	 * @param ctx the parse tree
	 */
	void exitCsLOOKUP(Rpg3Parser.CsLOOKUPContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMHHZO}.
	 * @param ctx the parse tree
	 */
	void enterCsMHHZO(Rpg3Parser.CsMHHZOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMHHZO}.
	 * @param ctx the parse tree
	 */
	void exitCsMHHZO(Rpg3Parser.CsMHHZOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMHLZO}.
	 * @param ctx the parse tree
	 */
	void enterCsMHLZO(Rpg3Parser.CsMHLZOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMHLZO}.
	 * @param ctx the parse tree
	 */
	void exitCsMHLZO(Rpg3Parser.CsMHLZOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMLHZO}.
	 * @param ctx the parse tree
	 */
	void enterCsMLHZO(Rpg3Parser.CsMLHZOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMLHZO}.
	 * @param ctx the parse tree
	 */
	void exitCsMLHZO(Rpg3Parser.CsMLHZOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMLLZO}.
	 * @param ctx the parse tree
	 */
	void enterCsMLLZO(Rpg3Parser.CsMLLZOContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMLLZO}.
	 * @param ctx the parse tree
	 */
	void exitCsMLLZO(Rpg3Parser.CsMLLZOContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMOVE}.
	 * @param ctx the parse tree
	 */
	void enterCsMOVE(Rpg3Parser.CsMOVEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMOVE}.
	 * @param ctx the parse tree
	 */
	void exitCsMOVE(Rpg3Parser.CsMOVEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMOVEA}.
	 * @param ctx the parse tree
	 */
	void enterCsMOVEA(Rpg3Parser.CsMOVEAContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMOVEA}.
	 * @param ctx the parse tree
	 */
	void exitCsMOVEA(Rpg3Parser.CsMOVEAContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMOVEL}.
	 * @param ctx the parse tree
	 */
	void enterCsMOVEL(Rpg3Parser.CsMOVELContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMOVEL}.
	 * @param ctx the parse tree
	 */
	void exitCsMOVEL(Rpg3Parser.CsMOVELContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMULT}.
	 * @param ctx the parse tree
	 */
	void enterCsMULT(Rpg3Parser.CsMULTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMULT}.
	 * @param ctx the parse tree
	 */
	void exitCsMULT(Rpg3Parser.CsMULTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csMVR}.
	 * @param ctx the parse tree
	 */
	void enterCsMVR(Rpg3Parser.CsMVRContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csMVR}.
	 * @param ctx the parse tree
	 */
	void exitCsMVR(Rpg3Parser.CsMVRContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csNEXT}.
	 * @param ctx the parse tree
	 */
	void enterCsNEXT(Rpg3Parser.CsNEXTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csNEXT}.
	 * @param ctx the parse tree
	 */
	void exitCsNEXT(Rpg3Parser.CsNEXTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csOCCUR}.
	 * @param ctx the parse tree
	 */
	void enterCsOCCUR(Rpg3Parser.CsOCCURContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csOCCUR}.
	 * @param ctx the parse tree
	 */
	void exitCsOCCUR(Rpg3Parser.CsOCCURContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csOPEN}.
	 * @param ctx the parse tree
	 */
	void enterCsOPEN(Rpg3Parser.CsOPENContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csOPEN}.
	 * @param ctx the parse tree
	 */
	void exitCsOPEN(Rpg3Parser.CsOPENContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csOREQ}.
	 * @param ctx the parse tree
	 */
	void enterCsOREQ(Rpg3Parser.CsOREQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csOREQ}.
	 * @param ctx the parse tree
	 */
	void exitCsOREQ(Rpg3Parser.CsOREQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORNE}.
	 * @param ctx the parse tree
	 */
	void enterCsORNE(Rpg3Parser.CsORNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORNE}.
	 * @param ctx the parse tree
	 */
	void exitCsORNE(Rpg3Parser.CsORNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORLE}.
	 * @param ctx the parse tree
	 */
	void enterCsORLE(Rpg3Parser.CsORLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORLE}.
	 * @param ctx the parse tree
	 */
	void exitCsORLE(Rpg3Parser.CsORLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORLT}.
	 * @param ctx the parse tree
	 */
	void enterCsORLT(Rpg3Parser.CsORLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORLT}.
	 * @param ctx the parse tree
	 */
	void exitCsORLT(Rpg3Parser.CsORLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORGE}.
	 * @param ctx the parse tree
	 */
	void enterCsORGE(Rpg3Parser.CsORGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORGE}.
	 * @param ctx the parse tree
	 */
	void exitCsORGE(Rpg3Parser.CsORGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csORGT}.
	 * @param ctx the parse tree
	 */
	void enterCsORGT(Rpg3Parser.CsORGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csORGT}.
	 * @param ctx the parse tree
	 */
	void exitCsORGT(Rpg3Parser.CsORGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csOUT}.
	 * @param ctx the parse tree
	 */
	void enterCsOUT(Rpg3Parser.CsOUTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csOUT}.
	 * @param ctx the parse tree
	 */
	void exitCsOUT(Rpg3Parser.CsOUTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csPARM}.
	 * @param ctx the parse tree
	 */
	void enterCsPARM(Rpg3Parser.CsPARMContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csPARM}.
	 * @param ctx the parse tree
	 */
	void exitCsPARM(Rpg3Parser.CsPARMContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csPLIST}.
	 * @param ctx the parse tree
	 */
	void enterCsPLIST(Rpg3Parser.CsPLISTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csPLIST}.
	 * @param ctx the parse tree
	 */
	void exitCsPLIST(Rpg3Parser.CsPLISTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csPOST}.
	 * @param ctx the parse tree
	 */
	void enterCsPOST(Rpg3Parser.CsPOSTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csPOST}.
	 * @param ctx the parse tree
	 */
	void exitCsPOST(Rpg3Parser.CsPOSTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREAD}.
	 * @param ctx the parse tree
	 */
	void enterCsREAD(Rpg3Parser.CsREADContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREAD}.
	 * @param ctx the parse tree
	 */
	void exitCsREAD(Rpg3Parser.CsREADContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREADC}.
	 * @param ctx the parse tree
	 */
	void enterCsREADC(Rpg3Parser.CsREADCContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREADC}.
	 * @param ctx the parse tree
	 */
	void exitCsREADC(Rpg3Parser.CsREADCContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREADE}.
	 * @param ctx the parse tree
	 */
	void enterCsREADE(Rpg3Parser.CsREADEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREADE}.
	 * @param ctx the parse tree
	 */
	void exitCsREADE(Rpg3Parser.CsREADEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREADP}.
	 * @param ctx the parse tree
	 */
	void enterCsREADP(Rpg3Parser.CsREADPContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREADP}.
	 * @param ctx the parse tree
	 */
	void exitCsREADP(Rpg3Parser.CsREADPContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREADPE}.
	 * @param ctx the parse tree
	 */
	void enterCsREADPE(Rpg3Parser.CsREADPEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREADPE}.
	 * @param ctx the parse tree
	 */
	void exitCsREADPE(Rpg3Parser.CsREADPEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csREL}.
	 * @param ctx the parse tree
	 */
	void enterCsREL(Rpg3Parser.CsRELContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csREL}.
	 * @param ctx the parse tree
	 */
	void exitCsREL(Rpg3Parser.CsRELContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csRESET}.
	 * @param ctx the parse tree
	 */
	void enterCsRESET(Rpg3Parser.CsRESETContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csRESET}.
	 * @param ctx the parse tree
	 */
	void exitCsRESET(Rpg3Parser.CsRESETContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csROLBK}.
	 * @param ctx the parse tree
	 */
	void enterCsROLBK(Rpg3Parser.CsROLBKContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csROLBK}.
	 * @param ctx the parse tree
	 */
	void exitCsROLBK(Rpg3Parser.CsROLBKContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSCAN}.
	 * @param ctx the parse tree
	 */
	void enterCsSCAN(Rpg3Parser.CsSCANContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSCAN}.
	 * @param ctx the parse tree
	 */
	void exitCsSCAN(Rpg3Parser.CsSCANContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSETGT}.
	 * @param ctx the parse tree
	 */
	void enterCsSETGT(Rpg3Parser.CsSETGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSETGT}.
	 * @param ctx the parse tree
	 */
	void exitCsSETGT(Rpg3Parser.CsSETGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSETLL}.
	 * @param ctx the parse tree
	 */
	void enterCsSETLL(Rpg3Parser.CsSETLLContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSETLL}.
	 * @param ctx the parse tree
	 */
	void exitCsSETLL(Rpg3Parser.CsSETLLContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSETOFF}.
	 * @param ctx the parse tree
	 */
	void enterCsSETOFF(Rpg3Parser.CsSETOFFContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSETOFF}.
	 * @param ctx the parse tree
	 */
	void exitCsSETOFF(Rpg3Parser.CsSETOFFContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSETON}.
	 * @param ctx the parse tree
	 */
	void enterCsSETON(Rpg3Parser.CsSETONContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSETON}.
	 * @param ctx the parse tree
	 */
	void exitCsSETON(Rpg3Parser.CsSETONContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSHTDN}.
	 * @param ctx the parse tree
	 */
	void enterCsSHTDN(Rpg3Parser.CsSHTDNContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSHTDN}.
	 * @param ctx the parse tree
	 */
	void exitCsSHTDN(Rpg3Parser.CsSHTDNContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSQRT}.
	 * @param ctx the parse tree
	 */
	void enterCsSQRT(Rpg3Parser.CsSQRTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSQRT}.
	 * @param ctx the parse tree
	 */
	void exitCsSQRT(Rpg3Parser.CsSQRTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSUB}.
	 * @param ctx the parse tree
	 */
	void enterCsSUB(Rpg3Parser.CsSUBContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSUB}.
	 * @param ctx the parse tree
	 */
	void exitCsSUB(Rpg3Parser.CsSUBContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSUBDUR}.
	 * @param ctx the parse tree
	 */
	void enterCsSUBDUR(Rpg3Parser.CsSUBDURContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSUBDUR}.
	 * @param ctx the parse tree
	 */
	void exitCsSUBDUR(Rpg3Parser.CsSUBDURContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csSUBST}.
	 * @param ctx the parse tree
	 */
	void enterCsSUBST(Rpg3Parser.CsSUBSTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csSUBST}.
	 * @param ctx the parse tree
	 */
	void exitCsSUBST(Rpg3Parser.CsSUBSTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTAG}.
	 * @param ctx the parse tree
	 */
	void enterCsTAG(Rpg3Parser.CsTAGContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTAG}.
	 * @param ctx the parse tree
	 */
	void exitCsTAG(Rpg3Parser.CsTAGContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTEST}.
	 * @param ctx the parse tree
	 */
	void enterCsTEST(Rpg3Parser.CsTESTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTEST}.
	 * @param ctx the parse tree
	 */
	void exitCsTEST(Rpg3Parser.CsTESTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTESTB}.
	 * @param ctx the parse tree
	 */
	void enterCsTESTB(Rpg3Parser.CsTESTBContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTESTB}.
	 * @param ctx the parse tree
	 */
	void exitCsTESTB(Rpg3Parser.CsTESTBContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTESTN}.
	 * @param ctx the parse tree
	 */
	void enterCsTESTN(Rpg3Parser.CsTESTNContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTESTN}.
	 * @param ctx the parse tree
	 */
	void exitCsTESTN(Rpg3Parser.CsTESTNContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTESTZ}.
	 * @param ctx the parse tree
	 */
	void enterCsTESTZ(Rpg3Parser.CsTESTZContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTESTZ}.
	 * @param ctx the parse tree
	 */
	void exitCsTESTZ(Rpg3Parser.CsTESTZContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csTIME}.
	 * @param ctx the parse tree
	 */
	void enterCsTIME(Rpg3Parser.CsTIMEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csTIME}.
	 * @param ctx the parse tree
	 */
	void exitCsTIME(Rpg3Parser.CsTIMEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csUNLOCK}.
	 * @param ctx the parse tree
	 */
	void enterCsUNLOCK(Rpg3Parser.CsUNLOCKContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csUNLOCK}.
	 * @param ctx the parse tree
	 */
	void exitCsUNLOCK(Rpg3Parser.CsUNLOCKContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csUPDATE}.
	 * @param ctx the parse tree
	 */
	void enterCsUPDATE(Rpg3Parser.CsUPDATEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csUPDATE}.
	 * @param ctx the parse tree
	 */
	void exitCsUPDATE(Rpg3Parser.CsUPDATEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENEQ}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENEQ(Rpg3Parser.CsWHENEQContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENEQ}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENEQ(Rpg3Parser.CsWHENEQContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENNE}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENNE(Rpg3Parser.CsWHENNEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENNE}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENNE(Rpg3Parser.CsWHENNEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENLE}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENLE(Rpg3Parser.CsWHENLEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENLE}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENLE(Rpg3Parser.CsWHENLEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENLT}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENLT(Rpg3Parser.CsWHENLTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENLT}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENLT(Rpg3Parser.CsWHENLTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENGE}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENGE(Rpg3Parser.CsWHENGEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENGE}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENGE(Rpg3Parser.CsWHENGEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWHENGT}.
	 * @param ctx the parse tree
	 */
	void enterCsWHENGT(Rpg3Parser.CsWHENGTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWHENGT}.
	 * @param ctx the parse tree
	 */
	void exitCsWHENGT(Rpg3Parser.CsWHENGTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csWRITE}.
	 * @param ctx the parse tree
	 */
	void enterCsWRITE(Rpg3Parser.CsWRITEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csWRITE}.
	 * @param ctx the parse tree
	 */
	void exitCsWRITE(Rpg3Parser.CsWRITEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csXFOOT}.
	 * @param ctx the parse tree
	 */
	void enterCsXFOOT(Rpg3Parser.CsXFOOTContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csXFOOT}.
	 * @param ctx the parse tree
	 */
	void exitCsXFOOT(Rpg3Parser.CsXFOOTContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csXLATE}.
	 * @param ctx the parse tree
	 */
	void enterCsXLATE(Rpg3Parser.CsXLATEContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csXLATE}.
	 * @param ctx the parse tree
	 */
	void exitCsXLATE(Rpg3Parser.CsXLATEContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csZ_ADD}.
	 * @param ctx the parse tree
	 */
	void enterCsZ_ADD(Rpg3Parser.CsZ_ADDContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csZ_ADD}.
	 * @param ctx the parse tree
	 */
	void exitCsZ_ADD(Rpg3Parser.CsZ_ADDContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#csZ_SUB}.
	 * @param ctx the parse tree
	 */
	void enterCsZ_SUB(Rpg3Parser.CsZ_SUBContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#csZ_SUB}.
	 * @param ctx the parse tree
	 */
	void exitCsZ_SUB(Rpg3Parser.CsZ_SUBContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cs_operationExtender}.
	 * @param ctx the parse tree
	 */
	void enterCs_operationExtender(Rpg3Parser.Cs_operationExtenderContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cs_operationExtender}.
	 * @param ctx the parse tree
	 */
	void exitCs_operationExtender(Rpg3Parser.Cs_operationExtenderContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(Rpg3Parser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(Rpg3Parser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#factorContent}.
	 * @param ctx the parse tree
	 */
	void enterFactorContent(Rpg3Parser.FactorContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#factorContent}.
	 * @param ctx the parse tree
	 */
	void exitFactorContent(Rpg3Parser.FactorContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#resultType}.
	 * @param ctx the parse tree
	 */
	void enterResultType(Rpg3Parser.ResultTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#resultType}.
	 * @param ctx the parse tree
	 */
	void exitResultType(Rpg3Parser.ResultTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#cs_fixed_comments}.
	 * @param ctx the parse tree
	 */
	void enterCs_fixed_comments(Rpg3Parser.Cs_fixed_commentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#cs_fixed_comments}.
	 * @param ctx the parse tree
	 */
	void exitCs_fixed_comments(Rpg3Parser.Cs_fixed_commentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#ispec_fixed}.
	 * @param ctx the parse tree
	 */
	void enterIspec_fixed(Rpg3Parser.Ispec_fixedContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#ispec_fixed}.
	 * @param ctx the parse tree
	 */
	void exitIspec_fixed(Rpg3Parser.Ispec_fixedContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#fieldRecordRelation}.
	 * @param ctx the parse tree
	 */
	void enterFieldRecordRelation(Rpg3Parser.FieldRecordRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#fieldRecordRelation}.
	 * @param ctx the parse tree
	 */
	void exitFieldRecordRelation(Rpg3Parser.FieldRecordRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#fieldIndicator}.
	 * @param ctx the parse tree
	 */
	void enterFieldIndicator(Rpg3Parser.FieldIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#fieldIndicator}.
	 * @param ctx the parse tree
	 */
	void exitFieldIndicator(Rpg3Parser.FieldIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#is_external_rec}.
	 * @param ctx the parse tree
	 */
	void enterIs_external_rec(Rpg3Parser.Is_external_recContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#is_external_rec}.
	 * @param ctx the parse tree
	 */
	void exitIs_external_rec(Rpg3Parser.Is_external_recContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#is_rec}.
	 * @param ctx the parse tree
	 */
	void enterIs_rec(Rpg3Parser.Is_recContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#is_rec}.
	 * @param ctx the parse tree
	 */
	void exitIs_rec(Rpg3Parser.Is_recContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#recordIdIndicator}.
	 * @param ctx the parse tree
	 */
	void enterRecordIdIndicator(Rpg3Parser.RecordIdIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#recordIdIndicator}.
	 * @param ctx the parse tree
	 */
	void exitRecordIdIndicator(Rpg3Parser.RecordIdIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#is_external_field}.
	 * @param ctx the parse tree
	 */
	void enterIs_external_field(Rpg3Parser.Is_external_fieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#is_external_field}.
	 * @param ctx the parse tree
	 */
	void exitIs_external_field(Rpg3Parser.Is_external_fieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#controlLevelIndicator}.
	 * @param ctx the parse tree
	 */
	void enterControlLevelIndicator(Rpg3Parser.ControlLevelIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#controlLevelIndicator}.
	 * @param ctx the parse tree
	 */
	void exitControlLevelIndicator(Rpg3Parser.ControlLevelIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#matchingFieldsIndicator}.
	 * @param ctx the parse tree
	 */
	void enterMatchingFieldsIndicator(Rpg3Parser.MatchingFieldsIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#matchingFieldsIndicator}.
	 * @param ctx the parse tree
	 */
	void exitMatchingFieldsIndicator(Rpg3Parser.MatchingFieldsIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#hspec_fixed}.
	 * @param ctx the parse tree
	 */
	void enterHspec_fixed(Rpg3Parser.Hspec_fixedContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#hspec_fixed}.
	 * @param ctx the parse tree
	 */
	void exitHspec_fixed(Rpg3Parser.Hspec_fixedContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#hs_expression}.
	 * @param ctx the parse tree
	 */
	void enterHs_expression(Rpg3Parser.Hs_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#hs_expression}.
	 * @param ctx the parse tree
	 */
	void exitHs_expression(Rpg3Parser.Hs_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#hs_parm}.
	 * @param ctx the parse tree
	 */
	void enterHs_parm(Rpg3Parser.Hs_parmContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#hs_parm}.
	 * @param ctx the parse tree
	 */
	void exitHs_parm(Rpg3Parser.Hs_parmContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#hs_string}.
	 * @param ctx the parse tree
	 */
	void enterHs_string(Rpg3Parser.Hs_stringContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#hs_string}.
	 * @param ctx the parse tree
	 */
	void exitHs_string(Rpg3Parser.Hs_stringContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#blank_line}.
	 * @param ctx the parse tree
	 */
	void enterBlank_line(Rpg3Parser.Blank_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#blank_line}.
	 * @param ctx the parse tree
	 */
	void exitBlank_line(Rpg3Parser.Blank_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#space_directive}.
	 * @param ctx the parse tree
	 */
	void enterSpace_directive(Rpg3Parser.Space_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#space_directive}.
	 * @param ctx the parse tree
	 */
	void exitSpace_directive(Rpg3Parser.Space_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_copy}.
	 * @param ctx the parse tree
	 */
	void enterDir_copy(Rpg3Parser.Dir_copyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_copy}.
	 * @param ctx the parse tree
	 */
	void exitDir_copy(Rpg3Parser.Dir_copyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_include}.
	 * @param ctx the parse tree
	 */
	void enterDir_include(Rpg3Parser.Dir_includeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_include}.
	 * @param ctx the parse tree
	 */
	void exitDir_include(Rpg3Parser.Dir_includeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_if}.
	 * @param ctx the parse tree
	 */
	void enterDir_if(Rpg3Parser.Dir_ifContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_if}.
	 * @param ctx the parse tree
	 */
	void exitDir_if(Rpg3Parser.Dir_ifContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_elseif}.
	 * @param ctx the parse tree
	 */
	void enterDir_elseif(Rpg3Parser.Dir_elseifContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_elseif}.
	 * @param ctx the parse tree
	 */
	void exitDir_elseif(Rpg3Parser.Dir_elseifContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_else}.
	 * @param ctx the parse tree
	 */
	void enterDir_else(Rpg3Parser.Dir_elseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_else}.
	 * @param ctx the parse tree
	 */
	void exitDir_else(Rpg3Parser.Dir_elseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_endif}.
	 * @param ctx the parse tree
	 */
	void enterDir_endif(Rpg3Parser.Dir_endifContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_endif}.
	 * @param ctx the parse tree
	 */
	void exitDir_endif(Rpg3Parser.Dir_endifContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_define}.
	 * @param ctx the parse tree
	 */
	void enterDir_define(Rpg3Parser.Dir_defineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_define}.
	 * @param ctx the parse tree
	 */
	void exitDir_define(Rpg3Parser.Dir_defineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_undefine}.
	 * @param ctx the parse tree
	 */
	void enterDir_undefine(Rpg3Parser.Dir_undefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_undefine}.
	 * @param ctx the parse tree
	 */
	void exitDir_undefine(Rpg3Parser.Dir_undefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#dir_eof}.
	 * @param ctx the parse tree
	 */
	void enterDir_eof(Rpg3Parser.Dir_eofContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#dir_eof}.
	 * @param ctx the parse tree
	 */
	void exitDir_eof(Rpg3Parser.Dir_eofContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#copyText}.
	 * @param ctx the parse tree
	 */
	void enterCopyText(Rpg3Parser.CopyTextContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#copyText}.
	 * @param ctx the parse tree
	 */
	void exitCopyText(Rpg3Parser.CopyTextContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#trailing_ws}.
	 * @param ctx the parse tree
	 */
	void enterTrailing_ws(Rpg3Parser.Trailing_wsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#trailing_ws}.
	 * @param ctx the parse tree
	 */
	void exitTrailing_ws(Rpg3Parser.Trailing_wsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#title_directive}.
	 * @param ctx the parse tree
	 */
	void enterTitle_directive(Rpg3Parser.Title_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#title_directive}.
	 * @param ctx the parse tree
	 */
	void exitTitle_directive(Rpg3Parser.Title_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#title_text}.
	 * @param ctx the parse tree
	 */
	void enterTitle_text(Rpg3Parser.Title_textContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#title_text}.
	 * @param ctx the parse tree
	 */
	void exitTitle_text(Rpg3Parser.Title_textContext ctx);
	/**
	 * Enter a parse tree produced by {@link Rpg3Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(Rpg3Parser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link Rpg3Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(Rpg3Parser.LiteralContext ctx);
}