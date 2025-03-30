/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.oms;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.jade.oms.entity.PartitionedEntity;
import modelengine.jade.oms.response.ResultVo;

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
