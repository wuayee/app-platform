INSERT INTO `tag` (id, name, description, created_by, created_at, updated_by, updated_at)
VALUES ('e4ac3eea2daf4ed7aa7f4b2cb6422ed0', 'aTag', '1', 'Wh', '2023-09-15 07:49:20.997', 'Wh',
        '2023-09-15 07:49:20.997');

INSERT INTO `tag` (id, name, description, created_by, created_at, updated_by, updated_at)
VALUES ('ab0ed8c78aa44d0c962dae1bea52b72e', 'twoTag', '1', 'Wh', '2023-09-16 07:49:20.997', 'Wh',
        '2023-09-16 07:49:20.997');

INSERT INTO `tag_usage` (id, tag_id, object_type, object_id, created_by, created_at)
VALUES ('8ab6bb32139b4a5e962255f218e404ee', 'e4ac3eea2daf4ed7aa7f4b2cb6422ed0', 'FLOW GRAPH',
        '9c03fc9ee86b4311b4a401be75ddb112', 'wh', '2023-11-03 20:25:39');

INSERT INTO `tag_usage` (id, tag_id, object_type, object_id, created_by, created_at)
VALUES ('8ab6bb32139b4a5e962255f218e404kk', 'ab0ed8c78aa44d0c962dae1bea52b72e', 'FLOW GRAPH',
        '9c03fc9ee86b4311b4a401be75ddb112', 'wh', '2023-11-03 20:25:39');

INSERT INTO `flow_graph` (id, version, tenant, status, created_by, updated_by, is_deleted, created_at,
                          updated_at)
VALUES ('9c03fc9ee86b4311b4a401be75ddb112', '1.0.0', 'test', 'active', 'Wh', 'Wh', 'f', '2023-11-03 20:25:39',
        '2023-11-03 20:25:39');

INSERT INTO `flow_graph` (id, version, tenant, status, created_by, updated_by, is_deleted, created_at,
                          updated_at)
VALUES ('9c03fc9ee86b4311b4a401be75ddb113', '1.0.0', 'test', 'active', 'Wh', 'Wh', 'f', '2023-11-03 20:25:39',
        '2023-11-03 20:25:39');
INSERT INTO `flow_graph` (id, version, tenant, status, created_by, updated_by, is_deleted, created_at,
                          updated_at)
VALUES ('9c03fc9ee86b4311b4a401be75ddb114', '1.0.0', 'test', 'unpublished', 'Wh', 'Wh', 'f', '2023-11-03 20:25:39',
        '2023-11-03 20:25:39');

INSERT INTO `flow_context` (context_id, trace_id, trans_id, root_id, stream_id, flow_data, position_id,
                            joined, status, parallel, parallel_mode, previous, batch_id,
                            to_batch, sent, create_at, update_at, archived_at)
VALUES ('75535197135e4c429011fe6b8846c926', 'ef6bb679582848e1af177ddda7cb7814', 'b4f9ffd05cde4970a05d5076c2245b40',
        'start1', '9c03fc9ee86b4311b4a401be75ddb113-1.0.0',
        '{"operator": "yxy", "groupData": {}, "startTime": "2023-09-05 08:28:25", "application": "A3000", "businessData": {}}',
        'A00009', 'f', 'NEW', '', '', '209eade42ea0459b8d996a54f9e8360c', NULL, NULL, 'f',
        '2023-10-28 18:10:40.713', '2023-10-28 18:10:41.775', '2023-10-28 18:10:41.775');




