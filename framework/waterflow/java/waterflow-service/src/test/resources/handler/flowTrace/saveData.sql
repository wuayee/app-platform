INSERT INTO `flow_trace` (trace_id, stream_id, operator, application_name, start_node, cur_nodes,
                          start_time, end_time, status, context_pool)
VALUES ('1', '7165df81d4754f17a273c58f9ec3497f-1.0.0', 'yyk', 'tianzhou', 'start1', 'default_node',
        '2023-12-11 10:43:07', NULL, 'RUNNING',
        '544196c17de34a72abbd11a437358e13, a44f3be1c1cd4c4a9f6a614ab13ae874');
INSERT INTO `flow_trace` (trace_id, stream_id, operator, application_name, start_node, cur_nodes,
                          start_time, end_time, status, context_pool)
VALUES ('2', '2-1.0.0', 'yyk', 'tianzhou', 'start1', 'default_node', '2023-12-11 10:43:07', NULL, 'ERROR',
        '544196c17de34a72abbd11a437358e13');