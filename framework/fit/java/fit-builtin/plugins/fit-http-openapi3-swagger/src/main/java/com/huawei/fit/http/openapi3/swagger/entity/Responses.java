/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.entity;

import com.huawei.fit.http.openapi3.swagger.Serializable;
import com.huawei.fit.http.openapi3.swagger.entity.support.DefaultResponses;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#responses-object">OpenAPI
 * 3.1.0</a> 文档中的所有响应信息。
 *
 * @author 季聿阶
 * @since 2023-08-27
 */
public interface Responses extends Serializable {
    /**
     * 向所有响应信息中设置指定 Http 状态码对应的响应信息。
     *
     * @param httpStatusCode 表示指定 Http 状态码的 {@link String}。
     * @param response 表示响应信息的 {@link Response}。
     */
    void put(String httpStatusCode, Response response);

    /**
     * 从所有响应信息中获取指定 Http 状态码的响应信息。
     *
     * @param httpStatusCode 表示指定 Http 状态码的 {@link String}。
     * @return 表示指定 Http 状态码的响应信息的 {@link Response}。
     */
    Response get(String httpStatusCode);

    /**
     * 获取所有响应信息。
     *
     * @return 表示所有响应信息的 {@link Map}{@code <}{@link String}{@code , }{@link Response}{@code >}。
     */
    Map<String, Response> getResponses();

    /**
     * 创建一个空的所有响应信息。
     *
     * @return 表示创建出来的空的所有响应信息的 {@link Responses}。
     */
    static Responses create() {
        return new DefaultResponses();
    }
}
