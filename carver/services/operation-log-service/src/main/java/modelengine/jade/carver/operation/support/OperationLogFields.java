/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.operation.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志记录数据类型。
 *
 * @author 方誉州
 * @since 2024-08-01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogFields {
    /**
     * 操作的功能模块。
     */
    private String functionModule;

    /**
     * 操作级别。
     */
    private String level;

    /**
     * 操作用户。
     */
    private String operator;

    /**
     * ip地址。
     */
    private String ipAddr;

    /**
     * 操作结果。
     */
    private String operationResult;

    /**
     * 具体的操作信息。
     */
    private String details;

    /**
     * 操作名称。
     */
    private String name;

    /**
     * 操作的资源对象。
     */
    private String resourceName;

    /**
     * 请求的URI，不包含服务器地址和端口。
     */
    private String requestUri;
}
