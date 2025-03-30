delete
from store_task;

insert into store_task ("task_name", "context", "tool_unique_name")
values ('automatic-speech-recognition', '{"defaultModel": "openai/whisper-large-v3"}', 'name1'),
       ('depth-estimation', '{"defaultModel": "Intel/dpt-large"}', 'name2');
