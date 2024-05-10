-- 失败场景
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('10174dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'yxy', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '31b1ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('31b1ebbbde674babb22fafa56fe9062d', '10174dc2b03e4e15a7611ad3e66e736e', '2144ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy", "errorInfo": {"nodeName": "测试ECHO_JOBER使用默认属性","errorCode": 10007501,"fitableId": "","errorMessage": "","originMessage": ""}, "startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'ERROR', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

--终止
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('11174dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'yxy', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '1111ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('1111ebbbde674babb22fafa56fe9062d', '11174dc2b03e4e15a7611ad3e66e736e', '1114ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'READY', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

-- 计算状态
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('22274dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'yxy', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '2221ebbbde674babb22fafa56fe9062d, 3331ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('2221ebbbde674babb22fafa56fe9062d', '22274dc2b03e4e15a7611ad3e66e736e', '2224ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'ARCHIVED', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('3331ebbbde674babb22fafa56fe9062d', '22274dc2b03e4e15a7611ad3e66e736e', '2224ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'ARCHIVED', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

-- 计算百分比
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('33374dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'yxy', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '4441ebbbde674babb22fafa56fe9062d, 5551ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('4441ebbbde674babb22fafa56fe9062d', '33374dc2b03e4e15a7611ad3e66e736e', '3334ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'start1', 'f', 'ARCHIVED', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('5551ebbbde674babb22fafa56fe9062d', '22274dc2b03e4e15a7611ad3e66e736e', '2224ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'ERROR', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

--根据streamId查询context
INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('6661ebbbde674babb22fafa56fe9062d', '33374dc2b03e4e15a7611ad3e66e736e', '3334ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-2.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state2', 'f', 'READY', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('7771ebbbde674babb22fafa56fe9062d', '22274dc2b03e4e15a7611ad3e66e736e', '2224ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-2.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'ERROR', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('8881ebbbde674babb22fafa56fe9062d', '33374dc2b03e4e15a7611ad3e66e736e', '3334ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-2.0.0', '{"operator": "yxy","startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'start1', 'f', 'ARCHIVED', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');