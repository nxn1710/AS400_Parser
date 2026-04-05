     H DFTACTGRP(*NO)
      /COPY QRPGLESRC,COPYMBR1
      /INCLUDE MYLIB/QRPGLESRC,COPYMBR2
      /COPY SIMPLEMEM
     FSTUDNTPF  IF   E           K DISK
     D Counter         S              5  0
     C                   EVAL      Counter = 1
     C                   EVAL      *INLR = *ON
