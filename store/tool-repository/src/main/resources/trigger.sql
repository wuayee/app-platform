-- 创建触发器函数
create or replace function update_when_update()
returns trigger as $$
begin
	new.updated_time = current_timestamp;
    return new;
end;
$$ language plpgsql;

-- 为 store_tool 表创建触发器
create trigger trigger_update_tool
    before update ON store_tool
    for each row
execute function update_when_update();

-- 为 store_tag 表创建触发器
create trigger trigger_update_tag
    before update ON store_tag
    for each row
execute function update_when_update();
     