**FREE
ctl-opt dftactgrp(*no) actgrp('MYGRP');
dcl-f STUDNTPF disk(*ext) usage(*input) keyed;
dcl-s Counter packed(5:0);
dcl-s FullName char(30);
dcl-c MAX_REC const(100);
dcl-pr UpdateLog extpgm('UPDLOG');
  logCount packed(5:0);
end-pr;

Counter = 0;
read STUDREC;
dow not %eof;
  Counter += 1;
  read STUDREC;
enddo;
callp UpdateLog(Counter);
*inlr = *on;
