INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
                            parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at,
                            update_at,
                            archived_at)
VALUES ('1', '1', '1', '1', '1', 'input', '1', false, 'PENDING', NULL, NULL, NULL, '2',
        '2', false, '2023-07-29T09:59:30.011', '2023-07-29T09:59:30.011', NULL);

INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
                            parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at,
                            update_at,
                            archived_at)
VALUES ('2', '2', '2', '2', '2', 'input', '1', false, 'ERROR', NULL, NULL, NULL, '3',
        '3', false, '2023-07-29T09:59:30.011', '2023-07-29T09:59:30.011', NULL);

INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
                            parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at,
                            update_at,
                            archived_at)
VALUES ('3', '3', '3', '3', '12ddd65a3ed54e69936a739ca7767c2f-1.0.0', 'input', '4', false, 'ERROR', NULL, NULL, NULL,
        '4', '4', false, '2023-07-29T09:59:30.011', '2023-07-29T09:59:30.011', NULL);

INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
                            parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at,
                            update_at,
                            archived_at)
VALUES ('4', '4', '4', '4', '12ddd65a3ed54e69936a739ca7767c2f-1.0.0', 'input', '5', false, 'ERROR', NULL, NULL, NULL,
        '5', '5', false, '2023-07-29T09:59:30.011', '2023-07-29T09:59:30.011', NULL);

INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id, joined, status,
                            parallel, parallel_mode, previous, batch_id, to_batch, sent, create_at,
                            update_at,
                            archived_at)
VALUES ('5', '4', '4', '4', '12ddd65a3ed54e69936a739ca7767c2f-1.0.0', 'input', '5', false, 'ERROR', NULL, NULL, NULL,
        '5', '5', false, '2023-07-29T09:59:30.011', '2023-07-29T09:59:30.011', NULL);