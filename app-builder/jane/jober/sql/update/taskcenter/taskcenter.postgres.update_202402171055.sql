CREATE TABLE client_login
(
    "id"       char(32) NOT NULL,
    client_id char(32) NOT NULL,
    "cookie"   text,
    PRIMARY KEY ("id")
)
;

CREATE INDEX "idx_client_id_cookie" ON client_login (client_id, cookie);

CREATE UNIQUE INDEX "idx_client_id" ON client_login (client_id);

COMMENT ON COLUMN "client_login"."id" IS '主键';

COMMENT ON COLUMN "client_login".client_id IS 'idea插件客户端id';

COMMENT ON COLUMN "client_login"."cookie" IS '登录cookie';
