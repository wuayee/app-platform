/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.config;

import modelengine.fel.rag.store.connector.schema.VectorSchema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

/**
 * 向量数据库配置信息。
 *
 * @since 2024-05-07
 */
@Setter
@Getter
@Builder
public class VectorConfig {
    private String databaseName;
    private String collectionName;
    private String vectorFieldName;
    private VectorSchema schema;
    private IndexType indexType;
    private MetricType metricType;
    private String extraParam;
    private boolean shouldNormalizeScore = true;

    /**
     * 按照默认值构建 {@link VectorConfig}实例
     */
    @Tolerate
    public VectorConfig() {
        this.databaseName = "default";
        this.collectionName = "vectorStore";
        this.vectorFieldName = "embedding";
        this.schema = null;
        this.indexType = IndexType.IVF_FLAT;
        this.metricType = MetricType.L2;
        this.extraParam = "{\"nlist\":16384}";
    }
}
