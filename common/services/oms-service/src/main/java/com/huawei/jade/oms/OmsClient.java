/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.jade.oms.entity.PartitionedEntity;
import com.huawei.jade.oms.response.ResultVo;

/**
 * 表示 OMS 服务客户端。
 *
 * @author 何天放
 * @author 李金绪
 * @since 2024-11-19
 */
public interface OmsClient {
    /**
     * 执行 OMS 服务操作，使用 json。
     *
     * @param service 表示服务名称的 {@link String}。
     * @param method 表示请求方法的类型的 {@link HttpRequestMethod}。
     * @param url 表示服务地址的 {@link String}。
     * @param param 表示自定义参数的 {@link Object}。
     * @param resultType 表示响应类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表示执行结果的 {@link ResultVo}{@code <}{@link T}{@code >}。
     */
    <T> ResultVo<T> executeJson(String service, HttpRequestMethod method, String url, Object param,
            Class<T> resultType);

    /**
     * 执行 OMS 服务操作，使用 text。
     *
     * @param service 表示服务名称的 {@link String}。
     * @param method 表示请求方法的类型的 {@link HttpRequestMethod}。
     * @param url 表示服务地址的 {@link String}。
     * @param param 表示自定义参数的 {@link String}。
     * @param resultType 表示响应类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表示执行结果的 {@link ResultVo}{@code <}{@link T}{@code >}。
     */
    <T> ResultVo<T> executeText(String service, HttpRequestMethod method, String url, String param,
            Class<T> resultType);

    /**
     * 上传文件。
     *
     * @param service 表示服务名称的 {@link String}。
     * @param method 表示请求方法的类型的 {@link HttpRequestMethod}。
     * @param url 表示服务地址的 {@link String}。
     * @param entity 表示消息体的 {@link PartitionedEntity}。
     * @param resultType 表示响应类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表示执行结果的 {@link ResultVo}{@code <}{@link T}{@code >}。
     */
    <T> ResultVo<T> upload(String service, HttpRequestMethod method, String url, PartitionedEntity entity, Class<T> resultType);
}
