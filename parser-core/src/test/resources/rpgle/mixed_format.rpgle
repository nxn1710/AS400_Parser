     H DATFMT(*ISO) DEBUG
     FSTUDNTPF  IF   E           K DISK
     D Counter        S              5P 0
      /FREE
       IF Counter > 0;
         EVAL Counter = Counter - 1;
       ENDIF;

       DOW NOT %EOF(STUDNTPF);
         READ STUDNTPF;
         IF %EOF(STUDNTPF);
           LEAVE;
         ENDIF;
       ENDDO;
      /END-FREE
     C                   SETON                                        LR
