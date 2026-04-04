Contents
ILE RPG Reference
About ILE RPG Reference
Who Should Use This Reference
Prerequisite and Related Information
What's New
What's New since 7.6?
What's New in 7.6?
What's New in 7.5?
What's New in 7.4?
What's New in 7.3?
What's New in 7.2?
What's New in 7.1?
What's New in 6.1?
What's New in V5R4?
What's New in V5R3?
What's New in V5R2?
What's New in V5R1?
What's New in V4R4?
What's New in V4R2?
What's New in V3R7?
What's New in V3R6/V3R2?
RPG IV Concepts
Symbolic Names and Reserved Words
Symbolic Names
Array Names
Conditional Compile Names
Data Structure Names
EXCEPT Names
Field Names
KLIST Names
Labels
Named Constants
Enumeration Names
PLIST Names
Prototype Names
Record Names
Subroutine Names
Table Names
RPG IV Words with Special Functions/Reserved Words
User Date Special Words
Rules for User Date
PAGE, PAGE1-PAGE7
Rules for PAGE, PAGE1-PAGE7
Compiler Directives
/FREE... /END-FREE
/TITLE
/EJECT
/SPACE
  iii
/SET
/RESTORE
/OVERLOAD DETAIL | NODETAIL
/CHARCOUNT NATURAL | STDCHARSIZE
/COPY or /INCLUDE
Results of the /COPY or /INCLUDE during Compile
Nested /COPY or /INCLUDE
Using /COPY, /INCLUDE in Source Files with Embedded SQL
Conditional Compilation Directives
Defining Conditions
/DEFINE
/UNDEFINE
Predefined Conditions
Conditions Relating to the Environment
Conditions Relating to the Command Being Used
Conditions Relating to the Target Release
Conditions Relating to Control Specification Keywords
Condition Expressions
Testing Conditions
/IF Condition-Expression
/ELSEIF Condition-Expression
/ELSE
/ENDIF
Rules for Testing Conditions
The /EOF Directive
/EOF
Handling of Directives by the RPG Preprocessor
Procedures and the Program Logic Cycle
Subprocedure Definition
Procedure Interface Definition
Return Values
Scope of Definitions
Subprocedures and Subroutines
Program Flow in RPG Modules: Cycle Versus Linear
Cycle Module
Use Caution Exporting Subprocedures in Cycle Modules
Linear Module
Linear Main Module
NOMAIN Module
Module Initialization
Initialization of Global Data
RPG Cycle and other implicit Logic
Program Cycle
General RPG IV Program Cycle
Detailed RPG IV Program Cycle
Subprocedure Calculations
Implicit Opening of Files and Locking of Data Areas
Implicit Closing of Files and Unlocking of Data Areas
RPG IV Indicators
Indicators Defined on RPG IV Specifications
Overflow Indicators
Record Identifying Indicators
Rules for Assigning Record Identifying Indicators
Control Level Indicators (L1-L9)
Rules for Control Level Indicators
Split Control Field
Field Indicators
Rules for Assigning Field Indicators
iv  
Resulting Indicators
Rules for Assigning Resulting Indicators
Indicators Not Defined on the RPG IV Specifications
External Indicators
Internal Indicators
First Page Indicator (1P)
Last Record Indicator (LR)
Matching Record Indicator (MR)
Return Indicator (RT)
Using Indicators
File Conditioning
Rules for File Conditioning
Field Record Relation Indicators
Assigning Field Record Relation Indicators
Function Key Indicators
Halt Indicators (H1-H9)
Indicators Conditioning Calculations
Positions 7 and 8
Positions 9-11
Indicators Used in Expressions
Indicators Conditioning Output
Indicators Referred to As Data
*IN
*INxx
Additional Rules
Summary of Indicators
File and Program Exception/Errors
File Exception/Errors
File Information Data Structure
File Feedback Information
Open Feedback Information
Input/Output Feedback Information
Device Specific Feedback Information
Get Attributes Feedback Information
Blocking Considerations
File Status Codes
File Exception/Error Subroutine (INFSR)
Program Exception/Errors
Program Status Data Structure
Program Status Codes
PSDS Example
Program Exception/Error Subroutine
General File Considerations
Rules for File Names
File devices
File device types
Global and Local Files
Open Access Files
Locating an Open Access Handler
Open Access Handlers
Example of an Open Access Handler
File Parameters
Variables Associated with Files
Example of passing a file and passing a data structure with the associated variables. ......201
Full Procedural Files
Primary/Secondary Multi-file Processing
Multi-file Processing with No Match Fields
Multi-file Processing with Match Fields
  v
Assigning Match Field Values (M1-M9)
Processing Matching Records
File Translation
Specifying File Translation
Translating One File or All Files
Translating More Than One File
Specifying the Files
Specifying the Table
How character data is processed for file I/O
Definitions
Defining Data and Prototypes
General Considerations
Scope of Definitions
Storage of Definitions
Standalone Fields
Variable Initialization
Constants
Literals
CCSID of literals and compile-time data
Example of Defining Literals
Example of Using Literals with Zero Length
Named Constants
Figurative Constants
Rules for Figurative Constants
Data Structures
Qualifying Data Structure Names
Array Data Structures
Defining Data Structure Parameters in a Prototype or Procedure Interface
Defining Data Structure Subfields
Specifying Subfield Length
Aligning Data Structure Subfields
Initialization of Nested Data Structures
Special Data Structures
Data Area Data Structure
File Information Data Structure
Program-Status Data Structure
Indicator Data Structure
Data Structure Examples
Prototypes and Parameters
Prototypes
Prototyped Parameters
Procedure Interface
Enumerations
Typed Enumerations
Using Arrays and Tables
Arrays
Array Name and Index
The Essential Array Specifications
Coding a Run-Time Array
Loading a Run-Time Array
Loading a Run-Time Array by Reading One Record from a File
Loading a Run-Time Array by Reading Several Records from A File
Loading an Array from Identical Externally-Described Fields
Sequencing Run-Time Arrays
Coding a Compile-Time Array
Loading a Compile-Time Array
Rules for Array Source Records
vi  
Coding a Prerun-Time Array
Example of Coding Arrays
Loading a Prerun-Time Array
Sequence Checking for Character Arrays
Initializing Arrays
Run-Time Arrays
Compile-Time and Prerun-Time Arrays
Defining Related Arrays
Searching Arrays
Searching an Array Without an Index
Searching an Array Data Structure
Searching an Array with an Index
Using Arrays
Specifying an Array in Calculations
Sorting Arrays
Sorting using part of the array as a key
Sorting an Array Data Structure
Array Output
Editing Entire Arrays
Using Dynamically-Sized Arrays 
Tables
LOOKUP with One Table
LOOKUP with Two Tables
Specifying the Table Element Found in a LOOKUP Operation
Data Types and Data Formats
Internal and External Formats
Internal Format
External Format
Specifying an External Format for a Numeric Field
Specifying an External Format for a Character, Graphic, or UCS-2 Field
Specifying an External Format for a Date-Time Field
Character Data Type
Character Format
Indicator Format
Graphic Format
UCS-2 Format
Variable-Length Character, Graphic and UCS-2 Formats
Size of the Length-Prefix for a Varying Length Item
Rules for Variable-Length Character, Graphic, and UCS-2 Formats
Using Variable-Length Fields
CVTOPT(*VARCHAR) and CVTOPT(*VARGRAPHIC)
Conversion between Character, Graphic and UCS-2 Data
CCSIDs of Data
Conversions
CCSID conversions during input and output operations
Processing string data by the natural size of each character
Alternate Collating Sequence
Changing the Collating Sequence
Using an External Collating Sequence
Specifying an Alternate Collating Sequence in Your Source
Formatting the Alternate Collating Sequence Records
Numeric Data Type
Binary-Decimal Format
Processing of a Program-Described Binary Input Field
Processing of an Externally Described Binary Input Field
Float Format
External Display Representation of a Floating-Point Field
Integer Format
  vii
Packed-Decimal Format
Determining the Digit Length of a Packed-Decimal Field
Unsigned Format
Zoned-Decimal Format
Considerations for Using Numeric Formats
Guidelines for Choosing the Numeric Format for a Field
Representation of Numeric Formats
Date Data Type
Separators
Initialization
Time Data Type
Separators
Initialization
*JOBRUN
Timestamp Data Type
Separators
Initialization
Object Data Type
Where You Can Specify an Object Field
Basing Pointer Data Type
Setting a Basing Pointer 
Examples
Procedure Pointer Data Type
Database Null Value Support
User Controlled Support for Null-Capable Fields and Key Fields
Null-capable fields in externally-described data structures 
Input of Null-Capable Fields
Output of Null-Capable Fields
Keyed Operations
Input-Only Support for Null-Capable Fields
ALWNULL(*NO)
Error Handling for Database Data Mapping Errors
Editing Numeric Fields
Edit Codes
Simple Edit Codes
Combination Edit Codes
User-Defined Edit Codes
Editing Considerations
Summary of Edit Codes
Edit Words
How to Code an Edit Word
Parts of an Edit Word
Forming the Body of an Edit Word
Forming the Status of an Edit Word
Formatting the Expansion of an Edit Word
Summary of Coding Rules for Edit Words
Editing Externally Described Files
Specifications
About Specifications
RPG IV Specification Types
Main Source Section Specifications
Subprocedure Specifications
Program Data
Free-Form Statements
Fully free-form statements
Conditional Directives Within a Free-Form Statement
Differences between fixed-form and free-form to be aware of
viii  
Common Entries
Syntax of Keywords
Continuation Rules
Control Specification Keyword Field
File Description Specification Keyword Field
Definition Specification Keyword Field
Calculation Specification Extended Factor-2
Free-Form Specification
Output Specification Constant/Editword Field
Definition and Procedure Specification Name Field
Control Specifications
Using a Data Area as a Control Specification
Free-Form Control Statement
Traditional Control-Specification Statement
Position 6 (Form Type)
Positions 7-80 (Keywords)
Control-Specification Keywords
ACTGRP(*STGMDL | *NEW | *CALLER | 'activation-group-name')
ALLOC(*STGMDL | *TERASPACE | *SNGLVL)
ALTSEQ{(*NONE | *SRC | *EXT)}
ALWNULL(*NO | *INPUTONLY | *USRCTL)
AUT(*LIBRCRTAUT | *ALL | *CHANGE | *USE | *EXCLUDE | 'authorization-list-name')........358
BNDDIR('binding-directory-name' {:'binding-directory-name' …})
CCSID control keyword
CCSID(*EXACT)
CCSID(*CHAR : *JOBRUN | *JOBRUNMIX | *UTF8 | *HEX | number)
CCSID(*GRAPH : *JOBRUN | *SRC | *HEX | *IGNORE | number)360
CCSID(*UCS2 : *UTF16 | number)
CCSIDCVT(*EXCP | *LIST)
CHARCOUNT(*NATURAL | *STDCHARSIZE)
CHARCOUNTTYPES(*UTF8 *UTF16 *JOBRUN *MIXEDEBCDIC *MIXEDASCII)
COPYNEST(number)
COPYRIGHT('copyright string')
CURSYM('sym')
CVTOPT(*{NO}DATETIME *{NO}GRAPHIC *{NO}VARCHAR *{NO}VARGRAPHIC)
DATEDIT(fmt{separator})
DATEYY(*ALLOW | *WARN | *NOALLOW)
DATFMT(fmt{separator})
DCLOPT(*NOCHGDSLEN)
DEBUG{(*DUMP | *INPUT | *RETVAL | *XMLSAX | *NO | *YES)}
DECEDIT(*JOBRUN | 'value')
DECPREC(30|31|63)
DFTACTGRP(*YES | *NO)
DFTNAME(rpg_name)
ENBPFRCOL(*PEP | *ENTRYEXIT | *FULL)
EXPROPTS(*MAXDIGITS | *RESDECPOS | *ALWBLANKNUM | *USEDECEDIT)
EXTBININT{(*NO | *YES)}
FIXNBR(*{NO}ZONED *{NO}INPUTPACKED)
FLTDIV{(*NO | *YES)}
FORMSALIGN{(*NO | *YES)}
FTRANS{(*NONE | *SRC)}
GENLVL(number)
INDENT(*NONE | 'character-value')
INTPREC(10 | 20)
LANGID(*JOBRUN | *JOB | 'language-identifier')
MAIN(main_procedure_name)
NOMAIN
OPENOPT (*{NO}INZOFL *{NO}CVTDATA)
  ix
OPTIMIZE(*NONE | *BASIC | *FULL)
OPTION(*{NO}XREF *{NO}GEN *{NO}SECLVL *{NO}SHOWCPY *{NO}EXPDDS *{NO}EXT
*{NO}SHOWSKP) *{NO}SRCSTMT) *{NO}DEBUGIO) *{NO}UNREF
PGMINFO(*PCML | *NO | *DCLCASE { : *MODULE { : *Vx }})
Examples
PRFDTA(*NOCOL | *COL)
REQPREXP(*NO | *WARN | *REQUIRE)
SRTSEQ(*HEX | *JOB | *JOBRUN | *LANGIDUNQ | *LANGIDSHR | 'sort-table-name')........381
STGMDL(*INHERIT | *SNGLVL | *TERASPACE)
TEXT(*SRCMBRTXT | *BLANK | 'description')
THREAD(*CONCURRENT | *SERIALIZE) 
THREAD(*CONCURRENT) 
THREAD(*SERIALIZE) 
General thread considerations
TIMFMT(fmt{separator})
TRUNCNBR(*YES | *NO)
USRPRF(*USER | *OWNER)
VALIDATE(*NODATETIME)
File Description Specifications
Free-Form File Definition Statement
Equivalent Free-form Coding for Fixed-Form File Entries
Device-type Keywords
Defining the File Usage in Free-Form
Traditional File Description Specification Statement
File-Description Keyword Continuation Line
Position 6 (Form Type)
Positions 7-16 (File Name)
Program-Described File
Externally-Described File
Position 17 (File Type)
Input Files
Output Files
Update Files
Combined Files
Position 18 (File Designation)
Primary File
Secondary File
Record Address File (RAF)
Array or Table File
Full Procedural File
Position 19 (End of File)
Position 20 (File Addition)
Position 21 (Sequence)
Position 22 (File Format)
Positions 23-27 (Record Length)
Position 28 (Limits Processing)
Positions 29-33 (Length of Key or Record Address)
Position 34 (Record Address Type)
Blank=Non-keyed Processing
A=Character Keys
P=Packed Keys
G=Graphic Keys
K=Key
D=Date Keys
T=Time Keys
Z=Timestamp Keys
F=Float Keys
Position 35 (File Organization)
x  
Blank=Non-keyed Program-Described File
I=Indexed File
T=Record Address File
Positions 36-42 (Device)
File device types
Position 43 (Reserved)
Positions 44-80 (Keywords)
File-Description Keywords
ALIAS
BLOCK(*YES |*NO)
COMMIT{(rpg_name)}
CHARCOUNT(*NATURAL | *STDCHARSIZE)
DATA(*CVT | *NOCVT)
Examples of the DATA keyword
DATFMT(format{separator})
DEVID(fieldname)
DISK{(*EXT | record-length)}
EXTDESC(external-filename)
EXTFILE(filename | *EXTDESC) 
EXTIND(*INUx)
EXTMBR(membername)
FORMLEN(number)
FORMOFL(number)
HANDLER(program-or-procedure { : communication-area)})
IGNORE(recformat{:recformat...})
INCLUDE(recformat{:recformat...})
INDDS(data_structure_name)
INFDS(DSname)
INFSR(SUBRname)
KEYED{(*CHAR : key-length)}
KEYLOC(number)
LIKEFILE(parent-filename)
Rules for the LIKEFILE keyword:
MAXDEV(*ONLY | *FILE)
OFLIND(indicator)
PASS(*NOIND)
PGMNAME(program_name)
PLIST(Plist_name)
PREFIX(prefix{:nbr_of_char_replaced})
PRINTER{(*EXT | record-length)}
PRTCTL(data_struct{:*COMPAT})
Extended Length PRTCTL Data Structure
*COMPAT PRTCTL Data Structure
QUALIFIED
Rules for the QUALIFIED keyword: 
RAFDATA(filename)
RECNO(fieldname)
RENAME(Ext_format:Int_format)
SAVEDS(DSname)
SAVEIND(number)
SEQ{(*EXT | record-length)}
SFILE(recformat:rrnfield)
SLN(number)
SPECIAL{(*EXT | record-length)}
STATIC
Rules for the STATIC keyword:
TEMPLATE
Rules for the TEMPLATE keyword: 
  xi
TIMFMT(format{separator})
USAGE(*INPUT *OUTPUT *UPDATE *DELETE)
USROPN
WORKSTN{(*EXT | record-length)}
File Types and Processing Methods
Definition Specifications
Free-Form Definition Statement
Data-type Keywords
Keyword differences between fixed form and free form definitions
Free-Form Named Constant Definition
Free-Form Enumeration Definition
Free-Form Standalone Field Definition
Equivalent Free-form Coding for Standalone Field Entries
Free-Form Data Structure Definition
Equivalent Free-form Coding for Data Structure Entries
Free-Form Subfield Definition
Equivalent Free-form Coding for Subfield Entries
Free-Form Prototype Definition
Equivalent Free-form Coding for Prototype Entries
Free-Form Procedure Interface Definition
Equivalent Free-form Coding for Procedure Interface Entries
Free-Form Parameter Definition
Equivalent Free-form Coding for Parameter Entries
Traditional Definition Specification Statement
Definition Specification Keyword Continuation Line
Definition Specification Continued Name Line
Position 6 (Form Type)
Positions 7-21 (Name)
Position 22 (External Description)
Position 23 (Type of Data Structure)
Positions 24-25 (Definition Type)
Positions 26-32 (From Position)
Positions 33-39 (To Position / Length)
Position 40 (Internal Data Type)
Positions 41-42 (Decimal Positions)
Position 43 (Reserved)
Positions 44-80 (Keywords)
Definition-Specification Keywords
ALIAS
ALIGN{(*FULL)}
ALT(array_name)
ALTSEQ(*NONE)
ASCEND
BASED(basing_pointer_name)
BINDEC(digits {: decimal-positions})
CCSID definition keyword
CCSID(*EXACT | *NOEXACT)
CHAR(length)
CLASS(*JAVA:class-name)
CONST{(constant)}
CTDATA
DATE{(format{separator})}
DATFMT(format{separator})
DFT (Default enumeration constant)
DESCEND
DIM({*AUTO:|*CTDATA|*VAR:}numeric_constant)
Varying-dimension arrays
DTAARA keyword
xii  
Free-form DTAARA keyword for a field or subfield
Free-form DTAARA keyword for a data structure
Fixed-form DTAARA keyword
EXPORT{(external_name)}
EXT
EXTFLD{(field_name)}
EXTFMT(code)
EXTNAME(file-name{:format-name}{:*ALL| *INPUT|*OUTPUT|*KEY|*NULL})
EXTPGM{(name)}
EXTPROC{({*CL|*CWIDEN|*CNOWIDEN|*JAVA:class-name:}name|*DCLCASE)}
Specifying *DCLCASE as the External Name
FLOAT(bytes)
FROMFILE(file_name)
GRAPH(length)
IMPORT{(external_name)}
INT(digits)
IND
INZ{(initial value)}
LEN(length)
Rules for the LEN keyword:
LIKE(name {: length-adjustment})
LIKE(object-name)
LIKEDS(data_structure_name)
LIKEFILE(filename)
Rules for the LIKEFILE keyword for prototyped parameters: 
LIKEREC(intrecname{:extract-types})
NOOPT
NULLIND{(null-indicator)}
OBJECT{(*JAVA:class-name)}
OCCURS(numeric_constant)
OPDESC
OPTIONS(*NOPASS *OMIT *VARSIZE *EXACT *STRING *TRIM *RIGHTADJ *NULLIND
*CONVERT)
OVERLAY(name{:start_pos | *NEXT})
OVERLOAD(prototype1 { : prototype2 ...})
PACKED(digits {: decimal-positions})
PACKEVEN
PERRCD(numeric_constant)
POINTER{(*PROC)}
POS(starting-position)
PREFIX(prefix{:nbr_of_char_replaced})
PROCPTR
PSDS
QUALIFIED
REQPROTO(*NO)
RTNPARM
SAMEPOS(subfield)
STATIC{(*ALLTHREAD)} 
Additional Considerations for STATIC(*ALLTHREAD) 
TEMPLATE
Rules for the TEMPLATE keyword for Definition specifications: 
TIME{(format{separator})}
TIMESTAMP{(fractional-seconds)}
TIMFMT(format{separator})
TOFILE(file_name)
UCS2(length)
UNS(digits)
VALUE
  xiii
VARCHAR(length {:2 | 4})
VARGRAPH(length {:2 | 4})
VARUCS2(length {:2 | 4})
VARYING{(2 | 4)}
ZONED(digits {: decimal-positions})
Summary According to Definition Specification Type
Input Specifications
Input Specification Statement
Program Described
Externally Described
Program Described Files
Position 6 (Form Type)
Record Identification Entries
Positions 7-16 (File Name)
Positions 16-18 (Logical Relationship)
Positions 17-18 (Sequence)
Alphabetic Entries
Numeric Entries
Position 19 (Number)
Position 20 (Option)
Positions 21-22 (Record Identifying Indicator, or **)
Indicators
Lookahead Fields
Positions 23-46 (Record Identification Codes)
Positions 23-27, 31-35, and 39-43 (Position)
Positions 28, 36, and 44 (Not)
Positions 29, 37, and 45 (Code Part)
Positions 30, 38, and 46 (Character)
AND Relationship
OR Relationship
Field Description Entries
Position 6 (Form Type)
Positions 7-30 (Reserved)
Positions 31-34 (Data Attributes)
Position 35 (Date/Time Separator)
Position 36 (Data Format)
Positions 37-46 (Field Location)
Positions 47-48 (Decimal Positions)
Positions 49-62 (Field Name)
Positions 63-64 (Control Level)
Positions 65-66 (Matching Fields)
Positions 67-68 (Field Record Relation)
Positions 69-74 (Field Indicators)
Externally Described Files
Position 6 (Form Type)
Record Identification Entries
Positions 7-16 (Record Name)
Positions 17-20 (Reserved)
Positions 21-22 (Record Identifying Indicator)
Positions 23-80 (Reserved)
Field Description Entries
Positions 7-20 (Reserved)
Positions 21-30 (External Field Name)
Positions 31-48 (Reserved)
Positions 49-62 (Field Name)
Positions 63-64 (Control Level)
Positions 65-66 (Matching Fields)
Positions 67-68 (Reserved)
xiv  
Positions 69-74 (Field Indicators)
Positions 75-80 (Reserved)
Calculation Specifications
Traditional Syntax
Calculation Specification Extended Factor-2 Continuation Line
Position 6 (Form Type)
Positions 7-8 (Control Level)
Control Level Indicators
Last Record Indicator
Subroutine Identifier
AND/OR Lines Identifier
Positions 9-11 (Indicators)
Positions 12-25 (Factor 1)
Positions 26-35 (Operation and Extender)
Operation Extender
Positions 36-49 (Factor 2)
Positions 50-63 (Result Field)
Positions 64-68 (Field Length)
Positions 69-70 (Decimal Positions)
Positions 71-76 (Resulting Indicators)
Extended Factor 2 Syntax
Positions 7-8 (Control Level)
Positions 9-11 (Indicators)
Positions 12-25 (Factor 1)
Positions 26-35 (Operation and Extender)
Operation Extender
Positions 36-80 (Extended Factor 2)
Free-Form Calculation Statement
Free-form Operations
Output Specifications
Output Specification Statement
Program Described
Externally Described
Program Described Files
Position 6 (Form Type)
Record Identification and Control Entries
Positions 7-16 (File Name)
Positions 16-18 ( Logical Relationship)
Position 17 (Type)
Positions 18-20 (Record Addition/Deletion)
Position 18 (Fetch Overflow/Release)
Fetch Overflow
Release
Positions 21-29 (Output Conditioning Indicators)
Positions 30-39 (EXCEPT Name)
Positions 40-51 (Space and Skip)
Positions 40-42 (Space Before)
Positions 43-45 (Space After)
Positions 46-48 (Skip Before)
Positions 49-51 (Skip After)
Field Description and Control Entries
Positions 21-29 (Output Indicators)
Positions 30-43 (Field Name)
Field Names, Blanks, Tables and Arrays
PAGE, PAGE1-PAGE7
*PLACE
User Date Reserved Words
*IN, *INxx, *IN(xx)
  xv
Position 44 (Edit Codes)
Position 45 (Blank After)
Positions 47-51 (End Position)
Position 52 (Data Format)
Positions 53-80 (Constant, Edit Word, Data Attributes, Format Name)
Constants
Edit Words
Data Attributes
Record Format Name
Externally Described Files
Position 6 (Form Type)
Record Identification and Control Entries
Positions 7-16 (Record Name)
Positions 16-18 ( Logical Relationship)
Position 17 (Type)
Position 18 (Release)
Positions 18-20 (Record Addition)
Positions 21-29 (Output Indicators)
Positions 30-39 (EXCEPT Name)
Field Description and Control Entries
Positions 21-29 (Output Indicators)
Positions 30-43 (Field Name)
Position 45 (Blank After)
Procedure Specifications
Free-Form Procedure Statement
Traditional Procedure Specification Statement
Procedure Specification Keyword Continuation Line
Procedure Specification Continued Name Line
Position 6 (Form Type)
Positions 7-21 (Name)
Position 24 (Begin/End Procedure)
Positions 44-80 (Keywords)
Procedure-Specification Keywords
EXPORT
PGMINFO(*YES | *NO)
REQPROTO(*NO)
SERIALIZE
Operations, Expressions, and Functions
Operations
Operation Codes
Built-in Functions
Arithmetic Operations
Ensuring Accuracy
Performance Considerations
Integer and Unsigned Arithmetic
Arithmetic Operations Examples
Array Operations
Bit Operations
Branching Operations
Call Operations
Prototyped Calls
Operational Descriptors
Parsing Program Names on a Call
Program CALL Example
Parsing System Built-In Names
Value of *ROUTINE
Compare Operations
xvi  
Conversion Operations
Rules for converting character values to numeric values using built-in functions
Data-Area Operations
Date Operations
Unexpected Results
Declarative Operations
Error-Handling Operations
File Operations
Keys for File Operations
Indicator-Setting Operations
Information Operations
Initialization Operations
Memory Management Operations
Message Operation
Move Operations
Moving Character, Graphic, UCS-2, and Numeric Data
Moving Date-Time Data
Examples of Converting a Character Field to a Date Field
Move Zone Operations
Result Operations
Size Operations
String Operations
Structured Programming Operations
Subroutine Operations
Coding Subroutines
Subroutine Coding Examples
Test Operations
XML Operations
Expressions
General Expression Rules
Expression Operands
Expression Operators
IN operator
Operation Precedence
Data Types
Data Types Supported by Expression Operands
Format of Numeric Intermediate Results
For the operators +, -, and *:
For the / operator:
For the ** operator:
Precision Rules for Numeric Operations
Using the Default Precision Rules
Precision of Intermediate Results
Example of Default Precision Rules
Using the "Result Decimal Position" Precision Rules
Example of "Result Decimal Position" Precision Rules
Short Circuit Evaluation
Order of Evaluation
Built-in Functions
%ABS (Absolute Value of Expression)
%ADDR (Get Address of Variable)
%ALLOC (Allocate Storage)
%BITAND (Bitwise AND Operation)
%BITNOT (Invert Bits)
%BITOR (Bitwise OR Operation)
%BITXOR (Bitwise Exclusive-OR Operation)
Examples of Bit Operations
%CHAR (Convert to Character Data)
  xvii
%CHAR(date|time|timestamp {: format})
%CHAR(numeric)
%CHAR(character | graphic | UCS2 {: ccsid})
%CHARCOUNT (Return the Number of Characters)
%CHECK (Check Characters)
%CHECKR (Check Reverse)
%CONCAT (Concatenate with Separator)
%CONCATARR (Concatenate Array Elements with Separator)
%DATA (document {:options})
%DATE (Convert to Date)
%DAYS (Number of Days)
%DEC (Convert to Packed Decimal Format)
Numeric or character expression 
Date, time or timestamp expression 
%DECH (Convert to Packed Decimal Format with Half Adjust)
%DECH Examples
%DECPOS (Get Number of Decimal Positions)
%DIFF (Difference Between Two Date, Time, or Timestamp Values)
%DIV (Return Integer Portion of Quotient)
%EDITC (Edit Value Using an Editcode)
%EDITFLT (Convert to Float External Representation)
%EDITW (Edit Value Using an Editword)
%ELEM (Get Number of Elements)
%EOF (Return End or Beginning of File Condition)
%EQUAL (Return Exact Match Condition)
%ERROR (Return Error Condition)
%FIELDS
%FIELDS (Fields to update)
%FIELDS (Subfields for sorting)
%FLOAT (Convert to Floating Format)
%FOUND (Return Found Condition)
%GEN (generator {: options})
%GRAPH (Convert to Graphic Value)
%HANDLER (handlingProcedure : communicationArea )
%HIVAL (Highest Value)
%HIVAL and %LOVAL (Highest Value or Lowest Value)
%HOURS (Number of Hours)
%INT (Convert to Integer Format)
%INTH (Convert to Integer Format with Half Adjust)
%KDS (Search Arguments in Data Structure)
%LEFT (Get Leftmost Characters)
%LEN (Get or Set Length)
%LEN Used for its Value
%LEN Used to Set the Length of Variable-Length Fields
%LEN Used to Get the Maximum Length of Varying-Length Expressions703
%LIST (item { : item { : item ... } } )
%LOOKUPxx (Look Up an Array Element)
Sequenced arrays that are not in the correct sequence
%LOVAL (Lowest Value)
%LOWER (Convert to Lower Case)
%LOWER and %UPPER (Convert to Lower or Upper Case)
%MAX (Maximum Value)
%MAXARR (Maximum Element in an Array)
%MIN (Minimum Value)
%MINARR (Minimum Element in an Array)
%MAX and %MIN (Maximum or Minimum Value)
%MAXARR and %MINARR (Maximum or Minimum Element in an Array)712
Determining the Common Type of Multiple Operands
xviii  
%MINUTES (Number of Minutes)
%MONTHS (Number of Months)
%MSECONDS (Number of Microseconds)
%MSG (message-id : message-file { : replacement-text } )
%NULLIND (Query or Set Null Indicator)
%OCCUR (Set/Get Occurrence of a Data Structure)
%OMITTED (Return Parameter-Omitted Condition)
%OPEN (Return File Open Condition)
%PADDR (Get Procedure Address)
%PADDR Used with a Prototype
%PARMS (Return Number of Parameters)
%PARMNUM (Return Parameter Number)
%PARSER (parser {: options})
%PASSED (Return Parameter-Passed Condition)
%PROC (Return Name of Current Procedure)
%RANGE (lower-limit : upper-limit)
%REALLOC (Reallocate Storage)
%REM (Return Integer Remainder)
%REPLACE (Replace Character String)
%RIGHT (Get Rightmost Characters)
%SCAN (Scan for Characters)
%SCANR (Scan Reverse for Characters)
%SCANRPL (Scan and Replace Characters)
%SECONDS (Number of Seconds)
%SHTDN (Shut Down)
%SIZE (Get Size in Bytes)
%SPLIT (Split String into Substrings)
%SQRT (Square Root of Expression)
%STATUS (Return File or Program Status)
%STR (Get or Store Null-Terminated String)
%STR Used to Get Null-Terminated String
%STR Used to Store Null-Terminated String
%SUBARR (Set/Get Portion of an Array)
%SUBDT (Extract a Portion of a Date, Time, or Timestamp)
%SUBST (Get Substring)
%SUBST Used for its Value
%SUBST Used as the Result of an Assignment
%TARGET (program-or-procedure { : offset } )
%THIS (Return Class Instance for Native Method)
%TIME (Convert to Time)
%TIMESTAMP (Convert to Timestamp)
%TLOOKUPxx (Look Up a Table Element)
%TRIM (Trim Characters at Edges)
%TRIML (Trim Leading Characters)
%TRIMR (Trim Trailing Characters)
%UCS2 (Convert to UCS-2 Value)
%UNS (Convert to Unsigned Format)
%UNSH (Convert to Unsigned Format with Half Adjust)
%UPPER (Convert to Upper Case)
%XFOOT (Sum Array Expression Elements)
%XLATE (Translate)
%XML (xmlDocument {:options})
%YEARS (Number of Years)
Operation Codes
ACQ (Acquire)
ADD (Add)
ADDDUR (Add Duration)
ALLOC (Allocate Storage)
  xix
ANDxx (And)
BEGSR (Beginning of Subroutine)
BITOFF (Set Bits Off)
BITON (Set Bits On)
CABxx (Compare and Branch)
CALL (Call a Program)
CALLB (Call a Bound Procedure)
CALLP (Call a Prototyped Procedure or Program)
CASxx (Conditionally Invoke Subroutine)
CAT (Concatenate Two Strings)
CHAIN (Random Retrieval from a File)
CHECK (Check Characters)
CHECKR (Check Reverse)
CLEAR (Clear)
Clearing Variables
Clearing Record Formats
CLEAR Examples
CLOSE (Close Files)
COMMIT (Commit)
COMP (Compare)
DATA-GEN (Generate a Document from a Variable)
%DATA options for the DATA-GEN operation code
Examples of DATA-GEN generators
DATA-INTO (Parse a Document into a Variable)
%DATA options for the DATA-INTO operation code
Expected format of data for DATA-INTO
Examples of DATA-INTO parsers
DEALLOC (Free Storage)
DEFINE (Field Definition)
*LIKE DEFINE
*DTAARA DEFINE
DELETE (Delete Record)
DIV (Divide)
DO (Do)
DOU (Do Until)
DOUxx (Do Until)
DOW (Do While)
DOWxx (Do While)
DSPLY (Display Message)
DUMP (Program Dump)
ELSE (Else)
ELSEIF (Else If)
ENDyy (End a Structured Group)
ENDSR (End of Subroutine)
EVAL (Evaluate expression)
EVALR (Evaluate expression, right adjust)
EVAL-CORR (Assign corresponding subfields)
Examples of the EVAL-CORR operation 
EXCEPT (Calculation Time Output)
EXFMT (Write/Then Read Format)
EXSR (Invoke Subroutine)
EXTRCT (Extract Date/Time/Timestamp)
FEOD (Force End of Data)
FOR (For)
FOR-EACH (For Each)
FORCE (Force a Certain File to Be Read Next Cycle)
GOTO (Go To)
IF (If)
xx  
IFxx (If)
IN (Retrieve a Data Area)
ITER (Iterate)
KFLD (Define Parts of a Key)
KLIST (Define a Composite Key)
LEAVE (Leave a Do/For Group)
LEAVESR (Leave a Subroutine)
LOOKUP (Look Up a Table or Array Element)
MHHZO (Move High to High Zone)
MHLZO (Move High to Low Zone)
MLHZO (Move Low to High Zone)
MLLZO (Move Low to Low Zone)
MONITOR (Begin a Monitor Group)
MOVE (Move)
MOVEA (Move Array)
Character, graphic, and UCS-2 MOVEA Operations
Numeric MOVEA Operations
General MOVEA Operations
MOVEL (Move Left)
MULT (Multiply)
MVR (Move Remainder)
NEXT (Next)
OCCUR (Set/Get Occurrence of a Data Structure)
ON-ERROR (On Error)
ON-EXCP (On Exception)
ON-EXIT (On Exit)
OPEN (Open File for Processing)
ORxx (Or)
OTHER (Otherwise Select)
OUT (Write a Data Area)
PARM (Identify Parameters)
PLIST (Identify a Parameter List)
POST (Post)
READ (Read a Record)
READC (Read Next Changed Record)
READE (Read Equal Key)
READP (Read Prior Record)
READPE (Read Prior Equal)
REALLOC (Reallocate Storage with New Length)
REL (Release)
RESET (Reset)
Resetting Variables
Resetting Record Formats
Additional Considerations
RESET Examples
RETURN (Return to Caller)
ROLBK (Roll Back)
SCAN (Scan String)
SELECT (Begin a Select Group)
SETGT (Set Greater Than)
SETLL (Set Lower Limit)
SETOFF (Set Indicator Off)
SETON (Set Indicator On)
SHTDN (Shut Down)
SND-MSG (Send a Message to the Joblog)
SORTA (Sort an Array)
SQRT (Square Root)
SUB (Subtract)
  xxi
SUBDUR (Subtract Duration)
Subtract a duration
Calculate a duration
Possible error situations
SUBDUR Examples
SUBST (Substring)
TAG (Tag)
TEST (Test Date/Time/Timestamp)
TESTB (Test Bit)
TESTN (Test Numeric)
TESTZ (Test Zone)
TIME (Retrieve Time and Date)
UNLOCK (Unlock a Data Area or Release a Record)
Unlocking data areas
Releasing record locks
UPDATE (Modify Existing Record)
WHEN (When True Then Select)
WHEN-IN (When the SELECT Operand is IN the WHEN-IN Operand)
WHEN-IS (When the SELECT Operand is Equal to the WHEN-IS Operand)
WHENxx (When True Then Select)
WRITE (Create New Records)
XFOOT (Summing the Elements of an Array)
XLATE (Translate)
XML-INTO (Parse an XML Document into a Variable)
Expected format of XML data
Rules for transferring data to RPG variables for XML-INTO and DATA-INTO
Examples of the XML-INTO operation
%DATA and %XML options for DATA-GEN, DATA-INTO, and XML-INTO
allowextra (default no)
allowmissing (default no)
case (default lower)
ccsid (default best)
countprefix
datasubf
doc (default string)
fileccsid (default utf8)
name (no default)
ns (default keep)
nsprefix
output (the default depends on the context)
path
renameprefix
trim (default all)
XML-SAX (Parse an XML Document)
%XML options for the XML-SAX operation code
XML-SAX event-handling procedure 
XML events
Examples of the XML-SAX operation
Z-ADD (Zero and Add)
Z-SUB (Zero and Subtract)
Appendixes
Appendix A. RPG IV Restrictions
Appendix B. EBCDIC Collating Sequence
Bibliography
xxii  
Notices
Programming interface information
Trademarks
Terms and conditions
Index