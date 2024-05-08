/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.reactive;

/**
 * 既是发布者，也是接收者：处理完数据后再发给下一个接受者
 *
 * @param <T> 接收的数据类型
 * @param <R> 处理后的数据类型
 * @since 1.0
 */
public interface Processor<T, R> extends Publisher<R>, Subscriber<T, R> {
    /**
     * close
     *
     * @return Subscriber<R, R>
     */
    Subscriber<R, R> close();

    /**
     * set processor id for flow display
     *
     * @param name processor name
     * @return 自身
     */
    Processor<T, R> displayAs(String name);

    /**
     * 展示
     *
     * @return 返回一个展示的name
     */
    String display();
}
