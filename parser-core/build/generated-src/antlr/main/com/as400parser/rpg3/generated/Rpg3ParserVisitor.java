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
	 * Visit a parse tree produced by {@link Rpg3Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(Rpg3Parser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(Rpg3Parser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#ifstatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfstatement(Rpg3Parser.IfstatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#elsestmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElsestmt(Rpg3Parser.ElsestmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#beginif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeginif(Rpg3Parser.BeginifContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#begsr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBegsr(Rpg3Parser.BegsrContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endsr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndsr(Rpg3Parser.EndsrContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndif(Rpg3Parser.EndifContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csENDIF_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsENDIF_rule(Rpg3Parser.CsENDIF_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#enddo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnddo(Rpg3Parser.EnddoContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspec_fixed_standard(Rpg3Parser.Cspec_fixed_standardContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csRETURN_FIXED}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsRETURN_FIXED(Rpg3Parser.CsRETURN_FIXEDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSORTA_FIXED}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSORTA_FIXED(Rpg3Parser.CsSORTA_FIXEDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#extensionSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtensionSpec(Rpg3Parser.ExtensionSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#lineCounterSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineCounterSpec(Rpg3Parser.LineCounterSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#compileTimeData}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompileTimeData(Rpg3Parser.CompileTimeDataContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirective(Rpg3Parser.DirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#symbolicConstants}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbolicConstants(Rpg3Parser.SymbolicConstantsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endSource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndSource(Rpg3Parser.EndSourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endSourceHead}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndSourceHead(Rpg3Parser.EndSourceHeadContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#endSourceLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndSourceLine(Rpg3Parser.EndSourceLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#star_comments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStar_comments(Rpg3Parser.Star_commentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#comments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComments(Rpg3Parser.CommentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#casestatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCasestatement(Rpg3Parser.CasestatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#casestatementend}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCasestatementend(Rpg3Parser.CasestatementendContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFxx(Rpg3Parser.CsIFxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOUxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOUxx(Rpg3Parser.CsDOUxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWxx(Rpg3Parser.CsDOWxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#complexCondxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexCondxx(Rpg3Parser.ComplexCondxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDxx(Rpg3Parser.CsANDxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORxx(Rpg3Parser.CsORxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#ospec_fixed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOspec_fixed(Rpg3Parser.Ospec_fixedContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOs_fixed_pgmdesc1(Rpg3Parser.Os_fixed_pgmdesc1Context ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputConditioningOnOffIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputConditioningOnOffIndicator(Rpg3Parser.OutputConditioningOnOffIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#outputConditioningIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputConditioningIndicator(Rpg3Parser.OutputConditioningIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc_compound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOs_fixed_pgmdesc_compound(Rpg3Parser.Os_fixed_pgmdesc_compoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmdesc2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOs_fixed_pgmdesc2(Rpg3Parser.Os_fixed_pgmdesc2Context ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#os_fixed_pgmfield}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOs_fixed_pgmfield(Rpg3Parser.Os_fixed_pgmfieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#fspec_fixed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFspec_fixed(Rpg3Parser.Fspec_fixedContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspec_fixed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspec_fixed(Rpg3Parser.Cspec_fixedContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspec_continuedIndicators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspec_continuedIndicators(Rpg3Parser.Cspec_continuedIndicatorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspec_blank}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspec_blank(Rpg3Parser.Cspec_blankContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#blank_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlank_spec(Rpg3Parser.Blank_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#subroutine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubroutine(Rpg3Parser.SubroutineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csBEGSR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsBEGSR(Rpg3Parser.CsBEGSRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csENDSR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsENDSR(Rpg3Parser.CsENDSRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#onOffIndicatorsFlag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOnOffIndicatorsFlag(Rpg3Parser.OnOffIndicatorsFlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cs_controlLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCs_controlLevel(Rpg3Parser.Cs_controlLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cs_indicators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCs_indicators(Rpg3Parser.Cs_indicatorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#resultIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResultIndicator(Rpg3Parser.ResultIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cspec_fixed_standard_parts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCspec_fixed_standard_parts(Rpg3Parser.Cspec_fixed_standard_partsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csACQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsACQ(Rpg3Parser.CsACQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csADD}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsADD(Rpg3Parser.CsADDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csADDDUR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsADDDUR(Rpg3Parser.CsADDDURContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDEQ(Rpg3Parser.CsANDEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDNE(Rpg3Parser.CsANDNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDLE(Rpg3Parser.CsANDLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDLT(Rpg3Parser.CsANDLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDGE(Rpg3Parser.CsANDGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csANDGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsANDGT(Rpg3Parser.CsANDGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csBITOFF}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsBITOFF(Rpg3Parser.CsBITOFFContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csBITON}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsBITON(Rpg3Parser.CsBITONContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABxx}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABxx(Rpg3Parser.CsCABxxContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABEQ(Rpg3Parser.CsCABEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABNE(Rpg3Parser.CsCABNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABLE(Rpg3Parser.CsCABLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABLT(Rpg3Parser.CsCABLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABGE(Rpg3Parser.CsCABGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCABGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCABGT(Rpg3Parser.CsCABGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCALL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCALL(Rpg3Parser.CsCALLContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASEQ(Rpg3Parser.CsCASEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASNE(Rpg3Parser.CsCASNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASLE(Rpg3Parser.CsCASLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASLT(Rpg3Parser.CsCASLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASGE(Rpg3Parser.CsCASGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCASGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCASGT(Rpg3Parser.CsCASGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCAS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCAS(Rpg3Parser.CsCASContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCAT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCAT(Rpg3Parser.CsCATContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCHAIN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCHAIN(Rpg3Parser.CsCHAINContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCHECK}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCHECK(Rpg3Parser.CsCHECKContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCHECKR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCHECKR(Rpg3Parser.CsCHECKRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCLEAR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCLEAR(Rpg3Parser.CsCLEARContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCLOSE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCLOSE(Rpg3Parser.CsCLOSEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCOMMIT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCOMMIT(Rpg3Parser.CsCOMMITContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csCOMP}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsCOMP(Rpg3Parser.CsCOMPContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDEFINE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDEFINE(Rpg3Parser.CsDEFINEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDELETE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDELETE(Rpg3Parser.CsDELETEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDIV}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDIV(Rpg3Parser.CsDIVContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDO(Rpg3Parser.CsDOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOUEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOUEQ(Rpg3Parser.CsDOUEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOUNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOUNE(Rpg3Parser.CsDOUNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOULE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOULE(Rpg3Parser.CsDOULEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOULT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOULT(Rpg3Parser.CsDOULTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOUGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOUGE(Rpg3Parser.CsDOUGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOUGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOUGT(Rpg3Parser.CsDOUGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWEQ(Rpg3Parser.CsDOWEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWNE(Rpg3Parser.CsDOWNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWLE(Rpg3Parser.CsDOWLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWLT(Rpg3Parser.CsDOWLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWGE(Rpg3Parser.CsDOWGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDOWGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDOWGT(Rpg3Parser.CsDOWGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDSPLY}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDSPLY(Rpg3Parser.CsDSPLYContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csDUMP}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsDUMP(Rpg3Parser.CsDUMPContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csELSE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsELSE(Rpg3Parser.CsELSEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csEND}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsEND(Rpg3Parser.CsENDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csENDCS}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsENDCS(Rpg3Parser.CsENDCSContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csENDDO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsENDDO(Rpg3Parser.CsENDDOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csEXCEPT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsEXCEPT(Rpg3Parser.CsEXCEPTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csEXFMT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsEXFMT(Rpg3Parser.CsEXFMTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csEXSR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsEXSR(Rpg3Parser.CsEXSRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csEXTRCT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsEXTRCT(Rpg3Parser.CsEXTRCTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csFEOD}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsFEOD(Rpg3Parser.CsFEODContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csFORCE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsFORCE(Rpg3Parser.CsFORCEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csGOTO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsGOTO(Rpg3Parser.CsGOTOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFEQ(Rpg3Parser.CsIFEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFNE(Rpg3Parser.CsIFNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFLE(Rpg3Parser.CsIFLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFLT(Rpg3Parser.CsIFLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFGE(Rpg3Parser.CsIFGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIFGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIFGT(Rpg3Parser.CsIFGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csIN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsIN(Rpg3Parser.CsINContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csITER}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsITER(Rpg3Parser.CsITERContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csKLIST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsKLIST(Rpg3Parser.CsKLISTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csKFLD}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsKFLD(Rpg3Parser.CsKFLDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csLEAVE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsLEAVE(Rpg3Parser.CsLEAVEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csLOOKUP}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsLOOKUP(Rpg3Parser.CsLOOKUPContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMHHZO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMHHZO(Rpg3Parser.CsMHHZOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMHLZO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMHLZO(Rpg3Parser.CsMHLZOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMLHZO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMLHZO(Rpg3Parser.CsMLHZOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMLLZO}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMLLZO(Rpg3Parser.CsMLLZOContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMOVE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMOVE(Rpg3Parser.CsMOVEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMOVEA}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMOVEA(Rpg3Parser.CsMOVEAContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMOVEL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMOVEL(Rpg3Parser.CsMOVELContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMULT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMULT(Rpg3Parser.CsMULTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csMVR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsMVR(Rpg3Parser.CsMVRContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csNEXT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsNEXT(Rpg3Parser.CsNEXTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csOCCUR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsOCCUR(Rpg3Parser.CsOCCURContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csOPEN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsOPEN(Rpg3Parser.CsOPENContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csOREQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsOREQ(Rpg3Parser.CsOREQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORNE(Rpg3Parser.CsORNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORLE(Rpg3Parser.CsORLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORLT(Rpg3Parser.CsORLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORGE(Rpg3Parser.CsORGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csORGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsORGT(Rpg3Parser.CsORGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csOUT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsOUT(Rpg3Parser.CsOUTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csPARM}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsPARM(Rpg3Parser.CsPARMContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csPLIST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsPLIST(Rpg3Parser.CsPLISTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csPOST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsPOST(Rpg3Parser.CsPOSTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREAD}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREAD(Rpg3Parser.CsREADContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREADC}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREADC(Rpg3Parser.CsREADCContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREADE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREADE(Rpg3Parser.CsREADEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREADP}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREADP(Rpg3Parser.CsREADPContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREADPE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREADPE(Rpg3Parser.CsREADPEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csREL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsREL(Rpg3Parser.CsRELContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csRESET}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsRESET(Rpg3Parser.CsRESETContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csROLBK}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsROLBK(Rpg3Parser.CsROLBKContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSCAN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSCAN(Rpg3Parser.CsSCANContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSETGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSETGT(Rpg3Parser.CsSETGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSETLL}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSETLL(Rpg3Parser.CsSETLLContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSETOFF}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSETOFF(Rpg3Parser.CsSETOFFContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSETON}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSETON(Rpg3Parser.CsSETONContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSHTDN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSHTDN(Rpg3Parser.CsSHTDNContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSQRT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSQRT(Rpg3Parser.CsSQRTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSUB}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSUB(Rpg3Parser.CsSUBContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSUBDUR}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSUBDUR(Rpg3Parser.CsSUBDURContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csSUBST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsSUBST(Rpg3Parser.CsSUBSTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTAG}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTAG(Rpg3Parser.CsTAGContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTEST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTEST(Rpg3Parser.CsTESTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTESTB}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTESTB(Rpg3Parser.CsTESTBContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTESTN}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTESTN(Rpg3Parser.CsTESTNContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTESTZ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTESTZ(Rpg3Parser.CsTESTZContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csTIME}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsTIME(Rpg3Parser.CsTIMEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csUNLOCK}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsUNLOCK(Rpg3Parser.CsUNLOCKContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csUPDATE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsUPDATE(Rpg3Parser.CsUPDATEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENEQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENEQ(Rpg3Parser.CsWHENEQContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENNE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENNE(Rpg3Parser.CsWHENNEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENLE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENLE(Rpg3Parser.CsWHENLEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENLT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENLT(Rpg3Parser.CsWHENLTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENGE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENGE(Rpg3Parser.CsWHENGEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWHENGT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWHENGT(Rpg3Parser.CsWHENGTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csWRITE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsWRITE(Rpg3Parser.CsWRITEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csXFOOT}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsXFOOT(Rpg3Parser.CsXFOOTContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csXLATE}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsXLATE(Rpg3Parser.CsXLATEContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csZ_ADD}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsZ_ADD(Rpg3Parser.CsZ_ADDContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#csZ_SUB}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsZ_SUB(Rpg3Parser.CsZ_SUBContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cs_operationExtender}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCs_operationExtender(Rpg3Parser.Cs_operationExtenderContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(Rpg3Parser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#factorContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactorContent(Rpg3Parser.FactorContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#resultType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResultType(Rpg3Parser.ResultTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#cs_fixed_comments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCs_fixed_comments(Rpg3Parser.Cs_fixed_commentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#ispec_fixed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIspec_fixed(Rpg3Parser.Ispec_fixedContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#fieldRecordRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldRecordRelation(Rpg3Parser.FieldRecordRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#fieldIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldIndicator(Rpg3Parser.FieldIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#is_external_rec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIs_external_rec(Rpg3Parser.Is_external_recContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#is_rec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIs_rec(Rpg3Parser.Is_recContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#recordIdIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordIdIndicator(Rpg3Parser.RecordIdIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#is_external_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIs_external_field(Rpg3Parser.Is_external_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#controlLevelIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitControlLevelIndicator(Rpg3Parser.ControlLevelIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#matchingFieldsIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchingFieldsIndicator(Rpg3Parser.MatchingFieldsIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#hspec_fixed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHspec_fixed(Rpg3Parser.Hspec_fixedContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#hs_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHs_expression(Rpg3Parser.Hs_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#hs_parm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHs_parm(Rpg3Parser.Hs_parmContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#hs_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHs_string(Rpg3Parser.Hs_stringContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#blank_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlank_line(Rpg3Parser.Blank_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#space_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpace_directive(Rpg3Parser.Space_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_copy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_copy(Rpg3Parser.Dir_copyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_include}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_include(Rpg3Parser.Dir_includeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_if(Rpg3Parser.Dir_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_elseif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_elseif(Rpg3Parser.Dir_elseifContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_else}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_else(Rpg3Parser.Dir_elseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_endif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_endif(Rpg3Parser.Dir_endifContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_define}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_define(Rpg3Parser.Dir_defineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_undefine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_undefine(Rpg3Parser.Dir_undefineContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#dir_eof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDir_eof(Rpg3Parser.Dir_eofContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#copyText}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCopyText(Rpg3Parser.CopyTextContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#trailing_ws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrailing_ws(Rpg3Parser.Trailing_wsContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#title_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTitle_directive(Rpg3Parser.Title_directiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#title_text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTitle_text(Rpg3Parser.Title_textContext ctx);
	/**
	 * Visit a parse tree produced by {@link Rpg3Parser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(Rpg3Parser.LiteralContext ctx);
}