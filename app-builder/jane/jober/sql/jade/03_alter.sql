-- 应用多版本管理.
-- ## app_suite_id
ALTER TABLE app_builder_app ADD app_suite_id VARCHAR(32) NULL;
UPDATE app_builder_app SET app_suite_id = task.template_id from task where task.attributes->>'app_id' = app_builder_app.id;
-- ## is_active
ALTER TABLE app_builder_app
    ADD is_active bool NULL DEFAULT false;
UPDATE app_builder_app
SET is_active =
        CASE
            WHEN state = 'active' THEN
                true
            ELSE
                false
            END
WHERE 1 = 1;
-- ## status
ALTER TABLE app_builder_app
    ADD status varchar(16) NULL;
UPDATE app_builder_app
SET status =
        CASE
            WHEN state = 'active' THEN
                'published'
            ELSE
                'draft'
            END
WHERE 1 = 1;
-- ## uniqueName
ALTER TABLE app_builder_app
    ADD unique_name varchar(64) NULL;
UPDATE app_builder_app
SET unique_name = task.attributes->>'store_id'
FROM task
WHERE app_builder_app.app_suite_id = task.template_id and app_builder_app.id = task.attributes->>'app_id';
-- ## publishAt
ALTER TABLE app_builder_app
    ADD publish_at timestamp(6) NULL;
UPDATE app_builder_app
SET publish_at = to_timestamp(task.attributes->>'publish_at', 'YYYY-MM-DD"T"HH24:MI:SS')
    FROM task
WHERE app_builder_app.app_suite_id = task.template_id and app_builder_app.id = task.attributes->>'app_id';
-- ## publish_description,publish_update_log
UPDATE app_builder_app
SET attributes = jsonb_set(app_builder_app.attributes::JSONB, '{publish_description}', coalesce(to_jsonb(task.attributes->>'publish_description'), '""'))
    FROM task
WHERE app_builder_app.app_suite_id = task.template_id and app_builder_app.id = task.attributes->>'app_id';

UPDATE app_builder_app
SET attributes = jsonb_set(app_builder_app.attributes::JSONB, '{publish_update_log}', coalesce(to_jsonb(task.attributes->>'publish_update_log'), '""'))
    FROM task
WHERE app_builder_app.app_suite_id = task.template_id and app_builder_app.id = task.attributes->>'app_id';
-- ## app_id
ALTER TABLE app_builder_app
    ADD app_id varchar(64) NULL;
UPDATE app_builder_app
SET app_id = id
WHERE 1 = 1;