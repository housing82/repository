DROP TRIGGER KAIT.TDA_HD_CODE_HOUSE;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_CODE_HOUSE
  AFTER DELETE
  ON KAIT.HD_CODE_HOUSE   for each row
/* ERwin Builtin Fri Sep 01 12:16:11 2006 */
/* default body for TDA_HD_CODE_HOUSE */
declare numrows INTEGER;
begin
   INSERT INTO HD_CODE_HOUSE_HIST
   (    HIST_DATE         ,
        DEPT_CODE         ,
        HOUSETAG          ,
        HOUSEDATE         ,
        COMPLETIONDATE    ,
        HOUSE_CNT         ,
        MOVEINSTARTDATE   ,
        MOVEINENDDATE     ,
        PENALTYRATE       ,
        HIC_TAG           ,
        TIRM_RATE         ,
        SODUK_RATE        ,
        JUMIN_RATE        ,
        DAMDANG           ,
        DAMDANG_TEL       ,
        PRINT_CNT         ,
        RESELL_TAG        ,
        VIRDEPOSIT_YN     ,
        VIRBANK_CODE      ,
        DEPT_ADDR_USE_YN  ,
        CONT_PBL_NAME     ,
        CONT_DW_NAME      ,
        REMARK            ,
        INPUT_DUTY_ID     ,
        INPUT_DATE        ,
        CHG_DUTY_ID       ,
        CHG_DATE          ,
        CONT_PBL_NAME2    ,
        CONT_DW_NAME2     ,
        CONT_PBL_NAME3    ,
        CONT_DW_NAME3     ,
        SLIPGROUP         ,
        TRI_TAG           ,
        APPLY_CHECK_YN    ,
        ANNE1A_PBL        ,
        ANNE1A_DW         ,
        ANNE1B_PBL        ,
        ANNE1B_DW         ,
        ANNE2A_PBL        ,
        ANNE2A_DW         ,
        ANNE2B_PBL        ,
        ANNE2B_DW         ,
        ANNE3_PBL         ,
        ANNE3_DW          ,
        ANNE4_PBL         ,
        ANNE4_DW          ,
        DELAYDAY          ,
        DELAYRATE         ,
        AGREE_TAG         ,
        INDEMINITY_TAG    ,
        INDEMINITY_FRDT   ,
        INDEMINITY_TODT   ,
        SEALTYPE          ,
        JIRO_TAG          ,
        JIRO_SN           ,
        HD_DAYMONTH_TAG   ,
        RT_DAYMONTH_TAG   ,
        RT_FIXRATE_TAG    ,
        PREDIS_TAG        ,
        PROXY_TAG         ,
        TRUST_TAG         ,
        PRINT_YN          ,
        RT_FIXRATE_DAY    ,
        RT_FIXRATE        ,
        VIRDEPOSIT2_YN    ,
        VIRBANK2_CODE     ,
        RT_FIXRATE2       ,
        RT_EXTRATE        ,
        RT_GURTYN         ,
        RT_RENTYN         ,
        ONCESALERATE      ,
        DELAY_BLOCK
   )
   VALUES
   (    SYSDATE                ,
        :OLD.DEPT_CODE         ,
        :OLD.HOUSETAG          ,
        :OLD.HOUSEDATE         ,
        :OLD.COMPLETIONDATE    ,
        :OLD.HOUSE_CNT         ,
        :OLD.MOVEINSTARTDATE   ,
        :OLD.MOVEINENDDATE     ,
        :OLD.PENALTYRATE       ,
        :OLD.HIC_TAG           ,
        :OLD.TIRM_RATE         ,
        :OLD.SODUK_RATE        ,
        :OLD.JUMIN_RATE        ,
        :OLD.DAMDANG           ,
        :OLD.DAMDANG_TEL       ,
        :OLD.PRINT_CNT         ,
        :OLD.RESELL_TAG        ,
        :OLD.VIRDEPOSIT_YN     ,
        :OLD.VIRBANK_CODE      ,
        :OLD.DEPT_ADDR_USE_YN  ,
        :OLD.CONT_PBL_NAME     ,
        :OLD.CONT_DW_NAME      ,
        :OLD.REMARK            ,
        :OLD.INPUT_DUTY_ID     ,
        :OLD.INPUT_DATE        ,
        :OLD.CHG_DUTY_ID       ,
        :OLD.CHG_DATE          ,
        :OLD.CONT_PBL_NAME2    ,
        :OLD.CONT_DW_NAME2     ,
        :OLD.CONT_PBL_NAME3    ,
        :OLD.CONT_DW_NAME3     ,
        :OLD.SLIPGROUP         ,
        'D'                    ,
        :OLD.APPLY_CHECK_YN    ,
        :OLD.ANNE1A_PBL        ,
        :OLD.ANNE1A_DW         ,
        :OLD.ANNE1B_PBL        ,
        :OLD.ANNE1B_DW         ,
        :OLD.ANNE2A_PBL        ,
        :OLD.ANNE2A_DW         ,
        :OLD.ANNE2B_PBL        ,
        :OLD.ANNE2B_DW         ,
        :OLD.ANNE3_PBL         ,
        :OLD.ANNE3_DW          ,
        :OLD.ANNE4_PBL         ,
        :OLD.ANNE4_DW          ,
        :OLD.DELAYDAY          ,
        :OLD.DELAYRATE         ,
        :OLD.AGREE_TAG         ,
        :OLD.INDEMINITY_TAG    ,
        :OLD.INDEMINITY_FRDT   ,
        :OLD.INDEMINITY_TODT   ,
        :OLD.SEALTYPE          ,
        :OLD.JIRO_TAG          ,
        :OLD.JIRO_SN           ,
        :OLD.HD_DAYMONTH_TAG   ,
        :OLD.RT_DAYMONTH_TAG   ,
        :OLD.RT_FIXRATE_TAG    ,
        :OLD.PREDIS_TAG        ,
        :OLD.PROXY_TAG         ,
        :OLD.TRUST_TAG         ,
        :OLD.PRINT_YN          ,
        :OLD.RT_FIXRATE_DAY    ,
        :OLD.RT_FIXRATE        ,
        :OLD.VIRDEPOSIT2_YN    ,
        :OLD.VIRBANK2_CODE     ,
        :OLD.RT_FIXRATE2       ,
        :OLD.RT_EXTRATE        ,
        :OLD.RT_GURTYN         ,
        :OLD.RT_RENTYN         ,
        :OLD.ONCESALERATE      ,
        :OLD.DELAY_BLOCK
   );
end;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_INCOME;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_INCOME AFTER DELETE ON KAIT.HD_HOUS_INCOME FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_INCOME_LOG
    WHERE CUST_CODE = :OLD.CUST_CODE
      AND SEQ       = :OLD.SEQ
      AND COUNTS    = :OLD.COUNTS
      AND TIMES     = :OLD.TIMES;

   INSERT INTO HD_HOUS_INCOME_LOG
             ( CUST_CODE,             SEQ,                COUNTS,              TIMES,
               WRITETAG,              WRITETIME,
               WRITESEQ,              DEPT_CODE,
               HOUSETAG,              BUILDNO,            HOUSENO,             DEPOSIT_NO,
               RECEIPTDATE,           RECEIPTAMT,         RECEIPTLANDAMT,      RECEIPTBUILDAMT,
               RECEIPTVATAMT,         DELAYDAYS,          DELAYAMT,            DISCNTDAYS,
               DISCNTAMT,             REALINCOMAMT,       REALLANDAMT,         REALBUILDAMT,
               REALVATAMT,            BANK_CODE,          BANK_NAME,           PAYTAG,
               INCOMTYPE,             MOD_YN,             REAL_PAY_TAG,        SLIPDT,
               SLIPSEQ,               TAXDATE,            TAXSEQ,              INSEQ,
               INPUT_DUTY_ID,         INPUT_DATE,         CHG_DUTY_ID,         CHG_DATE,
               SLIPTYPE,              VDEPOSIT_NO,        DETAILMOD_YN,        OUT_DT,
               OUT_TM,                OUT_SEQ,            OUT_BANK,            REMARK,
               RECEIPTMANAGEAMT,      REALMANAGEAMT,      CDNO,                CD_BANK,
               CD_EDATE,              CD_STYPE
            )
     VALUES ( :OLD.CUST_CODE,        :OLD.SEQ,           :OLD.COUNTS,         :OLD.TIMES,
              'D',                    TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,               :OLD.DEPT_CODE,
              :OLD.HOUSETAG,         :OLD.BUILDNO,       :OLD.HOUSENO,        :OLD.DEPOSIT_NO,
              :OLD.RECEIPTDATE,      :OLD.RECEIPTAMT,    :OLD.RECEIPTLANDAMT, :OLD.RECEIPTBUILDAMT,
              :OLD.RECEIPTVATAMT,    :OLD.DELAYDAYS,     :OLD.DELAYAMT,       :OLD.DISCNTDAYS,
              :OLD.DISCNTAMT,        :OLD.REALINCOMAMT,  :OLD.REALLANDAMT,    :OLD.REALBUILDAMT,
              :OLD.REALVATAMT,       :OLD.BANK_CODE,     :OLD.BANK_NAME,      :OLD.PAYTAG,
              :OLD.INCOMTYPE,        :OLD.MOD_YN,        :OLD.REAL_PAY_TAG,   :OLD.SLIPDT,
              :OLD.SLIPSEQ,          :OLD.TAXDATE,       :OLD.TAXSEQ,         :OLD.INSEQ,
              :OLD.INPUT_DUTY_ID,    :OLD.INPUT_DATE,    :OLD.CHG_DUTY_ID,    :OLD.CHG_DATE,
              :OLD.SLIPTYPE,         :OLD.VDEPOSIT_NO,   :OLD.DETAILMOD_YN,   :OLD.OUT_DT,
              :OLD.OUT_TM,           :OLD.OUT_SEQ,       :OLD.OUT_BANK,       :OLD.REMARK,
              :OLD.RECEIPTMANAGEAMT, :OLD.REALMANAGEAMT, :OLD.CDNO,           :OLD.CD_BANK,
              :OLD.CD_EDATE,         :OLD.CD_STYPE
            );
END;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_RATE_DELAY;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_RATE_DELAY AFTER DELETE ON KAIT.HD_HOUS_RATE_DELAY FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DELAY_LOG
    WHERE CUST_CODE  = :OLD.CUST_CODE
      AND SEQ        = :OLD.SEQ
      AND START_DAYS = :OLD.START_DAYS
      AND END_DAYS   = :OLD.END_DAYS
      AND STARTDATE  = :OLD.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DELAY_LOG
             ( CUST_CODE,       SEQ,              START_DAYS,       END_DAYS,
               STARTDATE,       WRITETAG,         WRITETIME,
               WRITESEQ,        ENDDATE,          DELAYRATE,        DELAYCUT,
               DELAYUNIT,       START_TAG,        END_TAG,          INPUT_DUTY_ID,
               INPUT_DATE,      CHG_DUTY_ID,      CHG_DATE )
      VALUES (:OLD.CUST_CODE,  :OLD.SEQ,         :OLD.START_DAYS,  :OLD.END_DAYS,
              :OLD.STARTDATE,  'D',               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :OLD.ENDDATE,     :OLD.DELAYRATE,   :OLD.DELAYCUT,
              :OLD.DELAYUNIT,  :OLD.START_TAG,   :OLD.END_TAG,     :OLD.INPUT_DUTY_ID,
              :OLD.INPUT_DATE, :OLD.CHG_DUTY_ID, :OLD.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_RATE_DISCOUNT;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_RATE_DISCOUNT AFTER DELETE ON KAIT.HD_HOUS_RATE_DISCOUNT FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DISCOUNT_LOG
    WHERE CUST_CODE = :OLD.CUST_CODE
      AND SEQ       = :OLD.SEQ
      AND STARTDATE = :OLD.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DISCOUNT_LOG
             ( CUST_CODE,       SEQ,                STARTDATE,       WRITETAG,
               WRITETIME,
               WRITESEQ,        ENDDATE,            DISCNTRATE,      DISCNTCUT,
               DISCNTUNIT,      INPUT_DUTY_ID,      INPUT_DATE,      CHG_DUTY_ID,
               CHG_DATE )
      VALUES (:OLD.CUST_CODE,  :OLD.SEQ,           :OLD.STARTDATE,  'D',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :OLD.ENDDATE,       :OLD.DISCNTRATE, :OLD.DISCNTCUT,
              :OLD.DISCNTUNIT, :OLD.INPUT_DUTY_ID, :OLD.INPUT_DATE, :OLD.CHG_DUTY_ID,
              :OLD.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_SELL;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_SELL AFTER DELETE ON KAIT.HD_HOUS_SELL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELL_LOG
    WHERE CUST_CODE = :OLD.CUST_CODE
      AND SEQ       = :OLD.SEQ;

   INSERT INTO HD_HOUS_SELL_LOG
             ( CUST_CODE,           SEQ,                   WRITETAG,                WRITETIME,
               WRITESEQ,            DEPT_CODE,             HOUSETAG,                BUILDNO,
               HOUSENO,             DONGHO,                CUST_NAME,               SQUARE,
               TYPE,                CLASS,                 OPTIONCODE,              CONTRACTTAG,
               CONTRACTDATE,        CONTRACTNO,            LOAN_TAG,                LEASETAG,
               LASTCHANGEDATE,      CHANGETAG,             CHANGEDATE,              CANCEL_REASON,
               CHILD_BUILDNO,       CHILD_HOUSENO,         RELA_CUSTCODE,           RELA_SEQ,
               VATTAG,              EXCLUSIVEAREA,         COMMONAREA,              ETCCOMMONAREA,
               PARKINGAREA,         SERVICEAREA,           SITEAREA,                MOVEINSTARTDATE,
               MOVEINENDDATE,       UNION_CNT,             REMARK,                  REFUNDMENTDATE,
               REFUNDMENTAMT,       PENALTYAMT,            LOAN_INTEREST,           SODUK_TAX,
               JUMIN_TAX,           BANK_LOAN_ORGAMT,      BANK_LOAN_INTEREST,      LOANBANK,
               LOANDEPOSIT,         LOANUSER,              REFUND_DEPOSIT,          REFUND_BANK,
               COMP_LOANAMT,        BILL_RETURNAMT,        DELAY_INDEMINITY,        DEPOSIT_COUNT,
               CO_CUSTCODE,         CO_SANGHO,             CO_CONDITION,            CO_CATEGORY,
               CATEGORY_NAME,       SLIPDATE,              SLIPSEQ,                 INPUT_DUTY_ID,
               INPUT_DATE,          CHG_DUTY_ID,           CHG_DATE,                APPLY_YN,
               APPLY_EMPNO,         APPLY_DATE,            PRTSQUARE,               BANK_LOAN_INTEREST2,
               ETC_AMT,             RENTHD_YN,             RENTHD_SEQ,              BALCONY_TAG,
               BALCONYAREA,         DAYMONTH_TAG,          FLOOR,                   CONT_CONDITION,
               LAND_RETURN,         INT_CALC_DATE,         PREDISAMT,               PROXYAMT,
               INCONT_DATE,         TRUSTAMT,              PREDIS_TAG,              PROXY_TAG,
               TRUST_TAG,           VIR_YN,                VDEPOSIT,
               REP_LIMITDT,         REP_YN,                REP_DATE
             )
      VALUES (:OLD.CUST_CODE,      :OLD.SEQ,              'D',                      TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,             :OLD.DEPT_CODE,        :OLD.HOUSETAG,           :OLD.BUILDNO,
              :OLD.HOUSENO,        :OLD.DONGHO,           :OLD.CUST_NAME,          :OLD.SQUARE,
              :OLD.TYPE,           :OLD.CLASS,            :OLD.OPTIONCODE,         :OLD.CONTRACTTAG,
              :OLD.CONTRACTDATE,   :OLD.CONTRACTNO,       :OLD.LOAN_TAG,           :OLD.LEASETAG,
              :OLD.LASTCHANGEDATE, :OLD.CHANGETAG,        :OLD.CHANGEDATE,         :OLD.CANCEL_REASON,
              :OLD.CHILD_BUILDNO,  :OLD.CHILD_HOUSENO,    :OLD.RELA_CUSTCODE,      :OLD.RELA_SEQ,
              :OLD.VATTAG,         :OLD.EXCLUSIVEAREA,    :OLD.COMMONAREA,         :OLD.ETCCOMMONAREA,
              :OLD.PARKINGAREA,    :OLD.SERVICEAREA,      :OLD.SITEAREA,           :OLD.MOVEINSTARTDATE,
              :OLD.MOVEINENDDATE,  :OLD.UNION_CNT,        :OLD.REMARK,             :OLD.REFUNDMENTDATE,
              :OLD.REFUNDMENTAMT,  :OLD.PENALTYAMT,       :OLD.LOAN_INTEREST,      :OLD.SODUK_TAX,
              :OLD.JUMIN_TAX,      :OLD.BANK_LOAN_ORGAMT, :OLD.BANK_LOAN_INTEREST, :OLD.LOANBANK,
              :OLD.LOANDEPOSIT,    :OLD.LOANUSER,         :OLD.REFUND_DEPOSIT,     :OLD.REFUND_BANK,
              :OLD.COMP_LOANAMT,   :OLD.BILL_RETURNAMT,   :OLD.DELAY_INDEMINITY,   :OLD.DEPOSIT_COUNT,
              :OLD.CO_CUSTCODE,    :OLD.CO_SANGHO,        :OLD.CO_CONDITION,       :OLD.CO_CATEGORY,
              :OLD.CATEGORY_NAME,  :OLD.SLIPDATE,         :OLD.SLIPSEQ,            :OLD.INPUT_DUTY_ID,
              :OLD.INPUT_DATE,     :OLD.CHG_DUTY_ID,      :OLD.CHG_DATE,           :OLD.APPLY_YN,
              :OLD.APPLY_EMPNO,    :OLD.APPLY_DATE,       :OLD.PRTSQUARE,          :OLD.BANK_LOAN_INTEREST2,
              :OLD.ETC_AMT,        :OLD.RENTHD_YN,        :OLD.RENTHD_SEQ,         :OLD.BALCONY_TAG,
              :OLD.BALCONYAREA,    :OLD.DAYMONTH_TAG,     :OLD.FLOOR,              :OLD.CONT_CONDITION,
              :OLD.LAND_RETURN,    :OLD.INT_CALC_DATE,    :OLD.PREDISAMT,          :OLD.PROXYAMT,
              :OLD.INCONT_DATE,    :OLD.TRUSTAMT,         :OLD.PREDIS_TAG,         :OLD.PROXY_TAG,
              :OLD.TRUST_TAG,      :OLD.VIR_YN,           :OLD.VDEPOSIT,
              :OLD.REP_LIMITDT,    :OLD.REP_YN,           :OLD.REP_DATE
             );
END;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_SELLDETAIL;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_SELLDETAIL AFTER DELETE ON KAIT.HD_HOUS_SELLDETAIL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELLDETAIL_LOG
    WHERE CUST_CODE = :OLD.CUST_CODE
      AND SEQ       = :OLD.SEQ
      AND COUNTS    = :OLD.COUNTS;

   INSERT INTO HD_HOUS_SELLDETAIL_LOG
             ( CUST_CODE,       SEQ,                COUNTS,               WRITETAG,
               WRITETIME,
               WRITESEQ,        DEPT_CODE,          HOUSETAG,             BUILDNO,
               HOUSENO,         AGREEDATE,          LANDAMT,              BUILDAMT,
               VATAMT,          BUNAMT,             DC_YN,                AC_YN,
               PERPECTTAG,      RECEIPTAMT,         DISTRIBUTE_RATE,      SLIPDT,
               SLIPSEQ,         INPUT_DUTY_ID,      INPUT_DATE,           CHG_DUTY_ID,
               CHG_DATE,        MANAGEAMT )
      VALUES (:OLD.CUST_CODE,  :OLD.SEQ,           :OLD.COUNTS,          'D',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :OLD.DEPT_CODE,     :OLD.HOUSETAG,        :OLD.BUILDNO,
              :OLD.HOUSENO,    :OLD.AGREEDATE,     :OLD.LANDAMT,         :OLD.BUILDAMT,
              :OLD.VATAMT,     :OLD.BUNAMT,        :OLD.DC_YN,           :OLD.AC_YN,
              :OLD.PERPECTTAG, :OLD.RECEIPTAMT,    :OLD.DISTRIBUTE_RATE, :OLD.SLIPDT,
              :OLD.SLIPSEQ,    :OLD.INPUT_DUTY_ID, :OLD.INPUT_DATE,      :OLD.CHG_DUTY_ID,
              :OLD.CHG_DATE,   :OLD.MANAGEAMT);
END;
/


DROP TRIGGER KAIT.TDA_HD_HOUS_SUPPLY;

CREATE OR REPLACE TRIGGER KAIT.TDA_HD_HOUS_SUPPLY
  AFTER DELETE
  on HD_HOUS_SUPPLY
  
  for each row
/* ERwin Builtin Fri Sep 01 12:16:11 2006 */
/* default body for TDA_HD_HOUS_SUPPLY */
DECLARE NEWSEQ INTEGER;

begin
   SELECT NVL(MAX(SEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SUPPLY_HIST
    WHERE DEPT_CODE = :OLD.DEPT_CODE
      AND HOUSETAG  = :OLD.HOUSETAG
      AND BUILDNO   = :OLD.BUILDNO
      AND HOUSENO   = :OLD.HOUSENO;

   INSERT INTO HD_HOUS_SUPPLY_HIST
   (    DEPT_CODE      ,
        HOUSETAG       ,
        BUILDNO        ,
        HOUSENO        ,
        HIST_DATE      ,
        SEQ            ,
        SQUARE         ,
        TYPE           ,
        CLASS          ,
        OPTIONCODE     ,
        VATTAG         ,
        EXCLUSIVEAREA  ,
        COMMONAREA     ,
        ETCCOMMONAREA  ,
        PARKINGAREA    ,
        SERVICEAREA    ,
        SITEAREA       ,
        FLOOR          ,
        GUBUN          ,
        CATEGORY_NAME  ,
        CONTRACTYESNO  ,
        VIRDEPOSIT     ,
        BANK_CODE      ,
        BANK_NAME      ,
        USE_YN         ,
        RENT_TAG       ,
        INPUT_DUTY_ID  ,
        INPUT_DATE     ,
        CHG_DUTY_ID    ,
        CHG_DATE       ,
        PRTSQUARE      ,
        PREDISAMT      ,
        TRI_TAG        ,
        VIRDEPOSIT2
   )
   VALUES
   (    :OLD.DEPT_CODE      ,
        :OLD.HOUSETAG       ,
        :OLD.BUILDNO        ,
        :OLD.HOUSENO        ,
        SYSDATE             ,
        NEWSEQ              ,
        :OLD.SQUARE         ,
        :OLD.TYPE           ,
        :OLD.CLASS          ,
        :OLD.OPTIONCODE     ,
        :OLD.VATTAG         ,
        :OLD.EXCLUSIVEAREA  ,
        :OLD.COMMONAREA     ,
        :OLD.ETCCOMMONAREA  ,
        :OLD.PARKINGAREA    ,
        :OLD.SERVICEAREA    ,
        :OLD.SITEAREA       ,
        :OLD.FLOOR          ,
        :OLD.GUBUN          ,
        :OLD.CATEGORY_NAME  ,
        :OLD.CONTRACTYESNO  ,
        :OLD.VIRDEPOSIT     ,
        :OLD.BANK_CODE      ,
        :OLD.BANK_NAME      ,
        :OLD.USE_YN         ,
        :OLD.RENT_TAG       ,
        :OLD.INPUT_DUTY_ID  ,
        :OLD.INPUT_DATE     ,
        :OLD.CHG_DUTY_ID    ,
        :OLD.CHG_DATE       ,
        :OLD.PRTSQUARE      ,
        :OLD.PREDISAMT      ,
        'D'                 ,
        :OLD.VIRDEPOSIT2
   );
end;
/


DROP TRIGGER KAIT.TDB_HD_HOUS_SUPPLY;

CREATE OR REPLACE TRIGGER KAIT.TDB_HD_HOUS_SUPPLY BEFORE DELETE ON KAIT.HD_HOUS_SUPPLY FOR EACH ROW
BEGIN
   DELETE HD_REFER_SELLDETAIL
    WHERE DEPT_CODE = :OLD.DEPT_CODE
      AND HOUSETAG  = :OLD.HOUSETAG
      AND BUILDNO   = :OLD.BUILDNO
      AND HOUSENO   = :OLD.HOUSENO;
END;
/


DROP TRIGGER KAIT.TDB_HD_REFER_SQUARE;

CREATE OR REPLACE TRIGGER KAIT.TDB_HD_REFER_SQUARE BEFORE DELETE ON KAIT.HD_REFER_SQUARE FOR EACH ROW
BEGIN
   DELETE HD_REFER_SQUAREDETAIL
    WHERE DEPT_CODE = :OLD.DEPT_CODE
      AND HOUSETAG  = :OLD.HOUSETAG
      AND SEQ       = :OLD.SEQ;
END;
/


DROP TRIGGER KAIT.TH_HD_CODE_DEPT;

CREATE OR REPLACE TRIGGER KAIT.TH_HD_CODE_DEPT
 AFTER UPDATE OR DELETE
 ON KAIT.HD_CODE_DEPT  FOR EACH ROW
DECLARE
/******************************************/
/***** HIST 테이블 DATA INSERT ************/
/******************************************/
   vTag VARCHAR2(1);
BEGIN

   IF UPDATING THEN
      vTag := 'U';
   ELSE
      vTag := 'D';
   END IF;

   INSERT INTO HD_CODE_DEPT_HIST
   ( HIST_DATE,
     DEPT_CODE,
     DEPT_NAME,
     TEL,
     ZIP,
     ADDR1,
     ADDR2,
     LISTTAG,
     JOBTAG,
     COMPANY_CODE,
     AMIS_DEPTCODE,
     TAX_TAG,
     SIHANG_VENDOR,
     SIHANG_NAME,
     SIHANG_DEPYO,
     SIHANG_UPTE,
     SIHANG_UPJONG,
     SIHANG_ZIP,
     SIHANG_ADDR1,
     SIHANG_ADDR2,
     SIGONG_NAME,
     SIGONG_DEPYO,
     SIGONG_CHARGE,
     SIGONG_TEL,
     MODEL_ZIP,
     MODEL_ADDR1,
     MODEL_ADDR2,
     MODEL_TEL,
     DEPT_TYPE_CODE,
     REMARK,
     INPUT_DUTY_ID,
     INPUT_DATE,
     CHG_DUTY_ID,
     CHG_DATE,
     SMS_NAME,
     SMS_TEL,
     OLD_DEPTCODE,
     ZIP_ORG,
     ADDR1_ORG,
     ADDR2_ORG,
     ADDR_TAG,
     SIHANG_ZIP_ORG,
     SIHANG_ADDR1_ORG,
     SIHANG_ADDR2_ORG,
     SIHANG_ADDR_TAG,
     MODEL_ZIP_ORG,
     MODEL_ADDR1_ORG,
     MODEL_ADDR2_ORG,
     MODEL_ADDR_TAG,
     TRI_TAG )
   VALUES
   (  SYSTIMESTAMP  ,
       :OLD.DEPT_CODE,
       :OLD.DEPT_NAME,
       :OLD.TEL,
       :OLD.ZIP,
       :OLD.ADDR1,
       :OLD.ADDR2,
       :OLD.LISTTAG,
       :OLD.JOBTAG,
       :OLD.COMPANY_CODE,
       :OLD.AMIS_DEPTCODE,
       :OLD.TAX_TAG,
       :OLD.SIHANG_VENDOR,
       :OLD.SIHANG_NAME,
       :OLD.SIHANG_DEPYO,
       :OLD.SIHANG_UPTE,
       :OLD.SIHANG_UPJONG,
       :OLD.SIHANG_ZIP,
       :OLD.SIHANG_ADDR1,
       :OLD.SIHANG_ADDR2,
       :OLD.SIGONG_NAME,
       :OLD.SIGONG_DEPYO,
       :OLD.SIGONG_CHARGE,
       :OLD.SIGONG_TEL,
       :OLD.MODEL_ZIP,
       :OLD.MODEL_ADDR1,
       :OLD.MODEL_ADDR2,
       :OLD.MODEL_TEL,
       :OLD.DEPT_TYPE_CODE,
       :OLD.REMARK,
       :OLD.INPUT_DUTY_ID,
       :OLD.INPUT_DATE,
       :OLD.CHG_DUTY_ID,
       :OLD.CHG_DATE,
       :OLD.SMS_NAME,
       :OLD.SMS_TEL,
       :OLD.OLD_DEPTCODE,
       :OLD.ZIP_ORG,
       :OLD.ADDR1_ORG,
       :OLD.ADDR2_ORG,
       :OLD.ADDR_TAG,
       :OLD.SIHANG_ZIP_ORG,
       :OLD.SIHANG_ADDR1_ORG,
       :OLD.SIHANG_ADDR2_ORG,
       :OLD.SIHANG_ADDR_TAG,
       :OLD.MODEL_ZIP_ORG,
       :OLD.MODEL_ADDR1_ORG,
       :OLD.MODEL_ADDR2_ORG,
       :OLD.MODEL_ADDR_TAG,
       vTag );
END;
/


DROP TRIGGER KAIT.TH_HD_CODE_SIHANG;

CREATE OR REPLACE TRIGGER KAIT.TH_HD_CODE_SIHANG
 AFTER UPDATE OR DELETE
 ON KAIT.HD_CODE_SIHANG  FOR EACH ROW
DECLARE
/******************************************/
/***** HIST 테이블 DATA INSERT ************/
/******************************************/
   vTag VARCHAR2(1);
BEGIN

   IF UPDATING THEN
      vTag := 'U';
   ELSE
      vTag := 'D';
   END IF;

   INSERT INTO HD_CODE_SIHANG_HIST
   ( HIST_DATE,
     DEPT_CODE,
     SEQ,
     SIHANG_VENDOR,
     SIHANG_DEPYO,
     SIHANG_UPTE,
     SIHANG_UPJONG,
     SIHANG_ZIP,
     SIHANG_ADDR1,
     SIHANG_ADDR2,
     INPUT_DUTY_ID,
     INPUT_DATE,
     CHG_DUTY_ID,
     SIHANG_NAME,
     CHG_DATE,
     SIHANG_ZIP_ORG,
     SIHANG_ADDR1_ORG,
     SIHANG_ADDR2_ORG,
     SIHANG_ADDR_TAG,
     TRI_TAG )
   VALUES
   (  SYSTIMESTAMP  ,
       :OLD.DEPT_CODE,
       :OLD.SEQ,
       :OLD.SIHANG_VENDOR,
       :OLD.SIHANG_DEPYO,
       :OLD.SIHANG_UPTE,
       :OLD.SIHANG_UPJONG,
       :OLD.SIHANG_ZIP,
       :OLD.SIHANG_ADDR1,
       :OLD.SIHANG_ADDR2,
       :OLD.INPUT_DUTY_ID,
       :OLD.INPUT_DATE,
       :OLD.CHG_DUTY_ID,
       :OLD.SIHANG_NAME,
       :OLD.CHG_DATE,
       :OLD.SIHANG_ZIP_ORG,
       :OLD.SIHANG_ADDR1_ORG,
       :OLD.SIHANG_ADDR2_ORG,
       :OLD.SIHANG_ADDR_TAG,
       vTag );
END;
/


DROP TRIGGER KAIT.TIA_HD_HOUS_INCOME;

CREATE OR REPLACE TRIGGER KAIT.TIA_HD_HOUS_INCOME AFTER INSERT ON KAIT.HD_HOUS_INCOME FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_INCOME_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND COUNTS    = :NEW.COUNTS
      AND TIMES     = :NEW.TIMES;

   INSERT INTO HD_HOUS_INCOME_LOG
             ( CUST_CODE,             SEQ,                COUNTS,              TIMES,
               WRITETAG,              WRITETIME,
               WRITESEQ,              DEPT_CODE,
               HOUSETAG,              BUILDNO,            HOUSENO,             DEPOSIT_NO,
               RECEIPTDATE,           RECEIPTAMT,         RECEIPTLANDAMT,      RECEIPTBUILDAMT,
               RECEIPTVATAMT,         DELAYDAYS,          DELAYAMT,            DISCNTDAYS,
               DISCNTAMT,             REALINCOMAMT,       REALLANDAMT,         REALBUILDAMT,
               REALVATAMT,            BANK_CODE,          BANK_NAME,           PAYTAG,
               INCOMTYPE,             MOD_YN,             REAL_PAY_TAG,        SLIPDT,
               SLIPSEQ,               TAXDATE,            TAXSEQ,              INSEQ,
               INPUT_DUTY_ID,         INPUT_DATE,         CHG_DUTY_ID,         CHG_DATE,
               SLIPTYPE,              VDEPOSIT_NO,        DETAILMOD_YN,        OUT_DT,
               OUT_TM,                OUT_SEQ,            OUT_BANK,            REMARK,
               RECEIPTMANAGEAMT,      REALMANAGEAMT,      CDNO,                CD_BANK,
               CD_EDATE,              CD_STYPE
            )
     VALUES ( :NEW.CUST_CODE,        :NEW.SEQ,           :NEW.COUNTS,         :NEW.TIMES,
              'I',                    TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,               :NEW.DEPT_CODE,
              :NEW.HOUSETAG,         :NEW.BUILDNO,       :NEW.HOUSENO,        :NEW.DEPOSIT_NO,
              :NEW.RECEIPTDATE,      :NEW.RECEIPTAMT,    :NEW.RECEIPTLANDAMT, :NEW.RECEIPTBUILDAMT,
              :NEW.RECEIPTVATAMT,    :NEW.DELAYDAYS,     :NEW.DELAYAMT,       :NEW.DISCNTDAYS,
              :NEW.DISCNTAMT,        :NEW.REALINCOMAMT,  :NEW.REALLANDAMT,    :NEW.REALBUILDAMT,
              :NEW.REALVATAMT,       :NEW.BANK_CODE,     :NEW.BANK_NAME,      :NEW.PAYTAG,
              :NEW.INCOMTYPE,        :NEW.MOD_YN,        :NEW.REAL_PAY_TAG,   :NEW.SLIPDT,
              :NEW.SLIPSEQ,          :NEW.TAXDATE,       :NEW.TAXSEQ,         :NEW.INSEQ,
              :NEW.INPUT_DUTY_ID,    :NEW.INPUT_DATE,    :NEW.CHG_DUTY_ID,    :NEW.CHG_DATE,
              :NEW.SLIPTYPE,         :NEW.VDEPOSIT_NO,   :NEW.DETAILMOD_YN,   :NEW.OUT_DT,
              :NEW.OUT_TM,           :NEW.OUT_SEQ,       :NEW.OUT_BANK,       :NEW.REMARK,
              :NEW.RECEIPTMANAGEAMT, :NEW.REALMANAGEAMT, :NEW.CDNO,           :NEW.CD_BANK,
              :NEW.CD_EDATE,         :NEW.CD_STYPE
             );
END;
/


DROP TRIGGER KAIT.TIA_HD_HOUS_RATE_DELAY;

CREATE OR REPLACE TRIGGER KAIT.TIA_HD_HOUS_RATE_DELAY AFTER INSERT ON KAIT.HD_HOUS_RATE_DELAY FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DELAY_LOG
    WHERE CUST_CODE  = :NEW.CUST_CODE
      AND SEQ        = :NEW.SEQ
      AND START_DAYS = :NEW.START_DAYS
      AND END_DAYS   = :NEW.END_DAYS
      AND STARTDATE  = :NEW.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DELAY_LOG
             ( CUST_CODE,       SEQ,              START_DAYS,      END_DAYS,
               STARTDATE,       WRITETAG,         WRITETIME,
               WRITESEQ,        ENDDATE,          DELAYRATE,       DELAYCUT,
               DELAYUNIT,       START_TAG,        END_TAG,         INPUT_DUTY_ID,
               INPUT_DATE,      CHG_DUTY_ID,      CHG_DATE )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,         :NEW.START_DAYS, :NEW.END_DAYS,
              :NEW.STARTDATE,  'I',               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.ENDDATE,     :NEW.DELAYRATE,  :NEW.DELAYCUT,
              :NEW.DELAYUNIT,  :NEW.START_TAG,   :NEW.END_TAG,    :NEW.INPUT_DUTY_ID,
              :NEW.INPUT_DATE, :NEW.CHG_DUTY_ID, :NEW.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TIA_HD_HOUS_RATE_DISCOUNT;

CREATE OR REPLACE TRIGGER KAIT.TIA_HD_HOUS_RATE_DISCOUNT AFTER INSERT ON KAIT.HD_HOUS_RATE_DISCOUNT FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DISCOUNT_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND STARTDATE = :NEW.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DISCOUNT_LOG
             ( CUST_CODE,       SEQ,                STARTDATE,       WRITETAG,
               WRITETIME,
               WRITESEQ,        ENDDATE,            DISCNTRATE,      DISCNTCUT,
               DISCNTUNIT,      INPUT_DUTY_ID,      INPUT_DATE,      CHG_DUTY_ID,
               CHG_DATE )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,           :NEW.STARTDATE,  'I',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.ENDDATE,       :NEW.DISCNTRATE, :NEW.DISCNTCUT,
              :NEW.DISCNTUNIT, :NEW.INPUT_DUTY_ID, :NEW.INPUT_DATE, :NEW.CHG_DUTY_ID,
              :NEW.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TIA_HD_HOUS_SELL;

CREATE OR REPLACE TRIGGER KAIT.TIA_HD_HOUS_SELL AFTER INSERT ON KAIT.HD_HOUS_SELL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELL_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ;

   INSERT INTO HD_HOUS_SELL_LOG
             ( CUST_CODE,           SEQ,                   WRITETAG,                WRITETIME,
               WRITESEQ,            DEPT_CODE,             HOUSETAG,                BUILDNO,
               HOUSENO,             DONGHO,                CUST_NAME,               SQUARE,
               TYPE,                CLASS,                 OPTIONCODE,              CONTRACTTAG,
               CONTRACTDATE,        CONTRACTNO,            LOAN_TAG,                LEASETAG,
               LASTCHANGEDATE,      CHANGETAG,             CHANGEDATE,              CANCEL_REASON,
               CHILD_BUILDNO,       CHILD_HOUSENO,         RELA_CUSTCODE,           RELA_SEQ,
               VATTAG,              EXCLUSIVEAREA,         COMMONAREA,              ETCCOMMONAREA,
               PARKINGAREA,         SERVICEAREA,           SITEAREA,                MOVEINSTARTDATE,
               MOVEINENDDATE,       UNION_CNT,             REMARK,                  REFUNDMENTDATE,
               REFUNDMENTAMT,       PENALTYAMT,            LOAN_INTEREST,           SODUK_TAX,
               JUMIN_TAX,           BANK_LOAN_ORGAMT,      BANK_LOAN_INTEREST,      LOANBANK,
               LOANDEPOSIT,         LOANUSER,              REFUND_DEPOSIT,          REFUND_BANK,
               COMP_LOANAMT,        BILL_RETURNAMT,        DELAY_INDEMINITY,        DEPOSIT_COUNT,
               CO_CUSTCODE,         CO_SANGHO,             CO_CONDITION,            CO_CATEGORY,
               CATEGORY_NAME,       SLIPDATE,              SLIPSEQ,                 INPUT_DUTY_ID,
               INPUT_DATE,          CHG_DUTY_ID,           CHG_DATE,                APPLY_YN,
               APPLY_EMPNO,         APPLY_DATE,            PRTSQUARE,               BANK_LOAN_INTEREST2,
               ETC_AMT,             RENTHD_YN,             RENTHD_SEQ,              BALCONY_TAG,
               BALCONYAREA,         DAYMONTH_TAG,          FLOOR,                   CONT_CONDITION,
               LAND_RETURN,         INT_CALC_DATE,         PREDISAMT,               PROXYAMT,
               INCONT_DATE,         TRUSTAMT,              PREDIS_TAG,              PROXY_TAG,
               TRUST_TAG,           VIR_YN,                VDEPOSIT,
               REP_LIMITDT,         REP_YN,                REP_DATE
             )
      VALUES (:NEW.CUST_CODE,      :NEW.SEQ,              'I',                      TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,             :NEW.DEPT_CODE,        :NEW.HOUSETAG,           :NEW.BUILDNO,
              :NEW.HOUSENO,        :NEW.DONGHO,           :NEW.CUST_NAME,          :NEW.SQUARE,
              :NEW.TYPE,           :NEW.CLASS,            :NEW.OPTIONCODE,         :NEW.CONTRACTTAG,
              :NEW.CONTRACTDATE,   :NEW.CONTRACTNO,       :NEW.LOAN_TAG,           :NEW.LEASETAG,
              :NEW.LASTCHANGEDATE, :NEW.CHANGETAG,        :NEW.CHANGEDATE,         :NEW.CANCEL_REASON,
              :NEW.CHILD_BUILDNO,  :NEW.CHILD_HOUSENO,    :NEW.RELA_CUSTCODE,      :NEW.RELA_SEQ,
              :NEW.VATTAG,         :NEW.EXCLUSIVEAREA,    :NEW.COMMONAREA,         :NEW.ETCCOMMONAREA,
              :NEW.PARKINGAREA,    :NEW.SERVICEAREA,      :NEW.SITEAREA,           :NEW.MOVEINSTARTDATE,
              :NEW.MOVEINENDDATE,  :NEW.UNION_CNT,        :NEW.REMARK,             :NEW.REFUNDMENTDATE,
              :NEW.REFUNDMENTAMT,  :NEW.PENALTYAMT,       :NEW.LOAN_INTEREST,      :NEW.SODUK_TAX,
              :NEW.JUMIN_TAX,      :NEW.BANK_LOAN_ORGAMT, :NEW.BANK_LOAN_INTEREST, :NEW.LOANBANK,
              :NEW.LOANDEPOSIT,    :NEW.LOANUSER,         :NEW.REFUND_DEPOSIT,     :NEW.REFUND_BANK,
              :NEW.COMP_LOANAMT,   :NEW.BILL_RETURNAMT,   :NEW.DELAY_INDEMINITY,   :NEW.DEPOSIT_COUNT,
              :NEW.CO_CUSTCODE,    :NEW.CO_SANGHO,        :NEW.CO_CONDITION,       :NEW.CO_CATEGORY,
              :NEW.CATEGORY_NAME,  :NEW.SLIPDATE,         :NEW.SLIPSEQ,            :NEW.INPUT_DUTY_ID,
              :NEW.INPUT_DATE,     :NEW.CHG_DUTY_ID,      :NEW.CHG_DATE,           :NEW.APPLY_YN,
              :NEW.APPLY_EMPNO,    :NEW.APPLY_DATE,       :NEW.PRTSQUARE,          :NEW.BANK_LOAN_INTEREST2,
              :NEW.ETC_AMT,        :NEW.RENTHD_YN,        :NEW.RENTHD_SEQ,         :NEW.BALCONY_TAG,
              :NEW.BALCONYAREA,    :NEW.DAYMONTH_TAG,     :NEW.FLOOR,              :NEW.CONT_CONDITION,
              :NEW.LAND_RETURN,    :NEW.INT_CALC_DATE,    :NEW.PREDISAMT,          :NEW.PROXYAMT,
              :NEW.INCONT_DATE,    :NEW.TRUSTAMT,         :NEW.PREDIS_TAG,         :NEW.PROXY_TAG,
              :NEW.TRUST_TAG,      :NEW.VIR_YN,           :NEW.VDEPOSIT,
              :NEW.REP_LIMITDT,    :NEW.REP_YN,           :NEW.REP_DATE
             );
END;
/


DROP TRIGGER KAIT.TIA_HD_HOUS_SELLDETAIL;

CREATE OR REPLACE TRIGGER KAIT.TIA_HD_HOUS_SELLDETAIL AFTER INSERT ON KAIT.HD_HOUS_SELLDETAIL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELLDETAIL_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND COUNTS    = :NEW.COUNTS;

   INSERT INTO HD_HOUS_SELLDETAIL_LOG
             ( CUST_CODE,       SEQ,                COUNTS,               WRITETAG,
               WRITETIME,
               WRITESEQ,        DEPT_CODE,          HOUSETAG,             BUILDNO,
               HOUSENO,         AGREEDATE,          LANDAMT,              BUILDAMT,
               VATAMT,          BUNAMT,             DC_YN,                AC_YN,
               PERPECTTAG,      RECEIPTAMT,         DISTRIBUTE_RATE,      SLIPDT,
               SLIPSEQ,         INPUT_DUTY_ID,      INPUT_DATE,           CHG_DUTY_ID,
               CHG_DATE,        MANAGEAMT )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,           :NEW.COUNTS,          'I',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.DEPT_CODE,     :NEW.HOUSETAG,        :NEW.BUILDNO,
              :NEW.HOUSENO,    :NEW.AGREEDATE,     :NEW.LANDAMT,         :NEW.BUILDAMT,
              :NEW.VATAMT,     :NEW.BUNAMT,        :NEW.DC_YN,           :NEW.AC_YN,
              :NEW.PERPECTTAG, :NEW.RECEIPTAMT,    :NEW.DISTRIBUTE_RATE, :NEW.SLIPDT,
              :NEW.SLIPSEQ,    :NEW.INPUT_DUTY_ID, :NEW.INPUT_DATE,      :NEW.CHG_DUTY_ID,
              :NEW.CHG_DATE,   :NEW.MANAGEAMT );
END;
/


DROP TRIGGER KAIT.TIB_HD_HOUS_SELL;

CREATE OR REPLACE TRIGGER KAIT.TIB_HD_HOUS_SELL BEFORE INSERT ON KAIT.HD_HOUS_SELL FOR EACH ROW
DECLARE CNT INTEGER;

BEGIN
   CNT := 0;
   IF :NEW.CHANGETAG = '1' THEN BEGIN
      SELECT COUNT(*)
        INTO CNT
        FROM HD_HOUS_SUPPLY
       WHERE DEPT_CODE     = :NEW.DEPT_CODE
         AND HOUSETAG      = :NEW.HOUSETAG
         AND BUILDNO       = :NEW.BUILDNO
         AND HOUSENO       = :NEW.HOUSENO
         AND CONTRACTYESNO = 'Y';
      IF CNT > 0 THEN BEGIN
         RAISE_APPLICATION_ERROR(-20111, '해당동호는 이미 계약이 되었습니다');
      END; END IF;
   END; END IF;

END;
/


DROP TRIGGER KAIT.TUA_HD_CODE_HOUSE;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_CODE_HOUSE
  AFTER UPDATE
  ON KAIT.HD_CODE_HOUSE   for each row
/* ERwin Builtin Fri Sep 01 12:16:11 2006 */
/* default body for TUA_HD_CODE_HOUSE */
declare numrows INTEGER;
begin
   INSERT INTO HD_CODE_HOUSE_HIST
   (    HIST_DATE         ,
        DEPT_CODE         ,
        HOUSETAG          ,
        HOUSEDATE         ,
        COMPLETIONDATE    ,
        HOUSE_CNT         ,
        MOVEINSTARTDATE   ,
        MOVEINENDDATE     ,
        PENALTYRATE       ,
        HIC_TAG           ,
        TIRM_RATE         ,
        SODUK_RATE        ,
        JUMIN_RATE        ,
        DAMDANG           ,
        DAMDANG_TEL       ,
        PRINT_CNT         ,
        RESELL_TAG        ,
        VIRDEPOSIT_YN     ,
        VIRBANK_CODE      ,
        DEPT_ADDR_USE_YN  ,
        CONT_PBL_NAME     ,
        CONT_DW_NAME      ,
        REMARK            ,
        INPUT_DUTY_ID     ,
        INPUT_DATE        ,
        CHG_DUTY_ID       ,
        CHG_DATE          ,
        CONT_PBL_NAME2    ,
        CONT_DW_NAME2     ,
        CONT_PBL_NAME3    ,
        CONT_DW_NAME3     ,
        SLIPGROUP         ,
        TRI_TAG           ,
        APPLY_CHECK_YN    ,
        ANNE1A_PBL        ,
        ANNE1A_DW         ,
        ANNE1B_PBL        ,
        ANNE1B_DW         ,
        ANNE2A_PBL        ,
        ANNE2A_DW         ,
        ANNE2B_PBL        ,
        ANNE2B_DW         ,
        ANNE3_PBL         ,
        ANNE3_DW          ,
        ANNE4_PBL         ,
        ANNE4_DW          ,
        DELAYDAY          ,
        DELAYRATE         ,
        AGREE_TAG         ,
        INDEMINITY_TAG    ,
        INDEMINITY_FRDT   ,
        INDEMINITY_TODT   ,
        SEALTYPE          ,
        JIRO_TAG          ,
        JIRO_SN           ,
        HD_DAYMONTH_TAG   ,
        RT_DAYMONTH_TAG   ,
        RT_FIXRATE_TAG    ,
        PREDIS_TAG        ,
        PROXY_TAG         ,
        TRUST_TAG         ,
        PRINT_YN          ,
        RT_FIXRATE_DAY    ,
        RT_FIXRATE        ,
        VIRDEPOSIT2_YN    ,
        VIRBANK2_CODE     ,
        RT_FIXRATE2       ,
        RT_EXTRATE        ,
        RT_GURTYN         ,
        RT_RENTYN         ,
        ONCESALERATE      ,
        DELAY_BLOCK
   )
   VALUES
   (    SYSDATE                ,
        :OLD.DEPT_CODE         ,
        :OLD.HOUSETAG          ,
        :OLD.HOUSEDATE         ,
        :OLD.COMPLETIONDATE    ,
        :OLD.HOUSE_CNT         ,
        :OLD.MOVEINSTARTDATE   ,
        :OLD.MOVEINENDDATE     ,
        :OLD.PENALTYRATE       ,
        :OLD.HIC_TAG           ,
        :OLD.TIRM_RATE         ,
        :OLD.SODUK_RATE        ,
        :OLD.JUMIN_RATE        ,
        :OLD.DAMDANG           ,
        :OLD.DAMDANG_TEL       ,
        :OLD.PRINT_CNT         ,
        :OLD.RESELL_TAG        ,
        :OLD.VIRDEPOSIT_YN     ,
        :OLD.VIRBANK_CODE      ,
        :OLD.DEPT_ADDR_USE_YN  ,
        :OLD.CONT_PBL_NAME     ,
        :OLD.CONT_DW_NAME      ,
        :OLD.REMARK            ,
        :OLD.INPUT_DUTY_ID     ,
        :OLD.INPUT_DATE        ,
        :OLD.CHG_DUTY_ID       ,
        :OLD.CHG_DATE          ,
        :OLD.CONT_PBL_NAME2    ,
        :OLD.CONT_DW_NAME2     ,
        :OLD.CONT_PBL_NAME3    ,
        :OLD.CONT_DW_NAME3     ,
        :OLD.SLIPGROUP         ,
        'U'                    ,
        :OLD.APPLY_CHECK_YN    ,
        :OLD.ANNE1A_PBL        ,
        :OLD.ANNE1A_DW         ,
        :OLD.ANNE1B_PBL        ,
        :OLD.ANNE1B_DW         ,
        :OLD.ANNE2A_PBL        ,
        :OLD.ANNE2A_DW         ,
        :OLD.ANNE2B_PBL        ,
        :OLD.ANNE2B_DW         ,
        :OLD.ANNE3_PBL         ,
        :OLD.ANNE3_DW          ,
        :OLD.ANNE4_PBL         ,
        :OLD.ANNE4_DW          ,
        :OLD.DELAYDAY          ,
        :OLD.DELAYRATE         ,
        :OLD.AGREE_TAG         ,
        :OLD.INDEMINITY_TAG    ,
        :OLD.INDEMINITY_FRDT   ,
        :OLD.INDEMINITY_TODT   ,
        :OLD.SEALTYPE          ,
        :OLD.JIRO_TAG          ,
        :OLD.JIRO_SN           ,
        :OLD.HD_DAYMONTH_TAG   ,
        :OLD.RT_DAYMONTH_TAG   ,
        :OLD.RT_FIXRATE_TAG    ,
        :OLD.PREDIS_TAG        ,
        :OLD.PROXY_TAG         ,
        :OLD.TRUST_TAG         ,
        :OLD.PRINT_YN          ,
        :OLD.RT_FIXRATE_DAY    ,
        :OLD.RT_FIXRATE        ,
        :OLD.VIRDEPOSIT2_YN    ,
        :OLD.VIRBANK2_CODE     ,
        :OLD.RT_FIXRATE2       ,
        :OLD.RT_EXTRATE        ,
        :OLD.RT_GURTYN         ,
        :OLD.RT_RENTYN         ,
        :OLD.ONCESALERATE      ,
        :OLD.DELAY_BLOCK
   );     
end;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_INCOME;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_INCOME AFTER UPDATE ON KAIT.HD_HOUS_INCOME FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_INCOME_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND COUNTS    = :NEW.COUNTS
      AND TIMES     = :NEW.TIMES;

   INSERT INTO HD_HOUS_INCOME_LOG
             ( CUST_CODE,          SEQ,               COUNTS,              TIMES,
               WRITETAG,           WRITETIME,
               WRITESEQ,           DEPT_CODE,
               HOUSETAG,           BUILDNO,           HOUSENO,             DEPOSIT_NO,
               RECEIPTDATE,        RECEIPTAMT,        RECEIPTLANDAMT,      RECEIPTBUILDAMT,
               RECEIPTVATAMT,      DELAYDAYS,         DELAYAMT,            DISCNTDAYS,
               DISCNTAMT,          REALINCOMAMT,      REALLANDAMT,         REALBUILDAMT,
               REALVATAMT,         BANK_CODE,         BANK_NAME,           PAYTAG,
               INCOMTYPE,          MOD_YN,            REAL_PAY_TAG,        SLIPDT,
               SLIPSEQ,            TAXDATE,           TAXSEQ,              INSEQ,
               INPUT_DUTY_ID,      INPUT_DATE,        CHG_DUTY_ID,         CHG_DATE,
               SLIPTYPE,           VDEPOSIT_NO,       DETAILMOD_YN,        OUT_DT,
               OUT_TM,             OUT_SEQ,           OUT_BANK,            REMARK
            )
     VALUES ( :NEW.CUST_CODE,     :NEW.SEQ,          :NEW.COUNTS,         :NEW.TIMES,
              'U',                 TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,            :NEW.DEPT_CODE,
              :NEW.HOUSETAG,      :NEW.BUILDNO,      :NEW.HOUSENO,        :NEW.DEPOSIT_NO,
              :NEW.RECEIPTDATE,   :NEW.RECEIPTAMT,   :NEW.RECEIPTLANDAMT, :NEW.RECEIPTBUILDAMT,
              :NEW.RECEIPTVATAMT, :NEW.DELAYDAYS,    :NEW.DELAYAMT,       :NEW.DISCNTDAYS,
              :NEW.DISCNTAMT,     :NEW.REALINCOMAMT, :NEW.REALLANDAMT,    :NEW.REALBUILDAMT,
              :NEW.REALVATAMT,    :NEW.BANK_CODE,    :NEW.BANK_NAME,      :NEW.PAYTAG,
              :NEW.INCOMTYPE,     :NEW.MOD_YN,       :NEW.REAL_PAY_TAG,   :NEW.SLIPDT,
              :NEW.SLIPSEQ,       :NEW.TAXDATE,      :NEW.TAXSEQ,         :NEW.INSEQ,
              :NEW.INPUT_DUTY_ID, :NEW.INPUT_DATE,   :NEW.CHG_DUTY_ID,    :NEW.CHG_DATE,
              :NEW.SLIPTYPE,      :NEW.VDEPOSIT_NO,  :NEW.DETAILMOD_YN,   :NEW.OUT_DT,
              :NEW.OUT_TM,        :NEW.OUT_SEQ,      :NEW.OUT_BANK,       :NEW.REMARK
            );
END;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_RATE_DELAY;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_RATE_DELAY AFTER UPDATE ON KAIT.HD_HOUS_RATE_DELAY FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DELAY_LOG
    WHERE CUST_CODE  = :NEW.CUST_CODE
      AND SEQ        = :NEW.SEQ
      AND START_DAYS = :NEW.START_DAYS
      AND END_DAYS   = :NEW.END_DAYS
      AND STARTDATE  = :NEW.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DELAY_LOG
             ( CUST_CODE,       SEQ,              START_DAYS,      END_DAYS,
               STARTDATE,       WRITETAG,         WRITETIME,
               WRITESEQ,        ENDDATE,          DELAYRATE,       DELAYCUT,
               DELAYUNIT,       START_TAG,        END_TAG,         INPUT_DUTY_ID,
               INPUT_DATE,      CHG_DUTY_ID,      CHG_DATE )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,         :NEW.START_DAYS, :NEW.END_DAYS,
              :NEW.STARTDATE,  'U',               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.ENDDATE,     :NEW.DELAYRATE,  :NEW.DELAYCUT,
              :NEW.DELAYUNIT,  :NEW.START_TAG,   :NEW.END_TAG,    :NEW.INPUT_DUTY_ID,
              :NEW.INPUT_DATE, :NEW.CHG_DUTY_ID, :NEW.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_RATE_DISCOUNT;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_RATE_DISCOUNT AFTER UPDATE ON KAIT.HD_HOUS_RATE_DISCOUNT FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_RATE_DISCOUNT_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND STARTDATE = :NEW.STARTDATE;

   INSERT INTO HD_HOUS_RATE_DISCOUNT_LOG
             ( CUST_CODE,       SEQ,                STARTDATE,       WRITETAG,
               WRITETIME,
               WRITESEQ,        ENDDATE,            DISCNTRATE,      DISCNTCUT,
               DISCNTUNIT,      INPUT_DUTY_ID,      INPUT_DATE,      CHG_DUTY_ID,
               CHG_DATE )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,           :NEW.STARTDATE,  'U',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.ENDDATE,       :NEW.DISCNTRATE, :NEW.DISCNTCUT,
              :NEW.DISCNTUNIT, :NEW.INPUT_DUTY_ID, :NEW.INPUT_DATE, :NEW.CHG_DUTY_ID,
              :NEW.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_SELL;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_SELL AFTER UPDATE ON KAIT.HD_HOUS_SELL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELL_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ;

   INSERT INTO HD_HOUS_SELL_LOG
             ( CUST_CODE,           SEQ,                   WRITETAG,                WRITETIME,
               WRITESEQ,            DEPT_CODE,             HOUSETAG,                BUILDNO,
               HOUSENO,             DONGHO,                CUST_NAME,               SQUARE,
               TYPE,                CLASS,                 OPTIONCODE,              CONTRACTTAG,
               CONTRACTDATE,        CONTRACTNO,            LOAN_TAG,                LEASETAG,
               LASTCHANGEDATE,      CHANGETAG,             CHANGEDATE,              CANCEL_REASON,
               CHILD_BUILDNO,       CHILD_HOUSENO,         RELA_CUSTCODE,           RELA_SEQ,
               VATTAG,              EXCLUSIVEAREA,         COMMONAREA,              ETCCOMMONAREA,
               PARKINGAREA,         SERVICEAREA,           SITEAREA,                MOVEINSTARTDATE,
               MOVEINENDDATE,       UNION_CNT,             REMARK,                  REFUNDMENTDATE,
               REFUNDMENTAMT,       PENALTYAMT,            LOAN_INTEREST,           SODUK_TAX,
               JUMIN_TAX,           BANK_LOAN_ORGAMT,      BANK_LOAN_INTEREST,      LOANBANK,
               LOANDEPOSIT,         LOANUSER,              REFUND_DEPOSIT,          REFUND_BANK,
               COMP_LOANAMT,        BILL_RETURNAMT,        DELAY_INDEMINITY,        DEPOSIT_COUNT,
               CO_CUSTCODE,         CO_SANGHO,             CO_CONDITION,            CO_CATEGORY,
               CATEGORY_NAME,       SLIPDATE,              SLIPSEQ,                 INPUT_DUTY_ID,
               INPUT_DATE,          CHG_DUTY_ID,           CHG_DATE,                APPLY_YN,
               APPLY_EMPNO,         APPLY_DATE,            PRTSQUARE,               BANK_LOAN_INTEREST2,
               ETC_AMT,             RENTHD_YN,             RENTHD_SEQ,              BALCONY_TAG,
               BALCONYAREA,         DAYMONTH_TAG,          FLOOR,                   CONT_CONDITION,
               LAND_RETURN,         INT_CALC_DATE,         PREDISAMT,               PROXYAMT,
               INCONT_DATE,         TRUSTAMT,              PREDIS_TAG,              PROXY_TAG,
               TRUST_TAG,           VIR_YN,                VDEPOSIT,
               REP_LIMITDT,         REP_YN,                REP_DATE
             )
      VALUES (:NEW.CUST_CODE,      :NEW.SEQ,              'U',                      TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,             :NEW.DEPT_CODE,        :NEW.HOUSETAG,           :NEW.BUILDNO,
              :NEW.HOUSENO,        :NEW.DONGHO,           :NEW.CUST_NAME,          :NEW.SQUARE,
              :NEW.TYPE,           :NEW.CLASS,            :NEW.OPTIONCODE,         :NEW.CONTRACTTAG,
              :NEW.CONTRACTDATE,   :NEW.CONTRACTNO,       :NEW.LOAN_TAG,           :NEW.LEASETAG,
              :NEW.LASTCHANGEDATE, :NEW.CHANGETAG,        :NEW.CHANGEDATE,         :NEW.CANCEL_REASON,
              :NEW.CHILD_BUILDNO,  :NEW.CHILD_HOUSENO,    :NEW.RELA_CUSTCODE,      :NEW.RELA_SEQ,
              :NEW.VATTAG,         :NEW.EXCLUSIVEAREA,    :NEW.COMMONAREA,         :NEW.ETCCOMMONAREA,
              :NEW.PARKINGAREA,    :NEW.SERVICEAREA,      :NEW.SITEAREA,           :NEW.MOVEINSTARTDATE,
              :NEW.MOVEINENDDATE,  :NEW.UNION_CNT,        :NEW.REMARK,             :NEW.REFUNDMENTDATE,
              :NEW.REFUNDMENTAMT,  :NEW.PENALTYAMT,       :NEW.LOAN_INTEREST,      :NEW.SODUK_TAX,
              :NEW.JUMIN_TAX,      :NEW.BANK_LOAN_ORGAMT, :NEW.BANK_LOAN_INTEREST, :NEW.LOANBANK,
              :NEW.LOANDEPOSIT,    :NEW.LOANUSER,         :NEW.REFUND_DEPOSIT,     :NEW.REFUND_BANK,
              :NEW.COMP_LOANAMT,   :NEW.BILL_RETURNAMT,   :NEW.DELAY_INDEMINITY,   :NEW.DEPOSIT_COUNT,
              :NEW.CO_CUSTCODE,    :NEW.CO_SANGHO,        :NEW.CO_CONDITION,       :NEW.CO_CATEGORY,
              :NEW.CATEGORY_NAME,  :NEW.SLIPDATE,         :NEW.SLIPSEQ,            :NEW.INPUT_DUTY_ID,
              :NEW.INPUT_DATE,     :NEW.CHG_DUTY_ID,      :NEW.CHG_DATE,           :NEW.APPLY_YN,
              :NEW.APPLY_EMPNO,    :NEW.APPLY_DATE,       :NEW.PRTSQUARE,          :NEW.BANK_LOAN_INTEREST2,
              :NEW.ETC_AMT,        :NEW.RENTHD_YN,        :NEW.RENTHD_SEQ,         :NEW.BALCONY_TAG,
              :NEW.BALCONYAREA,    :NEW.DAYMONTH_TAG,     :NEW.FLOOR,              :NEW.CONT_CONDITION,
              :NEW.LAND_RETURN,    :NEW.INT_CALC_DATE,    :NEW.PREDISAMT,          :NEW.PROXYAMT,
              :NEW.INCONT_DATE,    :NEW.TRUSTAMT,         :NEW.PREDIS_TAG,         :NEW.PROXY_TAG,
              :NEW.TRUST_TAG,      :NEW.VIR_YN,           :NEW.VDEPOSIT,
              :NEW.REP_LIMITDT,    :NEW.REP_YN,           :NEW.REP_DATE
             );

  IF  NVL(:OLD.APPLY_YN, 'N') <> NVL(:NEW.APPLY_YN, 'N') THEN BEGIN
      SELECT NVL(MAX(SN),0) + 1
        INTO NEWSEQ
        FROM HD_HOUS_APPLY
       WHERE CUST_CODE = :NEW.CUST_CODE
         AND SEQ       = :NEW.SEQ;
      INSERT INTO HD_HOUS_APPLY
                ( CUST_CODE,      SEQ,              SN,
                  DEPT_CODE,      HOUSETAG,         BUILDNO,        HOUSENO,
                  APPLY_YN,       APPLY_EMPNO,      APPLY_DATE )
         VALUES (:NEW.CUST_CODE, :NEW.SEQ,          NEWSEQ,
                 :NEW.DEPT_CODE, :NEW.HOUSETAG,    :NEW.BUILDNO,   :NEW.HOUSENO,
                 :NEW.APPLY_YN,  :NEW.APPLY_EMPNO, :NEW.APPLY_DATE);

   END; END IF;
END;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_SELLDETAIL;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_SELLDETAIL AFTER UPDATE ON KAIT.HD_HOUS_SELLDETAIL FOR EACH ROW
DECLARE NEWSEQ INTEGER;

BEGIN
   SELECT NVL(MAX(WRITESEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SELLDETAIL_LOG
    WHERE CUST_CODE = :NEW.CUST_CODE
      AND SEQ       = :NEW.SEQ
      AND COUNTS    = :NEW.COUNTS;

   INSERT INTO HD_HOUS_SELLDETAIL_LOG
             ( CUST_CODE,       SEQ,                COUNTS,               WRITETAG,
               WRITETIME,
               WRITESEQ,        DEPT_CODE,          HOUSETAG,             BUILDNO,
               HOUSENO,         AGREEDATE,          LANDAMT,              BUILDAMT,
               VATAMT,          BUNAMT,             DC_YN,                AC_YN,
               PERPECTTAG,      RECEIPTAMT,         DISTRIBUTE_RATE,      SLIPDT,
               SLIPSEQ,         INPUT_DUTY_ID,      INPUT_DATE,           CHG_DUTY_ID,
               CHG_DATE )
      VALUES (:NEW.CUST_CODE,  :NEW.SEQ,           :NEW.COUNTS,          'U',
               TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
               NEWSEQ,         :NEW.DEPT_CODE,     :NEW.HOUSETAG,        :NEW.BUILDNO,
              :NEW.HOUSENO,    :NEW.AGREEDATE,     :NEW.LANDAMT,         :NEW.BUILDAMT,
              :NEW.VATAMT,     :NEW.BUNAMT,        :NEW.DC_YN,           :NEW.AC_YN,
              :NEW.PERPECTTAG, :NEW.RECEIPTAMT,    :NEW.DISTRIBUTE_RATE, :NEW.SLIPDT,
              :NEW.SLIPSEQ,    :NEW.INPUT_DUTY_ID, :NEW.INPUT_DATE,      :NEW.CHG_DUTY_ID,
              :NEW.CHG_DATE);
END;
/


DROP TRIGGER KAIT.TUA_HD_HOUS_SUPPLY;

CREATE OR REPLACE TRIGGER KAIT.TUA_HD_HOUS_SUPPLY
  AFTER UPDATE
  on HD_HOUS_SUPPLY
  
  for each row
/* ERwin Builtin Fri Sep 01 12:16:11 2006 */
/* default body for TUA_HD_HOUS_SUPPLY */
DECLARE NEWSEQ INTEGER;

begin
   SELECT NVL(MAX(SEQ),0) + 1
     INTO NEWSEQ
     FROM HD_HOUS_SUPPLY_HIST
    WHERE DEPT_CODE = :OLD.DEPT_CODE
      AND HOUSETAG  = :OLD.HOUSETAG
      AND BUILDNO   = :OLD.BUILDNO
      AND HOUSENO   = :OLD.HOUSENO;

   INSERT INTO HD_HOUS_SUPPLY_HIST
   (    DEPT_CODE      ,
        HOUSETAG       ,
        BUILDNO        ,
        HOUSENO        ,
        HIST_DATE      ,
        SEQ            ,
        SQUARE         ,
        TYPE           ,
        CLASS          ,
        OPTIONCODE     ,
        VATTAG         ,
        EXCLUSIVEAREA  ,
        COMMONAREA     ,
        ETCCOMMONAREA  ,
        PARKINGAREA    ,
        SERVICEAREA    ,
        SITEAREA       ,
        FLOOR          ,
        GUBUN          ,
        CATEGORY_NAME  ,
        CONTRACTYESNO  ,
        VIRDEPOSIT     ,
        BANK_CODE      ,
        BANK_NAME      ,
        USE_YN         ,
        RENT_TAG       ,
        INPUT_DUTY_ID  ,
        INPUT_DATE     ,
        CHG_DUTY_ID    ,
        CHG_DATE       ,
        PRTSQUARE      ,
        PREDISAMT      ,
        TRI_TAG        ,
        VIRDEPOSIT2
   )
   VALUES
   (    :OLD.DEPT_CODE      ,
        :OLD.HOUSETAG       ,
        :OLD.BUILDNO        ,
        :OLD.HOUSENO        ,
        SYSDATE             ,
        NEWSEQ              ,
        :OLD.SQUARE         ,
        :OLD.TYPE           ,
        :OLD.CLASS          ,
        :OLD.OPTIONCODE     ,
        :OLD.VATTAG         ,
        :OLD.EXCLUSIVEAREA  ,
        :OLD.COMMONAREA     ,
        :OLD.ETCCOMMONAREA  ,
        :OLD.PARKINGAREA    ,
        :OLD.SERVICEAREA    ,
        :OLD.SITEAREA       ,
        :OLD.FLOOR          ,
        :OLD.GUBUN          ,
        :OLD.CATEGORY_NAME  ,
        :OLD.CONTRACTYESNO  ,
        :OLD.VIRDEPOSIT     ,
        :OLD.BANK_CODE      ,
        :OLD.BANK_NAME      ,
        :OLD.USE_YN         ,
        :OLD.RENT_TAG       ,
        :OLD.INPUT_DUTY_ID  ,
        :OLD.INPUT_DATE     ,
        :OLD.CHG_DUTY_ID    ,
        :OLD.CHG_DATE       ,
        :OLD.PRTSQUARE      ,
        :OLD.PREDISAMT      ,
        'U'                 ,
        :OLD.VIRDEPOSIT2
   );
end;
/

