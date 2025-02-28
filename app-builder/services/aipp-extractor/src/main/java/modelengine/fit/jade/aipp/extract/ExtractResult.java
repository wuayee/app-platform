/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本提取结果定义。
 *
 * @author 易文渊
 * @since 2024-10-24
 */
@Getter
@AllArgsConstructor
public class ExtractResult {
    /**
     * 是否提取成功。
     */
    private boolean success;

    /**
     * 提取内容。
     */
    private Object extractedParams;
}