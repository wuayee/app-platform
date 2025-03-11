/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表示主机与端口的组合的类。
 *
 * @author 方誉州
 * @since 2025-01-13
 */
@Data
@AllArgsConstructor
public class NetPoint {
    private String protocol;
    private String host;
    private int port;
}
