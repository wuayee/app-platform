/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store;

/**
 * Store接口，对各种类型的数据库进行封装。
 * <p>例如向量数据库、KV数据库、SQL数据库等。</p>
 *
 * @param <I> 表示对数据库的输入。
 * @param <O> 表示数据库的输出。
 * @param <Q> 表示查询参数，即Query。
 * @param <C> 表示数据库的相关配置，即Config。
 * @since 2024-05-07
 */
public interface Store<I, O, Q, C> {
    /**
     * 根据传入的查询参数和配置进行查询。
     *
     * @param query 表示查询参数。
     * @param conf 表示配置信息。
     * @return 返回查询到的结果。
     */
    O get(Q query, C conf);

    /**
     * 根据传入的输入信息和配置对数据库进行插入操作。
     *
     * @param input 表示对数据库的输入。
     * @param conf 表示配置信息。
     */
    void put(I input, C conf);

    /**
     * 根据传入的查询参数和配置进行删除。
     *
     * @param query 表示查询参数。
     * @param conf 表示配置信息。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    Boolean delete(Q query, C conf);
}
