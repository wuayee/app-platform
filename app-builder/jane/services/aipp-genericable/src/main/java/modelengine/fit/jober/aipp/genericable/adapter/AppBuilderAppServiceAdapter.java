/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jober.aipp.dto.chat.AppMetadata;
import modelengine.fit.jober.aipp.dto.chat.AppQueryParams;
import modelengine.fit.jober.common.RangedResultSet;

/**
 * 应用创建服务接口。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
public interface AppBuilderAppServiceAdapter {
    /**
     * 获取应用的列表信息。
     *
     * @param params 表示查询条件参数的 {@link AppQueryParams}。
     * @param httpRequest 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示获取到的应用列表信息的
     * {@link RangedResultSet}{@code <}{@link AppMetadata}{@code >}。
     */
    RangedResultSet<AppMetadata> list(AppQueryParams params, HttpClassicServerRequest httpRequest, String tenantId);
}
