**FREE
ctl-opt dftactgrp(*no) actgrp(*caller);

dcl-f CUSTFILE disk(*ext) usage(*input) keyed;

dcl-s CustName char(30);
dcl-s CustAddr char(50);

dcl-pi *n;
  CustId packed(5:0);
end-pi;

chain CustId CUSTREC;
if %found(CUSTFILE);
  CustName = CMNAME;
  CustAddr = CMADDR;
endif;

*inlr = *on;
return;
