/**
 * Define a grammar called Rpg3Lexer
 */
 
lexer grammar Rpg3Lexer;

// Parser Rules
    //End Source.  Not more parsing after this.
END_SOURCE :  '**' ~[\r\n]~[\r\n]~[\r\n]~[\r\n*]~[\r\n]* EOL  -> pushMode(EndOfSourceMode) ;
    //Ignore or skip leading 5 white space.
LEAD_WS5 :  '     ' -> pushMode(Selector),skip;

LEAD_WS5_Comments :  WORD5 -> pushMode(Selector),channel(HIDDEN);
    //5 position blank means FREE, unless..
EMPTY_LINE : '                                                                           ' 
    { getCharPositionInLine()>=80 }? {System.out.println("EMPTY_LINE");}
    -> pushMode(FIXED_CommentMode), channel(HIDDEN) ;
EMPTY_NEWLINE : [ ]* '\r'? '\n' {System.out.println("EMPTY_NEWLINE");}-> skip;
fragment WORD5 : ~[\r\n]~[\r\n]~[\r\n]~[\r\n]~[\r\n] ;

fragment NAME5 : NAMECHAR NAMECHAR NAMECHAR NAMECHAR NAMECHAR;

// valid characters in symbolic names.
fragment NAMECHAR : [A-Za-z0-9$#@_ ] ;

// names cannot start with _ or numbers
fragment INITNAMECHAR : [A-Za-z$#@] ;

fragment WORD_WCOLON : ~[\r\n] ;

fragment WORD5_WCOLON : WORD_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON;


mode Selector;
    // 6th position asterisk is a comment
COMMENT_SPEC_FIXED : .'*' -> popMode,pushMode(FIXED_CommentMode) ;
    // X specs 

FS_FIXED : [fF] -> popMode,pushMode(FIXED_FileSpec) ;

OS_FIXED : [oO] -> popMode,pushMode(FIXED_OutputSpec) ;

CS_FIXED : [cC] -> popMode,pushMode(FIXED_CalcSpec), pushMode(OnOffIndicatorMode), pushMode(IndicatorMode) ;


IS_FIXED : [iI] -> popMode,pushMode(FIXED_InputSpec) ;


HS_FIXED : [hH] -> popMode,pushMode(HeaderSpecMode) ;

ES_FIXED : [eE] -> popMode,pushMode(FIXED_ExtensionSpec) ;

LS_FIXED : [lL] -> popMode,pushMode(FIXED_LineCounterSpec) ;

BLANK_LINE : [ ] [ ]* NEWLINE -> popMode,skip;

BLANK_SPEC_LINE1 : ~[hHfFcCoOiIeElL\r\n] NEWLINE -> popMode,skip;

BLANK_SPEC_LINE : ~[hHfFcCoOiIeElL\r\n] [ ] [ ]* NEWLINE -> popMode,skip;


DIRECTIVE :  . [ ]*? '/' -> popMode,pushMode(DirectiveMode_Start) ;

NEWLINE : '\r'? '\n' -> popMode,skip;
WS : [ \t] { getCharPositionInLine()>6 }? [ \t]* -> skip ; // skip spaces, tabs

GUTTER_COMMENTS: '                                                                        ' {System.out.println("GUTTER_COMMENTS");}
    -> popMode,pushMode(FIXED_CommentMode), channel(HIDDEN) ;



mode DirectiveTitle;
TITLE_Text : ~[\r\n]+ -> type(DIR_OtherText);

TITLE_EOL : NEWLINE -> type(EOL), popMode, popMode;


mode DirectiveMode_Start;
DIR_TITLE :([tT] [iI] [tT] [lL] [eE]) -> popMode,pushMode(DirectiveMode),pushMode(DirectiveTitle);

DIR_EJECT : [eE] [jJ] [eE] [cC] [tT] -> popMode,pushMode(DirectiveMode),pushMode(SKIP_REMAINING_WS);

DIR_SPACE : [sS] [pP] [aA] [cC] [eE]  -> popMode,pushMode(DirectiveMode);

DIR_SET :  [sS] [eE] [tT] -> popMode,pushMode(DirectiveMode) ;

DIR_RESTORE : [rR] [eE] [sS] [tT] [oO] [rR] [eE]  -> popMode,pushMode(DirectiveMode);

DIR_COPY : [cC] [oO] [pP] [yY]  -> popMode,pushMode(CopyMode);

DIR_INCLUDE : [iI] [nN] [cC] [lL] [uU] [dD] [eE]  -> popMode,pushMode(DirectiveMode);

DIR_EOF : [eE] [oO] [fF] -> popMode,pushMode(DirectiveMode) ;

DIR_DEFINE : ([dD] [eE] [fF] [iI] [nN] [eE]) -> popMode,pushMode(DirectiveMode);

DIR_UNDEFINE : ([uU] [nN] [dD] [eE] [fF] [iI] [nN] [eE]) -> popMode,pushMode(DirectiveMode);

DIR_IF : ([iI] [fF]) -> popMode,pushMode(DirectiveMode);

DIR_ELSE : ([eE] [lL] [sS] [eE]) -> popMode,pushMode(DirectiveMode);

DIR_ELSEIF : ([eE] [lL] [sS] [eE] [iI] [fF]) -> popMode,pushMode(DirectiveMode);

DIR_ENDIF : ([eE] [nN] [dD] [iI] [fF]) -> popMode,pushMode(DirectiveMode);

DIR_Start_Any : -> popMode, pushMode(DirectiveMode);


mode CopyMode;
COPY_WhiteSpace : [ ] -> skip;
COPY_OtherText : ~[/'"\r\n \t,()] {getCharPositionInLine()<50}? DIR_OtherText -> type(DIR_OtherText) ;
COPY_Comma : DIR_Comma -> type(DIR_Comma);
COPY_Slash : DIR_Slash -> type(DIR_Slash);
COPY_EOL : DIR_EOL -> type(EOL), popMode;
COPY_Comment : ~[/'"\r\n \t,()] -> more,pushMode(CheckComment);
COPY_DblStringLiteralStart : ["] -> pushMode(InDoubleStringMode), type(StringLiteralStart) ;
COPY_StringLiteralStart : ['] -> pushMode(InStringMode), type(StringLiteralStart) ;
COPY_NA : -> skip, popMode;

mode DirectiveMode;
DIR_NOT : [nN] [oO] [tT] ;

DIR_DEFINED : [dD] [eE] [fF] [iI] [nN] [eE] [dD] ;

DIR_Number : NUMBER -> type(NUMBER);

DIR_WhiteSpace : [ ] -> skip;

DIR_OtherText : ~[/'"\r\n \t,()]+ ;

DIR_Comma : [,] -> skip;

DIR_Slash : [/] ;

DIR_OpenParen : [(] -> type(OPEN_PAREN);

DIR_CloseParen : [)] -> type(CLOSE_PAREN);

DIR_DblStringLiteralStart : ["] -> pushMode(InDoubleStringMode), type(StringLiteralStart) ;

DIR_StringLiteralStart : ['] -> pushMode(InStringMode), type(StringLiteralStart) ;

DIR_EOL : [ ]* NEWLINE { setText(getText().trim()); } -> type(EOL), popMode;


mode CheckComment;
CheckComment_COMMENT_SPEC_FIXED : .'*' -> type(COMMENT_SPEC_FIXED),popMode,pushMode(FIXED_CommentMode) ;
CheckComment_NoSpace : -> skip,popMode;

mode First5;
First5_LEAD_WS5 :  '     ' -> skip,popMode;
First5_LEAD_WS5_Comments : LEAD_WS5_Comments -> type(LEAD_WS5_Comments),channel(HIDDEN),popMode;

mode SKIP_REMAINING_WS;
DIR_FREE_OTHER_TEXT : ~[\r\n]* -> popMode, skip;


mode EndOfSourceMode;
EOS_Text : ~[\r\n]+ ;

EOS_EOL : NEWLINE -> type(EOL);

mode FixedOpCodes; //Referenced (not used)
OP_ADD : [aA] [dD] [dD] ;

OP_ADDDUR : OP_ADD [dD] [uU] [rR] ;

OP_ALLOC : [aA] [lL] [lL] [oO] [cC] ;

OP_ANDxx : [aA] [nN] [dD] [0-9] [0-9] ;

OP_ANDEQ : [aA] [nN] [dD] [eE] [qQ] ;

OP_ANDNE : [aA] [nN] [dD] [nN] [eE] ;

OP_ANDLE : [aA] [nN] [dD] [lL] [eE] ;

OP_ANDLT : [aA] [nN] [dD] [lL] [tT] ;

OP_ANDGE : [aA] [nN] [dD] [gG] [eE] ;

OP_ANDGT : [aA] [nN] [dD] [gG] [tT] ;

OP_BITOFF : [bB] [iI] [tT] [oO] [fF] [fF] ;

OP_BITON : [bB] [iI] [tT] [oO] [nN] ;

OP_CABxx : [cc] [aA] [bB] [0-9] [0-9] ;

OP_CABEQ : [cC] [aA] [bB] [eE] [qQ] ;

OP_CABNE : [cC] [aA] [bB] [nN] [eE] ;

OP_CABLE : [cC] [aA] [bB] [lL] [eE] ;

OP_CABLT : [cC] [aA] [bB] [lL] [tT] ;

OP_CABGE : [cC] [aA] [bB] [gG] [eE] ;

OP_CABGT : [cC] [aA] [bB] [gG] [tT] ;

OP_CALL : [Cc] [Aa] [Ll] [Ll] ;

OP_CALLB : OP_CALL [bB] ;

OP_CASEQ : [cC] [aA] [sS] [eE] [qQ] ;

OP_CASNE : [cC] [aA] [sS] [nN] [eE] ;

OP_CASLE : [cC] [aA] [sS] [lL] [eE] ;

OP_CASLT : [cC] [aA] [sS] [lL] [tT] ;

OP_CASGE : [cC] [aA] [sS] [gG] [eE] ;

OP_CASGT : [cC] [aA] [sS] [gG] [tT] ;

OP_CAS : [cC] [aA] [sS] ;

OP_CAT : [cC] [aA] [tT] ;

OP_CHECK : [cC] [hH] [eE] [cC] [kK] ;

OP_CHECKR : [cC] [hH] [eE] [cC] [kK] [rR] ;

OP_COMP : [cC] [oO] [mM] [pP] ;

OP_DEFINE : [dD] [eE] [fF] [iI] [nN] [eE] ;

OP_DIV : [dD] [iI] [vV] ;

OP_DO : [dD] [oO] ;

OP_DOUEQ : [dD] [oO] [uU] [eE] [qQ] ;

OP_DOUNE : [dD] [oO] [uU] [nN] [eE] ;

OP_DOULE : [dD] [oO] [uU] [lL] [eE] ;

OP_DOULT : [dD] [oO] [uU] [lL] [tT] ;

OP_DOUGE : [dD] [oO] [uU] [gG] [eE] ;

OP_DOUGT : [dD] [oO] [uU] [gG] [tT] ;

OP_DOWEQ : [dD] [oO] [wW] [eE] [qQ] ;

OP_DOWNE : [dD] [oO] [wW] [nN] [eE] ;

OP_DOWLE : [dD] [oO] [wW] [lL] [eE] ;

OP_DOWLT : [dD] [oO] [wW] [lL] [tT] ;

OP_DOWGE : [dD] [oO] [wW] [gG] [eE] ;

OP_DOWGT : [dD] [oO] [wW] [gG] [tT] ;

OP_END : [eE] [nN] [dD] ;

OP_ENDCS : [eE] [nN] [dD] [cC] [sS] ;

OP_EXTRCT : [eE] [xX] [tT] [rR] [cC] [tT] ;

OP_GOTO : [gG] [oO] [tT] [oO] ;

OP_IFEQ : [iI] [fF] [eE] [qQ] ;

OP_IFNE : [iI] [fF] [nN] [eE] ;

OP_IFLE : [iI] [fF] [lL] [eE] ;

OP_IFLT : [iI] [fF] [lL] [tT] ;

OP_IFGE : [iI] [fF] [gG] [eE] ;

OP_IFGT : [iI] [fF] [gG] [tT] ;

OP_KFLD : [kK] [fF] [lL] [dD] ;

OP_KLIST : [kK] [lL] [iI] [sS] [tT] ;

OP_LOOKUP : [lL] [oO] [oO] [kK] [uU] [pP] ;

OP_MHHZO : [mM] [hH] [hH] [zZ] [oO] ;

OP_MHLZO : [mM] [hH] [lL] [zZ] [oO] ;

OP_MLHZO : [mM] [lL] [hH] [zZ] [oO] ;

OP_MLLZO : [mM] [lL] [lL] [zZ] [oO] ;

OP_MOVE : [mM] [oO] [vV] [eE] ;

OP_MOVEA : [mM] [oO] [vV] [eE] [aA] ;

OP_MOVEL : [mM] [oO] [vV] [eE] [lL] ;

OP_MULT : [mM] [uU] [lL] [tT] ;

OP_MVR : [mM] [vV] [rR] ;

OP_OCCUR : [oO] [cC] [cC] [uU] [rR] ;

OP_OREQ : [oO] [rR] [eE] [qQ] ;

OP_ORNE : [oO] [rR] [nN] [eE] ;

OP_ORLE : [oO] [rR] [lL] [eE] ;

OP_ORLT : [oO] [rR] [lL] [tT] ;

OP_ORGE : [oO] [rR] [gG] [eE] ;

OP_ORGT : [oO] [rR] [gG] [tT] ;

OP_PARM : [pP] [aA] [rR] [mM] ;

OP_PLIST : [pP] [lL] [iI] [sS] [tT] ;

OP_REALLOC : [rR] [eE] [aA] [lL] [lL] [oO] [cC] ;

OP_SCAN : [sS] [cC] [aA] [nN] ;

OP_SETOFF : [sS] [eE] [tT] [oO] [fF] [fF] ;

OP_SETON : [sS] [eE] [tT] [oO] [nN] ;

OP_SHTDN : [sS] [hH] [tT] [dD] [nN] ;

OP_SQRT : [sS] [qQ] [rR] [tT] ;

OP_SUB : [sS] [uU] [bB] ;

OP_SUBDUR : [sS] [uU] [bB] [dD] [uU] [rR] ;

OP_SUBST : [sS] [uU] [bB] [sS] [tT] ;

OP_TAG : [tT] [aA] [gG] ;

OP_TESTB : [tT] [eE] [sS] [tT] [bB] ;

OP_TESTN : [tT] [eE] [sS] [tT] [nN] ;

OP_TESTZ : [tT] [eE] [sS] [tT] [zZ] ;

OP_TIME : [tT] [iI] [mM] [eE] ;

OP_WHENEQ : [wW] [hH] [eE] [nN] [eE] [qQ] ;

OP_WHENNE : [wW] [hH] [eE] [nN] [nN] [eE] ;

OP_WHENLE : [wW] [hH] [eE] [nN] [lL] [eE] ;

OP_WHENLT : [wW] [hH] [eE] [nN] [lL] [tT] ;

OP_WHENGE : [wW] [hH] [eE] [nN] [gG] [eE] ;

OP_WHENGT : [wW] [hH] [eE] [nN] [gG] [tT] ;

OP_XFOOT : [xX] [fF] [oO] [oO] [tT] ;

OP_XLATE : [xX] [lL] [aA] [tT] [eE] ;

OP_Z_ADD : [zZ] '-' [aA] [dD] [dD] ;

OP_Z_SUB : [zZ] '-' [sS] [uU] [bB] ;


mode InStringMode;
//  Any char except +,- or ', or a + or - followed by more than just whitespace
StringContent : (
       ~['\r\n+-]
       | [+-] [ ]* { _input.LA(1)!=' ' && _input.LA(1)!='\r' && _input.LA(1)!='\n' }? // Plus is ok as long as it's not the last char
       )+;// space or not

StringEscapedQuote : ['] ['] { setText("'");};

StringLiteralEnd : ['] -> popMode;

FIXED_FREE_STRING_CONTINUATION : ('+' [ ]* NEWLINE)
   { _modeStack.contains(FIXED_CalcSpec)
     || _modeStack.contains(FIXED_OutputSpec)}?
   -> pushMode(EatCommentLinesPlus), pushMode(EatCommentLines), skip;

FIXED_FREE_STRING_CONTINUATION_MINUS : ('-' [ ]* NEWLINE)
   { _modeStack.contains(FIXED_CalcSpec)
     || _modeStack.contains(FIXED_OutputSpec)}?
   -> pushMode(EatCommentLines), skip;

PlusOrMinus : [+-] ;


mode InDoubleStringMode;
//  Any char except +,- or ", or a + or - followed by more than just whitespace
DblStringContent : ~["\r\n]+ -> type(StringContent);

DblStringLiteralEnd : ["] -> popMode, type(StringLiteralEnd);


mode EatCommentLinesPlus;
EatCommentLinesPlus_Any : -> popMode, skip;


// Inside continuations, ignore comment and blank lines.
mode EatCommentLines;
EatCommentLines_WhiteSpace : WORD5~[\r\n]{ getCharPositionInLine()==6}?[ ]* NEWLINE -> skip;

EatCommentLines_StarComment :
   WORD5~[\r\n]{ getCharPositionInLine()==6 }? [*] ~[\r\n]* NEWLINE -> skip;

FIXED_FREE_STRING_CONTINUATION_Part2 :
   (
     WORD5
     ( [cC] { _modeStack.contains(FIXED_CalcSpec)}?
      | [oO] { _modeStack.contains(FIXED_OutputSpec)}?
     )
     ~[*]
     ( '                            ' { _modeStack.contains(FIXED_CalcSpec)}?

       | '                                             ' { _modeStack.contains(FIXED_OutputSpec)}?
     )
     ([ ]* { _modeStack.peek() == EatCommentLinesPlus}?
      |
     )  // If it plus continuation eat whitespace.
   )
   -> skip ;
   
//Deliberate match no char, pop out again
EatCommentLines_NothingLeft : -> popMode, skip;


mode InFactorStringMode;
InFactor_StringContent : ( ~[\r\n']
        {(getCharPositionInLine()>=12 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=36 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=50 && getCharPositionInLine()<=63)
        }? )+
        -> type(StringContent);

InFactor_StringEscapedQuote : ['] [']
        {(getCharPositionInLine()>=12 && getCharPositionInLine()<=24)
            || (getCharPositionInLine()>=36 && getCharPositionInLine()<=48)
            || (getCharPositionInLine()>=50 && getCharPositionInLine()<=62)
        }?
        -> type(StringEscapedQuote);

InFactor_StringLiteralEnd : [']
        {(getCharPositionInLine()>=12 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=36 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=50 && getCharPositionInLine()<=63)
        }?
        -> type(StringLiteralEnd), popMode;

InFactor_EndFactor : {(getCharPositionInLine()==25)
            || (getCharPositionInLine()==49)
            || (getCharPositionInLine()==61)
        }?
        -> skip, popMode;


mode FIXED_CommentMode;
BLANK_COMMENTS_TEXT : [ ]+ -> skip;

COMMENTS_TEXT : ~[\r\n]+ { setText(getText().trim());} -> channel(HIDDEN);

COMMENTS_EOL : NEWLINE -> popMode, skip;


mode FIXED_CommentMode_HIDDEN;
COMMENTS_TEXT_SKIP : [ ]+ -> skip;

COMMENTS_TEXT_HIDDEN :  ~[\r\n]* -> channel(HIDDEN);

COMMENTS_EOL_HIDDEN : NEWLINE->  channel(HIDDEN), popMode;



mode FIXED_FileSpec;
FS_BLANK_SPEC : BLANK_SPEC -> type(BLANK_SPEC);

FS_RecordName : WORD5 WORD5 { getCharPositionInLine()==16 }? ;

FS_Type : [a-zA-Z ] { getCharPositionInLine()==17 }? ;

FS_Designation : [a-zA-Z ] { getCharPositionInLine()==18 }? ;

FS_EndOfFile : [eE ] { getCharPositionInLine()==19 }? ;

FS_Addution : [aA ] { getCharPositionInLine()==20 }? ;

FS_Sequence : [aAdD ] { getCharPositionInLine()==21 }? ;

FS_Format : [eEfF ] { getCharPositionInLine()==22 }? ;

FS_RecordLength : WORD5 { getCharPositionInLine()==27 }? ;

FS_Limits : [lL ] { getCharPositionInLine()==28 }? ;

FS_LengthOfKey : [0-9 ] [0-9 ] [0-9 ] [0-9 ] [0-9 ] { getCharPositionInLine()==33 }? ;

FS_RecordAddressType : [a-zA-Z ] { getCharPositionInLine()==34 }? ;

FS_Organization : [a-zA-Z ] { getCharPositionInLine()==35 }? ;

FS_Device : WORD5 [a-zA-Z ] [a-zA-Z ] { getCharPositionInLine()==42 }? ;

FS_Reserved : [ ] { getCharPositionInLine()==43 }? -> skip;

// G-1 fix: consume remaining cols 44-80 (overflow keywords, comments, spaces)
// Leading predicate ensures this only activates after col 43 (all column-specific tokens handled)
FS_Overflow : { getCharPositionInLine()>=43 }? ~[\r\n]+ -> skip;

FS_WhiteSpace : [ \t] { getCharPositionInLine()>80 }? [ \t]* -> skip  ;

FS_EOL : NEWLINE -> type(EOL), popMode;


mode FIXED_OutputSpec;
OS_BLANK_SPEC : BLANK_SPEC -> type(BLANK_SPEC);

OS_RecordName : WORD5 WORD5 { getCharPositionInLine()==16 }? ;

OS_AndOr : '         ' ([aA] [nN] [dD] | [oO] [rR] ' ') '  ' ->
    pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode);

OS_FieldReserved :  '              ' { getCharPositionInLine()==20}?
    -> pushMode(FIXED_OutputSpec_PGMFIELD),
    pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode);

OS_Type : [a-zA-Z ] { getCharPositionInLine()==17 }? ;

OS_AddDelete : ([aA] [dD] [dD] | [dD] [eE] [lL]) 
    { getCharPositionInLine()==20 
    }? -> pushMode(FIXED_OutputSpec_PGM1), pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode);

OS_FetchOverflow : (' ' | [fFrR]) '  ' 
    { getCharPositionInLine()==20 
    }? -> pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode), pushMode(OnOffIndicatorMode);

OS_ExceptName : WORD5 WORD5 { getCharPositionInLine()==39 }? ;

OS_Space3 : [ 0-9] [ 0-9] [ 0-9] 
    { getCharPositionInLine()==42 || getCharPositionInLine()==45
      || getCharPositionInLine()==48 || getCharPositionInLine()==51 
     }? ;

OS_RemainingSpace :  '                             ' { getCharPositionInLine()==80 }? ;

OS_Comments : CS_Comments -> channel(HIDDEN) ; 

OS_WS : [ \t] { getCharPositionInLine()>80 }? [ \t]* -> type(WS), skip  ; // skip spaces, tabs, newlines

OS_EOL : NEWLINE -> type(EOL), popMode;


mode FIXED_OutputSpec_PGM1;
O1_ExceptName : WORD5 WORD5 { getCharPositionInLine()==39 }? -> type(OS_ExceptName);

O1_RemainingSpace : '                                         '  { getCharPositionInLine()==80}?
    -> type(OS_RemainingSpace), popMode;


mode FIXED_OutputSpec_PGMFIELD;
OS_FieldName : WORD5 WORD5 ~[\r\n] ~[\r\n] ~[\r\n] ~[\r\n] { getCharPositionInLine()==43 }? ;

OS_EditNames : [ 0-9A-Za-z] { getCharPositionInLine()==44 }? ;

OS_BlankAfter : [ bB] { getCharPositionInLine()==45 }? ;

OS_Reserved1 : [ ]  { getCharPositionInLine()==46}?-> skip;

OS_EndPosition : WORD5 { getCharPositionInLine()==51 }? ;

OS_DataFormat : [ 0-9A-Za-z] { getCharPositionInLine()==52 }?;

OS_Any : -> popMode;


mode FIXED_CalcSpec;
CS_Factor1_SPLAT_ALL : SPLAT_ALL {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ALL);

CS_Factor1_SPLAT_NONE : SPLAT_NONE {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_NONE);

CS_Factor1_SPLAT_ILERPG : SPLAT_ILERPG {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ILERPG);

CS_Factor1_SPLAT_CRTBNDRPG : SPLAT_CRTBNDRPG {11+10<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CRTBNDRPG);

CS_Factor1_SPLAT_CRTRPGMOD : SPLAT_CRTRPGMOD {11+10<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CRTRPGMOD);

CS_Factor1_SPLAT_VRM :  SPLAT_VRM{11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_VRM);

CS_Factor1_SPLAT_ALLG : SPLAT_ALLG {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ALLG);

CS_Factor1_SPLAT_ALLU : SPLAT_ALLU {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ALLU);

CS_Factor1_SPLAT_ALLX : SPLAT_ALLX {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ALLX);

CS_Factor1_SPLAT_BLANKS : SPLAT_BLANKS {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_BLANKS);

CS_Factor1_SPLAT_CANCL : SPLAT_CANCL {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CANCL);

CS_Factor1_SPLAT_CYMD : SPLAT_CYMD {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CYMD);

CS_Factor1_SPLAT_CMDY : SPLAT_CMDY {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CMDY);

CS_Factor1_SPLAT_CDMY : SPLAT_CDMY {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_CDMY);

CS_Factor1_SPLAT_MDY : SPLAT_MDY {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MDY);

CS_Factor1_SPLAT_DMY : SPLAT_DMY {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DMY);

CS_Factor1_SPLAT_YMD : SPLAT_YMD {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_YMD);

CS_Factor1_SPLAT_JUL : SPLAT_JUL {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_JUL);

CS_Factor1_SPLAT_ISO : SPLAT_ISO {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ISO);

CS_Factor1_SPLAT_USA : SPLAT_USA {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_USA);

CS_Factor1_SPLAT_EUR : SPLAT_EUR {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_EUR);

CS_Factor1_SPLAT_JIS : SPLAT_JIS {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_JIS);

CS_Factor1_SPLAT_DATE : SPLAT_DATE {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DATE);

CS_Factor1_SPLAT_DAY : SPLAT_DAY {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DAY);

CS_Factor1_SPLAT_DETC : SPLAT_DETC {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DETC);

CS_Factor1_SPLAT_DETL : SPLAT_DETL {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DETL);

CS_Factor1_SPLAT_DTAARA : SPLAT_DTAARA {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DTAARA);

CS_Factor1_SPLAT_END : SPLAT_END {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_END);

CS_Factor1_SPLAT_ENTRY : SPLAT_ENTRY {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ENTRY);

CS_Factor1_SPLAT_EQUATE : SPLAT_EQUATE {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_EQUATE);

CS_Factor1_SPLAT_EXTDFT : SPLAT_EXTDFT {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_EXTDFT);

CS_Factor1_SPLAT_EXT : SPLAT_EXT {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_EXT);

CS_Factor1_SPLAT_FILE : SPLAT_FILE {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_FILE);

CS_Factor1_SPLAT_GETIN : SPLAT_GETIN {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_GETIN);

CS_Factor1_SPLAT_HIVAL : SPLAT_HIVAL {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_HIVAL);

CS_Factor1_SPLAT_INIT : SPLAT_INIT {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_INIT);

CS_Factor1_SPLAT_INDICATOR : SPLAT_INDICATOR {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_INDICATOR);

CS_Factor1_SPLAT_INZSR : SPLAT_INZSR {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_INZSR);

CS_Factor1_SPLAT_IN : SPLAT_IN {11+3<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_IN);

CS_Factor1_SPLAT_JOBRUN : SPLAT_JOBRUN {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_JOBRUN);

CS_Factor1_SPLAT_JOB : SPLAT_JOB {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_JOB);

CS_Factor1_SPLAT_LDA : SPLAT_LDA {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_LDA);

CS_Factor1_SPLAT_LIKE : SPLAT_LIKE {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_LIKE);

CS_Factor1_SPLAT_LONGJUL : SPLAT_LONGJUL {11+8<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_LONGJUL);

CS_Factor1_SPLAT_LOVAL : SPLAT_LOVAL {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_LOVAL);

CS_Factor1_SPLAT_MONTH : SPLAT_MONTH {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MONTH);

CS_Factor1_SPLAT_NOIND : SPLAT_NOIND {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_NOIND);

CS_Factor1_SPLAT_NOKEY : SPLAT_NOKEY {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_NOKEY);

CS_Factor1_SPLAT_NULL : SPLAT_NULL {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_NULL);

CS_Factor1_SPLAT_OFL : SPLAT_OFL {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_OFL);

CS_Factor1_SPLAT_ON : SPLAT_ON {11+3<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ON);

CS_Factor1_SPLAT_OFF : SPLAT_OFF {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_OFF);

CS_Factor1_SPLAT_PDA : SPLAT_PDA {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_PDA);

CS_Factor1_SPLAT_PLACE : SPLAT_PLACE {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_PLACE);

CS_Factor1_SPLAT_PSSR : SPLAT_PSSR {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_PSSR);

CS_Factor1_SPLAT_ROUTINE : SPLAT_ROUTINE {11+8<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ROUTINE);

CS_Factor1_SPLAT_START : SPLAT_START {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_START);

CS_Factor1_SPLAT_SYS : SPLAT_SYS {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_SYS);

CS_Factor1_SPLAT_TERM : SPLAT_TERM {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_TERM);

CS_Factor1_SPLAT_TOTC : SPLAT_TOTC {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_TOTC);

CS_Factor1_SPLAT_TOTL : SPLAT_TOTL {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_TOTL);

CS_Factor1_SPLAT_USER : SPLAT_USER {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_USER);

CS_Factor1_SPLAT_VAR : SPLAT_VAR {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_VAR);

CS_Factor1_SPLAT_YEAR : SPLAT_YEAR {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_YEAR);

CS_Factor1_SPLAT_ZEROS : SPLAT_ZEROS {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_ZEROS);

CS_Factor1_SPLAT_HMS : SPLAT_HMS {11+4<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_HMS);

CS_Factor1_SPLAT_INLR : SPLAT_INLR {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_INLR);

CS_Factor1_SPLAT_INOF : SPLAT_INOF {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_INOF);

//DurationCodes
CS_Factor1_SPLAT_D : SPLAT_D {11+2<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_D);

CS_Factor1_SPLAT_DAYS : SPLAT_DAYS {11+5<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_DAYS);

CS_Factor1_SPLAT_H : SPLAT_H {11+2<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_H);

CS_Factor1_SPLAT_HOURS : SPLAT_HOURS {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_HOURS);

CS_Factor1_SPLAT_MINUTES : SPLAT_MINUTES {11+8<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MINUTES);

CS_Factor1_SPLAT_MONTHS : SPLAT_MONTHS {11+7<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MONTHS);

CS_Factor1_SPLAT_M : SPLAT_M {11+2<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_M);

CS_Factor1_SPLAT_MN : SPLAT_MN {11+3<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MN);

CS_Factor1_SPLAT_MS : SPLAT_MS {11+3<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MS);

CS_Factor1_SPLAT_MSECONDS : SPLAT_MSECONDS {11+9<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_MSECONDS);

CS_Factor1_SPLAT_SECONDS : SPLAT_SECONDS {11+8<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_SECONDS);

CS_Factor1_SPLAT_YEARS : SPLAT_YEARS {11+6<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_YEARS);

CS_Factor1_SPLAT_Y : SPLAT_Y {11+2<= getCharPositionInLine() && getCharPositionInLine()<=24 }? -> type(SPLAT_Y);

//Factor 2
CS_Factor2_SPLAT_ALL : SPLAT_ALL {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ALL);

CS_Factor2_SPLAT_NONE : SPLAT_NONE {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_NONE);

CS_Factor2_SPLAT_ILERPG : SPLAT_ILERPG {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ILERPG);

CS_Factor2_SPLAT_CRTBNDRPG : SPLAT_CRTBNDRPG {35+10<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CRTBNDRPG);

CS_Factor2_SPLAT_CRTRPGMOD : SPLAT_CRTRPGMOD {35+10<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CRTRPGMOD);

CS_Factor2_SPLAT_VRM : SPLAT_VRM {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_VRM);

CS_Factor2_SPLAT_ALLG : SPLAT_ALLG {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ALLG);

CS_Factor2_SPLAT_ALLU : SPLAT_ALLU {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ALLU);

CS_Factor2_SPLAT_ALLX : SPLAT_ALLX {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ALLX);

CS_Factor2_SPLAT_BLANKS : SPLAT_BLANKS {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_BLANKS);

CS_Factor2_SPLAT_CANCL : SPLAT_CANCL {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CANCL);

CS_Factor2_SPLAT_CYMD : SPLAT_CYMD {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CYMD);

CS_Factor2_SPLAT_CMDY : SPLAT_CMDY {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CMDY);

CS_Factor2_SPLAT_CDMY : SPLAT_CDMY {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_CDMY);

CS_Factor2_SPLAT_MDY : SPLAT_MDY {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MDY);

CS_Factor2_SPLAT_DMY : SPLAT_DMY {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DMY);

CS_Factor2_SPLAT_YMD : SPLAT_YMD {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_YMD);

CS_Factor2_SPLAT_JUL : SPLAT_JUL {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_JUL);

CS_Factor2_SPLAT_ISO : SPLAT_ISO {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ISO);

CS_Factor2_SPLAT_USA : SPLAT_USA {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_USA);

CS_Factor2_SPLAT_EUR : SPLAT_EUR {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_EUR);

CS_Factor2_SPLAT_JIS : SPLAT_JIS {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_JIS);

CS_Factor2_SPLAT_DATE : SPLAT_DATE {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DATE);

CS_Factor2_SPLAT_DAY : SPLAT_DAY {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DAY);

CS_Factor2_SPLAT_DETC : SPLAT_DETC {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DETC);

CS_Factor2_SPLAT_DETL : SPLAT_DETL {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DETL);

CS_Factor2_SPLAT_DTAARA : SPLAT_DTAARA {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DTAARA);

CS_Factor2_SPLAT_END : SPLAT_END {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_END);

CS_Factor2_SPLAT_ENTRY : SPLAT_ENTRY {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ENTRY);

CS_Factor2_SPLAT_EQUATE : SPLAT_EQUATE {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_EQUATE);

CS_Factor2_SPLAT_EXTDFT : SPLAT_EXTDFT {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_EXTDFT);

CS_Factor2_SPLAT_EXT : SPLAT_EXT {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_EXT);

CS_Factor2_SPLAT_FILE : SPLAT_FILE {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_FILE);

CS_Factor2_SPLAT_GETIN : SPLAT_GETIN {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_GETIN);

CS_Factor2_SPLAT_HIVAL : SPLAT_HIVAL {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_HIVAL);

CS_Factor2_SPLAT_INIT : SPLAT_INIT {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_INIT);

CS_Factor2_SPLAT_INDICATOR : SPLAT_INDICATOR {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_INDICATOR);

CS_Factor2_SPLAT_INZSR : SPLAT_INZSR {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_INZSR);

CS_Factor2_SPLAT_IN : SPLAT_IN {35+3<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_IN);

CS_Factor2_SPLAT_JOBRUN : SPLAT_JOBRUN {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_JOBRUN);

CS_Factor2_SPLAT_JOB : SPLAT_JOB {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_JOB);

CS_Factor2_SPLAT_LDA : SPLAT_LDA {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_LDA);

CS_Factor2_SPLAT_LIKE : SPLAT_LIKE {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_LIKE);

CS_Factor2_SPLAT_LONGJUL : SPLAT_LONGJUL {35+8<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_LONGJUL);

CS_Factor2_SPLAT_LOVAL : SPLAT_LOVAL {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_LOVAL);

CS_Factor2_SPLAT_MONTH : SPLAT_MONTH {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MONTH);

CS_Factor2_SPLAT_NOIND : SPLAT_NOIND {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_NOIND);

CS_Factor2_SPLAT_NOKEY : SPLAT_NOKEY {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_NOKEY);

CS_Factor2_SPLAT_NULL : SPLAT_NULL {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_NULL);

CS_Factor2_SPLAT_OFL : SPLAT_OFL {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_OFL);

CS_Factor2_SPLAT_ON : SPLAT_ON {35+3<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ON);

CS_Factor2_SPLAT_OFF : SPLAT_OFF {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_OFF);

CS_Factor2_SPLAT_PDA : SPLAT_PDA {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_PDA);

CS_Factor2_SPLAT_PLACE : SPLAT_PLACE {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_PLACE);

CS_Factor2_SPLAT_PSSR : SPLAT_PSSR {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_PSSR);

CS_Factor2_SPLAT_ROUTINE : SPLAT_ROUTINE {35+8<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ROUTINE);

CS_Factor2_SPLAT_START : SPLAT_START {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_START);

CS_Factor2_SPLAT_SYS : SPLAT_SYS {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_SYS);

CS_Factor2_SPLAT_TERM : SPLAT_TERM {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_TERM);

CS_Factor2_SPLAT_TOTC : SPLAT_TOTC {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_TOTC);

CS_Factor2_SPLAT_TOTL : SPLAT_TOTL {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_TOTL);

CS_Factor2_SPLAT_USER : SPLAT_USER {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_USER);

CS_Factor2_SPLAT_VAR : SPLAT_VAR {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_VAR);

CS_Factor2_SPLAT_YEAR : SPLAT_YEAR {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_YEAR);

CS_Factor2_SPLAT_ZEROS : SPLAT_ZEROS {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_ZEROS);

CS_Factor2_SPLAT_HMS : SPLAT_HMS {35+4<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_HMS);

CS_Factor2_SPLAT_INLR : SPLAT_INLR {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_INLR);

CS_Factor2_SPLAT_INOF : SPLAT_INOF {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_INOF);
//Duration
CS_Factor2_SPLAT_D : SPLAT_D {35+2<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_D);

CS_Factor2_SPLAT_DAYS : SPLAT_DAYS {35+5<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_DAYS);

CS_Factor2_SPLAT_H : SPLAT_H {35+2<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_H);

CS_Factor2_SPLAT_HOURS : SPLAT_HOURS {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_HOURS);

CS_Factor2_SPLAT_MINUTES : SPLAT_MINUTES {35+8<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MINUTES);

CS_Factor2_SPLAT_MONTHS : SPLAT_MONTHS {35+7<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MONTHS);

CS_Factor2_SPLAT_M : SPLAT_M {35+2<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_M);

CS_Factor2_SPLAT_MN : SPLAT_MN {35+3<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MN);

CS_Factor2_SPLAT_MS : SPLAT_MS {35+3<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MS);

CS_Factor2_SPLAT_MSECONDS : SPLAT_MSECONDS {35+9<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_MSECONDS);

CS_Factor2_SPLAT_SECONDS : SPLAT_SECONDS {35+8<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_SECONDS);

CS_Factor2_SPLAT_YEARS : SPLAT_YEARS {35+6<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_YEARS);

CS_Factor2_SPLAT_Y : SPLAT_Y {35+2<= getCharPositionInLine() && getCharPositionInLine()<=48 }? -> type(SPLAT_Y);

//Result 

//Duration
CS_Result_SPLAT_D : SPLAT_D {49+2<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_D);

CS_Result_SPLAT_DAYS : SPLAT_DAYS {49+5<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_DAYS);

CS_Result_SPLAT_H : SPLAT_H {49+2<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_H);

CS_Result_SPLAT_HOURS : SPLAT_HOURS {49+6<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_HOURS);

CS_Result_SPLAT_MINUTES : SPLAT_MINUTES {49+8<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_MINUTES);

CS_Result_SPLAT_MONTHS : SPLAT_MONTHS {49+7<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_MONTHS);

CS_Result_SPLAT_M : SPLAT_M {49+2<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_M);

CS_Result_SPLAT_MN : SPLAT_MN {49+3<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_MN);

CS_Result_SPLAT_MS : SPLAT_MS {49+3<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_MS);

CS_Result_SPLAT_MSECONDS : SPLAT_MSECONDS {49+9<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_MSECONDS);

CS_Result_SPLAT_SECONDS : SPLAT_SECONDS {49+8<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_SECONDS);

CS_Result_SPLAT_YEARS : SPLAT_YEARS {49+6<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_YEARS);

CS_Result_SPLAT_Y : SPLAT_Y {49+2<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_Y);

CS_Result_SPLAT_S : SPLAT_S {49+2<= getCharPositionInLine() && getCharPositionInLine()<=62 }? -> type(SPLAT_S);

CS_BlankFactor : '              '
        {(getCharPositionInLine()==25)
            || (getCharPositionInLine()==49)
            || (getCharPositionInLine()==63)}?
;

// Factor to end of line is blank
CS_BlankFactor_EOL : '              '
    { getCharPositionInLine()==25 }? [ ]* NEWLINE -> type(EOL), popMode;

CS_FactorWs : (' '
    { (getCharPositionInLine()>=12 && getCharPositionInLine()<=25)
      || (getCharPositionInLine()>=36 && getCharPositionInLine()<=49)
    }?  
    )+
    -> skip;

CS_FactorWs2 : (' '
    { (getCharPositionInLine()>=50 && getCharPositionInLine()<=63)
    }? )+
    -> skip;
        
// This rather awkward token matches a literal. including whitespace literals
CS_FactorContentHexLiteral : [xX] [']
    { (getCharPositionInLine()>=13 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=37 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=51 && getCharPositionInLine()<=63)
    }?
    -> type(HexLiteralStart), pushMode(InFactorStringMode);
        
CS_FactorContentDateLiteral : [dD] [']
    { (getCharPositionInLine()>=13 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=37 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=51 && getCharPositionInLine()<=63)
    }?
         -> type(DateLiteralStart), pushMode(InFactorStringMode);
        
CS_FactorContentTimeLiteral : [tT] [']
    { (getCharPositionInLine()>=13 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=37 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=51 && getCharPositionInLine()<=63)
    }?
    -> type(TimeLiteralStart), pushMode(InFactorStringMode);
        
CS_FactorContentGraphicLiteral : [gG] [']
    {(getCharPositionInLine()>=13 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=37 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=51 && getCharPositionInLine()<=63)
    }?
    -> type(GraphicLiteralStart), pushMode(InFactorStringMode);
        
CS_FactorContentUCS2Literal : [uU] ['] 
    {(getCharPositionInLine()>=13 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=37 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=51 && getCharPositionInLine()<=63)
    }?
    -> type(UCS2LiteralStart), pushMode(InFactorStringMode);
        
CS_FactorContentStringLiteral : ['] 
     {(getCharPositionInLine()>=12 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=36 && getCharPositionInLine()<=49)
            || (getCharPositionInLine()>=50 && getCharPositionInLine()<=63)
    }?
    -> type(StringLiteralStart), pushMode(InFactorStringMode);
                
CS_FactorContent : (~[\r\n' :]
    {(getCharPositionInLine()>=12 && getCharPositionInLine()<=25)
            || (getCharPositionInLine()>=36 && getCharPositionInLine()<=49)
    }? )+;

CS_ResultContent : (~[\r\n' :]
    { (getCharPositionInLine()>=50 && getCharPositionInLine()<=63)
    }? )+
    -> type(CS_FactorContent);

CS_FactorColon : ([:]
    { (getCharPositionInLine()>12 && getCharPositionInLine()<25)
      || (getCharPositionInLine()>36 && getCharPositionInLine()<49)
      || (getCharPositionInLine()>50 && getCharPositionInLine()<63)
    }? )
    -> type(COLON);

CS_OperationAndExtender_Blank :  '          '
    { getCharPositionInLine()==35 }? ;

CS_OperationAndExtender_WS : ( [ ]
    { getCharPositionInLine()>=26 && getCharPositionInLine()<36
    }? )+
    -> skip;

CS_Operation_ACQ : OP_ACQ { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_ACQ);

CS_Operation_ADD : OP_ADD { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_ADD);

CS_Operation_ADDDUR : OP_ADDDUR { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_ADDDUR);

CS_Operation_ALLOC : OP_ALLOC { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ALLOC);

CS_Operation_ANDEQ : OP_ANDEQ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDEQ);

CS_Operation_ANDNE : OP_ANDNE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDNE);

CS_Operation_ANDLE : OP_ANDLE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDLE);

CS_Operation_ANDLT : OP_ANDLT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDLT);

CS_Operation_ANDGE : OP_ANDGE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDGE);

CS_Operation_ANDGT : OP_ANDGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDGT);

CS_Operation_ANDxx : OP_ANDxx { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ANDxx);

CS_Operation_BEGSR : OP_BEGSR { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_BEGSR);

CS_Operation_BITOFF : OP_BITOFF { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_BITOFF);

CS_Operation_BITON : OP_BITON { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_BITON);

CS_Operation_CABxx : OP_CABxx { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABxx);

CS_Operation_CABEQ : OP_CABEQ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABEQ);

CS_Operation_CABNE : OP_CABNE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABNE);

CS_Operation_CABLE : OP_CABLE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABLE);

CS_Operation_CABLT : OP_CABLT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABLT);

CS_Operation_CABGE : OP_CABGE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABGE);

CS_Operation_CABGT : OP_CABGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CABGT);

CS_Operation_CALL : OP_CALL { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_CALL);

CS_Operation_CALLB : OP_CALLB { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CALLB);

CS_Operation_CALLP : OP_CALLP { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CALLP);

CS_Operation_CASEQ : OP_CASEQ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASEQ);

CS_Operation_CASNE : OP_CASNE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASNE);

CS_Operation_CASLE : OP_CASLE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASLE);

CS_Operation_CASLT : OP_CASLT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASLT);

CS_Operation_CASGE : OP_CASGE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASGE);

CS_Operation_CASGT : OP_CASGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CASGT);

CS_Operation_CAS : OP_CAS { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_CAS);

CS_Operation_CAT : OP_CAT { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_CAT);

CS_Operation_CHAIN : OP_CHAIN { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CHAIN);

CS_Operation_CHECK : OP_CHECK { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CHECK);

CS_Operation_CHECKR : OP_CHECKR { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_CHECKR);

CS_Operation_CLEAR : OP_CLEAR { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CLEAR);

CS_Operation_CLOSE : OP_CLOSE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_CLOSE);

CS_Operation_COMMIT : OP_COMMIT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_COMMIT);

CS_Operation_COMP : OP_COMP { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_COMP);

CS_Operation_DEALLOC : OP_DEALLOC { getCharPositionInLine()>=32 && getCharPositionInLine()<36 }? -> type(OP_DEALLOC);

CS_Operation_DEFINE : OP_DEFINE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DEFINE);

CS_Operation_DELETE : OP_DELETE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DELETE);

CS_Operation_DIV : OP_DIV { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_DIV);

CS_Operation_DO : OP_DO { getCharPositionInLine()>=27 && getCharPositionInLine()<36 }? -> type(OP_DO);

CS_Operation_DOUEQ : OP_DOUEQ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOUEQ);

CS_Operation_DOUNE : OP_DOUNE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOUNE);

CS_Operation_DOULE : OP_DOULE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOULE);

CS_Operation_DOULT : OP_DOULT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOULT);

CS_Operation_DOUGE : OP_DOUGE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOUGE);

CS_Operation_DOUGT : OP_DOUGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOUGT);

CS_Operation_DOWEQ : OP_DOWEQ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWEQ);

CS_Operation_DOWNE : OP_DOWNE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWNE);

CS_Operation_DOWLE : OP_DOWLE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWLE);

CS_Operation_DOWLT : OP_DOWLT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWLT);

CS_Operation_DOWGE : OP_DOWGE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWGE);

CS_Operation_DOWGT : OP_DOWGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DOWGT);

CS_Operation_DSPLY : OP_DSPLY { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_DSPLY);

CS_Operation_DUMP : OP_DUMP { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_DUMP);

CS_Operation_ELSE : OP_ELSE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ELSE);

CS_Operation_END : OP_END { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_END);

CS_Operation_ENDCS : OP_ENDCS { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ENDCS);

CS_Operation_ENDDO : OP_ENDDO { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ENDDO);

CS_Operation_ENDFOR : OP_ENDFOR { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_ENDFOR);

CS_Operation_ENDIF : OP_ENDIF { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ENDIF);

CS_Operation_ENDMON : OP_ENDMON { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_ENDMON);

CS_Operation_ENDSL : OP_ENDSL { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ENDSL);

CS_Operation_ENDSR : OP_ENDSR { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ENDSR);

CS_Operation_EVAL : OP_EVAL { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_EVAL);

CS_Operation_EVALR : OP_EVALR { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_EVALR);

CS_Operation_EVAL_CORR : OP_EVAL_CORR { getCharPositionInLine()>=34 && getCharPositionInLine()<36 }? -> type(OP_EVAL_CORR);

CS_Operation_EXCEPT : OP_EXCEPT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_EXCEPT);

CS_Operation_EXFMT : OP_EXFMT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_EXFMT);

CS_Operation_EXSR : OP_EXSR { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_EXSR);

CS_Operation_EXTRCT : OP_EXTRCT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_EXTRCT);

CS_Operation_FEOD : OP_FEOD { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_FEOD);

CS_Operation_FOR : OP_FOR { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_FOR);

CS_Operation_FORCE : OP_FORCE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_FORCE);

CS_Operation_GOTO : OP_GOTO { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_GOTO);

CS_Operation_IFEQ : OP_IFEQ { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFEQ);

CS_Operation_IFNE : OP_IFNE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFNE);

CS_Operation_IFLE : OP_IFLE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFLE);

CS_Operation_IFLT : OP_IFLT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFLT);

CS_Operation_IFGE : OP_IFGE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFGE);

CS_Operation_IFGT : OP_IFGT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_IFGT);

CS_Operation_IN : OP_IN { getCharPositionInLine()>=27 && getCharPositionInLine()<36 }? -> type(OP_IN);

CS_Operation_ITER : OP_ITER { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ITER);

CS_Operation_KFLD : OP_KFLD { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_KFLD);

CS_Operation_KLIST : OP_KLIST { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_KLIST);

CS_Operation_LEAVE : OP_LEAVE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_LEAVE);

CS_Operation_LEAVESR : OP_LEAVESR { getCharPositionInLine()>=32 && getCharPositionInLine()<36 }? -> type(OP_LEAVESR);

CS_Operation_LOOKUP : OP_LOOKUP { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_LOOKUP);

CS_Operation_MHHZO : OP_MHHZO { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MHHZO);

CS_Operation_MHLZO : OP_MHLZO { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MHLZO);

CS_Operation_MLHZO : OP_MLHZO { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MLHZO);

CS_Operation_MLLZO : OP_MLLZO { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MLLZO);

CS_Operation_MONITOR : OP_MONITOR { getCharPositionInLine()>=32 && getCharPositionInLine()<36 }? -> type(OP_MONITOR);

CS_Operation_MOVE : OP_MOVE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_MOVE);

CS_Operation_MOVEA : OP_MOVEA { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MOVEA);

CS_Operation_MOVEL : OP_MOVEL { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_MOVEL);

CS_Operation_MULT : OP_MULT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_MULT);

CS_Operation_MVR : OP_MVR { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_MVR);

CS_Operation_NEXT : OP_NEXT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_NEXT);

CS_Operation_OCCUR : OP_OCCUR { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_OCCUR);

CS_Operation_ON_ERROR : OP_ON_ERROR { getCharPositionInLine()>=33 && getCharPositionInLine()<36 }? -> type(OP_ON_ERROR);

CS_Operation_OPEN : OP_OPEN { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_OPEN);

CS_Operation_OREQ : OP_OREQ { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_OREQ);

CS_Operation_ORNE : OP_ORNE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ORNE);

CS_Operation_ORLE : OP_ORLE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ORLE);

CS_Operation_ORLT : OP_ORLT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ORLT);

CS_Operation_ORGE : OP_ORGE { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ORGE);

CS_Operation_ORGT : OP_ORGT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_ORGT);

CS_Operation_OTHER : OP_OTHER { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_OTHER);

CS_Operation_OUT : OP_OUT { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_OUT);

CS_Operation_PARM : OP_PARM { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_PARM);

CS_Operation_PLIST : OP_PLIST { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_PLIST);

CS_Operation_POST : OP_POST { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_POST);

CS_Operation_READ : OP_READ { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_READ);

CS_Operation_READC : OP_READC { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_READC);

CS_Operation_READE : OP_READE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_READE);

CS_Operation_READP : OP_READP { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_READP);

CS_Operation_READPE : OP_READPE { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_READPE);

CS_Operation_REALLOC : OP_REALLOC { getCharPositionInLine()>=32 && getCharPositionInLine()<36 }? -> type(OP_REALLOC);

CS_Operation_REL : OP_REL { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_REL);

CS_Operation_RESET : OP_RESET { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_RESET);

CS_Operation_RETURN : OP_RETURN { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_RETURN);

CS_Operation_ROLBK : OP_ROLBK { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_ROLBK);

CS_Operation_SCAN : OP_SCAN { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_SCAN);

CS_Operation_SELECT : OP_SELECT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_SELECT);

CS_Operation_SETGT : OP_SETGT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SETGT);

CS_Operation_SETLL : OP_SETLL { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SETLL);

CS_Operation_SETOFF : OP_SETOFF { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_SETOFF);

CS_Operation_SETON : OP_SETON { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SETON);

CS_Operation_SORTA : OP_SORTA { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SORTA);

CS_Operation_SHTDN : OP_SHTDN { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SHTDN);

CS_Operation_SQRT : OP_SQRT { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_SQRT);

CS_Operation_SUB : OP_SUB { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_SUB);

CS_Operation_SUBDUR : OP_SUBDUR { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_SUBDUR);

CS_Operation_SUBST : OP_SUBST { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_SUBST);

CS_Operation_TAG : OP_TAG { getCharPositionInLine()>=28 && getCharPositionInLine()<36 }? -> type(OP_TAG);

CS_Operation_TEST : OP_TEST { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_TEST);

CS_Operation_TESTB : OP_TESTB { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_TESTB);

CS_Operation_TESTN : OP_TESTN { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_TESTN);

CS_Operation_TESTZ : OP_TESTZ { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_TESTZ);

CS_Operation_TIME : OP_TIME { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_TIME);

CS_Operation_UNLOCK : OP_UNLOCK { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_UNLOCK);

CS_Operation_UPDATE : OP_UPDATE { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_UPDATE);

CS_Operation_WHEN : OP_WHEN { getCharPositionInLine()>=29 && getCharPositionInLine()<36 }? -> type(OP_WHEN);

CS_Operation_WHENEQ : OP_WHENEQ { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENEQ);

CS_Operation_WHENNE : OP_WHENNE { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENNE);

CS_Operation_WHENLE : OP_WHENLE { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENLE);

CS_Operation_WHENLT : OP_WHENLT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENLT);

CS_Operation_WHENGE : OP_WHENGE { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENGE);

CS_Operation_WHENGT : OP_WHENGT { getCharPositionInLine()>=31 && getCharPositionInLine()<36 }? -> type(OP_WHENGT);

CS_Operation_WRITE : OP_WRITE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_WRITE);

CS_Operation_XFOOT : OP_XFOOT { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_XFOOT);

CS_Operation_XLATE : OP_XLATE { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_XLATE);

CS_Operation_XML_INTO : OP_XML_INTO { getCharPositionInLine()>=33 && getCharPositionInLine()<36 }? -> type(OP_XML_INTO);

CS_Operation_XML_SAX : OP_XML_SAX { getCharPositionInLine()>=32 && getCharPositionInLine()<36 }? -> type(OP_XML_SAX);

CS_Operation_Z_ADD : OP_Z_ADD { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_Z_ADD);

CS_Operation_Z_SUB : OP_Z_SUB { getCharPositionInLine()>=30 && getCharPositionInLine()<36 }? -> type(OP_Z_SUB);

CS_OperationAndExtender :  ([a-zA-Z0-9\\-]
    { getCharPositionInLine()>=26 && getCharPositionInLine()<36
    }? )+;

CS_OperationExtenderOpen : OPEN_PAREN { getCharPositionInLine()>=26 && getCharPositionInLine()<36 }? -> type(OPEN_PAREN);

CS_OperationExtenderClose : CLOSE_PAREN { getCharPositionInLine()>=26 && getCharPositionInLine()<36 }?
    ( ' '
    { getCharPositionInLine()>=26 && getCharPositionInLine()<36
    }? )* { setText(getText().trim()); }
    -> type(CLOSE_PAREN);
  
CS_FieldLength : [+\- 0-9] [+\- 0-9] [+\- 0-9] [+\- 0-9] [+\- 0-9]  { getCharPositionInLine()==68 }? ;

CS_DecimalPositions : [ 0-9] [ 0-9] { getCharPositionInLine()==70 }?
    -> pushMode(IndicatorMode), pushMode(IndicatorMode), pushMode(IndicatorMode); 

CS_WhiteSpace : [ \t] { getCharPositionInLine()>=77 }? [ \t]* -> skip  ; 

CS_Comments : ~[\r\n] { getCharPositionInLine()>80 }? ~[\r\n]*  ;

CS_FixedComments : ~[\r\n] { getCharPositionInLine()>=77 }? ~[\r\n]*  ;

CS_EOL : NEWLINE -> type(EOL), popMode;




mode OnOffIndicatorMode;
BlankFlag : [ ] -> popMode, pushMode(IndicatorMode);

NoFlag : [nN] -> popMode, pushMode(IndicatorMode);


mode IndicatorMode;
BlankIndicator : [ ] [ ] -> popMode;

GeneralIndicator : ([0] [1-9] | [1-9] [0-9]) -> popMode;

FunctionKeyIndicator : [Kk] [A-NP-Ya-np-y] -> popMode;

ControlLevelIndicator : [lL] [1-9] -> popMode;

ControlLevel0Indicator : [lL] [0] -> popMode;

LastRecordIndicator : [lL] [rR] -> popMode;

MatchingRecordIndicator : [mM] [rR] -> popMode;

HaltIndicator : [hH] [1-9] -> popMode;

ReturnIndicator : [rR] [tT] -> popMode;

ExternalIndicator : [uU] [1-8] -> popMode;

OverflowIndicator : [oO] [A-GVa-gv] -> popMode;

SubroutineIndicator : [sS] [rR] -> popMode;

AndIndicator : [aA] [nN] -> popMode;

OrIndicator : [oO] [rR] -> popMode;

DoubleSplatIndicator : '**';

FirstPageIndicator : [1] [pP] ;

OtherTextIndicator : ~[\r\n]~[\r\n] ;


mode FIXED_InputSpec;
IS_BLANK_SPEC :  '                                                                           '
    { getCharPositionInLine()==80
    }?
    -> type(BLANK_SPEC);

IS_FileName : WORD5_WCOLON WORD5_WCOLON { getCharPositionInLine()==16 }? ;

IS_FieldReserved : '                        ' { getCharPositionInLine()==30 }? -> pushMode(FIXED_I_FIELD_SPEC), skip ;

IS_ExtFieldReserved :  '              ' { getCharPositionInLine()==20}?-> pushMode(FIXED_I_EXT_FIELD_SPEC), skip ;

IS_LogicalRelationship :  ('AND' | 'OR '| ' OR') { getCharPositionInLine()==18 }? ;

IS_ExtRecordReserved : '    '
    { getCharPositionInLine()==20
    }?
    -> pushMode(FIXED_I_EXT_REC_SPEC), pushMode(IndicatorMode) ;

IS_Sequence : WORD_WCOLON WORD_WCOLON  { getCharPositionInLine()==18 }?;

IS_Number : [ 1nN]  { getCharPositionInLine()==19 }?;

IS_Option : [ oO] { getCharPositionInLine()==20 }? -> pushMode(IndicatorMode);

IS_RecordIdCode :  WORD5_WCOLON WORD5_WCOLON WORD5_WCOLON WORD5_WCOLON
        WORD_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON  { getCharPositionInLine()==46 }? ; //TODO better lexing

IS_WS : [ \t] { getCharPositionInLine()>=47 }? [ \t]* -> skip  ; // skip spaces, tabs

IS_COMMENTS : ~[\r\n] { getCharPositionInLine()>80 }? ~[\r\n]* -> channel(HIDDEN) ; // skip spaces, tabs, newlines

IS_EOL : NEWLINE -> type(EOL), popMode;


mode FIXED_I_EXT_FIELD_SPEC;
IF_Name : WORD5_WCOLON WORD5_WCOLON { getCharPositionInLine()==30 }? ;

IF_Reserved : '                  ' { getCharPositionInLine()==48 }? -> skip;

IF_FieldName : WORD5_WCOLON WORD5_WCOLON WORD_WCOLON WORD_WCOLON
    WORD_WCOLON WORD_WCOLON  { getCharPositionInLine()==62 }? ->pushMode(IndicatorMode), pushMode(IndicatorMode);

IF_Reserved2 : '  ' { getCharPositionInLine()==68 }? ->pushMode(IndicatorMode), pushMode(IndicatorMode), pushMode(IndicatorMode), skip; // 3 Indicators in a row

IF_WS : [ \t] { getCharPositionInLine()>=75 }? [ \t]* -> popMode, skip  ; // skip spaces, tabs


mode FIXED_I_EXT_REC_SPEC;
IR_WS : [ \t]{ getCharPositionInLine()>=23 }? [ \t]* -> type(WS), popMode  ; // skip spaces, tabs


mode FIXED_I_FIELD_SPEC;
IFD_DATA_ATTR : WORD_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON { getCharPositionInLine()==34 }? ;

IFD_DATETIME_SEP : ~[\r\n] { getCharPositionInLine()==35 }? ;

IFD_DATA_FORMAT : [A-Z ] { getCharPositionInLine()==36 }? ;

IFD_FIELD_LOCATION : WORD5_WCOLON WORD5_WCOLON { getCharPositionInLine()==46 }? ;

IFD_DECIMAL_POSITIONS : [ 0-9] [ 0-9] { getCharPositionInLine()==48 }? ;

IFD_FIELD_NAME : WORD5_WCOLON WORD5_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON { getCharPositionInLine()==62 }? ;

IFD_CONTROL_LEVEL : ('L'[0-9] | '  ') { getCharPositionInLine()==64 }? ;

IFD_MATCHING_FIELDS : ('M'[0-9] | '  ') { getCharPositionInLine()==66 }? ->pushMode(IndicatorMode), pushMode(IndicatorMode),
    pushMode(IndicatorMode), pushMode(IndicatorMode);

IFD_BLANKS : '      ' { getCharPositionInLine()==80 }? -> skip;

IFD_COMMENTS : ~[\r\n]{ getCharPositionInLine()>80 }? ~[\r\n]* -> channel(HIDDEN) ;

IFD_EOL : NEWLINE -> type(EOL), popMode, popMode;


mode HeaderSpecMode;
HS_OPEN_PAREN : OPEN_PAREN -> type(OPEN_PAREN);

HS_CLOSE_PAREN : CLOSE_PAREN -> type(CLOSE_PAREN);

HS_StringLiteralStart : ['] -> type(StringLiteralStart), pushMode(InStringMode) ;

HS_COLON : ':' -> type(COLON);

HS_ID : [#@%$*a-zA-Z] [&#@$*a-zA-Z0-9_/,.-]* -> type(ID);

HS_WhiteSpace : [ \t]+ -> skip  ; // skip spaces, tabs

HS_CONTINUATION : NEWLINE 
    WORD5 [hH] ~[*] -> skip;

HS_EOL : NEWLINE -> type(EOL), popMode;





// =============================================
// SHARED TOKEN DEFINITIONS (extracted from FREE mode)
// These tokens are referenced by other modes via type() aliasing
// =============================================
mode SharedTokens;  // Never directly entered; tokens referenced by other modes

SPLAT_ALL : '*' [aA] [lL] [lL] ;

SPLAT_NONE : '*' [nN] [oO] [nN] [eE] ;

SPLAT_YES : '*' [yY] [eE] [sS] ;

SPLAT_NO : '*' [nN] [oO] ;

SPLAT_ILERPG : '*' [iI] [lL] [eE] [rR] [pP] [gG] ;

SPLAT_COMPAT : '*' [cC] [oO] [mM] [pP] [aA] [tT] ;

SPLAT_CRTBNDRPG : '*' [cC] [rR] [tT] [bB] [nN] [dD] [rR] [pP] [gG] ;

SPLAT_CRTRPGMOD : '*' [cC] [rR] [tT] [rR] [pP] [gG] [mM] [oO] [dD] ;

SPLAT_VRM : '*' [vV] [0-9] [rR] [0-9] [mM] [0-9] ;

SPLAT_ALLG : '*' [aA] [lL] [lL] [gG] ;

SPLAT_ALLU : '*' [aA] [lL] [lL] [uU] ;

SPLAT_ALLTHREAD : '*' [aA] [lL] [lL] [tT] [hH] [rR] [eE] [aA] [dD] ;

SPLAT_ALLX : '*' [aA] [lL] [lL] [xX] ;

SPLAT_BLANKS : ('*' [bB] [lL] [aA] [nN] [kK] [sS] | '*' [bB] [lL] [aA] [nN] [kK]);

SPLAT_CANCL : '*' [cC] [aA] [nN] [cC] [lL] ;

SPLAT_CYMD : '*' [cC] [yY] [mM] [dD]('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_CMDY : '*' [cC] [mM] [dD] [yY]('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_CDMY : '*' [cC] [dD] [mM] [yY]('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_MDY : '*' [mM] [dD] [yY] ('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_DMY : '*' [dD] [mM] [yY] ('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_DFT : '*' [dD] [fF] [tT] ;

SPLAT_YMD : '*' [yY] [mM] [dD] ('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_JUL : '*' [jJ] [uU] [lL] ('0' | '/' | '-' | '.' | ',' | '&')? ;

SPLAT_JAVA : '*' [jJ] [aA] [vV] [aA] ;

SPLAT_ISO : '*' [iI] [sS] [oO]('0' | '-')?;

SPLAT_USA : '*' [uU] [sS] [aA]('0' | '/')?;

SPLAT_EUR : '*' [eE] [uU] [rR]('0' | '.')?;

SPLAT_JIS : '*' [jJ] [iI] [sS]('0' | '-')?;

SPLAT_DATE : '*' [dD] [aA] [tT] [eE] ;

SPLAT_DAY :  '*' [dD] [aA] [yY] ;

SPLAT_DETC : '*' [dD] [eE] [tT] [cC] ;

SPLAT_DETL : '*' [dD] [eE] [tT] [lL] ;

SPLAT_DTAARA : '*' [dD] [tT] [aA] [aA] [rR] [aA] ;

SPLAT_END :  '*' [eE] [nN] [dD] ;

SPLAT_ENTRY : '*' [eE] [nN] [tT] [rR] [yY] ;

SPLAT_EQUATE : '*' [eE] [qQ] [uU] [aA] [tT] [eE] ;

SPLAT_EXTDFT : '*' [eE] [xX] [tT] [dD] [fF] [tT] ;

SPLAT_EXT : '*' [eE] [xX] [tT] ;

SPLAT_FILE : '*' [fF] [iI] [lL] [eE] ;

SPLAT_GETIN : '*' [gG] [eE] [tT] [iI] [nN] ;

SPLAT_HIVAL : '*' [hH] [iI] [vV] [aA] [lL] ;

SPLAT_INIT : '*' [iI] [nN] [iI] [tT] ;

SPLAT_INDICATOR : ( '*' [iI] [nN] [0-9] [0-9] | '*' [iI] [nN] '(' [0-9] [0-9] ')' );

SPLAT_INZSR : '*' [iI] [nN] [zZ] [sS] [rR] ;

SPLAT_IN : '*' [iI] [nN] ;

SPLAT_INPUT : '*' [iI] [nN] [pP] [uU] [tT] ;

SPLAT_OUTPUT : '*' [oO] [uU] [tT] [pP] [uU] [tT] ;

SPLAT_JOBRUN : '*' [jJ] [oO] [bB] [rR] [uU] [nN] ;

SPLAT_JOB : '*' [jJ] [oO] [bB] ;

SPLAT_LDA : '*' [lL] [dD] [aA] ;

SPLAT_LIKE : '*' [lL] [iI] [kK] [eE] ;

SPLAT_LONGJUL : '*' [lL] [oO] [nN] [gG] [jJ] [uU] [lL] ;

SPLAT_LOVAL : '*' [lL] [oO] [vV] [aA] [lL] ;

SPLAT_KEY : '*' [kK] [eE] [yY] ;

SPLAT_MONTH : '*' [mM] [oO] [nN] [tT] [hH] ;

SPLAT_NEXT : '*' [nN] [eE] [xX] [tT] ;

SPLAT_NOIND : '*' [nN] [oO] [iI] [nN] [dD] ;

SPLAT_NOKEY : '*' [nN] [oO] [kK] [eE] [yY] ;

SPLAT_NULL : '*' [nN] [uU] [lL] [lL] ;

SPLAT_OFL : '*' [oO] [fF] [lL] ;

SPLAT_ON : '*' [oO] [nN] ;

SPLAT_ONLY : '*' [oO] [nN] [lL] [yY] ;

SPLAT_OFF : '*' [oO] [fF] [fF] ;

SPLAT_PDA : '*' [pP] [dD] [aA] ;

SPLAT_PLACE : '*' [pP] [lL] [aA] [cC] [eE] ;

SPLAT_PSSR : '*' [pP] [sS] [sS] [rR] ;

SPLAT_ROUTINE : '*' [rR] [oO] [uU] [tT] [iI] [nN] [eE] ;

SPLAT_START : '*' [sS] [tT] [aA] [rR] [tT] ;

SPLAT_SYS : '*' [sS] [yY] [sS] ;

SPLAT_TERM : '*' [tT] [eE] [rR] [mM] ;

SPLAT_TOTC : '*' [tT] [oO] [tT] [cC] ;

SPLAT_TOTL : '*' [tT] [oO] [tT] [lL] ;

SPLAT_USER : '*' [uU] [sS] [eE] [rR] ;

SPLAT_VAR : '*' [vV] [aA] [rR] ;

SPLAT_YEAR : '*' [yY] [eE] [aA] [rR] ;

SPLAT_ZEROS : ('*' [zZ] [eE] [rR] [oO] [sS] | '*' [zZ] [eE] [rR] [oO]);

SPLAT_HMS : '*' [hH] [mM] [sS]('0' | '/' | '-' | '.' | ',' | '&')?;

SPLAT_INLR : '*' [iI] [nN] [lL] [rR] ;

SPLAT_INOF : '*' [iI] [nN] [oO] [fF] ;

SPLAT_DATA : '*' [dD] [aA] [tT] [aA] ;

SPLAT_ASTFILL : '*' [aA] [sS] [tT] [fF] [iI] [lL] ;

SPLAT_CURSYM : '*' [cC] [uU] [rR] [sS] [yY] [mM] ;

SPLAT_MAX : '*' [mM] [aA] [xX] ;

SPLAT_LOCK : '*' [lL] [oO] [cC] [kK] ;

SPLAT_PROGRAM : '*' [pP] [rR] [oO] [gG] [rR] [aA] [mM] ;

SPLAT_EXTDESC : '*' [eE] [xX] [tT] [dD] [eE] [sS] [cC] ;

SPLAT_STRING : '*' [sS] [tT] [rR] [iI] [nN] [gG] ;

SPLAT_CONSTRUCTOR : '*' [cC] [oO] [nN] [sS] [tT] [rR] [uU] [cC] [tT] [oO] [rR] ;

SPLAT_LIKEDS : '*' [lL] [iI] [kK] [eE] [dD] [sS] ;

SPLAT_VARSIZE : '*' [vV] [aA] [rR] [sS] [iI] [zZ] [eE] ;

SPLAT_NOPASS : '*' [nN] [oO] [pP] [aA] [sS] [sS];

SPLAT_PROC : '*' [pP] [rR] [oO] [cC] ;

SPLAT_STATUS : '*' [sS] [tT] [aA] [tT] [uU] [sS] ;

SPLAT_D : '*' [dD];

SPLAT_H : '*' [hH];

SPLAT_HOURS : '*' [hH] [oO] [uU] [rR] [sS];

SPLAT_DAYS :  SPLAT_DAY [sS];

SPLAT_M : '*' [mM];

SPLAT_MINUTES : '*' [mM] [iI] [nN] [uU] [tT] [eE] [sS];

SPLAT_MONTHS : SPLAT_MONTH[sS];

SPLAT_MSECONDS : '*' [mM] [sS] [eE] [cC] [oO] [nN] [dD] [sS];

SPLAT_S : '*' [sS];

SPLAT_SECONDS : '*' [sS] [eE] [cC] [oO] [nN] [dD] [sS];

SPLAT_Y : '*' [yY];

SPLAT_YEARS : SPLAT_YEAR[sS];

NUMBER : ([0-9]+([.] [0-9]*)?) | [.] [0-9]+ ;

OPEN_PAREN : '(';

CLOSE_PAREN : ')';

COLON : ':' ;

EQUAL : '=' ;

UDATE : [uU] [dD] [aA] [tT] [eE] ;

UMONTH : [uU] [mM] [oO] [nN] [tT] [hH] ;

UYEAR : [uU] [yY] [eE] [aA] [rR] ;

UDAY : [uU] [dD] [aA] [yY] ;

StringLiteralStart : ['] -> pushMode(InStringMode) ;

HexLiteralStart : [xX] ['] -> pushMode(InStringMode) ;

DateLiteralStart : [dD] ['] -> pushMode(InStringMode) ;

TimeLiteralStart : [tT] ['] -> pushMode(InStringMode) ;

TimeStampLiteralStart : [zZ] ['] -> pushMode(InStringMode) ;

GraphicLiteralStart : [gG] ['] -> pushMode(InStringMode) ;

UCS2LiteralStart : [uU] ['] -> pushMode(InStringMode) ;

ID : '*' [iI] [nN] ( [kK] [a-np-yA-NP-Y] | [uU] [1-8] )
   | '*' [mM] [a-zA-Z] [a-zA-Z] [a-zA-Z] [0-9] [0-9] [0-9] [0-9]
   | [_#@%$a-zA-Z] { getCharPositionInLine() >= 7 }? [#@$a-zA-Z0-9_]* ;

BLANK_SPEC :
    '                                                                           '
    { getCharPositionInLine()==81 }? ;

EOL : NEWLINE -> popMode;

OP_ACQ : [Aa] [Cc] [Qq] ;

OP_BEGSR : [Bb] [Ee] [Gg] [Ss] [Rr] ;

OP_CALLP : [Cc] [Aa] [Ll] [Ll] [Pp] ;

OP_CHAIN : [Cc] [Hh] [Aa] [Ii] [Nn] ;

OP_CLEAR : [Cc] [Ll] [Ee] [Aa] [Rr] ;

OP_CLOSE : [Cc] [Ll] [Oo] [Ss] [Ee] ;

OP_COMMIT : [Cc] [Oo] [Mm] [Mm] [Ii] [Tt] ;

OP_DEALLOC : [Dd] [Ee] [Aa] [Ll] [Ll] [Oo] [Cc] ;

OP_DELETE : [Dd] [Ee] [Ll] [Ee] [Tt] [Ee] ;

OP_DSPLY : [Dd] [Ss] [Pp] [Ll] [Yy] ;

OP_DUMP : [Dd] [Uu] [Mm] [Pp] ;

OP_ELSE : [Ee] [Ll] [Ss] [Ee] ;

OP_ENDDO : [Ee] [Nn] [Dd] [Dd] [Oo] ;

OP_ENDFOR : [Ee] [Nn] [Dd] [Ff] [Oo] [Rr] ;

OP_ENDIF : [Ee] [Nn] [Dd] [Ii] [Ff] ;

OP_ENDMON : [Ee] [Nn] [Dd] [Mm] [Oo] [Nn] ;

OP_ENDSL : [Ee] [Nn] [Dd] [Ss] [Ll] ;

OP_ENDSR : [Ee] [Nn] [Dd] [Ss] [Rr] ;

OP_EVAL : [Ee] [Vv] [Aa] [Ll] ;

OP_EVALR : [Ee] [Vv] [Aa] [Ll] [Rr] ;

OP_EVAL_CORR : [Ee] [Vv] [Aa] [Ll] [-] [Cc] [Oo] [Rr] [Rr] ;

OP_EXCEPT : [Ee] [Xx] [Cc] [Ee] [Pp] [Tt] ;

OP_EXFMT : [Ee] [Xx] [Ff] [Mm] [Tt] ;

OP_EXSR : [Ee] [Xx] [Ss] [Rr] ;

OP_FEOD : [Ff] [Ee] [Oo] [Dd] ;

OP_FOR : [Ff] [Oo] [Rr] ;

OP_FORCE : [Ff] [Oo] [Rr] [Cc] [Ee] ;

OP_IN : [Ii] [Nn] ;

OP_ITER : [Ii] [Tt] [Ee] [Rr] ;

OP_LEAVE : [Ll] [Ee] [Aa] [Vv] [Ee] ;

OP_LEAVESR : [Ll] [Ee] [Aa] [Vv] [Ee] [Ss] [Rr] ;

OP_MONITOR : [Mm] [Oo] [Nn] [Ii] [Tt] [Oo] [Rr] ;

OP_NEXT : [Nn] [Ee] [Xx] [Tt] ;

OP_ON_ERROR : [Oo] [Nn] [-] [Ee] [Rr] [Rr] [Oo] [Rr] ;

OP_OPEN : [Oo] [Pp] [Ee] [Nn] ;

OP_OTHER : [Oo] [Tt] [Hh] [Ee] [Rr] ;

OP_OUT : [Oo] [Uu] [Tt] ;

OP_POST : [Pp] [Oo] [Ss] [Tt] ;

OP_READ : [Rr] [Ee] [Aa] [Dd] ;

OP_READC : [Rr] [Ee] [Aa] [Dd] [Cc] ;

OP_READE : [Rr] [Ee] [Aa] [Dd] [Ee] ;

OP_READP : [Rr] [Ee] [Aa] [Dd] [Pp] ;

OP_READPE : [Rr] [Ee] [Aa] [Dd] [Pp] [Ee] ;

OP_REL : [Rr] [Ee] [Ll] ;

OP_RESET : [Rr] [Ee] [Ss] [Ee] [Tt] ;

OP_RETURN : [Rr] [Ee] [Tt] [Uu] [Rr] [Nn] ;

OP_ROLBK : [Rr] [Oo] [Ll] [Bb] [Kk] ;

OP_SELECT : [Ss] [Ee] [Ll] [Ee] [Cc] [Tt] ;

OP_SETGT : [Ss] [Ee] [Tt] [Gg] [Tt] ;

OP_SETLL : [Ss] [Ee] [Tt] [Ll] [Ll] ;

OP_SORTA : [Ss] [Oo] [Rr] [Tt] [Aa] ;

OP_TEST : [Tt] [Ee] [Ss] [Tt] ;

OP_UNLOCK : [Uu] [Nn] [Ll] [Oo] [Cc] [Kk] ;

OP_UPDATE : [Uu] [Pp] [Dd] [Aa] [Tt] [Ee] ;

OP_WHEN : [Ww] [Hh] [Ee] [Nn] ;

OP_WRITE : [Ww] [Rr] [Ii] [Tt] [Ee] ;

OP_XML_INTO : [Xx] [Mm] [Ll] [-] [Ii] [Nn] [Tt] [Oo] ;

OP_XML_SAX : [Xx] [Mm] [Ll] [-] [Ss] [Aa] [Xx] ;


// Missing duration SPLAT tokens
SPLAT_MN : '*' [mM] [nN] ;
SPLAT_MS : '*' [mM] [sS] ;

mode FIXED_ExtensionSpec;
ES_BLANK_SPEC : '                                                                           '
    { getCharPositionInLine()>=80 }? -> type(BLANK_SPEC);
ES_Reserved1 : '    ' { getCharPositionInLine()==10 }? -> skip;
ES_FromFileName : WORD5_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON { getCharPositionInLine()==18 }? ;
ES_ToFileName : WORD5_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON { getCharPositionInLine()==26 }? ;
ES_TableName : WORD5_WCOLON WORD_WCOLON { getCharPositionInLine()==32 }? ;
ES_EntriesPerRecord : [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==35 }? ;
ES_NumberOfEntries : [ 0-9] [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==39 }? ;
ES_EntryLength : [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==42 }? ;
ES_DataFormat : [ PpBbLlRr] { getCharPositionInLine()==43 }? ;
ES_DecimalPositions : [ 0-9] { getCharPositionInLine()==44 }? ;
ES_Sequence : [ AaDd] { getCharPositionInLine()==45 }? ;
ES_AlternatingName : WORD5_WCOLON WORD_WCOLON { getCharPositionInLine()==51 }? ;
ES_AlternatingLength : [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==54 }? ;
ES_AlternatingDataFormat : [ PpBbLlRr] { getCharPositionInLine()==55 }? ;
ES_AlternatingDecimalPositions : [ 0-9] { getCharPositionInLine()==56 }? ;
ES_AlternatingSequence : [ AaDd] { getCharPositionInLine()==57 }? ;
ES_Comments : ~[\r\n] { getCharPositionInLine()>57 }? ~[\r\n]* -> channel(HIDDEN) ;
ES_WS : [ \t] { getCharPositionInLine()>80 }? [ \t]* -> skip ;
ES_EOL : NEWLINE -> type(EOL), popMode;


mode FIXED_LineCounterSpec;
LS_FileName : WORD5_WCOLON WORD_WCOLON WORD_WCOLON WORD_WCOLON { getCharPositionInLine()==14 }? ;
LS_LinesPerPage : [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==17 }? ;
LS_OverflowLine : [ 0-9] [ 0-9] [ 0-9] { getCharPositionInLine()==20 }? ;
LS_Remaining : ~[\r\n]+ -> skip ;
LS_EOL : NEWLINE -> type(EOL), popMode;
