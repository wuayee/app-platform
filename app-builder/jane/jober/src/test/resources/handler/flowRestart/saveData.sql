
-- new
INSERT INTO `flow_trace`
    (trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('10174dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'A3000', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '31b1ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
    (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
     parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('31b1ebbbde674babb22fafa56fe9062d', '10174dc2b03e4e15a7611ad3e66e736e', '2144ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"operator": "A3000", "startTime": "2024-04-18T14:48:35.857", "application": "", "businessData": {}}',
        'state1', 'f', 'NEW', '', '', 'd507b793c29a4c799c41987bfccc67d4', '147ba4d31d3f4b3191facd2ee8ccca60', 'cac15a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

--pending
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('11174dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'A3000', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '1111ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('1111ebbbde674babb22fafa56fe9062d', '11174dc2b03e4e15a7611ad3e66e736e', '1114ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"application":"tianzhou","businessData":{"application":"tianzhou","cudehubTag":"tag","libingId":"id","cudehubBranch":"branch","libingStatus":"status","cudehubUser":"user"},"contextData":{},"operator":"gsy","startTime":"2024-04-24T19:53:21.316"}',
        'event2', 'f', 'PENDING', '', '', '1117b793c29a4c799c41987bfccc67d4', '111ba4d31d3f4b3191facd2ee8ccca60', '11115a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');

--ready
INSERT INTO `flow_trace`
(trace_id, stream_id, operator, application_name, start_node, cur_nodes, start_time, end_time, status, context_pool)
VALUES ('22274dc2b03e4e15a7611ad3e66e736e', 'executor-1.0.0', 'A3000', '', 'start1', 'state1', '2024-04-17 22:14:27.715', '2024-04-18 02:15:40.734', 'RUNNING', '2221ebbbde674babb22fafa56fe9062d');

INSERT INTO `flow_context`
(context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
 parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at, update_at, archived_at)
VALUES ('2221ebbbde674babb22fafa56fe9062d', '22274dc2b03e4e15a7611ad3e66e736e', '2224ead3de8b4dd484c80f77c562b698', 'start1',
        'executor-1.0.0', '{"application":"tianzhou","businessData":{"application":"tianzhou","cudehubTag":"tag","libingId":"id","cudehubBranch":"branch","libingStatus":"status","cudehubUser":"user"},"contextData":{},"operator":"gsy","startTime":"2024-04-24T19:53:21.316"}',
        'state1', 'f', 'READY', '', '', '1117b793c29a4c799c41987bfccc67d4', '111ba4d31d3f4b3191facd2ee8ccca60', '11115a4a091c4d1b8a68be8e4362f2de',
        'f', '2024-04-18 14:48:43.711', '2024-04-18 14:49:16.361', '2024-04-18 14:49:16.361');
