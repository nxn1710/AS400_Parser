     I*================================================================
     I* コピーブック: SCHOOLCPY
     I* 説明        : 学校マスター フィールドリネーム定義
     I* 対象ファイル: SCHOOLPF / SCHOOLL1
     I* リネーム規則: SCL → S2
     I*
     I* 使用方法:
     I*   ISCHOOLPF
     I*   I/COPY QCPYSRC,SCHOOLCPY
     I*================================================================
     I*   --- 主キー ---
     I                                       SCLID          S2ID
     I*   --- 基本情報 ---
     I                                       SCLNAM         S2NAM
     I                                       SCLTYP         S2TYP
     I*   --- 所在地情報 ---
     I                                       SCLADR         S2ADR
     I                                       SCLTEL         S2TEL
     I                                       SCLFAX         S2FAX
     I*   --- 責任者情報 ---
     I                                       SCLPRI         S2PRI
     I*   --- 統計情報 ---
     I                                       SCLCNT         S2CNT
     I*   --- 監査情報 ---
     I                                       SCLADD         S2ADD
     I                                       SCLUPD         S2UPD
