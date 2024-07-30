create table if not exists t_app_engine_worker_generator
(
    "worker_id"  serial primary key                    not null,
    "created_at" timestamp   default current_timestamp not null,
    "updated_at" timestamp   default current_timestamp not null,
    "created_by" varchar(10) default 'system'          not null,
    "updated_by" varchar(10) default 'system'          not null
);