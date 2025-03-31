INSERT INTO "public"."store_tool" ("name", "schema", "runnables", "unique_name", "group_name", "definition_name", "definition_group_name")
VALUES ('AI简历解析插件',
        '{"name":"AI简历解析插件","description":"解析简历内容，填充提示词模板，输出大模型提示词","parameters":{"type":"object","properties":{"fileUrl":{"type":"string","description":"简历文件URL"},"instanceId":{"type":"string","description":"实例ID"}},"required":["fileUrl","instanceId"]},"order":["fileUrl","instanceId"],"return":{"description":"Map结构包含提示词，插件处理过文件的标志位以及错误信息","type":"object","properties":{"isFileHandled":{"type":"boolean"},"cvAnalyzerPrompt":{"type":"string"},"errorMessage":{"type":"string"}},"converter":""}}',
        '{"FIT":{"fitableId":"cv.analyzer","genericableId":"modelengine.fit.jober.aipp.tool.cv.analyzer"}}',
        '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8',
        '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8', '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8',
        '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8') ON CONFLICT ("unique_name", "version") DO NOTHING;
INSERT INTO "public"."store_definition" ("name", "schema", "definition_group_name")
VALUES ('8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8',
        '{"name":"AI简历解析插件","description":"解析简历内容，填充提示词模板，输出大模型提示词","parameters":{"type":"object","properties":{"fileUrl":{"type":"string","description":"简历文件URL"},"instanceId":{"type":"string","description":"实例ID"}},"required":["fileUrl","instanceId"]},"order":["fileUrl","instanceId"],"return":{"description":"Map结构包含提示词，插件处理过文件的标志位以及错误信息","type":"object","properties":{"isFileHandled":{"type":"boolean"},"cvAnalyzerPrompt":{"type":"string"},"errorMessage":{"type":"string"}},"converter":""}}',
        '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8') ON CONFLICT ("definition_group_name", "name") DO NOTHING;
INSERT INTO "public"."store_tag" ("tool_unique_name", "name")
VALUES ('8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8', 'FIT') ON CONFLICT ("tool_unique_name", "name") DO NOTHING;
INSERT INTO "public"."store_tag" ("tool_unique_name", "name")
VALUES ('8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8', 'BUILTIN') ON CONFLICT ("tool_unique_name", "name") DO NOTHING;
INSERT INTO "public"."store_plugin_tool" ("tool_name", "tool_unique_name", "plugin_id")
VALUES ('AI简历解析插件', '8b7e54b7-ce07-40ed-ad93-5d608aa8f6d8',
        'f13a2bd6bdb5afdb2ce166fb2da6c445057b7e092791c743f9b8238dd78a62dd') ON CONFLICT ("plugin_id", "tool_unique_name") DO NOTHING;
INSERT INTO "public"."store_plugin" ("plugin_id", "plugin_name", "extension", "deploy_status", "is_builtin")
VALUES ('f13a2bd6bdb5afdb2ce166fb2da6c445057b7e092791c743f9b8238dd78a62dd', 'AI简历解析插件',
        '{"artifactId":"aipp-plugin","groupId":"modelengine.fit.jober","checksum":"aba5bd4a9ad359ede54794a189d36a2e77c6d611af6a4aef6c63bbd588c61b24","type":"java","description":"AI简历解析插件","pluginFullName":"aipp-plugin-0.1.0-SNAPSHOT_1726059364182.jar","pluginName":"AI简历解析插件"}',
        'DEPLOYED', TRUE) ON CONFLICT ("plugin_id") DO NOTHING;



INSERT INTO "public"."store_tool" ("name", "schema", "runnables", "unique_name", "group_name", "definition_name", "definition_group_name")
VALUES ('AI提示词拼接工具',
        '{"name":"AI提示词拼接工具","description":"这是一个对用户输入提示词进行拼接的工具。","parameters":{"type":"object","properties":{"appId":{"type":"string","description":"应用ID"},"instanceId":{"type":"string","description":"实例ID"},"input":{"type":"string","description":"用户输入"}},"required":["appId","instanceId","input"],"order":["appId","instanceId","input"]},"return":{"type":"string"}}',
        '{"FIT":{"genericableId":"modelengine.fit.jober.aipp.tool.prompt.word.splice","fitableId":"prompt.word.splice","alias":"prompt.word.splice"}}',
        'bdc009dc-969e-4839-b5d7-e9599009d50d',
        'bdc009dc-969e-4839-b5d7-e9599009d50d', 'bdc009dc-969e-4839-b5d7-e9599009d50d',
        'bdc009dc-969e-4839-b5d7-e9599009d50d') ON CONFLICT ("unique_name", "version") DO NOTHING;
INSERT INTO "public"."store_definition" ("name", "schema", "definition_group_name")
VALUES ('bdc009dc-969e-4839-b5d7-e9599009d50d',
        '{"name":"AI提示词拼接工具","description":"这是一个对用户输入提示词进行拼接的工具。","parameters":{"type":"object","properties":{"appId":{"type":"string","description":"应用ID"},"instanceId":{"type":"string","description":"实例ID"},"input":{"type":"string","description":"用户输入"}},"required":["appId","instanceId","input"],"order":["appId","instanceId","input"]},"return":{"type":"string"}}',
        'bdc009dc-969e-4839-b5d7-e9599009d50d') ON CONFLICT ("definition_group_name", "name") DO NOTHING;
INSERT INTO "public"."store_tag" ("tool_unique_name", "name")
VALUES ('bdc009dc-969e-4839-b5d7-e9599009d50d', 'FIT') ON CONFLICT ("tool_unique_name", "name") DO NOTHING;
INSERT INTO "public"."store_tag" ("tool_unique_name", "name")
VALUES ('bdc009dc-969e-4839-b5d7-e9599009d50d', 'BUILTIN') ON CONFLICT ("tool_unique_name", "name") DO NOTHING;
INSERT INTO "public"."store_plugin_tool" ("tool_name", "tool_unique_name", "plugin_id")
VALUES ('AI提示词拼接工具', 'bdc009dc-969e-4839-b5d7-e9599009d50d',
        'eb5adfcef2355b7b4e76dc53626a98a4b863c3e33c12c43221d03907c025bce8') ON CONFLICT ("plugin_id", "tool_unique_name") DO NOTHING;
INSERT INTO "public"."store_plugin" ("plugin_id", "plugin_name", "extension", "deploy_status", "is_builtin")
VALUES ('eb5adfcef2355b7b4e76dc53626a98a4b863c3e33c12c43221d03907c025bce8', 'AI提示词拼接工具', '{}', 'DEPLOYED', TRUE) ON CONFLICT ("plugin_id") DO NOTHING;