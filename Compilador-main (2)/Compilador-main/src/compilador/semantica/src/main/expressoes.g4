grammar Expressoes;

programa: exp;

exp:
  termo1=termo ( '+' outrosTermos+=termo)*;

termo:
  '(' expParenteses=exp ')' |
    variavel=ID  |
    constante= NT |
    'let' listDecl 'in' subexp=exp;

listDecl:
    decl (',' decl)*
;

decl:
   nome=ID '=' exp
;


ID: ('a'.. 'z' | 'A' .. 'Z'| '_') ('a'.. 'z' | 'A' .. 'Z'| '_'| '0'..'9')*;
INT: ('0'..'9')+;
WS: (' ' | '\n'  | '\r' | '\t'  ) -> skip;