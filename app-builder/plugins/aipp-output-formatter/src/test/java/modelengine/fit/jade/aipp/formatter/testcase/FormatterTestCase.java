/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.testcase;

import lombok.Getter;

/**
 * 格式化器用例结构。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
@Getter
public class FormatterTestCase {
    private Object data;
    private String expected;
    private String finalOutput;
}
