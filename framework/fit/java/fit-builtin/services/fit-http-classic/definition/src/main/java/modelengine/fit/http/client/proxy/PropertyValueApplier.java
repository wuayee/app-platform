/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.proxy;

/**
 * 表示属性值的填充器。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
@FunctionalInterface
public interface PropertyValueApplier {
    /**
     * 将指定值通过规则填充进 Http 请求。
     *
     * @param requestBuilder 表示待设置的 Http 请求建造者的 {@link RequestBuilder}。
     * @param value 表示指定值的 {@link Object}。
     */
    void apply(RequestBuilder requestBuilder, Object value);
}
