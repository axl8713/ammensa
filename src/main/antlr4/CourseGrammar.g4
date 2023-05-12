grammar CourseGrammar;

@header {
	package net.ammensa.parse.antlr;
}

courses	:	(course SEP NL)+
			;

course	:	NL? name=TEXT NL? (ingredients NL?)*	
			|	NL? name=TEXT									
			;

ingredients	:	'('+ ingredients* ')'+		
				|	'('? content ')'?				
 				;
 				
content		: TEXT (NL TEXT)* ;
 				
SEP	:	'ç' ; 				
NL    : '\r'? '\n' ;
WS		: [ ]+ -> skip ;
TEXT  : ~[\r\n()]+ ;

