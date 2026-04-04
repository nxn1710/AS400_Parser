     H DFTACTGRP(*NO) ACTGRP(*CALLER)
     FCUSTFILE  IF   E           K DISK
     D CustName        S             30A
     D CustAddr        S             50A
     C     *ENTRY        PLIST
     C                   PARM                    CustId            5 0
     C     CustId        CHAIN     CUSTREC                          99
     C                   IF        *IN99 = *OFF
     C                   EVAL      CustName = CMNAME
     C                   EVAL      CustAddr = CMADDR
     C                   ENDIF
     C                   SETON                                        LR
     OCUSTFILE  E            CUSTREC
     O                       CustName           30
     O                       CustAddr           80
     P GetTotal        B
     D GetTotal        PI            10I 0
     D  pQty                          5I 0 VALUE
     D  pPrice                        7P 2 VALUE
     C                   RETURN    pQty * pPrice
     P GetTotal        E
