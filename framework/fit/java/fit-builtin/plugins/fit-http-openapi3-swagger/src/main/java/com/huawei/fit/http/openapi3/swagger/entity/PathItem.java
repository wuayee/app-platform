/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.entity;

import com.huawei.fit.http.openapi3.swagger.Serializable;
import com.huawei.fit.http.openapi3.swagger.entity.support.DefaultPathItem;
import com.huawei.fit.http.protocol.HttpRequestMethod;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#path-item-object">OpenAPI
 * 3.1.0</a> 文档中的路径信息。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-16
 */
public interface PathItem extends Serializable {
    /**
     * 获取路径详细信息所属的路径。
     *
     * @return 表示路径详细信息所属的路径的 {@link String}。
     */
    String getPath();

    /**
     * 获取路径详细信息中所有操作的信息。
     *
     * @return 表示路径详细信息中所有操作的信息的 {@link Map}{@code <}{@link HttpRequestMethod}{@code , }{@link
     * Operation}{@code >}。
     */
    Map<HttpRequestMethod, Operation> getOperations();

    /**
     * 将指定方法的操作设置进路径详细信息中。
     *
     * @param method 表示指定的 Http 方法的 {@link HttpRequestMethod}。
     * @param operation 表示指定的操作详细信息的 {@link Operation}。
     */
    void put(HttpRequestMethod method, Operation operation);

    /**
     * 通过指定路径创建路径的详细信息。
     *
     * @param path 表示指定路径的 {@link String}。
     * @return 表示创建出来的路径详细信息的 {@link PathItem}。
     */
    static PathItem create(String path) {
        return new DefaultPathItem(path);
    }
}
