/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.index;

import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;
import com.huawei.jade.fel.rag.store.connector.schema.DbFieldType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 关系型表格内容存储。
 * <p>负责将chunk中的行内容存入指定的sql数据库中。</p>
 *
 * @since 2024-05-22
 */
public class TableIndex implements Indexer<List<Chunk>> {
    private final JdbcSqlConnector conn;
    private final List<DbFieldType> columnTypes;
    private final String tableName;

    /**
     * 根据传入连接器及表格相关信息构造 {@link TableIndex} 的实例。
     *
     * @param conn 表示向量数据库的 {@link JdbcSqlConnector}。
     * @param columnTypes 数据表中每列的类型 {@link List<DbFieldType>}
     * @param tableName 待写入表的名称 {@link String}
     */
    public TableIndex(JdbcSqlConnector conn, List<DbFieldType> columnTypes, String tableName) {
        this.conn = conn;
        this.columnTypes = columnTypes;
        this.tableName = tableName;
    }

    @Override
    public void process(List<Chunk> input) {
        conn.executeBatch(input.stream()
                .map(chunk -> formatInsertionSQL(chunk))
                .collect(Collectors.toList()));
    }

    private String formatInsertionSQL(Chunk chunk) {
        List<String> row = chunk.getRowContent();
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName);
        sb.append(" values (DEFAULT");
        for (int i = 0; i < row.size(); i++) {
            sb.append(", ");
            if (row.get(i).isEmpty()) {
                sb.append("null");
                continue;
            }
            switch (columnTypes.get(i)) {
                case VARCHAR:
                    sb.append("'");
                    sb.append(row.get(i));
                    sb.append("'");
                    break;
                case NUMBER:
                    sb.append(row.get(i));
                    break;
                default:
                    break;
            }
        }
        sb.append(");");
        return sb.toString();
    }
}
