/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import lombok.Data;

/**
 * http返回信息。
 *
 * @author 张越
 * @since 2024-11-21
 */
@Data
public class HttpResult {
    private Integer status;
    private String errorMsg;
    private Object data;
}
