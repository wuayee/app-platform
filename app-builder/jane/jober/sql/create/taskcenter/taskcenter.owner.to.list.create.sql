--将wide表中的text_4(owner)转为列表，序列
CREATE SEQUENCE "list_index" START 1 INCREMENT 1;
INSERT INTO list_text(id,instance_id,property_id,index,value)
SELECT generate_uuid_text() AS id, w.id AS instance_id,
       (SELECT id FROM task_property WHERE template_id IN (SELECT id FROM task_template_property WHERE task_template_id = '4f91b69973d1453aadf384d508aed894' AND name = 'owner') AND task_id=w.task_id) AS property_id,
       CASE
           WHEN row_number() OVER (PARTITION BY w.id ORDER BY w.id,s.index)=1
	THEN
               SETVAL('list_index', 1)
           ELSE
               NEXTVAL('list_index')
           END AS index, s.value
from task_instance_wide w,
     regexp_split_to_table(w.text_4, ',') WITH ORDINALITY AS s(value, index) where w.id in (SELECT s.id from task_instance_wide s INNER JOIN task t ON s.task_id=t.id WHERE t.template_id IN (SELECT find_template_children('4f91b69973d1453aadf384d508aed894') AS id));
DROP SEQUENCE "list_index";

--验证数量是否正确
SELECT DISTINCT ON (instance_id) id  FROM list_text;
SELECT count(1) FROM task_instance_wide;

--将普通任务模板中的处理人和审批任务中的审批人属性的值.将属性的数据类型修改为LIST_TEXT，sequence修改为0
--修改task_template_property和task_property表中owner对应的类型

UPDATE task_template_property SET data_type='LIST_TEXT',sequence=0 WHERE name='owner';
UPDATE task_property SET data_type='LIST_TEXT',sequence=0 WHERE template_id = (SELECT id FROM task_template_property WHERE name='owner');

--将List_text中的值除index，添加到index_text中
INSERT INTO index_text(id,instance_id,property_id,value)
SELECT generate_uuid_text(),instance_id,property_id,value
FROM list_text;


--回退：清空list_text表中所有数据
TRUNCATE TABLE list_text;
--回退task_template_property和task_property表
UPDATE task_template_property SET data_type='TEXT',sequence=4 WHERE name='owner';
UPDATE task_property SET data_type='TEXT',sequence=4 WHERE template_id = (SELECT id FROM task_template_property WHERE name='owner');
--回退index_text表
DELETE FROM index_text WHERE instance_id IN (SELECT instance_id FROM list_text);