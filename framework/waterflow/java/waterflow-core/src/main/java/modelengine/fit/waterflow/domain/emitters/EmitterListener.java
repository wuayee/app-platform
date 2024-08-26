/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.emitters;

/**
 * 数据发布的监听器
 *
 * @param <D> 待发布的数据类型
 * @param <T> session的类型
 * @since 1.0
 */
public interface EmitterListener<D, T> {
    /**
     * 处理发布过来的数据
     *
     * @param data 待处理的发布过来的数据
     * @param token 数据归属的session
     */
    void handle(D data, T token);

    /**
     * 订阅一个发布源
     *
     * @param emitter 发布源
     */
    default void offer(Emitter<D, T> emitter) {
        emitter.register(this);
    }
}
