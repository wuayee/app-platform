CREATE TABLE IF NOT EXISTS jane_relation (
    id CHAR(32) PRIMARY KEY COMMENT '主键id',
    object_id1 CHAR(32) NOT NULL COMMENT 'object的id1',
    object_type1 VARCHAR(16) NOT NULL COMMENT 'obje的type1',
    object_id2 CHAR(32) NOT NULL COMMENT 'object的id2',
    object_type2 VARCHAR(16) NOT NULL COMMENT 'object的type2',
    relation_type VARCHAR(16) NOT NULL COMMENT '关系类型',
    created_by VARCHAR(127) NOT NULL COMMENT '创建人',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间'
    ) COMMENT '任务关系表';
CREATE INDEX IF NOT EXISTS IDX_JANE_RELATION_OBJECT_ID1 ON jane_relation(object_id1);
CREATE INDEX IF NOT EXISTS IDX_JANE_RELATION_OBJECT_ID2 ON jane_relation(object_id2);
CREATE UNIQUE INDEX IF NOT EXISTS UK_IDX_JANE_RELATION_OBJECT_ID1_OBJECT_ID2 ON jane_relation(object_id1,object_id2);