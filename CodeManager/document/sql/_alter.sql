SELECT A.*, 'ALTER TABLE '||A.TABLE_NAME||' MODIFY('||A.COLUMN_NAME||' '||A.DATA_TYPE||'('||
    CASE
    WHEN ROUND(A.DATA_LENGTH + (A.DATA_LENGTH/2)) >= 4000 THEN 4000
    ELSE ROUND(A.DATA_LENGTH + (A.DATA_LENGTH/2))
    END
||'));' MODIFY_SQL FROM (
#{select}
) A
WHERE A.CNT > 0