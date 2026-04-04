Contents
Control language
Control language overview
What's new for IBM i 7.6
CL concepts
System operation control
Control language
Menus
Messages
Message descriptions
Message queues
CL commands
CL command names
Abbreviations used in CL commands and keywords
CL command parts
CL command syntax
CL command label
CL command parameters
CL command delimiter characters
CL command continuation
CL command comments
CL command definition
CL command coding rules
CL command information and documentation
CL command documentation format
CL command help
Printing CL command descriptions on the system
CL command prompters
CL commands that operate on IBM i objects
CL commands that operate on multiple objects
CL programs and procedures
CL procedure
CL module
CL program
Service program
CL parameters
Parameter values
Constant values
Variable name
Expressions
List of values
Parameters in keyword and positional form
Required, optional, and key parameters
Commonly used parameters
AUT parameter
CLS parameter
COUNTRY parameter
FILETYPE parameter
FRCRATIO parameter
IGCFEAT parameter
JOB parameter
LABEL parameter
  iii
LICOPT parameter
MAXACT parameter
OBJ parameter
OBJTYPE parameter
OUTPUT parameter
PRTTXT parameter
REPLACE parameter
JOBPTY, OUTPTY, and PTYLMT scheduling priority parameters
SEV parameter
SPLNBR parameter
TEXT parameter
VOL parameter
WAITFILE parameter
Parameter values used for testing and debugging
Program-variable description
Basing-pointer description
Subscript description
Qualified-name description
Control language elements
CL character sets and values
Character sets
Special character use
Symbolic operators
Predefined values
Expressions in CL commands
Arithmetic expressions
Character string expressions
Relational expressions
Logical expressions
Operators in expressions
Priority of operators when evaluating expressions
Built-in functions for CL
Naming within commands
Folder and document names
IBM i objects
Library objects
External object types
Simple and qualified object names 
Generic object names
Object naming rules
Communication names (*CNAME)
Generic names (*GENERIC)
Names (*NAME)
Path names (*PNAME)
Simple names (*SNAME)
Additional rules for unique names
Database files and device files used by CL commands
CL programming
Process for creating a CL program or CL procedure
Interactive entry
Batch entry
Parts of a CL source program
Example: Simple CL program
Commands used in CL programs or procedures
Common commands used in CL programs and procedures
Operations performed by CL programs or procedures
Variables in CL commands
Declaring variables to a CL program or procedure
iv  
Uses for based variables
Uses for defined variables
Variables to use for specifying a list or qualified name
Cases of characters in variables
Variables that replace reserved or numeric parameter values
Changing the value of a variable
Trailing blanks on command parameters
Writing comments in CL programs or procedures
Controlling processing within a CL program or CL procedure
GOTO command and command labels in a CL program or procedure
IF command in a CL program or procedure
ELSE command in a CL program or procedure
Embedded IF commands in a CL program or procedure
DO command and DO groups in a CL program or procedure
Showing DO and SELECT nesting levels
DOUNTIL command in a CL program or procedure
DOWHILE command in a CL program or procedure
DOFOR command in a CL program or procedure
ITERATE command in a CL program or procedure
LEAVE command in a CL program or procedure
CALLSUBR command in a CL program or procedure
SELECT command and SELECT groups in a CL program or procedure
SUBR command and subroutines in a CL program or procedure
*AND, *OR, and *NOT operators
%ADDRESS built-in function
%BINARY built-in function
%CHAR built-in function
%CHECK built-in function
%CHECKR built-in function
%DEC built-in function
%INT built-in function
%LEN built-in function
%LOWER built-in function
%OFFSET built-in function
%PARMS built-in function
%SCAN built-in function
%SIZE built-in function
%SUBSTRING built-in function
%SWITCH built-in function
%SWITCH with the IF command
%SWITCH with the Change Variable command
%TRIM built-in function
%TRIML built-in function
%TRIMR built-in function
%UINT built-in function
%UPPER built-in function
Monitor Message command
Retrieving values that can be used as variables
Retrieving system values
Example: Retrieving QTIME system value
Retrieving the QDATE system value into a CL variable
Retrieving configuration source
Retrieving configuration status
Retrieving network attributes
Example: Using the Retrieve Network Attributes command
Retrieving job attributes
Example: Using the Retrieve Job Attributes command
Retrieving user profile attributes
  v
Example: Using the Retrieve User Profile command
Retrieving member description information
Example: Using the Retrieve Member Description command
Compiling CL source program
Setting create options in the CL source program
Embedding CL commands from another source member
Logging CL program or procedure commands
Retrieving CL source code
CL module compiler listings
Common compilation errors
Obtaining a CL dump
Displaying module attributes
Displaying program attributes
Return code summary
Compiling source programs for a previous release
Previous-release (*PRV) libraries
Installing CL compiler support for a previous release
Controlling flow and communicating between programs and procedures......................................272
Passing control to another program or procedure
Using the Call Program command to pass control
Using the Call Bound Procedure command to pass control 
Using the Return command to pass control
Passing parameters
Using the Call Program command to pass control to a called program................................281
Common errors when calling programs and procedures
Communicating between programs and procedures
Using data queues
Remote data queues
Comparisons with using database files as queues
Similarities to message queues
Prerequisites for using data queues
Managing the storage used by a data queue
Allocating data queues
Examples: Using a data queue
Creating data queues associated with an output queue
Creating data queues associated with jobs
Using data areas
Local data area
Group data area
Program Initialization Parameter data area
Remote data areas
Creating a data area
Data area locking and allocation
Displaying a data area
Changing a data area
Retrieving a data area
Examples: Retrieving a data area
Example: Changing and retrieving a data area
Defining and documenting CL commands
Defining CL commands
CL command definition statements
CL command definition process
Defining a CL command
Data type and parameter restrictions
Defining a CL command list parameter
Specifying prompt control for a CL command parameter
Key parameters and prompt override programs for a CL command.....................................345
Creating a CL command
vi  
Displaying a CL command definition
Effect of changing the command definition of a CL command in a procedure or program...356
Command processing program for a CL command
Validity checking program for a CL command
Examples: Defining and creating CL commands
Documenting a CL command
CL commands and command online help
Writing CL command help
Generating HTML source for CL command documentation
Proxy CL commands
Command-related APIs
QCAPCMD program
QCMDEXC program
QCMDEXC program with DBCS data
QCMDCHK program
Prompting for user input at run time
Using the IBM i prompter within a CL procedure or program
Using selective prompting for CL commands
Using QCMDEXC with prompting in CL procedures and programs
Entering program source
Using the Start Programmer Menu command
Using the EXITPGM parameter of the Start Programmer Menu command...........................387
Command analyzer exit points
Designing application programs for DBCS data
Designing DBCS application programs
Converting alphanumeric programs to process DBCS data
Using DBCS data in a CL program
Unicode support in control language
Unicode overview 
Design of Unicode in control language
Example: Passing the EBCDIC and Unicode value
Calling Unicode-enabled commands
Loading and running an application from tape or optical media
Example: QINSTAPP program
Transferring control to improve performance
Example: Using the Transfer Control command
Passing parameters using the Transfer Control command
Examples: CL programming
Example: Initial program for setup (programmer)
Example: Saving specific objects in an application (system operator)
Example: Recovery from abnormal end (system operator)
Example: Timing out while waiting for input from a device display
Example: Performing date arithmetic
Debugging CL programs and procedures
Debugging ILE programs
Debug commands
Preparing a program object for a debug session
Using a root source view to debug ILE programs
Using a listing view to debug ILE programs
Encrypting the debug listing view
Using a statement view to debug ILE programs
Starting the ILE source debugger
Adding program objects to a debug session
Removing program objects from a debug session
Viewing the program source
Changing a module object
Changing the module object view
Setting and removing breakpoints
  vii
Setting and removing unconditional breakpoints
Setting and removing conditional breakpoints
Using the Work with Breakpoints display
Using the BREAK and CLEAR debug commands to set and remove conditional
breakpoints
National Language Sort Sequence
Examples: Conditional breakpoint
Removing all breakpoints
Using instruction stepping
F10 (Step) to step over program objects or F22 (Step into) to step into program objects...414
Using the STEP debug command to step through a program object.....................................414
Displaying variables
Example: Displaying logical variable
Examples: Displaying character variable
Example: Displaying decimal variable
Example: Displaying variables as hexadecimal values
Changing the value of variables
Example: Changing logical variable
Examples: Changing character variable
Examples: Changing decimal variable
Displaying variable attributes
Equating a name with a variable, an expression, or a command
Source debug and IBM i globalization
Working with *SOURCE view
Operations that temporarily remove steps
Debugging original program model programs
Starting debug mode
Adding programs to debug mode
Preventing updates to database files in production libraries
Displaying the call stack
Program activations
Handling unmonitored messages
Breakpoints
Adding breakpoints to programs
Adding conditional breakpoints
Removing breakpoints from programs
Traces
Adding traces to programs
Using instruction stepping
Using breakpoints within traces
Removing trace information from the system
Removing traces from programs
Displaying testing information
Displaying the values of variables
Changing the values of variables
Reasons for using a job to debug another job
Debugging batch jobs that are submitted to a job queue 
Debugging batch jobs that are not started from job queues
Debugging a job that is running
Debugging another interactive job
Considerations when debugging one job from another job
Debugging at the machine interface level
Security considerations
Operations that temporarily remove breakpoints
Objects and libraries
Objects
Object types and common attributes
Functions performed on objects
viii  
Functions the system performs automatically
Functions you can perform using commands
Libraries
Library lists
Functions of using a library list
A job's library list
Changing the library list
Considerations for using a library list
Displaying a library list
Using library lists to search for objects
Using libraries
Creating a library
Authority for libraries specification
Object authority
Data authority
Combined authority
Security considerations for objects
The Display Audit Journal Entries command to generate security journal audit reports.....454
Setting default public authority
Setting default auditing attribute
Placing objects in libraries
Deleting and clearing libraries
Displaying library names and contents
Displaying and retrieving library descriptions
Changing national language versions
Static prompt message for control language
Dynamic prompt message for control language
Describing objects
Displaying object descriptions
Retrieving object descriptions
Example: Using the Retrieve Object Description command
Creation information for objects
Detecting unused objects on the system
Moving objects from one library to another
Creating duplicate objects
Renaming objects
Object compression or decompression
Restrictions for compression of objects
Temporarily decompressed objects
Automatic decompression of objects
Deleting objects
Allocating resources
Lock states for objects
Displaying the lock states for objects
Accessing objects in CL programs
Accessing command definitions, files, and procedures
Accessing command definitions
Accessing files
Accessing procedures
Checking for the existence of an object
Working with files in CL programs or procedures
Data manipulation commands
Files in a CL program or procedure
Opening and closing files in a CL program or procedure
Declaring a file
Sending and receiving data with a display file
Example: Writing a CL program or procedure to control a menu
Overriding display files in a CL procedure or program (OVRDSPF command)............................496
  ix
Working with multiple device display files
Receiving data from a database file (RCVF command)
Reading a file multiple times (CLOSE command)
Overriding database files in a CL procedure or program (OVRDBF command)..........................500
Output files from display commands
Messages
Defining message descriptions
Creating a message file
Message files in independent ASPs
Determining the size of a message file
Adding messages to a file
Assigning a message identifier
Defining messages and message help
Defining substitution variables
Assigning a severity code
Specifying validity checking for replies
Example: Sending an immediate message and handling a reply
Defining default values for replies
Specifying default message handling for escape messages
Example: Describing a message
Defining double-byte messages
Viewing messages
Message file searching
System message file searches
Overriding message files
Message queues
Types of message queues
Creating or changing a message queue
Message queues in independent ASPs
Message queues in break mode
Placing a message queue in break mode automatically
Job message queues
External message queue
Call message queue
Commands used to send messages to a system user
Commands used to send messages from a CL program
Inquiry and informational messages
Completion and diagnostic messages
Status messages
Escape and notify messages
Examples: Sending messages
Identifying a call stack entry
Using the Send Program Message command as the base
Identifying the base entry by name
Using the program boundary as a base (*PGMBDY)
Using the most recently called procedure as a base (*PGMNAME)......................................545
Using a control boundary as a base (*CTLBDY)
Considerations for service programs
Receiving messages into a CL procedure or program
Receiving request messages
Writing request-processor procedures and programs
Determining if a request processor exists
Retrieving message descriptions from a message file
Removing messages from a message queue
Monitoring for messages in a CL program or procedure
Watching for messages
CL handling for unmonitored messages
Monitoring for notify messages
x  
Monitoring for status messages
Preventing the display of status messages 
Receiving a message from a program or procedure that has ended
Break-handling programs
Ways of handling replies to inquiry messages 
Using a sender copy message to obtain a reply
Finding the job that sent the reply 
Using the system reply list
Using the reply handling exit program
Message subfiles in a CL program or procedure
Log messages
Job log
Writing a job log to a file
Controlling information written in a job log
Job log message filtering
Example: Controlling information written in a job log
Job log sender or receiver information
Displaying a job log
Preventing the production of job logs
Job log considerations
Interactive job log considerations
Batch job log considerations
Message filtering through the Control Job Log Output API
Job log output files
QHST history log
Format of the history log
QHST file processing
QHST job start and completion messages
QHST files deletion
QSYSMSG message queue
Messages sent to QSYSMSG message queue
Example: Receiving messages from QSYSMSG