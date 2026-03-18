# 📘 DDS → IR Parser Summary

## 1. Core Model

### File Types
- **PF (Physical File)**  
  - 1 record format  
  - Chứa data thực  

- **LF (Logical File)**  
  - View trên PF  
  - Types:
    - Simple  
    - Multiple-format  
    - Join  

---

## 2. Structure Hierarchy

File
 ├── Record Format
 │    ├── Fields
 │    ├── Keys
 │    └── Select/Omit

---

## 3. Positional Parsing (1–44)

### Required fields

| Position | Meaning |
|----------|--------|
|1–5| Sequence number |
| 6 | Form type (A) |
| 7 | Comment (*) |
| 17 | Entry type |
| 19–28 | Name |
| 30–34 | Length |
| 35 | Data type |
| 36–37 | Decimal |
| 38 | Usage |
| 39–44 | comment |

---

## 4. Entry Type (Position 17)

### PF
- R → Record  
- K → Key  
- blank → Field  

### LF
- R → Record  
- J → Join  
- K → Key  
- S → Select  
- O → Omit  
- blank → Field  

---

## 5. Data Model (IR)

### Field
- name  
- type  
- length  
- decimal  
- usage  
- keywords[]  

---

### Record Format
- name  
- fields[]  
- keys[]  
- selectOmit[]  

---

### File
- type  
- formats[]  
- fileKeywords[]  

---

## 6. Data Types

| Type | Meaning |
|------|--------|
| A | Character |
| P | Packed |
| S | Zoned |
| B | Binary |
| F | Float |
| H | Hex |
| L | Date |
| T | Time |
| Z | Timestamp |
| G | Graphic |

---

## 7. Usage (Position 38)

| Value | Meaning |
|------|--------|
| B | Input + Output |
| I | Input-only |
| N | Neither |

---

## 8. Keys

- Entry type K  
- Ordered list  
- ≤ 120 fields  

---

## 9. Logical File Rules

### Simple / Multiple LF
- Phải có PFILE  

### Join LF
- Phải có:
  - JFILE  
  - JOIN  
  - JFLD  

---

## 10. Select / Omit

- S → select  
- O → omit  

### Keywords
- COMP  
- RANGE  
- VALUES  
- ALL  

---

## 11. Field Mapping

- Direct field  
- REF / REFFLD  
- Derived:
  - CONCAT  
  - SST  
  - RENAME  

---

## 12. Reference

- REF → file  
- REFFLD → field  
- Local override allowed  

---

## 13. Core Keywords (IR-impact)

### File-level
- UNIQUE  
- FIFO / LIFO  
- ALTSEQ  

### Record-level
- FORMAT  
- PFILE  
- JFILE  

### Field-level
- CONCAT  
- SST  
- RENAME  
- VARLEN  

### Key-level
- DESCEND  
- SIGNED / UNSIGNED  
- ABSVAL  

### Select/Omit
- COMP  
- RANGE  
- VALUES  

---

## 14. Output IR

### File
- type  
- formats  
- dependencies  

### Record
- name  
- fields  
- keys  
- selectOmit  

### Field
- name  
- type  
- length  
- decimal  
- usage  
- source  

---

## ✅ Summary

IR cần capture:

- Structure: File → Record → Field  
- Relationship: PF ↔ LF  
- Behavior:
  - Key / sort  
  - Select/Omit  
  - Derived field  
- Constraints:
  - Type  
  - Length  
  - Decimal  

# 📘 DDS Keywords – Sample Usage (Columns 45–80)

## 1. Common (PF & LF)

- ABSVAL  
  `A            AMOUNT        5S 0       ABSVAL`

- ALIAS  
  `A            CUSTNM        20A         ALIAS(CUSTOMER_NAME)`

- ALTSEQ  
  `A                                      ALTSEQ(MYLIB/TBL1)`

- CCSID  
  `A            NAME          30A         CCSID(1208)`

- CHECK  
  `A            AGE           3S 0       CHECK(AGE > 0)`

- CHKMSGID  
  `A            CODE          5A         CHKMSGID(CPF0001)`

- CMP  
  `A            STATUS        1A         CMP(EQ 'A')`

- COLHDG  
  `A            NAME          20A        COLHDG('Customer Name')`

- COMP  
  `A            STATUS        1A         COMP(EQ 'A')`

- DATFMT  
  `A            DOB           L           DATFMT(*ISO)`

- DATSEP  
  `A            DOB           L           DATSEP('-')`

- DESCEND  
  `A          K CUSTID                    DESCEND`

- DIGIT  
  `A            NUM           5S 0        DIGIT`

- EDTCDE  
  `A            AMT           7S 2        EDTCDE(J)`

- EDTWRD  
  `A            AMT           7S 2        EDTWRD('  ,   .  ')`

- FCFO  
  `A                                      FCFO`

- FIFO  
  `A                                      FIFO`

- FLTPCN  
  `A            RATE          8F          FLTPCN(2)`

- FORMAT  
  `A                                      FORMAT(CUSTREC)`

- LIFO  
  `A                                      LIFO`

- NOALTSEQ  
  `A          K NAME                      NOALTSEQ`

- RANGE  
  `A            AGE           3S 0        RANGE(18 60)`

- REFSHIFT  
  `A            FIELD1        5A         REFSHIFT(2)`

- SIGNED  
  `A          K AMOUNT                   SIGNED`

- TEXT  
  `A            STATUS        1A         TEXT('Active flag')`

- TIMFMT  
  `A            TSTAMP        T          TIMFMT(*ISO)`

- TIMSEP  
  `A            TSTAMP        T          TIMSEP(':')`

- UNIQUE  
  `A                                      UNIQUE`

- UNSIGNED  
  `A          K QTY                      UNSIGNED`

- VALUES  
  `A            STATUS        1A         VALUES('A' 'I')`

- VARLEN  
  `A            DESC          100A       VARLEN(200)`

- ZONE  
  `A            NUM           5S 0       ZONE`

---

## 2. Physical File Only (PF)

- ALWNULL  
  `A            NOTE          50A        ALWNULL`

- DFT  
  `A            STATUS        1A         DFT('A')`

- REF  
  `A                                      REF(CUSTFILE)`

- REFFLD  
  `A            NAME                      REFFLD(CUSTNM CUSTFILE)`

---

## 3. Logical File Only (LF)

- ALL  
  `A          S STATUS                   ALL`

- CONCAT  
  `A            FULLNM       40A         CONCAT(FIRST LAST)`

- DYNSLT  
  `A                                      DYNSLT`

- PFILE  
  `A                                      PFILE(CUSTPF)`

- REFACCPTH  
  `A                                      REFACCPTH(CUSTLF)`

- RENAME  
  `A            OLDNM        20A         RENAME(NEWNM)`

- SST  
  `A            SUBNM        10A         SST(NAME 1 10)`

- TRNTBL  
  `A            NAME         20A         TRNTBL(TBL1)`

---

## 4. Join Logical File Only

- JDFTVAL  
  `A                                      JDFTVAL`

- JDUPSEQ  
  `A                                      JDUPSEQ(CUSTID *DESCEND)`

- JFILE  
  `A                                      JFILE(CUSTPF ORDPF)`

- JFLD  
  `A                                      JFLD(CUSTID CUSTID)`

- JOIN  
  `A                                      JOIN(CUSTPF ORDPF)`

- JREF  
  `A                                      JREF(1)`

---

## 5. Select / Omit

- COMP  
  `A          S STATUS                   COMP(EQ 'A')`

- RANGE  
  `A          S AGE                      RANGE(18 60)`

- VALUES  
  `A          S STATUS                   VALUES('A' 'I')`

- ALL  
  `A          O STATUS                   ALL`

---

## ✅ Summary

- Mỗi keyword xuất hiện trong **columns 45–80**
- Có thể:
  - đứng một mình (`UNIQUE`)
  - hoặc có parameter (`COMP(EQ 'A')`)
- Có thể nhiều keyword trên cùng dòng