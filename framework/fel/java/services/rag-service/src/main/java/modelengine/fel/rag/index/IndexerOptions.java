/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 索引服务相关参数。
 *
 * @since 2024-06-03
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexerOptions {
    private String connectorId;
    private List<String> columnTypes;
    private String tableName;
    private int topK;
    private String expr;
}
