     I*================================================================
     I* コピーブック: STUDNTCPY
     I* 説明        : 学生マスター フィールドリネーム定義
     I* 対象ファイル: STUDNTPF / STUDNTL1 / STUDNTL2
     I* リネーム規則: STU → S1
     I*
     I* 使用方法:
     I*   IFILENAME
     I*   I/COPY QCPYSRC,STUDNTCPY
     I*================================================================
     I*   --- 主キー ---
     I                    STUID                           S1ID
     I*   --- 基本情報 ---
     I                    STUNAM                          S1NAM
     I                    STUKNA                          S1KNA
     I                    STUBDT                          S1BDT
     I                    STUGND                          S1GND
     I*   --- 連絡先情報 ---
     I                    STUADR                          S1ADR
     I                    STUTEL                          S1TEL
     I                    STUMAL                          S1MAL
     I*   --- 所属情報 ---
     I                    STUSCL                          S1SCL
     I                    STUYR                           S1YR
     I*   --- 状態管理 ---
     I                    STUSTS                          S1STS
     I*   --- 監査情報 ---
     I                    STUADD                          S1ADD
     I                    STUUPD                          S1UPD
     I                    STUADB                          S1ADB
     I                    STUUPB                          S1UPB
