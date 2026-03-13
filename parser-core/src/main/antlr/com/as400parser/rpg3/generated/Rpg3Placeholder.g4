// Placeholder RPG3 grammar — will be replaced with full grammar in Phase 2
// Using combined grammar for placeholder simplicity

grammar Rpg3Placeholder;

// Minimal rule for build validation
program : line* EOF ;
line : CONTENT NEWLINE ;

NEWLINE : '\r'? '\n' ;
CONTENT : ~[\r\n]+ ;
