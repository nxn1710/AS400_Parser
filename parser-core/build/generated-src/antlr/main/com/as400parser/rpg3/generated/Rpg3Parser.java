// Generated from D:/Code/AS400_Parser/parser-core/src/main/antlr/com/as400parser/rpg3/generated/Rpg3Parser.g4 by ANTLR 4.13.2
package com.as400parser.rpg3.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class Rpg3Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		END_SOURCE=1, LEAD_WS5=2, LEAD_WS5_Comments=3, EMPTY_NEWLINE=4, COMMENT_SPEC=5, 
		COMMENT_SPEC_STAR=6, HS_FIXED=7, FS_FIXED=8, ES_FIXED=9, IS_FIXED=10, 
		CS_FIXED=11, OS_FIXED=12, DIRECTIVE_SPEC=13, BLANK_SPEC_LINE=14, SEL_NEWLINE=15, 
		COMMENTS_TEXT=16, COMMENTS_EOL=17, DIR_COPY=18, DIR_EJECT=19, DIR_TITLE=20, 
		DIR_SPACE=21, DIR_OtherText=22, DIR_WS=23, DIR_Slash=24, HS_Content=25, 
		FS_RecordName=26, FS_Type=27, FS_Designation=28, FS_EndOfFile=29, FS_Sequence=30, 
		FS_Format=31, FS_RecordLength=32, FS_Mode=33, FS_LengthOfKeyArea=34, FS_KeyLength=35, 
		FS_Reserved1=36, FS_RecordAddressType=37, FS_Organization=38, FS_OverflowIndicator=39, 
		FS_KeyFieldStart=40, FS_ExtensionCode=41, FS_Device=42, FS_Reserved2=43, 
		FS_Continuation=44, ES_Reserved=45, ES_FromFileName=46, ES_ToFileName=47, 
		ES_ArrayName=48, ES_EntriesPerRecord=49, ES_EntriesPerArray=50, ES_Length=51, 
		ES_DataFormat=52, ES_DecimalPositions=53, ES_Sequence=54, ES_AltArrayName=55, 
		ES_AltLength=56, ES_AltDataFormat=57, ES_AltDecimalPositions=58, ES_AltSequence=59, 
		ES_Comments=60, IS_Identifier=61, IS_RecordIdArea=62, IS_DataFormat=63, 
		IS_FromPosition=64, IS_ToPosition=65, IS_DecimalPositions=66, IS_FieldName=67, 
		IS_ControlLevel=68, IS_MatchingFields=69, IS_FieldRelation=70, IS_FieldIndPlus=71, 
		IS_FieldIndMinus=72, IS_FieldIndZero=73, IS_Comments=74, CS_ControlLevel=75, 
		CS_Factor1=76, CS_OpExtender=77, CS_Operation_IFEQ=78, CS_Operation_IFNE=79, 
		CS_Operation_IFLE=80, CS_Operation_IFLT=81, CS_Operation_IFGE=82, CS_Operation_IFGT=83, 
		CS_Operation_DOWEQ=84, CS_Operation_DOWNE=85, CS_Operation_DOWLE=86, CS_Operation_DOWLT=87, 
		CS_Operation_DOWGE=88, CS_Operation_DOWGT=89, CS_Operation_DOUEQ=90, CS_Operation_DOUNE=91, 
		CS_Operation_DOULE=92, CS_Operation_DOULT=93, CS_Operation_DOUGE=94, CS_Operation_DOUGT=95, 
		CS_Operation_ANDEQ=96, CS_Operation_ANDNE=97, CS_Operation_ANDLE=98, CS_Operation_ANDLT=99, 
		CS_Operation_ANDGE=100, CS_Operation_ANDGT=101, CS_Operation_OREQ=102, 
		CS_Operation_ORNE=103, CS_Operation_ORLE=104, CS_Operation_ORLT=105, CS_Operation_ORGE=106, 
		CS_Operation_ORGT=107, CS_Operation_CASEQ=108, CS_Operation_CASNE=109, 
		CS_Operation_CASLE=110, CS_Operation_CASLT=111, CS_Operation_CASGE=112, 
		CS_Operation_CASGT=113, CS_Operation_CAS=114, CS_Operation_CABEQ=115, 
		CS_Operation_CABNE=116, CS_Operation_CABLE=117, CS_Operation_CABLT=118, 
		CS_Operation_CABGE=119, CS_Operation_CABGT=120, CS_Operation_MOVEL=121, 
		CS_Operation_MOVEA=122, CS_Operation_MOVE=123, CS_Operation_Z_ADD=124, 
		CS_Operation_Z_SUB=125, CS_Operation_ADD=126, CS_Operation_SUB=127, CS_Operation_MULT=128, 
		CS_Operation_DIV=129, CS_Operation_MVR=130, CS_Operation_SQRT=131, CS_Operation_XFOOT=132, 
		CS_Operation_CHAIN=133, CS_Operation_READ=134, CS_Operation_READC=135, 
		CS_Operation_READE=136, CS_Operation_READP=137, CS_Operation_REDPE=138, 
		CS_Operation_WRITE=139, CS_Operation_UPDAT=140, CS_Operation_DELET=141, 
		CS_Operation_SETLL=142, CS_Operation_SETGT=143, CS_Operation_OPEN=144, 
		CS_Operation_CLOSE=145, CS_Operation_FORCE=146, CS_Operation_FEOD=147, 
		CS_Operation_NEXT=148, CS_Operation_UNLCK=149, CS_Operation_ACQ=150, CS_Operation_REL=151, 
		CS_Operation_POST=152, CS_Operation_COMIT=153, CS_Operation_ROLBK=154, 
		CS_Operation_GOTO=155, CS_Operation_TAG=156, CS_Operation_BEGSR=157, CS_Operation_ENDSR=158, 
		CS_Operation_EXSR=159, CS_Operation_ENDIF=160, CS_Operation_ENDDO=161, 
		CS_Operation_ENDCS=162, CS_Operation_END=163, CS_Operation_ELSE=164, CS_Operation_DO=165, 
		CS_Operation_LEAVE=166, CS_Operation_ITER=167, CS_Operation_CALL=168, 
		CS_Operation_PARM=169, CS_Operation_PLIST=170, CS_Operation_KLIST=171, 
		CS_Operation_KFLD=172, CS_Operation_EXCPT=173, CS_Operation_EXFMT=174, 
		CS_Operation_DSPLY=175, CS_Operation_SETON=176, CS_Operation_SETOF=177, 
		CS_Operation_CAT=178, CS_Operation_SCAN=179, CS_Operation_SUBST=180, CS_Operation_XLATE=181, 
		CS_Operation_CHECK=182, CS_Operation_COMP=183, CS_Operation_LOKUP=184, 
		CS_Operation_SORTA=185, CS_Operation_TIME=186, CS_Operation_OCCUR=187, 
		CS_Operation_TESTB=188, CS_Operation_TESTN=189, CS_Operation_TESTZ=190, 
		CS_Operation_BITON=191, CS_Operation_BITOF=192, CS_Operation_MHHZO=193, 
		CS_Operation_MHLZO=194, CS_Operation_MLHZO=195, CS_Operation_MLLZO=196, 
		CS_Operation_CLEAR=197, CS_Operation_DUMP=198, CS_Operation_SHTDN=199, 
		CS_Operation_DEFN=200, CS_Operation_IN=201, CS_Operation_OUT=202, CS_Operation_RETRN=203, 
		CS_Operation_RESET=204, CS_Operation_Other=205, CS_Factor2=206, CS_Result=207, 
		CS_FieldLength=208, CS_DecimalPositions=209, CS_HalfAdjust=210, CS_ResultInd1=211, 
		CS_ResultInd2=212, CS_ResultInd3=213, CS_Comments=214, OS_RecordName=215, 
		OS_Type=216, OS_AddDelete=217, OS_SpaceBefore=218, OS_SpaceAfter=219, 
		OS_SkipBefore=220, OS_SkipAfter=221, OS_CondInd1_Flag=222, OS_CondInd1=223, 
		OS_CondInd2_Flag=224, OS_CondInd2=225, OS_CondInd3_Flag=226, OS_CondInd3=227, 
		OS_ExceptName=228, OS_Remaining=229, BlankFlag=230, NoFlag=231, BlankIndicator=232, 
		GeneralIndicator=233, FunctionKeyIndicator=234, ControlLevelIndicator=235, 
		ControlLevel0Indicator=236, LastRecordIndicator=237, MatchingRecordIndicator=238, 
		HaltIndicator=239, ReturnIndicator=240, ExternalIndicator=241, OverflowIndicator=242, 
		SubroutineIndicator=243, AndIndicator=244, OrIndicator=245, FirstPageIndicator=246, 
		OtherIndicator=247, EOS_Text=248, EOL=249;
	public static final int
		RULE_rpg3Program = 0, RULE_headerSpec = 1, RULE_fileSpec = 2, RULE_extensionSpec = 3, 
		RULE_alternatingArray = 4, RULE_inputSpec = 5, RULE_inputFieldDetail = 6, 
		RULE_calculationSpec = 7, RULE_subroutine = 8, RULE_calcStatement = 9, 
		RULE_commentLine = 10, RULE_ifBlock = 11, RULE_doBlock = 12, RULE_dowBlock = 13, 
		RULE_douBlock = 14, RULE_caseBlock = 15, RULE_cspecPrefix = 16, RULE_cspecSuffix = 17, 
		RULE_calcLine = 18, RULE_cspecIFxx = 19, RULE_cspecELSE = 20, RULE_cspecENDIF = 21, 
		RULE_cspecDO = 22, RULE_cspecDOWxx = 23, RULE_cspecDOUxx = 24, RULE_cspecENDDO = 25, 
		RULE_cspecCASxx = 26, RULE_cspecCAS = 27, RULE_cspecENDCS = 28, RULE_cspecANDxx = 29, 
		RULE_cspecORxx = 30, RULE_cspecBEGSR = 31, RULE_cspecENDSR = 32, RULE_csOperation = 33, 
		RULE_onOffFlag = 34, RULE_csIndicator = 35, RULE_csResultIndicators = 36, 
		RULE_outputSpec = 37, RULE_outputRecordSpec = 38, RULE_outputFieldSpec = 39, 
		RULE_outputCondIndicators = 40, RULE_endSource = 41, RULE_endSourceLine = 42;
	private static String[] makeRuleNames() {
		return new String[] {
			"rpg3Program", "headerSpec", "fileSpec", "extensionSpec", "alternatingArray", 
			"inputSpec", "inputFieldDetail", "calculationSpec", "subroutine", "calcStatement", 
			"commentLine", "ifBlock", "doBlock", "dowBlock", "douBlock", "caseBlock", 
			"cspecPrefix", "cspecSuffix", "calcLine", "cspecIFxx", "cspecELSE", "cspecENDIF", 
			"cspecDO", "cspecDOWxx", "cspecDOUxx", "cspecENDDO", "cspecCASxx", "cspecCAS", 
			"cspecENDCS", "cspecANDxx", "cspecORxx", "cspecBEGSR", "cspecENDSR", 
			"csOperation", "onOffFlag", "csIndicator", "csResultIndicators", "outputSpec", 
			"outputRecordSpec", "outputFieldSpec", "outputCondIndicators", "endSource", 
			"endSourceLine"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'     '", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "END_SOURCE", "LEAD_WS5", "LEAD_WS5_Comments", "EMPTY_NEWLINE", 
			"COMMENT_SPEC", "COMMENT_SPEC_STAR", "HS_FIXED", "FS_FIXED", "ES_FIXED", 
			"IS_FIXED", "CS_FIXED", "OS_FIXED", "DIRECTIVE_SPEC", "BLANK_SPEC_LINE", 
			"SEL_NEWLINE", "COMMENTS_TEXT", "COMMENTS_EOL", "DIR_COPY", "DIR_EJECT", 
			"DIR_TITLE", "DIR_SPACE", "DIR_OtherText", "DIR_WS", "DIR_Slash", "HS_Content", 
			"FS_RecordName", "FS_Type", "FS_Designation", "FS_EndOfFile", "FS_Sequence", 
			"FS_Format", "FS_RecordLength", "FS_Mode", "FS_LengthOfKeyArea", "FS_KeyLength", 
			"FS_Reserved1", "FS_RecordAddressType", "FS_Organization", "FS_OverflowIndicator", 
			"FS_KeyFieldStart", "FS_ExtensionCode", "FS_Device", "FS_Reserved2", 
			"FS_Continuation", "ES_Reserved", "ES_FromFileName", "ES_ToFileName", 
			"ES_ArrayName", "ES_EntriesPerRecord", "ES_EntriesPerArray", "ES_Length", 
			"ES_DataFormat", "ES_DecimalPositions", "ES_Sequence", "ES_AltArrayName", 
			"ES_AltLength", "ES_AltDataFormat", "ES_AltDecimalPositions", "ES_AltSequence", 
			"ES_Comments", "IS_Identifier", "IS_RecordIdArea", "IS_DataFormat", "IS_FromPosition", 
			"IS_ToPosition", "IS_DecimalPositions", "IS_FieldName", "IS_ControlLevel", 
			"IS_MatchingFields", "IS_FieldRelation", "IS_FieldIndPlus", "IS_FieldIndMinus", 
			"IS_FieldIndZero", "IS_Comments", "CS_ControlLevel", "CS_Factor1", "CS_OpExtender", 
			"CS_Operation_IFEQ", "CS_Operation_IFNE", "CS_Operation_IFLE", "CS_Operation_IFLT", 
			"CS_Operation_IFGE", "CS_Operation_IFGT", "CS_Operation_DOWEQ", "CS_Operation_DOWNE", 
			"CS_Operation_DOWLE", "CS_Operation_DOWLT", "CS_Operation_DOWGE", "CS_Operation_DOWGT", 
			"CS_Operation_DOUEQ", "CS_Operation_DOUNE", "CS_Operation_DOULE", "CS_Operation_DOULT", 
			"CS_Operation_DOUGE", "CS_Operation_DOUGT", "CS_Operation_ANDEQ", "CS_Operation_ANDNE", 
			"CS_Operation_ANDLE", "CS_Operation_ANDLT", "CS_Operation_ANDGE", "CS_Operation_ANDGT", 
			"CS_Operation_OREQ", "CS_Operation_ORNE", "CS_Operation_ORLE", "CS_Operation_ORLT", 
			"CS_Operation_ORGE", "CS_Operation_ORGT", "CS_Operation_CASEQ", "CS_Operation_CASNE", 
			"CS_Operation_CASLE", "CS_Operation_CASLT", "CS_Operation_CASGE", "CS_Operation_CASGT", 
			"CS_Operation_CAS", "CS_Operation_CABEQ", "CS_Operation_CABNE", "CS_Operation_CABLE", 
			"CS_Operation_CABLT", "CS_Operation_CABGE", "CS_Operation_CABGT", "CS_Operation_MOVEL", 
			"CS_Operation_MOVEA", "CS_Operation_MOVE", "CS_Operation_Z_ADD", "CS_Operation_Z_SUB", 
			"CS_Operation_ADD", "CS_Operation_SUB", "CS_Operation_MULT", "CS_Operation_DIV", 
			"CS_Operation_MVR", "CS_Operation_SQRT", "CS_Operation_XFOOT", "CS_Operation_CHAIN", 
			"CS_Operation_READ", "CS_Operation_READC", "CS_Operation_READE", "CS_Operation_READP", 
			"CS_Operation_REDPE", "CS_Operation_WRITE", "CS_Operation_UPDAT", "CS_Operation_DELET", 
			"CS_Operation_SETLL", "CS_Operation_SETGT", "CS_Operation_OPEN", "CS_Operation_CLOSE", 
			"CS_Operation_FORCE", "CS_Operation_FEOD", "CS_Operation_NEXT", "CS_Operation_UNLCK", 
			"CS_Operation_ACQ", "CS_Operation_REL", "CS_Operation_POST", "CS_Operation_COMIT", 
			"CS_Operation_ROLBK", "CS_Operation_GOTO", "CS_Operation_TAG", "CS_Operation_BEGSR", 
			"CS_Operation_ENDSR", "CS_Operation_EXSR", "CS_Operation_ENDIF", "CS_Operation_ENDDO", 
			"CS_Operation_ENDCS", "CS_Operation_END", "CS_Operation_ELSE", "CS_Operation_DO", 
			"CS_Operation_LEAVE", "CS_Operation_ITER", "CS_Operation_CALL", "CS_Operation_PARM", 
			"CS_Operation_PLIST", "CS_Operation_KLIST", "CS_Operation_KFLD", "CS_Operation_EXCPT", 
			"CS_Operation_EXFMT", "CS_Operation_DSPLY", "CS_Operation_SETON", "CS_Operation_SETOF", 
			"CS_Operation_CAT", "CS_Operation_SCAN", "CS_Operation_SUBST", "CS_Operation_XLATE", 
			"CS_Operation_CHECK", "CS_Operation_COMP", "CS_Operation_LOKUP", "CS_Operation_SORTA", 
			"CS_Operation_TIME", "CS_Operation_OCCUR", "CS_Operation_TESTB", "CS_Operation_TESTN", 
			"CS_Operation_TESTZ", "CS_Operation_BITON", "CS_Operation_BITOF", "CS_Operation_MHHZO", 
			"CS_Operation_MHLZO", "CS_Operation_MLHZO", "CS_Operation_MLLZO", "CS_Operation_CLEAR", 
			"CS_Operation_DUMP", "CS_Operation_SHTDN", "CS_Operation_DEFN", "CS_Operation_IN", 
			"CS_Operation_OUT", "CS_Operation_RETRN", "CS_Operation_RESET", "CS_Operation_Other", 
			"CS_Factor2", "CS_Result", "CS_FieldLength", "CS_DecimalPositions", "CS_HalfAdjust", 
			"CS_ResultInd1", "CS_ResultInd2", "CS_ResultInd3", "CS_Comments", "OS_RecordName", 
			"OS_Type", "OS_AddDelete", "OS_SpaceBefore", "OS_SpaceAfter", "OS_SkipBefore", 
			"OS_SkipAfter", "OS_CondInd1_Flag", "OS_CondInd1", "OS_CondInd2_Flag", 
			"OS_CondInd2", "OS_CondInd3_Flag", "OS_CondInd3", "OS_ExceptName", "OS_Remaining", 
			"BlankFlag", "NoFlag", "BlankIndicator", "GeneralIndicator", "FunctionKeyIndicator", 
			"ControlLevelIndicator", "ControlLevel0Indicator", "LastRecordIndicator", 
			"MatchingRecordIndicator", "HaltIndicator", "ReturnIndicator", "ExternalIndicator", 
			"OverflowIndicator", "SubroutineIndicator", "AndIndicator", "OrIndicator", 
			"FirstPageIndicator", "OtherIndicator", "EOS_Text", "EOL"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Rpg3Parser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Rpg3Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Rpg3ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(Rpg3Parser.EOF, 0); }
		public List<HeaderSpecContext> headerSpec() {
			return getRuleContexts(HeaderSpecContext.class);
		}
		public HeaderSpecContext headerSpec(int i) {
			return getRuleContext(HeaderSpecContext.class,i);
		}
		public List<FileSpecContext> fileSpec() {
			return getRuleContexts(FileSpecContext.class);
		}
		public FileSpecContext fileSpec(int i) {
			return getRuleContext(FileSpecContext.class,i);
		}
		public List<ExtensionSpecContext> extensionSpec() {
			return getRuleContexts(ExtensionSpecContext.class);
		}
		public ExtensionSpecContext extensionSpec(int i) {
			return getRuleContext(ExtensionSpecContext.class,i);
		}
		public List<InputSpecContext> inputSpec() {
			return getRuleContexts(InputSpecContext.class);
		}
		public InputSpecContext inputSpec(int i) {
			return getRuleContext(InputSpecContext.class,i);
		}
		public List<CalculationSpecContext> calculationSpec() {
			return getRuleContexts(CalculationSpecContext.class);
		}
		public CalculationSpecContext calculationSpec(int i) {
			return getRuleContext(CalculationSpecContext.class,i);
		}
		public List<OutputSpecContext> outputSpec() {
			return getRuleContexts(OutputSpecContext.class);
		}
		public OutputSpecContext outputSpec(int i) {
			return getRuleContext(OutputSpecContext.class,i);
		}
		public EndSourceContext endSource() {
			return getRuleContext(EndSourceContext.class,0);
		}
		public Rpg3ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rpg3Program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterRpg3Program(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitRpg3Program(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitRpg3Program(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rpg3ProgramContext rpg3Program() throws RecognitionException {
		Rpg3ProgramContext _localctx = new Rpg3ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_rpg3Program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HS_FIXED) {
				{
				{
				setState(86);
				headerSpec();
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FS_FIXED) {
				{
				{
				setState(92);
				fileSpec();
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ES_FIXED) {
				{
				{
				setState(98);
				extensionSpec();
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IS_FIXED) {
				{
				{
				setState(104);
				inputSpec();
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2144L) != 0)) {
				{
				{
				setState(110);
				calculationSpec();
				}
				}
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OS_FIXED) {
				{
				{
				setState(116);
				outputSpec();
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==END_SOURCE) {
				{
				setState(122);
				endSource();
				}
			}

			setState(125);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HeaderSpecContext extends ParserRuleContext {
		public TerminalNode HS_FIXED() { return getToken(Rpg3Parser.HS_FIXED, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public TerminalNode HS_Content() { return getToken(Rpg3Parser.HS_Content, 0); }
		public HeaderSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_headerSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterHeaderSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitHeaderSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitHeaderSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeaderSpecContext headerSpec() throws RecognitionException {
		HeaderSpecContext _localctx = new HeaderSpecContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_headerSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(HS_FIXED);
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HS_Content) {
				{
				setState(128);
				match(HS_Content);
				}
			}

			setState(131);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileSpecContext extends ParserRuleContext {
		public TerminalNode FS_FIXED() { return getToken(Rpg3Parser.FS_FIXED, 0); }
		public TerminalNode FS_RecordName() { return getToken(Rpg3Parser.FS_RecordName, 0); }
		public TerminalNode FS_Type() { return getToken(Rpg3Parser.FS_Type, 0); }
		public TerminalNode FS_Designation() { return getToken(Rpg3Parser.FS_Designation, 0); }
		public TerminalNode FS_EndOfFile() { return getToken(Rpg3Parser.FS_EndOfFile, 0); }
		public TerminalNode FS_Sequence() { return getToken(Rpg3Parser.FS_Sequence, 0); }
		public TerminalNode FS_Format() { return getToken(Rpg3Parser.FS_Format, 0); }
		public TerminalNode FS_RecordLength() { return getToken(Rpg3Parser.FS_RecordLength, 0); }
		public TerminalNode FS_Mode() { return getToken(Rpg3Parser.FS_Mode, 0); }
		public TerminalNode FS_LengthOfKeyArea() { return getToken(Rpg3Parser.FS_LengthOfKeyArea, 0); }
		public TerminalNode FS_KeyLength() { return getToken(Rpg3Parser.FS_KeyLength, 0); }
		public TerminalNode FS_RecordAddressType() { return getToken(Rpg3Parser.FS_RecordAddressType, 0); }
		public TerminalNode FS_Organization() { return getToken(Rpg3Parser.FS_Organization, 0); }
		public TerminalNode FS_OverflowIndicator() { return getToken(Rpg3Parser.FS_OverflowIndicator, 0); }
		public TerminalNode FS_KeyFieldStart() { return getToken(Rpg3Parser.FS_KeyFieldStart, 0); }
		public TerminalNode FS_ExtensionCode() { return getToken(Rpg3Parser.FS_ExtensionCode, 0); }
		public TerminalNode FS_Device() { return getToken(Rpg3Parser.FS_Device, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public TerminalNode FS_Continuation() { return getToken(Rpg3Parser.FS_Continuation, 0); }
		public FileSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fileSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterFileSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitFileSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitFileSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileSpecContext fileSpec() throws RecognitionException {
		FileSpecContext _localctx = new FileSpecContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_fileSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			match(FS_FIXED);
			setState(134);
			match(FS_RecordName);
			setState(135);
			match(FS_Type);
			setState(136);
			match(FS_Designation);
			setState(137);
			match(FS_EndOfFile);
			setState(138);
			match(FS_Sequence);
			setState(139);
			match(FS_Format);
			setState(140);
			match(FS_RecordLength);
			setState(141);
			match(FS_Mode);
			setState(142);
			match(FS_LengthOfKeyArea);
			setState(143);
			match(FS_KeyLength);
			setState(144);
			match(FS_RecordAddressType);
			setState(145);
			match(FS_Organization);
			setState(146);
			match(FS_OverflowIndicator);
			setState(147);
			match(FS_KeyFieldStart);
			setState(148);
			match(FS_ExtensionCode);
			setState(149);
			match(FS_Device);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FS_Continuation) {
				{
				setState(150);
				match(FS_Continuation);
				}
			}

			setState(153);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtensionSpecContext extends ParserRuleContext {
		public TerminalNode ES_FIXED() { return getToken(Rpg3Parser.ES_FIXED, 0); }
		public TerminalNode ES_FromFileName() { return getToken(Rpg3Parser.ES_FromFileName, 0); }
		public TerminalNode ES_ToFileName() { return getToken(Rpg3Parser.ES_ToFileName, 0); }
		public TerminalNode ES_ArrayName() { return getToken(Rpg3Parser.ES_ArrayName, 0); }
		public TerminalNode ES_EntriesPerRecord() { return getToken(Rpg3Parser.ES_EntriesPerRecord, 0); }
		public TerminalNode ES_EntriesPerArray() { return getToken(Rpg3Parser.ES_EntriesPerArray, 0); }
		public TerminalNode ES_Length() { return getToken(Rpg3Parser.ES_Length, 0); }
		public TerminalNode ES_DataFormat() { return getToken(Rpg3Parser.ES_DataFormat, 0); }
		public TerminalNode ES_DecimalPositions() { return getToken(Rpg3Parser.ES_DecimalPositions, 0); }
		public TerminalNode ES_Sequence() { return getToken(Rpg3Parser.ES_Sequence, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public AlternatingArrayContext alternatingArray() {
			return getRuleContext(AlternatingArrayContext.class,0);
		}
		public ExtensionSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extensionSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterExtensionSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitExtensionSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitExtensionSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtensionSpecContext extensionSpec() throws RecognitionException {
		ExtensionSpecContext _localctx = new ExtensionSpecContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_extensionSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(ES_FIXED);
			setState(156);
			match(ES_FromFileName);
			setState(157);
			match(ES_ToFileName);
			setState(158);
			match(ES_ArrayName);
			setState(159);
			match(ES_EntriesPerRecord);
			setState(160);
			match(ES_EntriesPerArray);
			setState(161);
			match(ES_Length);
			setState(162);
			match(ES_DataFormat);
			setState(163);
			match(ES_DecimalPositions);
			setState(164);
			match(ES_Sequence);
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ES_AltArrayName) {
				{
				setState(165);
				alternatingArray();
				}
			}

			setState(168);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AlternatingArrayContext extends ParserRuleContext {
		public TerminalNode ES_AltArrayName() { return getToken(Rpg3Parser.ES_AltArrayName, 0); }
		public TerminalNode ES_AltLength() { return getToken(Rpg3Parser.ES_AltLength, 0); }
		public TerminalNode ES_AltDataFormat() { return getToken(Rpg3Parser.ES_AltDataFormat, 0); }
		public TerminalNode ES_AltDecimalPositions() { return getToken(Rpg3Parser.ES_AltDecimalPositions, 0); }
		public TerminalNode ES_AltSequence() { return getToken(Rpg3Parser.ES_AltSequence, 0); }
		public AlternatingArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alternatingArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterAlternatingArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitAlternatingArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitAlternatingArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AlternatingArrayContext alternatingArray() throws RecognitionException {
		AlternatingArrayContext _localctx = new AlternatingArrayContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_alternatingArray);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(ES_AltArrayName);
			setState(171);
			match(ES_AltLength);
			setState(172);
			match(ES_AltDataFormat);
			setState(173);
			match(ES_AltDecimalPositions);
			setState(174);
			match(ES_AltSequence);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InputSpecContext extends ParserRuleContext {
		public TerminalNode IS_FIXED() { return getToken(Rpg3Parser.IS_FIXED, 0); }
		public TerminalNode IS_Identifier() { return getToken(Rpg3Parser.IS_Identifier, 0); }
		public TerminalNode IS_RecordIdArea() { return getToken(Rpg3Parser.IS_RecordIdArea, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public InputFieldDetailContext inputFieldDetail() {
			return getRuleContext(InputFieldDetailContext.class,0);
		}
		public InputSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterInputSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitInputSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitInputSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputSpecContext inputSpec() throws RecognitionException {
		InputSpecContext _localctx = new InputSpecContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_inputSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(IS_FIXED);
			setState(177);
			match(IS_Identifier);
			setState(178);
			match(IS_RecordIdArea);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IS_DataFormat) {
				{
				setState(179);
				inputFieldDetail();
				}
			}

			setState(182);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InputFieldDetailContext extends ParserRuleContext {
		public TerminalNode IS_DataFormat() { return getToken(Rpg3Parser.IS_DataFormat, 0); }
		public TerminalNode IS_FromPosition() { return getToken(Rpg3Parser.IS_FromPosition, 0); }
		public TerminalNode IS_ToPosition() { return getToken(Rpg3Parser.IS_ToPosition, 0); }
		public TerminalNode IS_DecimalPositions() { return getToken(Rpg3Parser.IS_DecimalPositions, 0); }
		public TerminalNode IS_FieldName() { return getToken(Rpg3Parser.IS_FieldName, 0); }
		public TerminalNode IS_ControlLevel() { return getToken(Rpg3Parser.IS_ControlLevel, 0); }
		public TerminalNode IS_MatchingFields() { return getToken(Rpg3Parser.IS_MatchingFields, 0); }
		public TerminalNode IS_FieldRelation() { return getToken(Rpg3Parser.IS_FieldRelation, 0); }
		public TerminalNode IS_FieldIndPlus() { return getToken(Rpg3Parser.IS_FieldIndPlus, 0); }
		public TerminalNode IS_FieldIndMinus() { return getToken(Rpg3Parser.IS_FieldIndMinus, 0); }
		public TerminalNode IS_FieldIndZero() { return getToken(Rpg3Parser.IS_FieldIndZero, 0); }
		public InputFieldDetailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputFieldDetail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterInputFieldDetail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitInputFieldDetail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitInputFieldDetail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputFieldDetailContext inputFieldDetail() throws RecognitionException {
		InputFieldDetailContext _localctx = new InputFieldDetailContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_inputFieldDetail);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			match(IS_DataFormat);
			setState(185);
			match(IS_FromPosition);
			setState(186);
			match(IS_ToPosition);
			setState(187);
			match(IS_DecimalPositions);
			setState(188);
			match(IS_FieldName);
			setState(189);
			match(IS_ControlLevel);
			setState(190);
			match(IS_MatchingFields);
			setState(191);
			match(IS_FieldRelation);
			setState(192);
			match(IS_FieldIndPlus);
			setState(193);
			match(IS_FieldIndMinus);
			setState(194);
			match(IS_FieldIndZero);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CalculationSpecContext extends ParserRuleContext {
		public SubroutineContext subroutine() {
			return getRuleContext(SubroutineContext.class,0);
		}
		public CalcStatementContext calcStatement() {
			return getRuleContext(CalcStatementContext.class,0);
		}
		public CalculationSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calculationSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCalculationSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCalculationSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCalculationSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CalculationSpecContext calculationSpec() throws RecognitionException {
		CalculationSpecContext _localctx = new CalculationSpecContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_calculationSpec);
		try {
			setState(198);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(196);
				subroutine();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(197);
				calcStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SubroutineContext extends ParserRuleContext {
		public CspecBEGSRContext cspecBEGSR() {
			return getRuleContext(CspecBEGSRContext.class,0);
		}
		public CspecENDSRContext cspecENDSR() {
			return getRuleContext(CspecENDSRContext.class,0);
		}
		public List<CalcStatementContext> calcStatement() {
			return getRuleContexts(CalcStatementContext.class);
		}
		public CalcStatementContext calcStatement(int i) {
			return getRuleContext(CalcStatementContext.class,i);
		}
		public SubroutineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subroutine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterSubroutine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitSubroutine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitSubroutine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubroutineContext subroutine() throws RecognitionException {
		SubroutineContext _localctx = new SubroutineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_subroutine);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			cspecBEGSR();
			setState(204);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(201);
					calcStatement();
					}
					} 
				}
				setState(206);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			setState(207);
			cspecENDSR();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CalcStatementContext extends ParserRuleContext {
		public IfBlockContext ifBlock() {
			return getRuleContext(IfBlockContext.class,0);
		}
		public DoBlockContext doBlock() {
			return getRuleContext(DoBlockContext.class,0);
		}
		public DouBlockContext douBlock() {
			return getRuleContext(DouBlockContext.class,0);
		}
		public DowBlockContext dowBlock() {
			return getRuleContext(DowBlockContext.class,0);
		}
		public CaseBlockContext caseBlock() {
			return getRuleContext(CaseBlockContext.class,0);
		}
		public CalcLineContext calcLine() {
			return getRuleContext(CalcLineContext.class,0);
		}
		public CommentLineContext commentLine() {
			return getRuleContext(CommentLineContext.class,0);
		}
		public CalcStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calcStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCalcStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCalcStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCalcStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CalcStatementContext calcStatement() throws RecognitionException {
		CalcStatementContext _localctx = new CalcStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_calcStatement);
		try {
			setState(216);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				ifBlock();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				doBlock();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(211);
				douBlock();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(212);
				dowBlock();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(213);
				caseBlock();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(214);
				calcLine();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(215);
				commentLine();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommentLineContext extends ParserRuleContext {
		public TerminalNode COMMENT_SPEC() { return getToken(Rpg3Parser.COMMENT_SPEC, 0); }
		public TerminalNode COMMENT_SPEC_STAR() { return getToken(Rpg3Parser.COMMENT_SPEC_STAR, 0); }
		public CommentLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commentLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCommentLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCommentLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCommentLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentLineContext commentLine() throws RecognitionException {
		CommentLineContext _localctx = new CommentLineContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_commentLine);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			_la = _input.LA(1);
			if ( !(_la==COMMENT_SPEC || _la==COMMENT_SPEC_STAR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfBlockContext extends ParserRuleContext {
		public CspecIFxxContext cspecIFxx() {
			return getRuleContext(CspecIFxxContext.class,0);
		}
		public CspecENDIFContext cspecENDIF() {
			return getRuleContext(CspecENDIFContext.class,0);
		}
		public List<CalcStatementContext> calcStatement() {
			return getRuleContexts(CalcStatementContext.class);
		}
		public CalcStatementContext calcStatement(int i) {
			return getRuleContext(CalcStatementContext.class,i);
		}
		public CspecELSEContext cspecELSE() {
			return getRuleContext(CspecELSEContext.class,0);
		}
		public IfBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterIfBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitIfBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitIfBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfBlockContext ifBlock() throws RecognitionException {
		IfBlockContext _localctx = new IfBlockContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ifBlock);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			cspecIFxx();
			setState(224);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(221);
					calcStatement();
					}
					} 
				}
				setState(226);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			setState(234);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(227);
				cspecELSE();
				setState(231);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(228);
						calcStatement();
						}
						} 
					}
					setState(233);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				}
				}
				break;
			}
			setState(236);
			cspecENDIF();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DoBlockContext extends ParserRuleContext {
		public CspecDOContext cspecDO() {
			return getRuleContext(CspecDOContext.class,0);
		}
		public CspecENDDOContext cspecENDDO() {
			return getRuleContext(CspecENDDOContext.class,0);
		}
		public List<CalcStatementContext> calcStatement() {
			return getRuleContexts(CalcStatementContext.class);
		}
		public CalcStatementContext calcStatement(int i) {
			return getRuleContext(CalcStatementContext.class,i);
		}
		public DoBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterDoBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitDoBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitDoBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoBlockContext doBlock() throws RecognitionException {
		DoBlockContext _localctx = new DoBlockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_doBlock);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			cspecDO();
			setState(242);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(239);
					calcStatement();
					}
					} 
				}
				setState(244);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			setState(245);
			cspecENDDO();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DowBlockContext extends ParserRuleContext {
		public CspecDOWxxContext cspecDOWxx() {
			return getRuleContext(CspecDOWxxContext.class,0);
		}
		public CspecENDDOContext cspecENDDO() {
			return getRuleContext(CspecENDDOContext.class,0);
		}
		public List<CalcStatementContext> calcStatement() {
			return getRuleContexts(CalcStatementContext.class);
		}
		public CalcStatementContext calcStatement(int i) {
			return getRuleContext(CalcStatementContext.class,i);
		}
		public DowBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dowBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterDowBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitDowBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitDowBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DowBlockContext dowBlock() throws RecognitionException {
		DowBlockContext _localctx = new DowBlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_dowBlock);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			cspecDOWxx();
			setState(251);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(248);
					calcStatement();
					}
					} 
				}
				setState(253);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			setState(254);
			cspecENDDO();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DouBlockContext extends ParserRuleContext {
		public CspecDOUxxContext cspecDOUxx() {
			return getRuleContext(CspecDOUxxContext.class,0);
		}
		public CspecENDDOContext cspecENDDO() {
			return getRuleContext(CspecENDDOContext.class,0);
		}
		public List<CalcStatementContext> calcStatement() {
			return getRuleContexts(CalcStatementContext.class);
		}
		public CalcStatementContext calcStatement(int i) {
			return getRuleContext(CalcStatementContext.class,i);
		}
		public DouBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_douBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterDouBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitDouBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitDouBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DouBlockContext douBlock() throws RecognitionException {
		DouBlockContext _localctx = new DouBlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_douBlock);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			cspecDOUxx();
			setState(260);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(257);
					calcStatement();
					}
					} 
				}
				setState(262);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(263);
			cspecENDDO();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CaseBlockContext extends ParserRuleContext {
		public CspecENDCSContext cspecENDCS() {
			return getRuleContext(CspecENDCSContext.class,0);
		}
		public List<CspecCASxxContext> cspecCASxx() {
			return getRuleContexts(CspecCASxxContext.class);
		}
		public CspecCASxxContext cspecCASxx(int i) {
			return getRuleContext(CspecCASxxContext.class,i);
		}
		public CspecCASContext cspecCAS() {
			return getRuleContext(CspecCASContext.class,0);
		}
		public CaseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCaseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCaseBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCaseBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CaseBlockContext caseBlock() throws RecognitionException {
		CaseBlockContext _localctx = new CaseBlockContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_caseBlock);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(266); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(265);
					cspecCASxx();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(268); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(271);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(270);
				cspecCAS();
				}
				break;
			}
			setState(273);
			cspecENDCS();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecPrefixContext extends ParserRuleContext {
		public TerminalNode CS_FIXED() { return getToken(Rpg3Parser.CS_FIXED, 0); }
		public TerminalNode CS_ControlLevel() { return getToken(Rpg3Parser.CS_ControlLevel, 0); }
		public OnOffFlagContext onOffFlag() {
			return getRuleContext(OnOffFlagContext.class,0);
		}
		public CsIndicatorContext csIndicator() {
			return getRuleContext(CsIndicatorContext.class,0);
		}
		public TerminalNode CS_Factor1() { return getToken(Rpg3Parser.CS_Factor1, 0); }
		public TerminalNode CS_OpExtender() { return getToken(Rpg3Parser.CS_OpExtender, 0); }
		public CspecPrefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecPrefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecPrefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecPrefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecPrefixContext cspecPrefix() throws RecognitionException {
		CspecPrefixContext _localctx = new CspecPrefixContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_cspecPrefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			match(CS_FIXED);
			setState(276);
			match(CS_ControlLevel);
			setState(277);
			onOffFlag();
			setState(278);
			csIndicator();
			setState(279);
			match(CS_Factor1);
			setState(280);
			match(CS_OpExtender);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecSuffixContext extends ParserRuleContext {
		public TerminalNode CS_Factor2() { return getToken(Rpg3Parser.CS_Factor2, 0); }
		public TerminalNode CS_Result() { return getToken(Rpg3Parser.CS_Result, 0); }
		public TerminalNode CS_FieldLength() { return getToken(Rpg3Parser.CS_FieldLength, 0); }
		public TerminalNode CS_DecimalPositions() { return getToken(Rpg3Parser.CS_DecimalPositions, 0); }
		public TerminalNode CS_HalfAdjust() { return getToken(Rpg3Parser.CS_HalfAdjust, 0); }
		public CsResultIndicatorsContext csResultIndicators() {
			return getRuleContext(CsResultIndicatorsContext.class,0);
		}
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public CspecSuffixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecSuffix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecSuffix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecSuffix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecSuffix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecSuffixContext cspecSuffix() throws RecognitionException {
		CspecSuffixContext _localctx = new CspecSuffixContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_cspecSuffix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			match(CS_Factor2);
			setState(283);
			match(CS_Result);
			setState(284);
			match(CS_FieldLength);
			setState(285);
			match(CS_DecimalPositions);
			setState(286);
			match(CS_HalfAdjust);
			setState(287);
			csResultIndicators();
			setState(288);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CalcLineContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CsOperationContext csOperation() {
			return getRuleContext(CsOperationContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CalcLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calcLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCalcLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCalcLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCalcLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CalcLineContext calcLine() throws RecognitionException {
		CalcLineContext _localctx = new CalcLineContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_calcLine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			cspecPrefix();
			setState(291);
			csOperation();
			setState(292);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecIFxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_IFEQ() { return getToken(Rpg3Parser.CS_Operation_IFEQ, 0); }
		public TerminalNode CS_Operation_IFNE() { return getToken(Rpg3Parser.CS_Operation_IFNE, 0); }
		public TerminalNode CS_Operation_IFLE() { return getToken(Rpg3Parser.CS_Operation_IFLE, 0); }
		public TerminalNode CS_Operation_IFLT() { return getToken(Rpg3Parser.CS_Operation_IFLT, 0); }
		public TerminalNode CS_Operation_IFGE() { return getToken(Rpg3Parser.CS_Operation_IFGE, 0); }
		public TerminalNode CS_Operation_IFGT() { return getToken(Rpg3Parser.CS_Operation_IFGT, 0); }
		public List<CspecANDxxContext> cspecANDxx() {
			return getRuleContexts(CspecANDxxContext.class);
		}
		public CspecANDxxContext cspecANDxx(int i) {
			return getRuleContext(CspecANDxxContext.class,i);
		}
		public List<CspecORxxContext> cspecORxx() {
			return getRuleContexts(CspecORxxContext.class);
		}
		public CspecORxxContext cspecORxx(int i) {
			return getRuleContext(CspecORxxContext.class,i);
		}
		public CspecIFxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecIFxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecIFxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecIFxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecIFxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecIFxxContext cspecIFxx() throws RecognitionException {
		CspecIFxxContext _localctx = new CspecIFxxContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_cspecIFxx);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			cspecPrefix();
			setState(295);
			_la = _input.LA(1);
			if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(296);
			cspecSuffix();
			setState(301);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(299);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
					case 1:
						{
						setState(297);
						cspecANDxx();
						}
						break;
					case 2:
						{
						setState(298);
						cspecORxx();
						}
						break;
					}
					} 
				}
				setState(303);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecELSEContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public TerminalNode CS_Operation_ELSE() { return getToken(Rpg3Parser.CS_Operation_ELSE, 0); }
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CspecELSEContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecELSE; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecELSE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecELSE(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecELSE(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecELSEContext cspecELSE() throws RecognitionException {
		CspecELSEContext _localctx = new CspecELSEContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_cspecELSE);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			cspecPrefix();
			setState(305);
			match(CS_Operation_ELSE);
			setState(306);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecENDIFContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_ENDIF() { return getToken(Rpg3Parser.CS_Operation_ENDIF, 0); }
		public TerminalNode CS_Operation_END() { return getToken(Rpg3Parser.CS_Operation_END, 0); }
		public CspecENDIFContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecENDIF; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecENDIF(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecENDIF(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecENDIF(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecENDIFContext cspecENDIF() throws RecognitionException {
		CspecENDIFContext _localctx = new CspecENDIFContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_cspecENDIF);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			cspecPrefix();
			setState(309);
			_la = _input.LA(1);
			if ( !(_la==CS_Operation_ENDIF || _la==CS_Operation_END) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(310);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecDOContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public TerminalNode CS_Operation_DO() { return getToken(Rpg3Parser.CS_Operation_DO, 0); }
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CspecDOContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecDO; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecDO(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecDO(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecDO(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecDOContext cspecDO() throws RecognitionException {
		CspecDOContext _localctx = new CspecDOContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_cspecDO);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			cspecPrefix();
			setState(313);
			match(CS_Operation_DO);
			setState(314);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecDOWxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_DOWEQ() { return getToken(Rpg3Parser.CS_Operation_DOWEQ, 0); }
		public TerminalNode CS_Operation_DOWNE() { return getToken(Rpg3Parser.CS_Operation_DOWNE, 0); }
		public TerminalNode CS_Operation_DOWLE() { return getToken(Rpg3Parser.CS_Operation_DOWLE, 0); }
		public TerminalNode CS_Operation_DOWLT() { return getToken(Rpg3Parser.CS_Operation_DOWLT, 0); }
		public TerminalNode CS_Operation_DOWGE() { return getToken(Rpg3Parser.CS_Operation_DOWGE, 0); }
		public TerminalNode CS_Operation_DOWGT() { return getToken(Rpg3Parser.CS_Operation_DOWGT, 0); }
		public List<CspecANDxxContext> cspecANDxx() {
			return getRuleContexts(CspecANDxxContext.class);
		}
		public CspecANDxxContext cspecANDxx(int i) {
			return getRuleContext(CspecANDxxContext.class,i);
		}
		public List<CspecORxxContext> cspecORxx() {
			return getRuleContexts(CspecORxxContext.class);
		}
		public CspecORxxContext cspecORxx(int i) {
			return getRuleContext(CspecORxxContext.class,i);
		}
		public CspecDOWxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecDOWxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecDOWxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecDOWxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecDOWxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecDOWxxContext cspecDOWxx() throws RecognitionException {
		CspecDOWxxContext _localctx = new CspecDOWxxContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_cspecDOWxx);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			cspecPrefix();
			setState(317);
			_la = _input.LA(1);
			if ( !(((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(318);
			cspecSuffix();
			setState(323);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(321);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						setState(319);
						cspecANDxx();
						}
						break;
					case 2:
						{
						setState(320);
						cspecORxx();
						}
						break;
					}
					} 
				}
				setState(325);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecDOUxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_DOUEQ() { return getToken(Rpg3Parser.CS_Operation_DOUEQ, 0); }
		public TerminalNode CS_Operation_DOUNE() { return getToken(Rpg3Parser.CS_Operation_DOUNE, 0); }
		public TerminalNode CS_Operation_DOULE() { return getToken(Rpg3Parser.CS_Operation_DOULE, 0); }
		public TerminalNode CS_Operation_DOULT() { return getToken(Rpg3Parser.CS_Operation_DOULT, 0); }
		public TerminalNode CS_Operation_DOUGE() { return getToken(Rpg3Parser.CS_Operation_DOUGE, 0); }
		public TerminalNode CS_Operation_DOUGT() { return getToken(Rpg3Parser.CS_Operation_DOUGT, 0); }
		public List<CspecANDxxContext> cspecANDxx() {
			return getRuleContexts(CspecANDxxContext.class);
		}
		public CspecANDxxContext cspecANDxx(int i) {
			return getRuleContext(CspecANDxxContext.class,i);
		}
		public List<CspecORxxContext> cspecORxx() {
			return getRuleContexts(CspecORxxContext.class);
		}
		public CspecORxxContext cspecORxx(int i) {
			return getRuleContext(CspecORxxContext.class,i);
		}
		public CspecDOUxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecDOUxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecDOUxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecDOUxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecDOUxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecDOUxxContext cspecDOUxx() throws RecognitionException {
		CspecDOUxxContext _localctx = new CspecDOUxxContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_cspecDOUxx);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			cspecPrefix();
			setState(327);
			_la = _input.LA(1);
			if ( !(((((_la - 90)) & ~0x3f) == 0 && ((1L << (_la - 90)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(328);
			cspecSuffix();
			setState(333);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(331);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
					case 1:
						{
						setState(329);
						cspecANDxx();
						}
						break;
					case 2:
						{
						setState(330);
						cspecORxx();
						}
						break;
					}
					} 
				}
				setState(335);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecENDDOContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_ENDDO() { return getToken(Rpg3Parser.CS_Operation_ENDDO, 0); }
		public TerminalNode CS_Operation_END() { return getToken(Rpg3Parser.CS_Operation_END, 0); }
		public CspecENDDOContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecENDDO; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecENDDO(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecENDDO(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecENDDO(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecENDDOContext cspecENDDO() throws RecognitionException {
		CspecENDDOContext _localctx = new CspecENDDOContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_cspecENDDO);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			cspecPrefix();
			setState(337);
			_la = _input.LA(1);
			if ( !(_la==CS_Operation_ENDDO || _la==CS_Operation_END) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(338);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecCASxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_CASEQ() { return getToken(Rpg3Parser.CS_Operation_CASEQ, 0); }
		public TerminalNode CS_Operation_CASNE() { return getToken(Rpg3Parser.CS_Operation_CASNE, 0); }
		public TerminalNode CS_Operation_CASLE() { return getToken(Rpg3Parser.CS_Operation_CASLE, 0); }
		public TerminalNode CS_Operation_CASLT() { return getToken(Rpg3Parser.CS_Operation_CASLT, 0); }
		public TerminalNode CS_Operation_CASGE() { return getToken(Rpg3Parser.CS_Operation_CASGE, 0); }
		public TerminalNode CS_Operation_CASGT() { return getToken(Rpg3Parser.CS_Operation_CASGT, 0); }
		public CspecCASxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecCASxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecCASxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecCASxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecCASxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecCASxxContext cspecCASxx() throws RecognitionException {
		CspecCASxxContext _localctx = new CspecCASxxContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_cspecCASxx);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			cspecPrefix();
			setState(341);
			_la = _input.LA(1);
			if ( !(((((_la - 108)) & ~0x3f) == 0 && ((1L << (_la - 108)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(342);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecCASContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public TerminalNode CS_Operation_CAS() { return getToken(Rpg3Parser.CS_Operation_CAS, 0); }
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CspecCASContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecCAS; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecCAS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecCAS(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecCAS(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecCASContext cspecCAS() throws RecognitionException {
		CspecCASContext _localctx = new CspecCASContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_cspecCAS);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			cspecPrefix();
			setState(345);
			match(CS_Operation_CAS);
			setState(346);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecENDCSContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_ENDCS() { return getToken(Rpg3Parser.CS_Operation_ENDCS, 0); }
		public TerminalNode CS_Operation_END() { return getToken(Rpg3Parser.CS_Operation_END, 0); }
		public CspecENDCSContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecENDCS; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecENDCS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecENDCS(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecENDCS(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecENDCSContext cspecENDCS() throws RecognitionException {
		CspecENDCSContext _localctx = new CspecENDCSContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_cspecENDCS);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			cspecPrefix();
			setState(349);
			_la = _input.LA(1);
			if ( !(_la==CS_Operation_ENDCS || _la==CS_Operation_END) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(350);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecANDxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_ANDEQ() { return getToken(Rpg3Parser.CS_Operation_ANDEQ, 0); }
		public TerminalNode CS_Operation_ANDNE() { return getToken(Rpg3Parser.CS_Operation_ANDNE, 0); }
		public TerminalNode CS_Operation_ANDLE() { return getToken(Rpg3Parser.CS_Operation_ANDLE, 0); }
		public TerminalNode CS_Operation_ANDLT() { return getToken(Rpg3Parser.CS_Operation_ANDLT, 0); }
		public TerminalNode CS_Operation_ANDGE() { return getToken(Rpg3Parser.CS_Operation_ANDGE, 0); }
		public TerminalNode CS_Operation_ANDGT() { return getToken(Rpg3Parser.CS_Operation_ANDGT, 0); }
		public CspecANDxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecANDxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecANDxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecANDxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecANDxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecANDxxContext cspecANDxx() throws RecognitionException {
		CspecANDxxContext _localctx = new CspecANDxxContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_cspecANDxx);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			cspecPrefix();
			setState(353);
			_la = _input.LA(1);
			if ( !(((((_la - 96)) & ~0x3f) == 0 && ((1L << (_la - 96)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(354);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecORxxContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public TerminalNode CS_Operation_OREQ() { return getToken(Rpg3Parser.CS_Operation_OREQ, 0); }
		public TerminalNode CS_Operation_ORNE() { return getToken(Rpg3Parser.CS_Operation_ORNE, 0); }
		public TerminalNode CS_Operation_ORLE() { return getToken(Rpg3Parser.CS_Operation_ORLE, 0); }
		public TerminalNode CS_Operation_ORLT() { return getToken(Rpg3Parser.CS_Operation_ORLT, 0); }
		public TerminalNode CS_Operation_ORGE() { return getToken(Rpg3Parser.CS_Operation_ORGE, 0); }
		public TerminalNode CS_Operation_ORGT() { return getToken(Rpg3Parser.CS_Operation_ORGT, 0); }
		public CspecORxxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecORxx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecORxx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecORxx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecORxx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecORxxContext cspecORxx() throws RecognitionException {
		CspecORxxContext _localctx = new CspecORxxContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_cspecORxx);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
			cspecPrefix();
			setState(357);
			_la = _input.LA(1);
			if ( !(((((_la - 102)) & ~0x3f) == 0 && ((1L << (_la - 102)) & 63L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(358);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecBEGSRContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public TerminalNode CS_Operation_BEGSR() { return getToken(Rpg3Parser.CS_Operation_BEGSR, 0); }
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CspecBEGSRContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecBEGSR; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecBEGSR(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecBEGSR(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecBEGSR(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecBEGSRContext cspecBEGSR() throws RecognitionException {
		CspecBEGSRContext _localctx = new CspecBEGSRContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_cspecBEGSR);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			cspecPrefix();
			setState(361);
			match(CS_Operation_BEGSR);
			setState(362);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CspecENDSRContext extends ParserRuleContext {
		public CspecPrefixContext cspecPrefix() {
			return getRuleContext(CspecPrefixContext.class,0);
		}
		public TerminalNode CS_Operation_ENDSR() { return getToken(Rpg3Parser.CS_Operation_ENDSR, 0); }
		public CspecSuffixContext cspecSuffix() {
			return getRuleContext(CspecSuffixContext.class,0);
		}
		public CspecENDSRContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cspecENDSR; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCspecENDSR(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCspecENDSR(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCspecENDSR(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CspecENDSRContext cspecENDSR() throws RecognitionException {
		CspecENDSRContext _localctx = new CspecENDSRContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_cspecENDSR);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			cspecPrefix();
			setState(365);
			match(CS_Operation_ENDSR);
			setState(366);
			cspecSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CsOperationContext extends ParserRuleContext {
		public TerminalNode CS_Operation_IFEQ() { return getToken(Rpg3Parser.CS_Operation_IFEQ, 0); }
		public TerminalNode CS_Operation_IFNE() { return getToken(Rpg3Parser.CS_Operation_IFNE, 0); }
		public TerminalNode CS_Operation_IFLE() { return getToken(Rpg3Parser.CS_Operation_IFLE, 0); }
		public TerminalNode CS_Operation_IFLT() { return getToken(Rpg3Parser.CS_Operation_IFLT, 0); }
		public TerminalNode CS_Operation_IFGE() { return getToken(Rpg3Parser.CS_Operation_IFGE, 0); }
		public TerminalNode CS_Operation_IFGT() { return getToken(Rpg3Parser.CS_Operation_IFGT, 0); }
		public TerminalNode CS_Operation_DOWEQ() { return getToken(Rpg3Parser.CS_Operation_DOWEQ, 0); }
		public TerminalNode CS_Operation_DOWNE() { return getToken(Rpg3Parser.CS_Operation_DOWNE, 0); }
		public TerminalNode CS_Operation_DOWLE() { return getToken(Rpg3Parser.CS_Operation_DOWLE, 0); }
		public TerminalNode CS_Operation_DOWLT() { return getToken(Rpg3Parser.CS_Operation_DOWLT, 0); }
		public TerminalNode CS_Operation_DOWGE() { return getToken(Rpg3Parser.CS_Operation_DOWGE, 0); }
		public TerminalNode CS_Operation_DOWGT() { return getToken(Rpg3Parser.CS_Operation_DOWGT, 0); }
		public TerminalNode CS_Operation_DOUEQ() { return getToken(Rpg3Parser.CS_Operation_DOUEQ, 0); }
		public TerminalNode CS_Operation_DOUNE() { return getToken(Rpg3Parser.CS_Operation_DOUNE, 0); }
		public TerminalNode CS_Operation_DOULE() { return getToken(Rpg3Parser.CS_Operation_DOULE, 0); }
		public TerminalNode CS_Operation_DOULT() { return getToken(Rpg3Parser.CS_Operation_DOULT, 0); }
		public TerminalNode CS_Operation_DOUGE() { return getToken(Rpg3Parser.CS_Operation_DOUGE, 0); }
		public TerminalNode CS_Operation_DOUGT() { return getToken(Rpg3Parser.CS_Operation_DOUGT, 0); }
		public TerminalNode CS_Operation_ANDEQ() { return getToken(Rpg3Parser.CS_Operation_ANDEQ, 0); }
		public TerminalNode CS_Operation_ANDNE() { return getToken(Rpg3Parser.CS_Operation_ANDNE, 0); }
		public TerminalNode CS_Operation_ANDLE() { return getToken(Rpg3Parser.CS_Operation_ANDLE, 0); }
		public TerminalNode CS_Operation_ANDLT() { return getToken(Rpg3Parser.CS_Operation_ANDLT, 0); }
		public TerminalNode CS_Operation_ANDGE() { return getToken(Rpg3Parser.CS_Operation_ANDGE, 0); }
		public TerminalNode CS_Operation_ANDGT() { return getToken(Rpg3Parser.CS_Operation_ANDGT, 0); }
		public TerminalNode CS_Operation_OREQ() { return getToken(Rpg3Parser.CS_Operation_OREQ, 0); }
		public TerminalNode CS_Operation_ORNE() { return getToken(Rpg3Parser.CS_Operation_ORNE, 0); }
		public TerminalNode CS_Operation_ORLE() { return getToken(Rpg3Parser.CS_Operation_ORLE, 0); }
		public TerminalNode CS_Operation_ORLT() { return getToken(Rpg3Parser.CS_Operation_ORLT, 0); }
		public TerminalNode CS_Operation_ORGE() { return getToken(Rpg3Parser.CS_Operation_ORGE, 0); }
		public TerminalNode CS_Operation_ORGT() { return getToken(Rpg3Parser.CS_Operation_ORGT, 0); }
		public TerminalNode CS_Operation_CASEQ() { return getToken(Rpg3Parser.CS_Operation_CASEQ, 0); }
		public TerminalNode CS_Operation_CASNE() { return getToken(Rpg3Parser.CS_Operation_CASNE, 0); }
		public TerminalNode CS_Operation_CASLE() { return getToken(Rpg3Parser.CS_Operation_CASLE, 0); }
		public TerminalNode CS_Operation_CASLT() { return getToken(Rpg3Parser.CS_Operation_CASLT, 0); }
		public TerminalNode CS_Operation_CASGE() { return getToken(Rpg3Parser.CS_Operation_CASGE, 0); }
		public TerminalNode CS_Operation_CASGT() { return getToken(Rpg3Parser.CS_Operation_CASGT, 0); }
		public TerminalNode CS_Operation_CAS() { return getToken(Rpg3Parser.CS_Operation_CAS, 0); }
		public TerminalNode CS_Operation_CABEQ() { return getToken(Rpg3Parser.CS_Operation_CABEQ, 0); }
		public TerminalNode CS_Operation_CABNE() { return getToken(Rpg3Parser.CS_Operation_CABNE, 0); }
		public TerminalNode CS_Operation_CABLE() { return getToken(Rpg3Parser.CS_Operation_CABLE, 0); }
		public TerminalNode CS_Operation_CABLT() { return getToken(Rpg3Parser.CS_Operation_CABLT, 0); }
		public TerminalNode CS_Operation_CABGE() { return getToken(Rpg3Parser.CS_Operation_CABGE, 0); }
		public TerminalNode CS_Operation_CABGT() { return getToken(Rpg3Parser.CS_Operation_CABGT, 0); }
		public TerminalNode CS_Operation_MOVEL() { return getToken(Rpg3Parser.CS_Operation_MOVEL, 0); }
		public TerminalNode CS_Operation_MOVEA() { return getToken(Rpg3Parser.CS_Operation_MOVEA, 0); }
		public TerminalNode CS_Operation_MOVE() { return getToken(Rpg3Parser.CS_Operation_MOVE, 0); }
		public TerminalNode CS_Operation_Z_ADD() { return getToken(Rpg3Parser.CS_Operation_Z_ADD, 0); }
		public TerminalNode CS_Operation_Z_SUB() { return getToken(Rpg3Parser.CS_Operation_Z_SUB, 0); }
		public TerminalNode CS_Operation_ADD() { return getToken(Rpg3Parser.CS_Operation_ADD, 0); }
		public TerminalNode CS_Operation_SUB() { return getToken(Rpg3Parser.CS_Operation_SUB, 0); }
		public TerminalNode CS_Operation_MULT() { return getToken(Rpg3Parser.CS_Operation_MULT, 0); }
		public TerminalNode CS_Operation_DIV() { return getToken(Rpg3Parser.CS_Operation_DIV, 0); }
		public TerminalNode CS_Operation_MVR() { return getToken(Rpg3Parser.CS_Operation_MVR, 0); }
		public TerminalNode CS_Operation_SQRT() { return getToken(Rpg3Parser.CS_Operation_SQRT, 0); }
		public TerminalNode CS_Operation_XFOOT() { return getToken(Rpg3Parser.CS_Operation_XFOOT, 0); }
		public TerminalNode CS_Operation_CHAIN() { return getToken(Rpg3Parser.CS_Operation_CHAIN, 0); }
		public TerminalNode CS_Operation_READ() { return getToken(Rpg3Parser.CS_Operation_READ, 0); }
		public TerminalNode CS_Operation_READC() { return getToken(Rpg3Parser.CS_Operation_READC, 0); }
		public TerminalNode CS_Operation_READE() { return getToken(Rpg3Parser.CS_Operation_READE, 0); }
		public TerminalNode CS_Operation_READP() { return getToken(Rpg3Parser.CS_Operation_READP, 0); }
		public TerminalNode CS_Operation_REDPE() { return getToken(Rpg3Parser.CS_Operation_REDPE, 0); }
		public TerminalNode CS_Operation_WRITE() { return getToken(Rpg3Parser.CS_Operation_WRITE, 0); }
		public TerminalNode CS_Operation_UPDAT() { return getToken(Rpg3Parser.CS_Operation_UPDAT, 0); }
		public TerminalNode CS_Operation_DELET() { return getToken(Rpg3Parser.CS_Operation_DELET, 0); }
		public TerminalNode CS_Operation_SETLL() { return getToken(Rpg3Parser.CS_Operation_SETLL, 0); }
		public TerminalNode CS_Operation_SETGT() { return getToken(Rpg3Parser.CS_Operation_SETGT, 0); }
		public TerminalNode CS_Operation_OPEN() { return getToken(Rpg3Parser.CS_Operation_OPEN, 0); }
		public TerminalNode CS_Operation_CLOSE() { return getToken(Rpg3Parser.CS_Operation_CLOSE, 0); }
		public TerminalNode CS_Operation_FORCE() { return getToken(Rpg3Parser.CS_Operation_FORCE, 0); }
		public TerminalNode CS_Operation_FEOD() { return getToken(Rpg3Parser.CS_Operation_FEOD, 0); }
		public TerminalNode CS_Operation_NEXT() { return getToken(Rpg3Parser.CS_Operation_NEXT, 0); }
		public TerminalNode CS_Operation_UNLCK() { return getToken(Rpg3Parser.CS_Operation_UNLCK, 0); }
		public TerminalNode CS_Operation_ACQ() { return getToken(Rpg3Parser.CS_Operation_ACQ, 0); }
		public TerminalNode CS_Operation_REL() { return getToken(Rpg3Parser.CS_Operation_REL, 0); }
		public TerminalNode CS_Operation_POST() { return getToken(Rpg3Parser.CS_Operation_POST, 0); }
		public TerminalNode CS_Operation_COMIT() { return getToken(Rpg3Parser.CS_Operation_COMIT, 0); }
		public TerminalNode CS_Operation_ROLBK() { return getToken(Rpg3Parser.CS_Operation_ROLBK, 0); }
		public TerminalNode CS_Operation_GOTO() { return getToken(Rpg3Parser.CS_Operation_GOTO, 0); }
		public TerminalNode CS_Operation_TAG() { return getToken(Rpg3Parser.CS_Operation_TAG, 0); }
		public TerminalNode CS_Operation_BEGSR() { return getToken(Rpg3Parser.CS_Operation_BEGSR, 0); }
		public TerminalNode CS_Operation_ENDSR() { return getToken(Rpg3Parser.CS_Operation_ENDSR, 0); }
		public TerminalNode CS_Operation_EXSR() { return getToken(Rpg3Parser.CS_Operation_EXSR, 0); }
		public TerminalNode CS_Operation_ENDIF() { return getToken(Rpg3Parser.CS_Operation_ENDIF, 0); }
		public TerminalNode CS_Operation_ENDDO() { return getToken(Rpg3Parser.CS_Operation_ENDDO, 0); }
		public TerminalNode CS_Operation_ENDCS() { return getToken(Rpg3Parser.CS_Operation_ENDCS, 0); }
		public TerminalNode CS_Operation_END() { return getToken(Rpg3Parser.CS_Operation_END, 0); }
		public TerminalNode CS_Operation_ELSE() { return getToken(Rpg3Parser.CS_Operation_ELSE, 0); }
		public TerminalNode CS_Operation_DO() { return getToken(Rpg3Parser.CS_Operation_DO, 0); }
		public TerminalNode CS_Operation_LEAVE() { return getToken(Rpg3Parser.CS_Operation_LEAVE, 0); }
		public TerminalNode CS_Operation_ITER() { return getToken(Rpg3Parser.CS_Operation_ITER, 0); }
		public TerminalNode CS_Operation_CALL() { return getToken(Rpg3Parser.CS_Operation_CALL, 0); }
		public TerminalNode CS_Operation_PARM() { return getToken(Rpg3Parser.CS_Operation_PARM, 0); }
		public TerminalNode CS_Operation_PLIST() { return getToken(Rpg3Parser.CS_Operation_PLIST, 0); }
		public TerminalNode CS_Operation_KLIST() { return getToken(Rpg3Parser.CS_Operation_KLIST, 0); }
		public TerminalNode CS_Operation_KFLD() { return getToken(Rpg3Parser.CS_Operation_KFLD, 0); }
		public TerminalNode CS_Operation_EXCPT() { return getToken(Rpg3Parser.CS_Operation_EXCPT, 0); }
		public TerminalNode CS_Operation_EXFMT() { return getToken(Rpg3Parser.CS_Operation_EXFMT, 0); }
		public TerminalNode CS_Operation_DSPLY() { return getToken(Rpg3Parser.CS_Operation_DSPLY, 0); }
		public TerminalNode CS_Operation_SETON() { return getToken(Rpg3Parser.CS_Operation_SETON, 0); }
		public TerminalNode CS_Operation_SETOF() { return getToken(Rpg3Parser.CS_Operation_SETOF, 0); }
		public TerminalNode CS_Operation_CAT() { return getToken(Rpg3Parser.CS_Operation_CAT, 0); }
		public TerminalNode CS_Operation_SCAN() { return getToken(Rpg3Parser.CS_Operation_SCAN, 0); }
		public TerminalNode CS_Operation_SUBST() { return getToken(Rpg3Parser.CS_Operation_SUBST, 0); }
		public TerminalNode CS_Operation_XLATE() { return getToken(Rpg3Parser.CS_Operation_XLATE, 0); }
		public TerminalNode CS_Operation_CHECK() { return getToken(Rpg3Parser.CS_Operation_CHECK, 0); }
		public TerminalNode CS_Operation_COMP() { return getToken(Rpg3Parser.CS_Operation_COMP, 0); }
		public TerminalNode CS_Operation_LOKUP() { return getToken(Rpg3Parser.CS_Operation_LOKUP, 0); }
		public TerminalNode CS_Operation_SORTA() { return getToken(Rpg3Parser.CS_Operation_SORTA, 0); }
		public TerminalNode CS_Operation_TIME() { return getToken(Rpg3Parser.CS_Operation_TIME, 0); }
		public TerminalNode CS_Operation_OCCUR() { return getToken(Rpg3Parser.CS_Operation_OCCUR, 0); }
		public TerminalNode CS_Operation_TESTB() { return getToken(Rpg3Parser.CS_Operation_TESTB, 0); }
		public TerminalNode CS_Operation_TESTN() { return getToken(Rpg3Parser.CS_Operation_TESTN, 0); }
		public TerminalNode CS_Operation_TESTZ() { return getToken(Rpg3Parser.CS_Operation_TESTZ, 0); }
		public TerminalNode CS_Operation_BITON() { return getToken(Rpg3Parser.CS_Operation_BITON, 0); }
		public TerminalNode CS_Operation_BITOF() { return getToken(Rpg3Parser.CS_Operation_BITOF, 0); }
		public TerminalNode CS_Operation_MHHZO() { return getToken(Rpg3Parser.CS_Operation_MHHZO, 0); }
		public TerminalNode CS_Operation_MHLZO() { return getToken(Rpg3Parser.CS_Operation_MHLZO, 0); }
		public TerminalNode CS_Operation_MLHZO() { return getToken(Rpg3Parser.CS_Operation_MLHZO, 0); }
		public TerminalNode CS_Operation_MLLZO() { return getToken(Rpg3Parser.CS_Operation_MLLZO, 0); }
		public TerminalNode CS_Operation_CLEAR() { return getToken(Rpg3Parser.CS_Operation_CLEAR, 0); }
		public TerminalNode CS_Operation_DUMP() { return getToken(Rpg3Parser.CS_Operation_DUMP, 0); }
		public TerminalNode CS_Operation_SHTDN() { return getToken(Rpg3Parser.CS_Operation_SHTDN, 0); }
		public TerminalNode CS_Operation_DEFN() { return getToken(Rpg3Parser.CS_Operation_DEFN, 0); }
		public TerminalNode CS_Operation_IN() { return getToken(Rpg3Parser.CS_Operation_IN, 0); }
		public TerminalNode CS_Operation_OUT() { return getToken(Rpg3Parser.CS_Operation_OUT, 0); }
		public TerminalNode CS_Operation_RETRN() { return getToken(Rpg3Parser.CS_Operation_RETRN, 0); }
		public TerminalNode CS_Operation_RESET() { return getToken(Rpg3Parser.CS_Operation_RESET, 0); }
		public TerminalNode CS_Operation_Other() { return getToken(Rpg3Parser.CS_Operation_Other, 0); }
		public CsOperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_csOperation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCsOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCsOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCsOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CsOperationContext csOperation() throws RecognitionException {
		CsOperationContext _localctx = new CsOperationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_csOperation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(368);
			_la = _input.LA(1);
			if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & -1L) != 0) || ((((_la - 142)) & ~0x3f) == 0 && ((1L << (_la - 142)) & -1L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OnOffFlagContext extends ParserRuleContext {
		public TerminalNode BlankFlag() { return getToken(Rpg3Parser.BlankFlag, 0); }
		public TerminalNode NoFlag() { return getToken(Rpg3Parser.NoFlag, 0); }
		public OnOffFlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onOffFlag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterOnOffFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitOnOffFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitOnOffFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnOffFlagContext onOffFlag() throws RecognitionException {
		OnOffFlagContext _localctx = new OnOffFlagContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_onOffFlag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(370);
			_la = _input.LA(1);
			if ( !(_la==BlankFlag || _la==NoFlag) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CsIndicatorContext extends ParserRuleContext {
		public TerminalNode BlankIndicator() { return getToken(Rpg3Parser.BlankIndicator, 0); }
		public TerminalNode GeneralIndicator() { return getToken(Rpg3Parser.GeneralIndicator, 0); }
		public TerminalNode FunctionKeyIndicator() { return getToken(Rpg3Parser.FunctionKeyIndicator, 0); }
		public TerminalNode ControlLevelIndicator() { return getToken(Rpg3Parser.ControlLevelIndicator, 0); }
		public TerminalNode ControlLevel0Indicator() { return getToken(Rpg3Parser.ControlLevel0Indicator, 0); }
		public TerminalNode LastRecordIndicator() { return getToken(Rpg3Parser.LastRecordIndicator, 0); }
		public TerminalNode MatchingRecordIndicator() { return getToken(Rpg3Parser.MatchingRecordIndicator, 0); }
		public TerminalNode HaltIndicator() { return getToken(Rpg3Parser.HaltIndicator, 0); }
		public TerminalNode ReturnIndicator() { return getToken(Rpg3Parser.ReturnIndicator, 0); }
		public TerminalNode ExternalIndicator() { return getToken(Rpg3Parser.ExternalIndicator, 0); }
		public TerminalNode OverflowIndicator() { return getToken(Rpg3Parser.OverflowIndicator, 0); }
		public TerminalNode SubroutineIndicator() { return getToken(Rpg3Parser.SubroutineIndicator, 0); }
		public TerminalNode AndIndicator() { return getToken(Rpg3Parser.AndIndicator, 0); }
		public TerminalNode OrIndicator() { return getToken(Rpg3Parser.OrIndicator, 0); }
		public TerminalNode FirstPageIndicator() { return getToken(Rpg3Parser.FirstPageIndicator, 0); }
		public TerminalNode OtherIndicator() { return getToken(Rpg3Parser.OtherIndicator, 0); }
		public CsIndicatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_csIndicator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCsIndicator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCsIndicator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCsIndicator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CsIndicatorContext csIndicator() throws RecognitionException {
		CsIndicatorContext _localctx = new CsIndicatorContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_csIndicator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			_la = _input.LA(1);
			if ( !(((((_la - 232)) & ~0x3f) == 0 && ((1L << (_la - 232)) & 65535L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CsResultIndicatorsContext extends ParserRuleContext {
		public TerminalNode CS_ResultInd1() { return getToken(Rpg3Parser.CS_ResultInd1, 0); }
		public TerminalNode CS_ResultInd2() { return getToken(Rpg3Parser.CS_ResultInd2, 0); }
		public TerminalNode CS_ResultInd3() { return getToken(Rpg3Parser.CS_ResultInd3, 0); }
		public CsResultIndicatorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_csResultIndicators; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterCsResultIndicators(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitCsResultIndicators(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitCsResultIndicators(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CsResultIndicatorsContext csResultIndicators() throws RecognitionException {
		CsResultIndicatorsContext _localctx = new CsResultIndicatorsContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_csResultIndicators);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(374);
			match(CS_ResultInd1);
			setState(375);
			match(CS_ResultInd2);
			setState(376);
			match(CS_ResultInd3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputSpecContext extends ParserRuleContext {
		public OutputRecordSpecContext outputRecordSpec() {
			return getRuleContext(OutputRecordSpecContext.class,0);
		}
		public OutputFieldSpecContext outputFieldSpec() {
			return getRuleContext(OutputFieldSpecContext.class,0);
		}
		public OutputSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterOutputSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitOutputSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitOutputSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputSpecContext outputSpec() throws RecognitionException {
		OutputSpecContext _localctx = new OutputSpecContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_outputSpec);
		try {
			setState(380);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(378);
				outputRecordSpec();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(379);
				outputFieldSpec();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputRecordSpecContext extends ParserRuleContext {
		public TerminalNode OS_FIXED() { return getToken(Rpg3Parser.OS_FIXED, 0); }
		public TerminalNode OS_RecordName() { return getToken(Rpg3Parser.OS_RecordName, 0); }
		public TerminalNode OS_Type() { return getToken(Rpg3Parser.OS_Type, 0); }
		public TerminalNode OS_AddDelete() { return getToken(Rpg3Parser.OS_AddDelete, 0); }
		public TerminalNode OS_SpaceBefore() { return getToken(Rpg3Parser.OS_SpaceBefore, 0); }
		public TerminalNode OS_SpaceAfter() { return getToken(Rpg3Parser.OS_SpaceAfter, 0); }
		public TerminalNode OS_SkipBefore() { return getToken(Rpg3Parser.OS_SkipBefore, 0); }
		public TerminalNode OS_SkipAfter() { return getToken(Rpg3Parser.OS_SkipAfter, 0); }
		public OutputCondIndicatorsContext outputCondIndicators() {
			return getRuleContext(OutputCondIndicatorsContext.class,0);
		}
		public TerminalNode OS_ExceptName() { return getToken(Rpg3Parser.OS_ExceptName, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public TerminalNode OS_Remaining() { return getToken(Rpg3Parser.OS_Remaining, 0); }
		public OutputRecordSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputRecordSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterOutputRecordSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitOutputRecordSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitOutputRecordSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputRecordSpecContext outputRecordSpec() throws RecognitionException {
		OutputRecordSpecContext _localctx = new OutputRecordSpecContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_outputRecordSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(382);
			match(OS_FIXED);
			setState(383);
			match(OS_RecordName);
			setState(384);
			match(OS_Type);
			setState(385);
			match(OS_AddDelete);
			setState(386);
			match(OS_SpaceBefore);
			setState(387);
			match(OS_SpaceAfter);
			setState(388);
			match(OS_SkipBefore);
			setState(389);
			match(OS_SkipAfter);
			setState(390);
			outputCondIndicators();
			setState(391);
			match(OS_ExceptName);
			setState(393);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_Remaining) {
				{
				setState(392);
				match(OS_Remaining);
				}
			}

			setState(395);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputFieldSpecContext extends ParserRuleContext {
		public TerminalNode OS_FIXED() { return getToken(Rpg3Parser.OS_FIXED, 0); }
		public TerminalNode OS_RecordName() { return getToken(Rpg3Parser.OS_RecordName, 0); }
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public TerminalNode OS_Type() { return getToken(Rpg3Parser.OS_Type, 0); }
		public TerminalNode OS_AddDelete() { return getToken(Rpg3Parser.OS_AddDelete, 0); }
		public TerminalNode OS_SpaceBefore() { return getToken(Rpg3Parser.OS_SpaceBefore, 0); }
		public TerminalNode OS_SpaceAfter() { return getToken(Rpg3Parser.OS_SpaceAfter, 0); }
		public TerminalNode OS_SkipBefore() { return getToken(Rpg3Parser.OS_SkipBefore, 0); }
		public TerminalNode OS_SkipAfter() { return getToken(Rpg3Parser.OS_SkipAfter, 0); }
		public OutputCondIndicatorsContext outputCondIndicators() {
			return getRuleContext(OutputCondIndicatorsContext.class,0);
		}
		public TerminalNode OS_ExceptName() { return getToken(Rpg3Parser.OS_ExceptName, 0); }
		public TerminalNode OS_Remaining() { return getToken(Rpg3Parser.OS_Remaining, 0); }
		public OutputFieldSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputFieldSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterOutputFieldSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitOutputFieldSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitOutputFieldSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputFieldSpecContext outputFieldSpec() throws RecognitionException {
		OutputFieldSpecContext _localctx = new OutputFieldSpecContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_outputFieldSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			match(OS_FIXED);
			setState(398);
			match(OS_RecordName);
			setState(400);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_Type) {
				{
				setState(399);
				match(OS_Type);
				}
			}

			setState(403);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_AddDelete) {
				{
				setState(402);
				match(OS_AddDelete);
				}
			}

			setState(406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_SpaceBefore) {
				{
				setState(405);
				match(OS_SpaceBefore);
				}
			}

			setState(409);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_SpaceAfter) {
				{
				setState(408);
				match(OS_SpaceAfter);
				}
			}

			setState(412);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_SkipBefore) {
				{
				setState(411);
				match(OS_SkipBefore);
				}
			}

			setState(415);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_SkipAfter) {
				{
				setState(414);
				match(OS_SkipAfter);
				}
			}

			setState(418);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_CondInd1_Flag) {
				{
				setState(417);
				outputCondIndicators();
				}
			}

			setState(421);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_ExceptName) {
				{
				setState(420);
				match(OS_ExceptName);
				}
			}

			setState(424);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OS_Remaining) {
				{
				setState(423);
				match(OS_Remaining);
				}
			}

			setState(426);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OutputCondIndicatorsContext extends ParserRuleContext {
		public TerminalNode OS_CondInd1_Flag() { return getToken(Rpg3Parser.OS_CondInd1_Flag, 0); }
		public TerminalNode OS_CondInd1() { return getToken(Rpg3Parser.OS_CondInd1, 0); }
		public TerminalNode OS_CondInd2_Flag() { return getToken(Rpg3Parser.OS_CondInd2_Flag, 0); }
		public TerminalNode OS_CondInd2() { return getToken(Rpg3Parser.OS_CondInd2, 0); }
		public TerminalNode OS_CondInd3_Flag() { return getToken(Rpg3Parser.OS_CondInd3_Flag, 0); }
		public TerminalNode OS_CondInd3() { return getToken(Rpg3Parser.OS_CondInd3, 0); }
		public OutputCondIndicatorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputCondIndicators; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterOutputCondIndicators(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitOutputCondIndicators(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitOutputCondIndicators(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputCondIndicatorsContext outputCondIndicators() throws RecognitionException {
		OutputCondIndicatorsContext _localctx = new OutputCondIndicatorsContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_outputCondIndicators);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			match(OS_CondInd1_Flag);
			setState(429);
			match(OS_CondInd1);
			setState(430);
			match(OS_CondInd2_Flag);
			setState(431);
			match(OS_CondInd2);
			setState(432);
			match(OS_CondInd3_Flag);
			setState(433);
			match(OS_CondInd3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EndSourceContext extends ParserRuleContext {
		public TerminalNode END_SOURCE() { return getToken(Rpg3Parser.END_SOURCE, 0); }
		public List<EndSourceLineContext> endSourceLine() {
			return getRuleContexts(EndSourceLineContext.class);
		}
		public EndSourceLineContext endSourceLine(int i) {
			return getRuleContext(EndSourceLineContext.class,i);
		}
		public EndSourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endSource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterEndSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitEndSource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitEndSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndSourceContext endSource() throws RecognitionException {
		EndSourceContext _localctx = new EndSourceContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_endSource);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435);
			match(END_SOURCE);
			setState(439);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EOS_Text || _la==EOL) {
				{
				{
				setState(436);
				endSourceLine();
				}
				}
				setState(441);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EndSourceLineContext extends ParserRuleContext {
		public TerminalNode EOL() { return getToken(Rpg3Parser.EOL, 0); }
		public TerminalNode EOS_Text() { return getToken(Rpg3Parser.EOS_Text, 0); }
		public EndSourceLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endSourceLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).enterEndSourceLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Rpg3ParserListener ) ((Rpg3ParserListener)listener).exitEndSourceLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Rpg3ParserVisitor ) return ((Rpg3ParserVisitor<? extends T>)visitor).visitEndSourceLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndSourceLineContext endSourceLine() throws RecognitionException {
		EndSourceLineContext _localctx = new EndSourceLineContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_endSourceLine);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOS_Text) {
				{
				setState(442);
				match(EOS_Text);
				}
			}

			setState(445);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u00f9\u01c0\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007"+
		"\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007"+
		"\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007"+
		"\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0001\u0000\u0005\u0000X"+
		"\b\u0000\n\u0000\f\u0000[\t\u0000\u0001\u0000\u0005\u0000^\b\u0000\n\u0000"+
		"\f\u0000a\t\u0000\u0001\u0000\u0005\u0000d\b\u0000\n\u0000\f\u0000g\t"+
		"\u0000\u0001\u0000\u0005\u0000j\b\u0000\n\u0000\f\u0000m\t\u0000\u0001"+
		"\u0000\u0005\u0000p\b\u0000\n\u0000\f\u0000s\t\u0000\u0001\u0000\u0005"+
		"\u0000v\b\u0000\n\u0000\f\u0000y\t\u0000\u0001\u0000\u0003\u0000|\b\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u0082\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002\u0098\b\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"\u00a7\b\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005\u00b5\b\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0003\u0007\u00c7\b\u0007\u0001\b\u0001\b\u0005\b\u00cb\b"+
		"\b\n\b\f\b\u00ce\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0003\t\u00d9\b\t\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0005\u000b\u00df\b\u000b\n\u000b\f\u000b\u00e2\t\u000b\u0001\u000b"+
		"\u0001\u000b\u0005\u000b\u00e6\b\u000b\n\u000b\f\u000b\u00e9\t\u000b\u0003"+
		"\u000b\u00eb\b\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0005\f\u00f1"+
		"\b\f\n\f\f\f\u00f4\t\f\u0001\f\u0001\f\u0001\r\u0001\r\u0005\r\u00fa\b"+
		"\r\n\r\f\r\u00fd\t\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0005\u000e"+
		"\u0103\b\u000e\n\u000e\f\u000e\u0106\t\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000f\u0004\u000f\u010b\b\u000f\u000b\u000f\f\u000f\u010c\u0001\u000f"+
		"\u0003\u000f\u0110\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u012c\b\u0013"+
		"\n\u0013\f\u0013\u012f\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0005\u0017\u0142\b\u0017\n\u0017\f\u0017\u0145\t\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018"+
		"\u014c\b\u0018\n\u0018\f\u0018\u014f\t\u0018\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001\"\u0001"+
		"\"\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0001%\u0001%\u0003%\u017d"+
		"\b%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001"+
		"&\u0001&\u0003&\u018a\b&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0003\'"+
		"\u0191\b\'\u0001\'\u0003\'\u0194\b\'\u0001\'\u0003\'\u0197\b\'\u0001\'"+
		"\u0003\'\u019a\b\'\u0001\'\u0003\'\u019d\b\'\u0001\'\u0003\'\u01a0\b\'"+
		"\u0001\'\u0003\'\u01a3\b\'\u0001\'\u0003\'\u01a6\b\'\u0001\'\u0003\'\u01a9"+
		"\b\'\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001("+
		"\u0001)\u0001)\u0005)\u01b6\b)\n)\f)\u01b9\t)\u0001*\u0003*\u01bc\b*\u0001"+
		"*\u0001*\u0001*\u0000\u0000+\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPR"+
		"T\u0000\r\u0001\u0000\u0005\u0006\u0001\u0000NS\u0002\u0000\u00a0\u00a0"+
		"\u00a3\u00a3\u0001\u0000TY\u0001\u0000Z_\u0002\u0000\u00a1\u00a1\u00a3"+
		"\u00a3\u0001\u0000lq\u0001\u0000\u00a2\u00a3\u0001\u0000`e\u0001\u0000"+
		"fk\u0001\u0000N\u00cd\u0001\u0000\u00e6\u00e7\u0001\u0000\u00e8\u00f7"+
		"\u01c2\u0000Y\u0001\u0000\u0000\u0000\u0002\u007f\u0001\u0000\u0000\u0000"+
		"\u0004\u0085\u0001\u0000\u0000\u0000\u0006\u009b\u0001\u0000\u0000\u0000"+
		"\b\u00aa\u0001\u0000\u0000\u0000\n\u00b0\u0001\u0000\u0000\u0000\f\u00b8"+
		"\u0001\u0000\u0000\u0000\u000e\u00c6\u0001\u0000\u0000\u0000\u0010\u00c8"+
		"\u0001\u0000\u0000\u0000\u0012\u00d8\u0001\u0000\u0000\u0000\u0014\u00da"+
		"\u0001\u0000\u0000\u0000\u0016\u00dc\u0001\u0000\u0000\u0000\u0018\u00ee"+
		"\u0001\u0000\u0000\u0000\u001a\u00f7\u0001\u0000\u0000\u0000\u001c\u0100"+
		"\u0001\u0000\u0000\u0000\u001e\u010a\u0001\u0000\u0000\u0000 \u0113\u0001"+
		"\u0000\u0000\u0000\"\u011a\u0001\u0000\u0000\u0000$\u0122\u0001\u0000"+
		"\u0000\u0000&\u0126\u0001\u0000\u0000\u0000(\u0130\u0001\u0000\u0000\u0000"+
		"*\u0134\u0001\u0000\u0000\u0000,\u0138\u0001\u0000\u0000\u0000.\u013c"+
		"\u0001\u0000\u0000\u00000\u0146\u0001\u0000\u0000\u00002\u0150\u0001\u0000"+
		"\u0000\u00004\u0154\u0001\u0000\u0000\u00006\u0158\u0001\u0000\u0000\u0000"+
		"8\u015c\u0001\u0000\u0000\u0000:\u0160\u0001\u0000\u0000\u0000<\u0164"+
		"\u0001\u0000\u0000\u0000>\u0168\u0001\u0000\u0000\u0000@\u016c\u0001\u0000"+
		"\u0000\u0000B\u0170\u0001\u0000\u0000\u0000D\u0172\u0001\u0000\u0000\u0000"+
		"F\u0174\u0001\u0000\u0000\u0000H\u0176\u0001\u0000\u0000\u0000J\u017c"+
		"\u0001\u0000\u0000\u0000L\u017e\u0001\u0000\u0000\u0000N\u018d\u0001\u0000"+
		"\u0000\u0000P\u01ac\u0001\u0000\u0000\u0000R\u01b3\u0001\u0000\u0000\u0000"+
		"T\u01bb\u0001\u0000\u0000\u0000VX\u0003\u0002\u0001\u0000WV\u0001\u0000"+
		"\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001"+
		"\u0000\u0000\u0000Z_\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000"+
		"\\^\u0003\u0004\u0002\u0000]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`e\u0001\u0000"+
		"\u0000\u0000a_\u0001\u0000\u0000\u0000bd\u0003\u0006\u0003\u0000cb\u0001"+
		"\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000"+
		"ef\u0001\u0000\u0000\u0000fk\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000"+
		"\u0000hj\u0003\n\u0005\u0000ih\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000"+
		"\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lq\u0001\u0000"+
		"\u0000\u0000mk\u0001\u0000\u0000\u0000np\u0003\u000e\u0007\u0000on\u0001"+
		"\u0000\u0000\u0000ps\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000"+
		"qr\u0001\u0000\u0000\u0000rw\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000"+
		"\u0000tv\u0003J%\u0000ut\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000"+
		"wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x{\u0001\u0000\u0000"+
		"\u0000yw\u0001\u0000\u0000\u0000z|\u0003R)\u0000{z\u0001\u0000\u0000\u0000"+
		"{|\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0005\u0000\u0000"+
		"\u0001~\u0001\u0001\u0000\u0000\u0000\u007f\u0081\u0005\u0007\u0000\u0000"+
		"\u0080\u0082\u0005\u0019\u0000\u0000\u0081\u0080\u0001\u0000\u0000\u0000"+
		"\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000"+
		"\u0083\u0084\u0005\u00f9\u0000\u0000\u0084\u0003\u0001\u0000\u0000\u0000"+
		"\u0085\u0086\u0005\b\u0000\u0000\u0086\u0087\u0005\u001a\u0000\u0000\u0087"+
		"\u0088\u0005\u001b\u0000\u0000\u0088\u0089\u0005\u001c\u0000\u0000\u0089"+
		"\u008a\u0005\u001d\u0000\u0000\u008a\u008b\u0005\u001e\u0000\u0000\u008b"+
		"\u008c\u0005\u001f\u0000\u0000\u008c\u008d\u0005 \u0000\u0000\u008d\u008e"+
		"\u0005!\u0000\u0000\u008e\u008f\u0005\"\u0000\u0000\u008f\u0090\u0005"+
		"#\u0000\u0000\u0090\u0091\u0005%\u0000\u0000\u0091\u0092\u0005&\u0000"+
		"\u0000\u0092\u0093\u0005\'\u0000\u0000\u0093\u0094\u0005(\u0000\u0000"+
		"\u0094\u0095\u0005)\u0000\u0000\u0095\u0097\u0005*\u0000\u0000\u0096\u0098"+
		"\u0005,\u0000\u0000\u0097\u0096\u0001\u0000\u0000\u0000\u0097\u0098\u0001"+
		"\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009a\u0005"+
		"\u00f9\u0000\u0000\u009a\u0005\u0001\u0000\u0000\u0000\u009b\u009c\u0005"+
		"\t\u0000\u0000\u009c\u009d\u0005.\u0000\u0000\u009d\u009e\u0005/\u0000"+
		"\u0000\u009e\u009f\u00050\u0000\u0000\u009f\u00a0\u00051\u0000\u0000\u00a0"+
		"\u00a1\u00052\u0000\u0000\u00a1\u00a2\u00053\u0000\u0000\u00a2\u00a3\u0005"+
		"4\u0000\u0000\u00a3\u00a4\u00055\u0000\u0000\u00a4\u00a6\u00056\u0000"+
		"\u0000\u00a5\u00a7\u0003\b\u0004\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000"+
		"\u00a8\u00a9\u0005\u00f9\u0000\u0000\u00a9\u0007\u0001\u0000\u0000\u0000"+
		"\u00aa\u00ab\u00057\u0000\u0000\u00ab\u00ac\u00058\u0000\u0000\u00ac\u00ad"+
		"\u00059\u0000\u0000\u00ad\u00ae\u0005:\u0000\u0000\u00ae\u00af\u0005;"+
		"\u0000\u0000\u00af\t\u0001\u0000\u0000\u0000\u00b0\u00b1\u0005\n\u0000"+
		"\u0000\u00b1\u00b2\u0005=\u0000\u0000\u00b2\u00b4\u0005>\u0000\u0000\u00b3"+
		"\u00b5\u0003\f\u0006\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b4\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6\u00b7"+
		"\u0005\u00f9\u0000\u0000\u00b7\u000b\u0001\u0000\u0000\u0000\u00b8\u00b9"+
		"\u0005?\u0000\u0000\u00b9\u00ba\u0005@\u0000\u0000\u00ba\u00bb\u0005A"+
		"\u0000\u0000\u00bb\u00bc\u0005B\u0000\u0000\u00bc\u00bd\u0005C\u0000\u0000"+
		"\u00bd\u00be\u0005D\u0000\u0000\u00be\u00bf\u0005E\u0000\u0000\u00bf\u00c0"+
		"\u0005F\u0000\u0000\u00c0\u00c1\u0005G\u0000\u0000\u00c1\u00c2\u0005H"+
		"\u0000\u0000\u00c2\u00c3\u0005I\u0000\u0000\u00c3\r\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c7\u0003\u0010\b\u0000\u00c5\u00c7\u0003\u0012\t\u0000"+
		"\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c5\u0001\u0000\u0000\u0000"+
		"\u00c7\u000f\u0001\u0000\u0000\u0000\u00c8\u00cc\u0003>\u001f\u0000\u00c9"+
		"\u00cb\u0003\u0012\t\u0000\u00ca\u00c9\u0001\u0000\u0000\u0000\u00cb\u00ce"+
		"\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd"+
		"\u0001\u0000\u0000\u0000\u00cd\u00cf\u0001\u0000\u0000\u0000\u00ce\u00cc"+
		"\u0001\u0000\u0000\u0000\u00cf\u00d0\u0003@ \u0000\u00d0\u0011\u0001\u0000"+
		"\u0000\u0000\u00d1\u00d9\u0003\u0016\u000b\u0000\u00d2\u00d9\u0003\u0018"+
		"\f\u0000\u00d3\u00d9\u0003\u001c\u000e\u0000\u00d4\u00d9\u0003\u001a\r"+
		"\u0000\u00d5\u00d9\u0003\u001e\u000f\u0000\u00d6\u00d9\u0003$\u0012\u0000"+
		"\u00d7\u00d9\u0003\u0014\n\u0000\u00d8\u00d1\u0001\u0000\u0000\u0000\u00d8"+
		"\u00d2\u0001\u0000\u0000\u0000\u00d8\u00d3\u0001\u0000\u0000\u0000\u00d8"+
		"\u00d4\u0001\u0000\u0000\u0000\u00d8\u00d5\u0001\u0000\u0000\u0000\u00d8"+
		"\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000\u00d9"+
		"\u0013\u0001\u0000\u0000\u0000\u00da\u00db\u0007\u0000\u0000\u0000\u00db"+
		"\u0015\u0001\u0000\u0000\u0000\u00dc\u00e0\u0003&\u0013\u0000\u00dd\u00df"+
		"\u0003\u0012\t\u0000\u00de\u00dd\u0001\u0000\u0000\u0000\u00df\u00e2\u0001"+
		"\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e1\u00ea\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e3\u00e7\u0003(\u0014\u0000\u00e4\u00e6\u0003\u0012"+
		"\t\u0000\u00e5\u00e4\u0001\u0000\u0000\u0000\u00e6\u00e9\u0001\u0000\u0000"+
		"\u0000\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000"+
		"\u0000\u00e8\u00eb\u0001\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000"+
		"\u0000\u00ea\u00e3\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000"+
		"\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ed\u0003*\u0015\u0000"+
		"\u00ed\u0017\u0001\u0000\u0000\u0000\u00ee\u00f2\u0003,\u0016\u0000\u00ef"+
		"\u00f1\u0003\u0012\t\u0000\u00f0\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f4"+
		"\u0001\u0000\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f2\u00f3"+
		"\u0001\u0000\u0000\u0000\u00f3\u00f5\u0001\u0000\u0000\u0000\u00f4\u00f2"+
		"\u0001\u0000\u0000\u0000\u00f5\u00f6\u00032\u0019\u0000\u00f6\u0019\u0001"+
		"\u0000\u0000\u0000\u00f7\u00fb\u0003.\u0017\u0000\u00f8\u00fa\u0003\u0012"+
		"\t\u0000\u00f9\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fd\u0001\u0000\u0000"+
		"\u0000\u00fb\u00f9\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000"+
		"\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00fb\u0001\u0000\u0000"+
		"\u0000\u00fe\u00ff\u00032\u0019\u0000\u00ff\u001b\u0001\u0000\u0000\u0000"+
		"\u0100\u0104\u00030\u0018\u0000\u0101\u0103\u0003\u0012\t\u0000\u0102"+
		"\u0101\u0001\u0000\u0000\u0000\u0103\u0106\u0001\u0000\u0000\u0000\u0104"+
		"\u0102\u0001\u0000\u0000\u0000\u0104\u0105\u0001\u0000\u0000\u0000\u0105"+
		"\u0107\u0001\u0000\u0000\u0000\u0106\u0104\u0001\u0000\u0000\u0000\u0107"+
		"\u0108\u00032\u0019\u0000\u0108\u001d\u0001\u0000\u0000\u0000\u0109\u010b"+
		"\u00034\u001a\u0000\u010a\u0109\u0001\u0000\u0000\u0000\u010b\u010c\u0001"+
		"\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010c\u010d\u0001"+
		"\u0000\u0000\u0000\u010d\u010f\u0001\u0000\u0000\u0000\u010e\u0110\u0003"+
		"6\u001b\u0000\u010f\u010e\u0001\u0000\u0000\u0000\u010f\u0110\u0001\u0000"+
		"\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u0112\u00038\u001c"+
		"\u0000\u0112\u001f\u0001\u0000\u0000\u0000\u0113\u0114\u0005\u000b\u0000"+
		"\u0000\u0114\u0115\u0005K\u0000\u0000\u0115\u0116\u0003D\"\u0000\u0116"+
		"\u0117\u0003F#\u0000\u0117\u0118\u0005L\u0000\u0000\u0118\u0119\u0005"+
		"M\u0000\u0000\u0119!\u0001\u0000\u0000\u0000\u011a\u011b\u0005\u00ce\u0000"+
		"\u0000\u011b\u011c\u0005\u00cf\u0000\u0000\u011c\u011d\u0005\u00d0\u0000"+
		"\u0000\u011d\u011e\u0005\u00d1\u0000\u0000\u011e\u011f\u0005\u00d2\u0000"+
		"\u0000\u011f\u0120\u0003H$\u0000\u0120\u0121\u0005\u00f9\u0000\u0000\u0121"+
		"#\u0001\u0000\u0000\u0000\u0122\u0123\u0003 \u0010\u0000\u0123\u0124\u0003"+
		"B!\u0000\u0124\u0125\u0003\"\u0011\u0000\u0125%\u0001\u0000\u0000\u0000"+
		"\u0126\u0127\u0003 \u0010\u0000\u0127\u0128\u0007\u0001\u0000\u0000\u0128"+
		"\u012d\u0003\"\u0011\u0000\u0129\u012c\u0003:\u001d\u0000\u012a\u012c"+
		"\u0003<\u001e\u0000\u012b\u0129\u0001\u0000\u0000\u0000\u012b\u012a\u0001"+
		"\u0000\u0000\u0000\u012c\u012f\u0001\u0000\u0000\u0000\u012d\u012b\u0001"+
		"\u0000\u0000\u0000\u012d\u012e\u0001\u0000\u0000\u0000\u012e\'\u0001\u0000"+
		"\u0000\u0000\u012f\u012d\u0001\u0000\u0000\u0000\u0130\u0131\u0003 \u0010"+
		"\u0000\u0131\u0132\u0005\u00a4\u0000\u0000\u0132\u0133\u0003\"\u0011\u0000"+
		"\u0133)\u0001\u0000\u0000\u0000\u0134\u0135\u0003 \u0010\u0000\u0135\u0136"+
		"\u0007\u0002\u0000\u0000\u0136\u0137\u0003\"\u0011\u0000\u0137+\u0001"+
		"\u0000\u0000\u0000\u0138\u0139\u0003 \u0010\u0000\u0139\u013a\u0005\u00a5"+
		"\u0000\u0000\u013a\u013b\u0003\"\u0011\u0000\u013b-\u0001\u0000\u0000"+
		"\u0000\u013c\u013d\u0003 \u0010\u0000\u013d\u013e\u0007\u0003\u0000\u0000"+
		"\u013e\u0143\u0003\"\u0011\u0000\u013f\u0142\u0003:\u001d\u0000\u0140"+
		"\u0142\u0003<\u001e\u0000\u0141\u013f\u0001\u0000\u0000\u0000\u0141\u0140"+
		"\u0001\u0000\u0000\u0000\u0142\u0145\u0001\u0000\u0000\u0000\u0143\u0141"+
		"\u0001\u0000\u0000\u0000\u0143\u0144\u0001\u0000\u0000\u0000\u0144/\u0001"+
		"\u0000\u0000\u0000\u0145\u0143\u0001\u0000\u0000\u0000\u0146\u0147\u0003"+
		" \u0010\u0000\u0147\u0148\u0007\u0004\u0000\u0000\u0148\u014d\u0003\""+
		"\u0011\u0000\u0149\u014c\u0003:\u001d\u0000\u014a\u014c\u0003<\u001e\u0000"+
		"\u014b\u0149\u0001\u0000\u0000\u0000\u014b\u014a\u0001\u0000\u0000\u0000"+
		"\u014c\u014f\u0001\u0000\u0000\u0000\u014d\u014b\u0001\u0000\u0000\u0000"+
		"\u014d\u014e\u0001\u0000\u0000\u0000\u014e1\u0001\u0000\u0000\u0000\u014f"+
		"\u014d\u0001\u0000\u0000\u0000\u0150\u0151\u0003 \u0010\u0000\u0151\u0152"+
		"\u0007\u0005\u0000\u0000\u0152\u0153\u0003\"\u0011\u0000\u01533\u0001"+
		"\u0000\u0000\u0000\u0154\u0155\u0003 \u0010\u0000\u0155\u0156\u0007\u0006"+
		"\u0000\u0000\u0156\u0157\u0003\"\u0011\u0000\u01575\u0001\u0000\u0000"+
		"\u0000\u0158\u0159\u0003 \u0010\u0000\u0159\u015a\u0005r\u0000\u0000\u015a"+
		"\u015b\u0003\"\u0011\u0000\u015b7\u0001\u0000\u0000\u0000\u015c\u015d"+
		"\u0003 \u0010\u0000\u015d\u015e\u0007\u0007\u0000\u0000\u015e\u015f\u0003"+
		"\"\u0011\u0000\u015f9\u0001\u0000\u0000\u0000\u0160\u0161\u0003 \u0010"+
		"\u0000\u0161\u0162\u0007\b\u0000\u0000\u0162\u0163\u0003\"\u0011\u0000"+
		"\u0163;\u0001\u0000\u0000\u0000\u0164\u0165\u0003 \u0010\u0000\u0165\u0166"+
		"\u0007\t\u0000\u0000\u0166\u0167\u0003\"\u0011\u0000\u0167=\u0001\u0000"+
		"\u0000\u0000\u0168\u0169\u0003 \u0010\u0000\u0169\u016a\u0005\u009d\u0000"+
		"\u0000\u016a\u016b\u0003\"\u0011\u0000\u016b?\u0001\u0000\u0000\u0000"+
		"\u016c\u016d\u0003 \u0010\u0000\u016d\u016e\u0005\u009e\u0000\u0000\u016e"+
		"\u016f\u0003\"\u0011\u0000\u016fA\u0001\u0000\u0000\u0000\u0170\u0171"+
		"\u0007\n\u0000\u0000\u0171C\u0001\u0000\u0000\u0000\u0172\u0173\u0007"+
		"\u000b\u0000\u0000\u0173E\u0001\u0000\u0000\u0000\u0174\u0175\u0007\f"+
		"\u0000\u0000\u0175G\u0001\u0000\u0000\u0000\u0176\u0177\u0005\u00d3\u0000"+
		"\u0000\u0177\u0178\u0005\u00d4\u0000\u0000\u0178\u0179\u0005\u00d5\u0000"+
		"\u0000\u0179I\u0001\u0000\u0000\u0000\u017a\u017d\u0003L&\u0000\u017b"+
		"\u017d\u0003N\'\u0000\u017c\u017a\u0001\u0000\u0000\u0000\u017c\u017b"+
		"\u0001\u0000\u0000\u0000\u017dK\u0001\u0000\u0000\u0000\u017e\u017f\u0005"+
		"\f\u0000\u0000\u017f\u0180\u0005\u00d7\u0000\u0000\u0180\u0181\u0005\u00d8"+
		"\u0000\u0000\u0181\u0182\u0005\u00d9\u0000\u0000\u0182\u0183\u0005\u00da"+
		"\u0000\u0000\u0183\u0184\u0005\u00db\u0000\u0000\u0184\u0185\u0005\u00dc"+
		"\u0000\u0000\u0185\u0186\u0005\u00dd\u0000\u0000\u0186\u0187\u0003P(\u0000"+
		"\u0187\u0189\u0005\u00e4\u0000\u0000\u0188\u018a\u0005\u00e5\u0000\u0000"+
		"\u0189\u0188\u0001\u0000\u0000\u0000\u0189\u018a\u0001\u0000\u0000\u0000"+
		"\u018a\u018b\u0001\u0000\u0000\u0000\u018b\u018c\u0005\u00f9\u0000\u0000"+
		"\u018cM\u0001\u0000\u0000\u0000\u018d\u018e\u0005\f\u0000\u0000\u018e"+
		"\u0190\u0005\u00d7\u0000\u0000\u018f\u0191\u0005\u00d8\u0000\u0000\u0190"+
		"\u018f\u0001\u0000\u0000\u0000\u0190\u0191\u0001\u0000\u0000\u0000\u0191"+
		"\u0193\u0001\u0000\u0000\u0000\u0192\u0194\u0005\u00d9\u0000\u0000\u0193"+
		"\u0192\u0001\u0000\u0000\u0000\u0193\u0194\u0001\u0000\u0000\u0000\u0194"+
		"\u0196\u0001\u0000\u0000\u0000\u0195\u0197\u0005\u00da\u0000\u0000\u0196"+
		"\u0195\u0001\u0000\u0000\u0000\u0196\u0197\u0001\u0000\u0000\u0000\u0197"+
		"\u0199\u0001\u0000\u0000\u0000\u0198\u019a\u0005\u00db\u0000\u0000\u0199"+
		"\u0198\u0001\u0000\u0000\u0000\u0199\u019a\u0001\u0000\u0000\u0000\u019a"+
		"\u019c\u0001\u0000\u0000\u0000\u019b\u019d\u0005\u00dc\u0000\u0000\u019c"+
		"\u019b\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000\u0000\u0000\u019d"+
		"\u019f\u0001\u0000\u0000\u0000\u019e\u01a0\u0005\u00dd\u0000\u0000\u019f"+
		"\u019e\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000\u0000\u0000\u01a0"+
		"\u01a2\u0001\u0000\u0000\u0000\u01a1\u01a3\u0003P(\u0000\u01a2\u01a1\u0001"+
		"\u0000\u0000\u0000\u01a2\u01a3\u0001\u0000\u0000\u0000\u01a3\u01a5\u0001"+
		"\u0000\u0000\u0000\u01a4\u01a6\u0005\u00e4\u0000\u0000\u01a5\u01a4\u0001"+
		"\u0000\u0000\u0000\u01a5\u01a6\u0001\u0000\u0000\u0000\u01a6\u01a8\u0001"+
		"\u0000\u0000\u0000\u01a7\u01a9\u0005\u00e5\u0000\u0000\u01a8\u01a7\u0001"+
		"\u0000\u0000\u0000\u01a8\u01a9\u0001\u0000\u0000\u0000\u01a9\u01aa\u0001"+
		"\u0000\u0000\u0000\u01aa\u01ab\u0005\u00f9\u0000\u0000\u01abO\u0001\u0000"+
		"\u0000\u0000\u01ac\u01ad\u0005\u00de\u0000\u0000\u01ad\u01ae\u0005\u00df"+
		"\u0000\u0000\u01ae\u01af\u0005\u00e0\u0000\u0000\u01af\u01b0\u0005\u00e1"+
		"\u0000\u0000\u01b0\u01b1\u0005\u00e2\u0000\u0000\u01b1\u01b2\u0005\u00e3"+
		"\u0000\u0000\u01b2Q\u0001\u0000\u0000\u0000\u01b3\u01b7\u0005\u0001\u0000"+
		"\u0000\u01b4\u01b6\u0003T*\u0000\u01b5\u01b4\u0001\u0000\u0000\u0000\u01b6"+
		"\u01b9\u0001\u0000\u0000\u0000\u01b7\u01b5\u0001\u0000\u0000\u0000\u01b7"+
		"\u01b8\u0001\u0000\u0000\u0000\u01b8S\u0001\u0000\u0000\u0000\u01b9\u01b7"+
		"\u0001\u0000\u0000\u0000\u01ba\u01bc\u0005\u00f8\u0000\u0000\u01bb\u01ba"+
		"\u0001\u0000\u0000\u0000\u01bb\u01bc\u0001\u0000\u0000\u0000\u01bc\u01bd"+
		"\u0001\u0000\u0000\u0000\u01bd\u01be\u0005\u00f9\u0000\u0000\u01beU\u0001"+
		"\u0000\u0000\u0000)Y_ekqw{\u0081\u0097\u00a6\u00b4\u00c6\u00cc\u00d8\u00e0"+
		"\u00e7\u00ea\u00f2\u00fb\u0104\u010c\u010f\u012b\u012d\u0141\u0143\u014b"+
		"\u014d\u017c\u0189\u0190\u0193\u0196\u0199\u019c\u019f\u01a2\u01a5\u01a8"+
		"\u01b7\u01bb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}