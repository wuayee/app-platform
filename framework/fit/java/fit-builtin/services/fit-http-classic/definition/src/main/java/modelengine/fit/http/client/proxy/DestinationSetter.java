/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.proxy;

/**
 * 表示数据目标设置器。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
public interface DestinationSetter {
    /**
     * 将数据设置进 Http 请求。
     *
     * @param requestBuilder 表示 Http 请求建造者的 {@link RequestBuilder}。
     * @param value 表示待设置的值的 {@link Object}。
     */
    void set(RequestBuilder requestBuilder, Object value);
}
