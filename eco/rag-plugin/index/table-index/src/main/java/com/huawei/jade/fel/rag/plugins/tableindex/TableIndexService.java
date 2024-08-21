/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.plugins.tableindex;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Destroy;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.schedule.annotation.Scheduled;
import com.huawei.jade.fel.rag.index.IndexConfig;
import com.huawei.jade.fel.rag.index.IndexService;
import com.huawei.jade.fel.rag.index.IndexerOptions;
import com.huawei.jade.fel.rag.index.TableIndex;
import com.huawei.jade.fel.rag.protocol.FlatChunk;
import com.huawei.jade.fel.rag.store.connector.ConnectorProperties;
import com.huawei.jade.fel.rag.store.connector.JdbcSqlConnector;
import com.huawei.jade.fel.rag.store.connector.JdbcType;
import com.huawei.jade.fel.rag.store.connector.schema.DbFieldType;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 关系数据库索引的表格实现。
 *
 * @since 2024-06-03
 */
@Component("table-index-service")
public class TableIndexService implements IndexService {
    private static final long MAX_IDLE_TIME = 1800000L; // 30分钟

    /**
     * 同一个数据库的不同表、相同表的操作应该复用连接
     */
    private final Map<String, ConnectorWrapper> connectorMap = new ConcurrentHashMap<>();

    @Override
    @Fitable("table-index")
    public void index(List<FlatChunk> flatChunks, IndexerOptions options) {
        if (!this.connectorMap.containsKey(options.getConnectorId())) {
            throw new IllegalArgumentException("Table connector dosenot exists");
        }
        JdbcSqlConnector connector = this.connectorMap.get(options.getConnectorId()).getConnector();
        TableIndex tableIndex = new TableIndex(connector,
                options.getColumnTypes().stream().map(DbFieldType::valueOf).collect(Collectors.toList()),
                options.getTableName());
        tableIndex.process(flatChunks.stream().map(FlatChunk::toChunk).collect(Collectors.toList()));
    }

    @Override
    @Fitable("table-search")
    public List<FlatChunk> search(String query, IndexerOptions options) {
        return Collections.emptyList(); // 后续使用到了再扩充
    }

    @Override
    @Fitable("table-add-connector")
    public String addConnector(IndexConfig config) {
        if (config == null || config.isInvalid()) {
            throw new IllegalArgumentException("Table index config invalid");
        }

        String connectorId = config.generateId();
        if (!this.connectorMap.containsKey(connectorId)) {
            this.connectorMap.put(connectorId, new ConnectorWrapper(new JdbcSqlConnector(
                    JdbcType.valueOf(config.getDbType().toUpperCase()),
                    new ConnectorProperties(
                            config.getHost(), config.getPort(), config.getUsername(), config.getPassword()),
                    config.getDatabaseName())));
        }
        return connectorId;
    }

    @Override
    @Fitable("table-remove-connector")
    public void removeConnector(String id) {
        ConnectorWrapper connector = this.connectorMap.get(id);
        if (connector != null) {
            connector.setIdle(true);
        }
    }

    /**
     * 定时（3分钟）清理空闲超时的数据库连接。
     * <p>超时立即清理。</p>
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "180000")
    public void removeIdleConnectors() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, ConnectorWrapper>> iterator = connectorMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ConnectorWrapper> entry = iterator.next();
            ConnectorWrapper connector = entry.getValue();
            if (connector.isIdle() && (currentTime - connector.getLastUsedTime() > MAX_IDLE_TIME)) {
                connector.close();
                iterator.remove();
            }
        }
    }

    /**
     * 关闭所有连接。
     */
    @Destroy
    public void closeAllConnectors() {
        for (ConnectorWrapper connector : connectorMap.values()) {
            connector.close();
        }
        connectorMap.clear();
    }
}
