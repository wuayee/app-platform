CREATE
OR REPLACE FUNCTION generate_uuid_text()
RETURNS CHAR(32) AS $$
BEGIN
RETURN md5(random()::text || clock_timestamp()::text);
END;
$$
LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION now_utc()
RETURNS TIMESTAMP AS $$
BEGIN
RETURN CURRENT_TIMESTAMP AT TIME ZONE 'UTC';
END;
$$
LANGUAGE plpgsql;

INSERT INTO category_group(id, name, description, created_by, created_at, updated_by, updated_at)
VALUES (generate_uuid_text(), 'status', '表示系统级别的状态定义。', 'admin', now_utc(), 'admin', now_utc()),
       (generate_uuid_text(), 'health', '表示系统级别的健康度定义。', 'admin', now_utc(), 'admin', now_utc());

INSERT INTO category(id, category_group_id, name, description, created_by, created_at, updated_by,
                     updated_at)
VALUES (generate_uuid_text(), (SELECT id FROM category_group WHERE name = 'status'), '未开始',
        '表示任务尚未开始处理。', 'admin', now_utc(), 'admin', now_utc()),
       (generate_uuid_text(), (SELECT id FROM category_group WHERE name = 'status'), '处理中',
        '表示任务正在处理中。', 'admin', now_utc(), 'admin', now_utc()),
       (generate_uuid_text(), (SELECT id FROM category_group WHERE name = 'status'), '已完成',
        '表示任务已闭环完成。', 'admin', now_utc(), 'admin', now_utc()),
       (generate_uuid_text(), (SELECT id FROM category_group WHERE name = 'health'), '无风险',
        '表示当前任务的进展不存在风险。', 'admin', now_utc(), 'admin', now_utc()),
       (generate_uuid_text(), (SELECT id FROM category_group WHERE name = 'health'), '风险',
        '表示当前任务存在风险。', 'admin', now_utc(), 'admin', now_utc());

UPDATE task_type
SET task_id = '2479308f35ab4cc492892aea265b2025'
WHERE tree_id = '6a5db7109bb546109f2887347cf42054'
UPDATE task_type
SET task_id = '265520c71c534393af4678129ac90719'
WHERE tree_id = '2abeca128a194064a2777f1882ea2967'

UPDATE task_type
SET parent_id = tree_id
WHERE parent_id = '00000000000000000000000000000000';

CREATE TABLE IF NOT EXISTS task_source_refresh_in_time (
    id CHAR(32) PRIMARY KEY COMMENT '主键id',
    create_fitable_id CHAR(32) NOT NULL COMMENT 'create_fitable_id',
    patch_fitable_id CHAR(32) NOT NULL COMMENT 'patch_fitable_id',
    delete_fitable_id CHAR(32) NOT NULL COMMENT 'delete_fitable_id',
    retrieve_fitable_id CHAR(32) NOT NULL COMMENT 'retrieve_fitable_id',
    list_fitable_id CHAR(32) NOT NULL COMMENT 'list_fitable_id'
) COMMENT '任务源刷新表';

CREATE TABLE `authorization` (
    `id` CHAR(32) PRIMARY KEY COMMENT '主键id',
    `system` VARCHAR(64) NOT NULL COMMENT '系统',
    `user` VARCHAR(127) NOT NULL COMMENT '用户',
    `token` TEXT NOT NULL COMMENT 'token',
    `expiration` INTEGER NOT NULL COMMENT '过期时间',
    `created_by` VARCHAR(127) NOT NULL COMMENT '创建人',
    `created_at` TIMESTAMP NOT NULL COMMENT '创建时间',
    `updated_by` VARCHAR(127) NOT NULL COMMENT '更新人',
    `updated_at` TIMESTAMP NOT NULL COMMENT '更新时间'
) COMMENT '权限表';