/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.rerank;

import modelengine.fel.rag.Chunks;

/**
 * 基于模型的重排序基类，将检索到的数据利用模型进行重排序。
 *
 * @since 2024-05-07
 */
public abstract class ModelRerank implements Rerank<Chunks, Chunks> {
    /**
     * 本方法不支持调用，会抛出异常。
     *
     * @param data data 表示经过索引检索到的数据的 {@link Chunks}。
     * @return 不应该返回
     * @throws UnsupportedOperationException UnsupportedOperationException
     */
    @Override
    public final Chunks invoke(Chunks data) {
        throw new UnsupportedOperationException("Unsupported: a query is required.");
    }

    /**
     * 根据查询信息内容对检索到的数据进行重排序。
     *
     * @param query 表示来自检索器的查询信息的 {@link String}。
     * @param data 表示经过索引检索到的数据的 {@link Chunks}。
     * @return 返回重排序后的数据。
     */
    @Override
    public Chunks invoke(String query, Chunks data) {
        return new Chunks();
    }
}
