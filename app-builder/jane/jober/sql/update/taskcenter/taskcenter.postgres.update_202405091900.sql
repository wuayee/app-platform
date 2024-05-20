DROP INDEX task_template_name_idx;

CREATE UNIQUE INDEX task_template_name_tenant_idx ON task_template ("name","tenant_id");