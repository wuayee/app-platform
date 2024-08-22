/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.index;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fel.rag.protocol.FlatChunk;

import java.util.List;

/**
 * 向量型索引服务
 *
 * @since 2024-06-04
 */
public interface IndexService {
    /**
     * 入库并建立索引。
     *
     * @param flatChunks 表示入库数据的 {@link List}{@code <}{@link FlatChunk}{@code >}。
     * @param options 表示索引服务超参数的 {@link IndexerOptions}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.indexer.index")
    void index(List<FlatChunk> flatChunks, IndexerOptions options);

    /**
     * 根据指定查询进行搜索。
     *
     * @param query 表示查询的 {@link String}
     * @param options 表示查询参数的{@link IndexerOptions}
     * @return 返回搜索结果列表
     */
    @Genericable(id = "com.huawei.jade.fel.rag.indexer.search")
    List<FlatChunk> search(String query, IndexerOptions options);

    /**
     *  根据传入的配置创建数据库连接器，生成其对应的唯一标识。
     *
     * @param config 表示配置参数的{@link IndexConfig}
     * @return String
     */
    @Genericable(id = "com.huawei.jade.fel.rag.indexer.search.addconnector")
    String addConnector(IndexConfig config);

    /**
     * 根据传入的id删除对应的数据库连接。
     *
     * @param id 表示Id的{@link String}。
     */
    @Genericable(id = "com.huawei.jade.fel.rag.indexer.search.removeconnector")
    void removeConnector(String id);
}
