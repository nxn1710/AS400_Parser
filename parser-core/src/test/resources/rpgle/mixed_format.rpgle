     H DFTACTGRP(*NO)
     FSTUDNTPF  UF   E           K DISK
     D Counter         S              5  0
      /FREE
        Counter = 0;
        read STUDREC;
        dow not %eof;
          Counter += 1;
          read STUDREC;
        enddo;
        *inlr = *on;
      /END-FREE
