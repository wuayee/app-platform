CREATE TABLE IF NOT EXISTS flow_graph
(
    id               VARCHAR(32) NOT NULL,
    version          VARCHAR(16) NOT NULL,
    tenant           VARCHAR(32) NOT NULL,
    status           VARCHAR(32) NOT NULL,
    name             VARCHAR(256),
    data             TEXT,
    created_by       VARCHAR(32) NOT NULL,
    created_at       timestamp without time zone NOT NULL,
    updated_by       VARCHAR(32) NOT NULL,
    updated_at       timestamp without time zone NOT NULL,
    previous         VARCHAR(50),
    is_deleted       BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id, version)
);