--创建函数增加宽表列51-100
CREATE OR REPLACE FUNCTION "public"."wide_add_text_column"()
  RETURNS "pg_catalog"."void" AS $BODY$
DECLARE
i INT := 51;
BEGIN
		WHILE i <= 100 LOOP
		  EXECUTE format ( 'ALTER TABLE task_instance_wide ADD COLUMN text_%s TEXT', i );
          EXECUTE format ( 'ALTER TABLE task_instance_deleted ADD COLUMN text_%s TEXT', i );
			i := i+1;
END LOOP;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
  COST 100

--调用函数
SELECT wide_add_text_column();