--模板继承表
CREATE TABLE IF NOT EXISTS extend_table
(
    id    VARCHAR(32) PRIMARY KEY COMMENT '主键id',
    parent_id  VARCHAR(32) NOT NULL COMMENT '父任务id'
) COMMENT '模板继承表';

--根据子任务查询所有父任务的id
CREATE OR REPLACE FUNCTION find_template_parents(input_id varchar)
  RETURNS TABLE(template_parents_id varchar) AS $BODY$
BEGIN
RETURN QUERY WITH RECURSIVE dump_parents(id, parent_id) AS (
		SELECT id, parent_id FROM extend_table WHERE id = input_id
		UNION ALL
		SELECT p.id, p.parent_id FROM dump_parents AS c, extend_table AS p WHERE c.parent_id = p.id
		)
SELECT input_id
UNION ALL
SELECT parent_id FROM dump_parents WHERE parent_id IS NOT NULL
                                     AND parent_id != '00000000000000000000000000000000';
RETURN;
END;
  $BODY$
LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;

--根据父任务查询所有子任务的id
CREATE OR REPLACE FUNCTION find_template_children(input_id varchar)
  RETURNS TABLE(children_template_id varchar) AS $BODY$
BEGIN
RETURN QUERY WITH RECURSIVE dump_children(id) AS (
          SELECT id FROM extend_table WHERE parent_id = input_id
          UNION ALL
          SELECT c.id FROM dump_children AS p, extend_table AS c WHERE c.parent_id = p.id
            )
SELECT input_id
UNION ALL
SELECT id FROM dump_children;
RETURN;
END;
  $BODY$
LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;