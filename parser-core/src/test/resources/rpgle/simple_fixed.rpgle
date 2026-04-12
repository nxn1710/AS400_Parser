     H DATFMT(*ISO) TIMFMT(*ISO) DEBUG
     FSTUDNTPF  IF   E           K DISK
     FSTUDSPF   CF   E             WORKSTN
     D StudentName     S             30A   INZ('John Doe')
     D Counter         S              5P 0 INZ(0)
     D MAX_ITEMS       C                   CONST(100)
     D StudentDS       DS
     D  StdName                      30A
     D  StdAge                        3P 0
     C     *IN03         IFEQ      *ON
     C                   EXSR      LOADSR
     C                   ENDIF
     C     *INLR         DOWEQ     *OFF
     C                   READ      STUDREC                              99
     C   99              LEAVE
     C                   EVAL      Counter = Counter + 1
     C                   ENDDO
     C                   SETON                                        LR
     C     LOADSR        BEGSR
     C                   CHAIN     StdKey        STUDREC               50
     C                   ENDSR
     P ProcessData     B                   EXPORT
     P ProcessData     E
     OSTUDRECO  H
     O                                  8 'STUDENT'
     O          D
     O                   StdName       30
