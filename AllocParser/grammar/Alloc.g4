/*
 * @author "Cassie Nicol"
*/
grammar Alloc;            // Define a grammar called Hello

/*
 * By default, Antlr puts generated java files into the default java package.
 * That's not really best practice and makes 
 */
@header {
	package com.cenicol.antlr4.alloc.parser;
   
   	import com.cenicol.antlr4.alloc.tools.*;
   
    import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory; 
    
}

/* ------------------------------------------------------------------------ */

@lexer::members {

	private Log log = LogFactory.getLog(AllocLexer.class);
	
	public Token nextToken() {
		Token token = super.nextToken();
		if(token.getType() == Token.EOF) {
		    TokenStreamStack tss = TokenStreamStack.instance();
			if( tss.empty() ) {
				return token;
			}
			//log.info("detected EOF");
			tss.popTS();
		}
		return token;
	}
}


script : (stat)+ ;

stat : set
     | write
     | exit
     | call
     | block
     | filtlist
     | ifstat
     | copybook
     ;

set  : 'SET' ID EQ expr ;

write : 'WRITE' expr ;

exit : EXIT (CODE '(' INT ')' )? ;

call : 'CALL' (VDSEXIT | QUOTACHK) ;

block : 'DO' stat+ 'END' ;

filtlist : 'FILTLIST' ID 'INCLUDE' list (EXCLUDE list)? ;

list : '(' path (',' path  )* ')' ;

path 
  : STRING # PathSTRING
  | GLOB   # PathGLOB
  ;

ifstat : 'IF' bexpr 'THEN' stat (ELSE stat)? ;

copybook : 'COPYBOOK' STRING 
{
	Token t = $STRING;
	String name = t.getText();
	String filename = name.substring(1,name.length()-1) + ".txt";
	//System.out.println("COPYBOOK " + $STRING.getText() );
	//System.out.println(filename);
	TokenStreamStack tss = (TokenStreamStack) _input;
	tss.pushTS(filename);
}
;

/* ------------------------------------------------------------------------ */

CODE : 'CODE' ;
ELSE : 'ELSE' ;
EXIT : 'EXIT' ;
EXCLUDE  : 'EXCLUDE' ;
VDSEXIT  : 'VDSEXIT' DIGIT;
QUOTACHK : 'QUOTACHK' ;


/* ------------------------------------------------------------------------ */

expr:   expr op=('*'|'/') expr      # MulDiv
    |   expr op=('+'|'-') expr      # AddSub
    |   INT                         # int
    |   ID                          # id
    |   STRING                      # string
    |   GLOB                        # glob
    |   '(' expr ')'                # parens
    ;

bexpr : bexpr AND bexpr # Band
      | bexpr OR  bexpr # Bor
      | boolval         # Bval
      ;


boolval : expr op=( EQ | SEQ
                  | NE | '!='       
                  | LT | '<'        
                  | GT | '>'        
	 	          | LE | '<='       
	 	  		  | GE | '>='       
	 	          ) expr            # cexpr
      	; 

/* ------------------------------------------------------------------------ */

AND :   'AND' | '&&' ;
OR  :   'OR'  | '|'  ;
EQ  :   '='  ;
SEQ :   'EQ' ;
NE  :   'NE' | '!=' ;
LT  :   '<'  ;
GT  :   '>'  ;
LE  :   '<=' ;
GE  :   '>=' ;

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;

ID  :   '&' [A-Z][A-Z0-9_]* ;      // match identifiers

INT :   DIGIT+ ;         // match integers

fragment DIGIT: [0-9];

STRING : '\'' ('\'\''|~'\'')* '\'' ;

GLOB : [A-Z0-9.*?]+ ;

WS : [ \t\r\n]+ -> skip ;           // skip spaces, tabs, newlines, \r (Windows)

COMMENT : '/*' .*? '*/' -> skip ;   // skip comments


