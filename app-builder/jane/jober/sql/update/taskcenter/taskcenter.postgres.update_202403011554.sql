ALTER TABLE task_template
    ADD COLUMN "tenant_id" char(32) NOT NULL DEFAULT '00000000000000000000000000000000';

COMMENT ON COLUMN "task_template"."tenant_id" IS '租户Id';

UPDATE task_template set tenant_id = (SELECT id from "tenant" where name = 'public') where name = '普通任务'