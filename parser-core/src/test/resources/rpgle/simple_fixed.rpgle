     H DFTACTGRP(*NO) ACTGRP('MYGRP')
     FSTUDNTPF  IF   E           K DISK
     FSTUDNTLF  IF   E           K DISK    RENAME(STUDREC:STUDLF)
     D Counter         S              5  0
     D FullName        S             30A
     D MaxRec          C                   CONST(100)
     C     *ENTRY        PLIST
     C                   PARM                    PARM1            10
     C                   EVAL      Counter = 0
     C                   READ      STUDREC
     C                   DOW       NOT %EOF
     C                   EVAL      Counter = Counter + 1
     C                   READ      STUDREC
     C                   ENDDO
     C                   CALL      'RPTPGM'
     C                   CALLP     UpdateLog(Counter)
     C                   EVAL      *INLR = *ON
