/* MONMSG patterns fixture */
             PGM
             MONMSG     MSGID(CPF0000) EXEC(GOTO CMDLBL(ERROR))
             ADDLIBLE   LIB(MYLIB)
             MONMSG     MSGID(CPF2103)
             CALL       PGM(MYPGM)
             MONMSG     MSGID(CPF0000) EXEC(DO)
             GOTO       CMDLBL(ERROR)
             ENDDO
             GOTO       CMDLBL(ENDPGM)
 ERROR:
             SNDPGMMSG  MSGID(CPF9898) MSGF(QCPFMSG) MSGTYPE(*ESCAPE)
 ENDPGM:
             ENDPGM
