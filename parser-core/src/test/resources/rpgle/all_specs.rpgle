     H DFTACTGRP(*NO) ACTGRP(*NEW)
     FSTUDNTPF  IF   E           K DISK
     FREPORTPF  O    F  132        PRINTER
     D Counter         S              5  0
     D RecName         S             10A
     ISTUDNTPF  NS
     I                                  1   10 STUDID
     I                                 11   30 STUDNM
     C     *ENTRY        PLIST
     C                   PARM                    PARM1            10
     C                   EVAL      Counter = 0
     C                   CALL      'RPTPGM'
     OREPORTPF  H    1P
     O                                           10 'STUDENT RPT'
     O          D    1
     O                         STUDNM       30
     P MyProc          B                   EXPORT
     D MyProc          PI            10A
     D  pInput                       10A   CONST
     C                   RETURN    pInput
     P MyProc          E
