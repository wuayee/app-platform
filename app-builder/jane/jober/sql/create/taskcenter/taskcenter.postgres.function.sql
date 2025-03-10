-- 移动property_old表的数据到新的property
-- 移动instance_old表的数据到新的instance
CREATE OR REPLACE FUNCTION move_old_data_to_new_table(p_task_id CHAR(32)) RETURNS void AS
$BODY$
DECLARE
d_cur_properties CURSOR (c_task_id CHAR(32)) FOR SELECT * FROM task_property_old WHERE task_id = c_task_id;
d_record_property   RECORD;
    d_record_template   RECORD;
    d_sql_insert_into   VARCHAR := '(';
    d_sql_values        VARCHAR := 'SELECT ';
    d_index             INTEGER := 0;
    d_count             INTEGER ;
    d_TEXT_SEQUENCE     INTEGER := 13;
    d_DATETIME_SEQUENCE INTEGER := 4;
    d_other_SEQUWNCE    INTEGER := 1;
    d_data_type VARCHAR;
    d_sequence INTEGER;
    d_template_id CHAR(32);
BEGIN
SELECT COUNT(task_id) INTO d_count FROM task_property_old WHERE task_id = p_task_id;
RAISE NOTICE 'task_id=%, has % properties.',p_task_id, d_count;
OPEN d_cur_properties(p_task_id);
WHILE d_index < d_count LOOP
            FETCH NEXT FROM d_cur_properties INTO d_record_property;
            d_sql_values := d_sql_values || d_record_property.data_type || '_' || d_record_property.sequence || ',';
SELECT * INTO d_record_template from task_template_property where name = d_record_property.name;
if d_record_template is not null THEN
                d_data_type := d_record_template.data_type;
                d_sequence := d_record_template.sequence;
                d_template_id := d_record_template.id;
ELSE
                d_data_type := d_record_property.data_type;
                d_template_id := '00000000000000000000000000000000';
                if d_record_property.data_type = 'TEXT' THEN
                    d_sequence := d_TEXT_SEQUENCE;
                    d_TEXT_SEQUENCE := d_TEXT_SEQUENCE + 1;
                ELSIF d_record_property.data_type = 'DATETIME' THEN
                    d_sequence := d_DATETIME_SEQUENCE;
                    d_DATETIME_SEQUENCE := d_DATETIME_SEQUENCE + 1;
ELSE
                    d_sequence := d_other_SEQUWNCE;
                    d_other_SEQUWNCE := d_other_SEQUWNCE + 1;
end if;
end if;
            RAISE NOTICE 'EXECUTE sql move property to new table, id=%. [%/%]', d_record_property.id, d_index+1, d_count;
INSERT INTO task_property(id, task_id, name, required, description, scope, data_type, sequence, appearance, identifiable, template_id)
VALUES (d_record_property.id, d_record_property.task_id, d_record_property.name, d_record_property.required,
        d_record_property.description, d_record_property.scope, d_data_type, d_sequence,
        d_record_property.appearance, d_record_property.identifiable, d_template_id);
d_sql_insert_into := d_sql_insert_into || d_data_type || '_' || d_sequence || ',';
            d_index := d_index+1;
end loop;
CLOSE d_cur_properties;
d_sql_insert_into := d_sql_insert_into || 'id, task_id, task_type_id, source_id)';
    d_sql_values := d_sql_values ||'id, task_id, task_type_id, source_id ';

    RAISE NOTICE 'EXECUTE sql move instance to new table : % %.', d_sql_insert_into, d_sql_values;
EXECUTE ('INSERT INTO task_instance_wide' || d_sql_insert_into || d_sql_values || 'from task_instance_wide_old where task_id = ''' ||
         p_task_id || '''');
RAISE NOTICE 'EXECUTE sql move instance_delete to new table : % %.', d_sql_insert_into, d_sql_values;
EXECUTE ('INSERT INTO task_instance_deleted' || d_sql_insert_into || d_sql_values || 'from task_instance_deleted_old where task_id = ''' ||
         p_task_id || '''');
end;

$BODY$ LANGUAGE plpgsql;



-- 修改原有instance表和property表，并创建新表
CREATE OR REPLACE FUNCTION back_table_and_create_new() RETURNS void as
$$
DECLARE
alter_table_instance  VARCHAR := 'ALTER TABLE task_instance_wide rename to task_instance_wide_old';
    alter_table_instance_delete  VARCHAR := 'ALTER TABLE task_instance_deleted rename to task_instance_deleted_old';
    alter_table_property  VARCHAR := 'ALTER TABLE task_property rename to task_property_old';
    create_table_instance VARCHAR := 'CREATE TABLE task_instance_wide (LIKE task_instance_wide_old INCLUDING INDEXES INCLUDING COMMENTS);';
    create_table_property VARCHAR := 'CREATE TABLE task_property (LIKE task_property_old INCLUDING INDEXES INCLUDING COMMENTS);';
    create_table_instance_delete VARCHAR := 'CREATE TABLE task_instance_deleted (LIKE task_instance_deleted_old INCLUDING INDEXES INCLUDING COMMENTS);';

BEGIN
    RAISE NOTICE 'Execute alter table task_instance_wide: "%".', alter_table_instance;
EXECUTE (alter_table_instance);
RAISE NOTICE 'Execute alter table task_property: "%".', alter_table_property;
EXECUTE (alter_table_property);
RAISE NOTICE 'Execute alter table task_instance_deleted: "%".', alter_table_instance_delete;
EXECUTE (alter_table_instance_delete);
RAISE NOTICE 'Execute create table task_instance_wide: "%".', create_table_instance;
EXECUTE (create_table_instance);
RAISE NOTICE 'Execute create table task_property: "%".', create_table_property;
EXECUTE (create_table_property);
RAISE NOTICE 'Execute create table task_instance_deleted: "%".', create_table_instance_delete;
EXECUTE (create_table_instance_delete);
end;

$$ LANGUAGE plpgsql;

-- 修改task表的template为默认模板
CREATE OR REPLACE FUNCTION modify_task_add_template() RETURNS void as
$$
DECLARE
d_cur_task_ids CURSOR FOR SELECT id
                          FROM task;
d_task_id     CHAR(32);
    d_task_count  INTEGER;
    d_task_index  INTEGER := 0;
    d_template_id CHAR(32);
BEGIN
    PERFORM back_table_and_create_new();
SELECT COUNT(id) into d_task_count FROM task;
SELECT id into d_template_id from task_template where name = '普通任务';
OPEN d_cur_task_ids;
WHILE d_task_index < d_task_count LOOP
            FETCH NEXT FROM d_cur_task_ids INTO d_task_id;
UPDATE task set template_id = d_template_id where CURRENT OF d_cur_task_ids;
d_task_index := d_task_index + 1;
            RAISE NOTICE 'Add template to task "%". [%/%]', d_task_id, d_task_index, d_task_count;
            PERFORM move_old_data_to_new_table(d_task_id);
END LOOP;
END;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION rollback_template_update() RETURNS void as
$$
DECLARE
alter_table_instance         VARCHAR := 'ALTER TABLE task_instance_wide rename to task_instance_wide_error';
    alter_table_instance_delete  VARCHAR := 'ALTER TABLE task_instance_deleted rename to task_instance_deleted_error';
    alter_table_property         VARCHAR := 'ALTER TABLE task_property rename to task_property_error';
    alter_table_instance_r         VARCHAR := 'ALTER TABLE task_instance_wide_old rename to task_instance_wide';
    alter_table_instance_delete_r  VARCHAR := 'ALTER TABLE task_instance_deleted_old rename to task_instance_deleted';
    alter_table_property_r         VARCHAR := 'ALTER TABLE task_property_old rename to task_property';
BEGIN
    RAISE NOTICE 'Execute rollback sql.';
DROP TABLE IF EXISTS task_instance_wide_error;
DROP TABLE IF EXISTS task_instance_deleted_error;
DROP TABLE IF EXISTS task_property_error;
EXECUTE (alter_table_instance);
EXECUTE (alter_table_instance_delete);
EXECUTE (alter_table_property);
EXECUTE (alter_table_instance_r);
EXECUTE (alter_table_instance_delete_r);
EXECUTE (alter_table_property_r);
UPDATE task set template_id = '00000000000000000000000000000000';
end;
$$ LANGUAGE plpgsql;

select modify_task_add_template();