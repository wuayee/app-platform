/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import lombok.Data;

/**
 * http鉴权信息。
 *
 * @author 张越
 * @since 2024-11-21
 */
@Data
public class Authentication {
    private String type;
    private String header;
    private String authKey;
}
