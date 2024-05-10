create table if not exists form_data(
    form_id varchar(64) not null,
    form_version varchar(32) not null,
    form_name varchar(256) not null,
    tenant_id varchar(64),
    update_time timestamp,
    update_user varchar(64),
    create_time timestamp not null,
    create_user varchar(64),
    constraint form_pkey primary key (form_id, form_version)
);


create or replace function update_time() returns trigger as $$
begin
    NEW.update_time = now();
    return NEW;
end;
$$ language plpgsql;

create trigger form_data_updater before UPDATE on form_data for each row execute function update_time();
