parser grammar Rpg3Parser;

options { tokenVocab = Rpg3Lexer; }


// =============================================
// RPG3 PROGRAM STRUCTURE  
// =============================================

rpg3Program
    : (hspec_fixed | fspec_fixed | extensionSpec | lineCounterSpec
       | ispec_fixed | cspec_fixed | ospec_fixed
       | block | subroutine
       | star_comments | blank_line | blank_spec | directive)*
      compileTimeData?
      EOF
    ;

statement:
    ospec_fixed
    | fspec_fixed 
    | block
    | cspec_fixed
    | blank_spec
    | ispec_fixed 
    | hspec_fixed
    | star_comments
    | blank_line 
    | directive
    ;

block:
    ((csDOUxx | csDOWxx) statement* enddo)
    | ((CS_FIXED cspec_continuedIndicators* cs_controlLevel 
        indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
        csDO) statement* enddo)
    | ifstatement
    | casestatement
    ;

ifstatement:
    csIFxx
    statement*
    (elsestmt statement*)?
    endif
    ;

elsestmt:
    CS_FIXED cspec_continuedIndicators*
    cs_controlLevel indicatorsOff=onOffIndicatorsFlag 
    indicators=cs_indicators factor1=factor 
    OP_ELSE cspec_fixed_standard_parts
    ;

beginif: csIFxx;

begsr: csBEGSR;
endsr: csENDSR;

endif:
    CS_FIXED cspec_continuedIndicators*
    cs_controlLevel indicatorsOff=onOffIndicatorsFlag 
    indicators=cs_indicators factor1=factor 
    (csEND | csENDIF_rule)
    ;

csENDIF_rule: operation=OP_ENDIF cspec_fixed_standard_parts;

enddo:
    CS_FIXED cspec_continuedIndicators*
    cs_controlLevel indicatorsOff=onOffIndicatorsFlag 
    indicators=cs_indicators factor1=factor 
    (csEND | csENDDO)
    ;

cspec_fixed_standard: 
    csACQ | csADD | csADDDUR
    | csBITOFF | csBITON
    | csCABxx | csCABEQ | csCABNE | csCABLE | csCABLT | csCABGE | csCABGT
    | csCALL | csCAT | csCHAIN | csCHECK | csCHECKR
    | csCLEAR | csCLOSE | csCOMMIT | csCOMP
    | csDEFINE | csDELETE | csDIV | csDO
    | csDSPLY | csDUMP
    | csELSE | csEND | csENDCS | csENDDO
    | csEXCEPT | csEXFMT | csEXSR | csEXTRCT
    | csFEOD | csFORCE | csGOTO
    | csIN | csITER | csKLIST | csLEAVE | csLOOKUP
    | csMHHZO | csMHLZO | csMLHZO | csMLLZO
    | csMOVE | csMOVEA | csMOVEL | csMULT
    | csNEXT | csOCCUR | csOPEN | csOUT
    | csPLIST | csPOST
    | csREAD | csREADC | csREADE | csREADP | csREADPE
    | csREL | csRESET | csRETURN_FIXED | csROLBK
    | csSCAN | csSETGT | csSETLL | csSETOFF | csSETON | csSHTDN
    | csSORTA_FIXED | csSQRT | csSUB | csSUBDUR | csSUBST
    | csTAG | csTEST | csTESTB | csTESTN | csTESTZ | csTIME
    | csUNLOCK | csUPDATE | csWRITE
    | csXFOOT | csXLATE | csZ_ADD | csZ_SUB
    |(operation=CS_OperationAndExtender
    operationExtender=cs_operationExtender?
    cspec_fixed_standard_parts);

csRETURN_FIXED: operation=OP_RETURN cspec_fixed_standard_parts;
csSORTA_FIXED: operation=OP_SORTA cspec_fixed_standard_parts;

// =============================================
// EXTENSION SPEC (E-SPEC) - RPG3 specific  
// =============================================
extensionSpec: ES_FIXED 
    ES_FromFileName ES_ToFileName ES_TableName
    ES_EntriesPerRecord ES_NumberOfEntries ES_EntryLength ES_DataFormat
    ES_DecimalPositions ES_Sequence ES_AlternatingName ES_AlternatingLength
    ES_AlternatingDataFormat ES_AlternatingDecimalPositions ES_AlternatingSequence
    ES_Comments? (EOL|EOF) ;

// =============================================
// LINE COUNTER SPEC (L-SPEC)
// =============================================
lineCounterSpec: LS_FIXED 
    LS_FileName LS_LinesPerPage LS_OverflowLine (EOL|EOF) ;

// =============================================
// COMPILE-TIME DATA
// =============================================
compileTimeData: endSource;

// =============================================
// DIRECTIVE (no /FREE or /END-FREE)
// =============================================
directive: DIRECTIVE 
        ( title_directive
        | DIR_EJECT
        | space_directive
        | DIR_SET
        | DIR_RESTORE
        | dir_copy
        | dir_include 
        | dir_eof
        | dir_define
        | dir_undefine
        | dir_if
        | dir_elseif
        | dir_else
        | dir_endif
        )
    (EOL|EOF);

// =============================================
// SYMBOLIC CONSTANTS
// =============================================
symbolicConstants:
   SPLAT_ALL | SPLAT_NONE | SPLAT_NO | SPLAT_YES
   | SPLAT_ILERPG | SPLAT_COMPAT | SPLAT_CRTBNDRPG | SPLAT_CRTRPGMOD | SPLAT_VRM
   | SPLAT_ALLG | SPLAT_ALLU | SPLAT_ALLTHREAD | SPLAT_ALLX
   | SPLAT_BLANKS | SPLAT_CANCL
   | SPLAT_CYMD | SPLAT_CMDY | SPLAT_CDMY | SPLAT_MDY | SPLAT_DMY | SPLAT_DFT | SPLAT_YMD
   | SPLAT_JUL | SPLAT_INPUT | SPLAT_OUTPUT | SPLAT_ISO | SPLAT_KEY | SPLAT_NEXT
   | SPLAT_USA | SPLAT_EUR | SPLAT_JIS | SPLAT_JAVA
   | SPLAT_DATE | SPLAT_DAY | SPLAT_DETC | SPLAT_DETL | SPLAT_DTAARA
   | SPLAT_END | SPLAT_ENTRY | SPLAT_EQUATE | SPLAT_EXTDFT | SPLAT_EXT
   | SPLAT_FILE | SPLAT_GETIN | SPLAT_HIVAL | SPLAT_INIT | SPLAT_INDICATOR | SPLAT_INZSR
   | SPLAT_IN | SPLAT_JOBRUN | SPLAT_JOB | SPLAT_LDA | SPLAT_LIKE | SPLAT_LONGJUL
   | SPLAT_LOVAL | SPLAT_MONTH | SPLAT_NOIND | SPLAT_NOKEY | SPLAT_NULL
   | SPLAT_OFL | SPLAT_ON | SPLAT_ONLY | SPLAT_OFF | SPLAT_PDA | SPLAT_PLACE
   | SPLAT_PSSR | SPLAT_ROUTINE | SPLAT_START | SPLAT_SYS | SPLAT_TERM
   | SPLAT_TOTC | SPLAT_TOTL | SPLAT_USER | SPLAT_VAR | SPLAT_YEAR | SPLAT_ZEROS
   | SPLAT_HMS | SPLAT_INLR | SPLAT_INOF | SPLAT_DATA | SPLAT_ASTFILL | SPLAT_CURSYM
   | SPLAT_MAX | SPLAT_LOCK | SPLAT_PROGRAM
   | SPLAT_D | SPLAT_DAYS | SPLAT_H | SPLAT_HOURS | SPLAT_M | SPLAT_MINUTES
   | SPLAT_MONTHS | SPLAT_MN | SPLAT_MS | SPLAT_MSECONDS | SPLAT_S | SPLAT_SECONDS
   | SPLAT_Y | SPLAT_YEARS | SPLAT_EXTDESC | SPLAT_STRING
   | SPLAT_CONSTRUCTOR | SPLAT_LIKEDS | SPLAT_VARSIZE | SPLAT_NOPASS | SPLAT_PROC | SPLAT_STATUS
   ;

endSource: endSourceHead endSourceLine*;
endSourceHead: END_SOURCE ;
endSourceLine: EOS_Text (EOL|EOF);

star_comments: COMMENT_SPEC_FIXED comments?;//comments COMMENTS_EOL;
comments: COMMENTS_TEXT; 
casestatement:
	((CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor)
	(csCASEQ
	| csCASNE
	| csCASLE
	| csCASLT
	| csCASGE
	| csCASGT
	| csCAS))+
	casestatementend
;
casestatementend:
	CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor
	(csEND | csENDCS)
;

csIFxx:
CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	 (csIFEQ
	| csIFNE
	| csIFLE
	| csIFLT
	| csIFGE
	| csIFGT)
	andConds=csANDxx*
	orConds=csORxx*
;
csDOUxx:
CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	 (csDOUEQ
	| csDOUNE
	| csDOULE
	| csDOULT
	| csDOUGE
	| csDOUGT)
	andConds=csANDxx*
	orConds=csORxx*
;

csDOWxx:
CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	 (csDOWEQ
	| csDOWNE
	| csDOWLE
	| csDOWLT
	| csDOWGE
	| csDOWGT)
	andConds=csANDxx*
	orConds=csORxx*
;
		
complexCondxx:
	csANDxx
	|csORxx
;

csANDxx:
CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	(csANDEQ
	| csANDNE
	| csANDLE
	| csANDLT
	| csANDGE
	| csANDGT
	)
;

csORxx:
CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	(csOREQ
	| csORNE
	| csORLE
	| csORLT
	| csORGE
	| csORGT
	)
	andConds=csANDxx*
;
ospec_fixed: OS_FIXED (((OS_RecordName 
	OS_Type
	(os_fixed_pgmdesc1 | os_fixed_pgmdesc2)) | os_fixed_pgmfield) 
	  |os_fixed_pgmdesc_compound)
	OS_Comments?
	(EOL | EOF);

os_fixed_pgmdesc1:
	OS_FetchOverflow
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	OS_ExceptName
	OS_Space3 OS_Space3 OS_Space3 OS_Space3
	OS_RemainingSpace;

outputConditioningOnOffIndicator:
	onOffIndicatorsFlag
	outputConditioningIndicator;

outputConditioningIndicator:
	BlankIndicator
	| GeneralIndicator
	| FunctionKeyIndicator
	| ControlLevelIndicator
	| HaltIndicator
	| ExternalIndicator
	| OverflowIndicator
	| MatchingRecordIndicator
	| LastRecordIndicator
	| ReturnIndicator
	| FirstPageIndicator;
	
os_fixed_pgmdesc_compound:
	OS_AndOr
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	OS_ExceptName
	OS_Space3 OS_Space3 OS_Space3 OS_Space3
	OS_RemainingSpace;
	
os_fixed_pgmdesc2:
	OS_AddDelete
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	OS_ExceptName
	OS_RemainingSpace;
	
os_fixed_pgmfield:
	OS_FieldReserved
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	outputConditioningOnOffIndicator
	OS_FieldName 
	OS_EditNames
	OS_BlankAfter
	OS_EndPosition
	OS_DataFormat;
fspec_fixed: FS_FIXED FS_RecordName FS_Type FS_Designation FS_EndOfFile FS_Addution 
	FS_Sequence FS_Format FS_RecordLength FS_Limits FS_LengthOfKey FS_RecordAddressType FS_Organization FS_Device FS_Reserved? 
	(EOL|EOF);	
cspec_fixed:
	CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	cspec_fixed_standard;

cspec_continuedIndicators:
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag 
	indicators=cs_indicators 
	EOL
	CS_FIXED 
	;
	
cspec_blank:
	CS_FIXED
	BlankIndicator
	BlankFlag 
	BlankIndicator
	CS_BlankFactor
	CS_OperationAndExtender_Blank
	CS_BlankFactor
	CS_BlankFactor
	CS_FieldLength
	CS_DecimalPositions
	BlankIndicator
	BlankIndicator
	BlankIndicator
	(EOL | EOF);

blank_spec:
  cspec_blank
  | ( FS_FIXED | IS_FIXED | OS_FIXED)
	BLANK_SPEC
	(EOL | EOF)
 ;
subroutine:
begin=begsr
statement*
end=endsr
;
csBEGSR:
	CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	operation=OP_BEGSR
	cspec_fixed_standard_parts;
csENDSR:
	CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel 
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators factor1=factor 
	operation=OP_ENDSR
	cspec_fixed_standard_parts;
onOffIndicatorsFlag:
    BlankFlag
    | NoFlag;

cs_controlLevel:
    BlankIndicator
	| ControlLevel0Indicator
	| ControlLevelIndicator
	| LastRecordIndicator
	| SubroutineIndicator
	| AndIndicator
	| OrIndicator
;
cs_indicators:
BlankIndicator
	| GeneralIndicator
	| ControlLevelIndicator
	| FunctionKeyIndicator 
	| LastRecordIndicator
	| MatchingRecordIndicator
	| HaltIndicator
	| ReturnIndicator
	| ExternalIndicator
	| OverflowIndicator
;
resultIndicator:
 BlankIndicator
    | GeneralIndicator
	| ControlLevelIndicator
	| FunctionKeyIndicator 
	| LastRecordIndicator
	| MatchingRecordIndicator
	| HaltIndicator
	| ExternalIndicator
	| OverflowIndicator
	| ReturnIndicator
;
cspec_fixed_standard_parts: 
	factor2=factor
	result=resultType 
	len=CS_FieldLength 
	decimalPositions=CS_DecimalPositions 
	hi=resultIndicator 
	lo=resultIndicator
	eq=resultIndicator 
	cs_fixed_comments? (EOL|EOF);	

/*
 * Fixed op codes 
 */	
csACQ:
	operation=OP_ACQ
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;	
csADD:
	operation=OP_ADD
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;	
csADDDUR:
	operation=OP_ADDDUR
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csANDEQ:
	operation=OP_ANDEQ
	cspec_fixed_standard_parts;
csANDNE:
	operation=OP_ANDNE
	cspec_fixed_standard_parts;
csANDLE:
	operation=OP_ANDLE
	cspec_fixed_standard_parts;
csANDLT:
	operation=OP_ANDLT
	cspec_fixed_standard_parts;
csANDGE:
	operation=OP_ANDGE
	cspec_fixed_standard_parts;
csANDGT:
	operation=OP_ANDGT
	cspec_fixed_standard_parts;
//csBEGSR:
//	operation=OP_BEGSR
//	cspec_fixed_standard_parts;
csBITOFF:
	operation=OP_BITOFF
	cspec_fixed_standard_parts;
csBITON:
	operation=OP_BITON
	cspec_fixed_standard_parts;
csCABxx:
	operation=OP_CABxx
	cspec_fixed_standard_parts;
csCABEQ:
	operation=OP_CABEQ
	cspec_fixed_standard_parts;
csCABNE:
	operation=OP_CABNE
	cspec_fixed_standard_parts;
csCABLE:
	operation=OP_CABLE
	cspec_fixed_standard_parts;
csCABLT:
	operation=OP_CABLT
	cspec_fixed_standard_parts;
csCABGE:
	operation=OP_CABGE
	cspec_fixed_standard_parts;
csCABGT:
	operation=OP_CABGT
	cspec_fixed_standard_parts;
csCALL:
	operation=OP_CALL
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts
	csPARM*;
csCASEQ:
	operation=OP_CASEQ
	cspec_fixed_standard_parts;
csCASNE:
	operation=OP_CASNE
	cspec_fixed_standard_parts;
csCASLE:
	operation=OP_CASLE
	cspec_fixed_standard_parts;
csCASLT:
	operation=OP_CASLT
	cspec_fixed_standard_parts;
csCASGE:
	operation=OP_CASGE
	cspec_fixed_standard_parts;
csCASGT:
	operation=OP_CASGT
	cspec_fixed_standard_parts;
csCAS:
	operation=OP_CAS
	cspec_fixed_standard_parts;
csCAT:
	operation=OP_CAT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCHAIN:
	operation=OP_CHAIN
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCHECK:
	operation=OP_CHECK
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCHECKR:
	operation=OP_CHECKR
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCLEAR:
	operation=OP_CLEAR
	cspec_fixed_standard_parts;
csCLOSE:
	operation=OP_CLOSE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCOMMIT:
	operation=OP_COMMIT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csCOMP:
	operation=OP_COMP
	cspec_fixed_standard_parts;
csDEFINE:
	operation=OP_DEFINE
	cspec_fixed_standard_parts;
csDELETE:
	operation=OP_DELETE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csDIV:
	operation=OP_DIV
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts
	csMVR?;
csDO:
	operation=OP_DO
	cspec_fixed_standard_parts;
csDOUEQ:
	operation=OP_DOUEQ
	cspec_fixed_standard_parts;
csDOUNE:
	operation=OP_DOUNE
	cspec_fixed_standard_parts;
csDOULE:
	operation=OP_DOULE
	cspec_fixed_standard_parts;
csDOULT:
	operation=OP_DOULT
	cspec_fixed_standard_parts;
csDOUGE:
	operation=OP_DOUGE
	cspec_fixed_standard_parts;
csDOUGT:
	operation=OP_DOUGT
	cspec_fixed_standard_parts;
csDOWEQ:
	operation=OP_DOWEQ
	cspec_fixed_standard_parts;
csDOWNE:
	operation=OP_DOWNE
	cspec_fixed_standard_parts;
csDOWLE:
	operation=OP_DOWLE
	cspec_fixed_standard_parts;
csDOWLT:
	operation=OP_DOWLT
	cspec_fixed_standard_parts;
csDOWGE:
	operation=OP_DOWGE
	cspec_fixed_standard_parts;
csDOWGT:
	operation=OP_DOWGT
	cspec_fixed_standard_parts;
csDSPLY:
	operation=OP_DSPLY
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csDUMP:
	operation=OP_DUMP
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csELSE:
	operation=OP_ELSE
	cspec_fixed_standard_parts;
csEND:
	operation=OP_END
	cspec_fixed_standard_parts;
csENDCS:
	operation=OP_ENDCS
	cspec_fixed_standard_parts;
csENDDO:
	operation=OP_ENDDO
	cspec_fixed_standard_parts;
csEXCEPT:
	operation=OP_EXCEPT
	cspec_fixed_standard_parts;
csEXFMT:
	operation=OP_EXFMT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csEXSR:
	operation=OP_EXSR
	cspec_fixed_standard_parts;
csEXTRCT:
	operation=OP_EXTRCT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csFEOD:
	operation=OP_FEOD
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csFORCE:
	operation=OP_FORCE
	cspec_fixed_standard_parts;
csGOTO:
	operation=OP_GOTO
	cspec_fixed_standard_parts;
csIFEQ:
	operation=OP_IFEQ
	cspec_fixed_standard_parts;
csIFNE:
	operation=OP_IFNE
	cspec_fixed_standard_parts;
csIFLE:
	operation=OP_IFLE
	cspec_fixed_standard_parts;
csIFLT:
	operation=OP_IFLT
	cspec_fixed_standard_parts;
csIFGE:
	operation=OP_IFGE
	cspec_fixed_standard_parts;
csIFGT:
	operation=OP_IFGT
	cspec_fixed_standard_parts;
csIN:
	operation=OP_IN
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csITER:
	operation=OP_ITER
	cspec_fixed_standard_parts;
csKLIST:
	operation=OP_KLIST
	cspec_fixed_standard_parts
	csKFLD*;
csKFLD:
	CS_FIXED
	BlankIndicator
	BlankFlag 
	BlankIndicator
	CS_BlankFactor
	operation=OP_KFLD
	cspec_fixed_standard_parts;
csLEAVE:
	operation=OP_LEAVE
	cspec_fixed_standard_parts;
csLOOKUP:
	operation=OP_LOOKUP
	cspec_fixed_standard_parts;
csMHHZO:
	operation=OP_MHHZO
	cspec_fixed_standard_parts;
csMHLZO:
	operation=OP_MHLZO
	cspec_fixed_standard_parts;
csMLHZO:
	operation=OP_MLHZO
	cspec_fixed_standard_parts;
csMLLZO:
	operation=OP_MLLZO
	cspec_fixed_standard_parts;
csMOVE:
	operation=OP_MOVE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csMOVEA:
	operation=OP_MOVEA
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csMOVEL:
	operation=OP_MOVEL
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csMULT:
	operation=OP_MULT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csMVR:
	CS_FIXED
	BlankIndicator
	BlankFlag 
	BlankIndicator
	CS_BlankFactor
	operation=OP_MVR
	cspec_fixed_standard_parts;
csNEXT:
	operation=OP_NEXT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csOCCUR:
	operation=OP_OCCUR
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csOPEN:
	operation=OP_OPEN
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csOREQ:
	operation=OP_OREQ
	cspec_fixed_standard_parts;
csORNE:
	operation=OP_ORNE
	cspec_fixed_standard_parts;
csORLE:
	operation=OP_ORLE
	cspec_fixed_standard_parts;
csORLT:
	operation=OP_ORLT
	cspec_fixed_standard_parts;
csORGE:
	operation=OP_ORGE
	cspec_fixed_standard_parts;
csORGT:
	operation=OP_ORGT
	cspec_fixed_standard_parts;
csOUT:
	operation=OP_OUT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csPARM:
	CS_FIXED
	cspec_continuedIndicators*
	cs_controlLevel
	indicatorsOff=onOffIndicatorsFlag indicators=cs_indicators
	factor1=factor
	operation=OP_PARM
	cspec_fixed_standard_parts;
csPLIST:
	operation=OP_PLIST
	cspec_fixed_standard_parts
	csPARM*;
csPOST:
	operation=OP_POST
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREAD:
	operation=OP_READ
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREADC:
	operation=OP_READC
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREADE:
	operation=OP_READE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREADP:
	operation=OP_READP
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREADPE:
	operation=OP_READPE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csREL:
	operation=OP_REL
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csRESET:
	operation=OP_RESET
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csROLBK:
	operation=OP_ROLBK
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSCAN:
	operation=OP_SCAN
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSETGT:
	operation=OP_SETGT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSETLL:
	operation=OP_SETLL
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSETOFF:
	operation=OP_SETOFF
	cspec_fixed_standard_parts;
csSETON:
	operation=OP_SETON
	cspec_fixed_standard_parts;
csSHTDN:
	operation=OP_SHTDN
	cspec_fixed_standard_parts;
csSQRT:
	operation=OP_SQRT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSUB:
	operation=OP_SUB
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSUBDUR:
	operation=OP_SUBDUR
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csSUBST:
	operation=OP_SUBST
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csTAG:
	operation=OP_TAG
	cspec_fixed_standard_parts;
csTEST:
	operation=OP_TEST
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csTESTB:
	operation=OP_TESTB
	cspec_fixed_standard_parts;
csTESTN:
	operation=OP_TESTN
	cspec_fixed_standard_parts;
csTESTZ:
	operation=OP_TESTZ
	cspec_fixed_standard_parts;
csTIME:
	operation=OP_TIME
	cspec_fixed_standard_parts;
csUNLOCK:
	operation=OP_UNLOCK
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csUPDATE:
	operation=OP_UPDATE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csWHENEQ:
	operation=OP_WHENEQ
	cspec_fixed_standard_parts;
csWHENNE:
	operation=OP_WHENNE
	cspec_fixed_standard_parts;
csWHENLE:
	operation=OP_WHENLE
	cspec_fixed_standard_parts;
csWHENLT:
	operation=OP_WHENLT
	cspec_fixed_standard_parts;
csWHENGE:
	operation=OP_WHENGE
	cspec_fixed_standard_parts;
csWHENGT:
	operation=OP_WHENGT
	cspec_fixed_standard_parts;
csWRITE:
	operation=OP_WRITE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csXFOOT:
	operation=OP_XFOOT
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csXLATE:
	operation=OP_XLATE
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csZ_ADD:
	operation=OP_Z_ADD
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
csZ_SUB:
	operation=OP_Z_SUB
	operationExtender=cs_operationExtender? 
	cspec_fixed_standard_parts;
			
cs_operationExtender:
  OPEN_PAREN?
  extender=CS_OperationAndExtender
  extender2=CS_OperationAndExtender?
  extender3=CS_OperationAndExtender?
  extender4=CS_OperationAndExtender?
  CLOSE_PAREN;	
factor:
   content=factorContent (COLON (content2=factorContent | constant2=symbolicConstants))? | CS_BlankFactor | constant=symbolicConstants literal?;
   
factorContent:
CS_FactorContent | literal;

resultType:	
   CS_FactorContent (COLON (constant=symbolicConstants))?  | CS_BlankFactor;
cs_fixed_comments:CS_FixedComments;		

ispec_fixed: IS_FIXED 
	((IS_FileName
	//IS_LogicalRelationship
		(is_external_rec
		|is_rec)
		(EOL|EOF)
	)
	| (is_external_field
		(EOL|EOF)
	)
	| (IFD_DATA_ATTR
		IFD_DATETIME_SEP
		IFD_DATA_FORMAT
		IFD_FIELD_LOCATION
		IFD_DECIMAL_POSITIONS
		IFD_FIELD_NAME
		IFD_CONTROL_LEVEL
		IFD_MATCHING_FIELDS
		fieldRecordRelation
		fieldIndicator
		fieldIndicator
		fieldIndicator
		IFD_COMMENTS?
		(EOL|EOF)
	))
	;
	
fieldRecordRelation:
 BlankIndicator
    | GeneralIndicator
	| ControlLevelIndicator
	| MatchingRecordIndicator
	| ExternalIndicator
	| HaltIndicator
	| ReturnIndicator
;	

fieldIndicator:
 BlankIndicator
    | GeneralIndicator
	| ControlLevelIndicator
	| HaltIndicator
	| ExternalIndicator
	| ReturnIndicator
;	
is_external_rec:	
			IS_ExtRecordReserved
			resultIndicator
			WS?; 
is_rec:				
			IS_Sequence
			IS_Number
			IS_Option
			recordIdIndicator
			IS_RecordIdCode;
recordIdIndicator:
	GeneralIndicator
  | HaltIndicator
  | ControlLevelIndicator
  | LastRecordIndicator
  | ExternalIndicator
  | ReturnIndicator
  | BlankIndicator;

is_external_field:
			IF_Name
			IF_FieldName
			controlLevelIndicator
			matchingFieldsIndicator
			resultIndicator
			resultIndicator
			resultIndicator
;

controlLevelIndicator:
ControlLevelIndicator
| BlankIndicator;

matchingFieldsIndicator:
MatchingRecordIndicator
| BlankIndicator;

hspec_fixed: HS_FIXED 
	hs_expression*
	(EOL|EOF);
hs_expression: (ID (OPEN_PAREN (hs_parm (COLON hs_parm)*)? CLOSE_PAREN)?);
hs_parm: ID | hs_string | symbolicConstants;
hs_string: StringLiteralStart (StringContent | StringEscapedQuote )* StringLiteralEnd;
blank_line: BLANK_LINE;
space_directive: DIR_SPACE (NUMBER)?;
dir_copy: (DIR_COPY
		   ( 
			(((library=copyText DIR_Slash)? file=copyText)? member=copyText)
			| (DIR_Slash? (copyText DIR_Slash)+ copyText)
		   )
		  );
dir_include: (DIR_INCLUDE 
		   (
			(((library=copyText DIR_Slash)? file=copyText)? member=copyText)
			| (DIR_Slash? (copyText DIR_Slash)+ copyText)
		   )
		   (DIR_OtherText+)?
		  );
dir_if:
	DIR_IF not=DIR_NOT? DIR_DEFINED OPEN_PAREN copyText CLOSE_PAREN;
dir_elseif:
	DIR_ELSEIF not=DIR_NOT? DIR_DEFINED OPEN_PAREN copyText CLOSE_PAREN;
dir_else: DIR_ELSE;
dir_endif: DIR_ENDIF;
dir_define: DIR_DEFINE name=DIR_OtherText;
dir_undefine: DIR_UNDEFINE name=DIR_OtherText;
dir_eof:DIR_EOF;
copyText: DIR_OtherText
 | (StringLiteralStart StringContent StringLiteralEnd)
 | DIR_NOT
 | DIR_DEFINE;
trailing_ws: DIR_FREE_OTHER_TEXT;
//title_directive: DIR_TITLE title_text;
//title_directive: DIR_TITLE (DIR_OtherText)?;
title_directive: DIR_TITLE title_text*;
//title_text: (NUMBER | DIR_OtherText) (NUMBER | DIR_OtherText);
title_text: (NUMBER | DIR_OtherText);

literal: (StringLiteralStart|HexLiteralStart|DateLiteralStart|TimeLiteralStart|TimeStampLiteralStart|UCS2LiteralStart|GraphicLiteralStart) 
	content=(StringContent | StringEscapedQuote | PlusOrMinus)* StringLiteralEnd;

