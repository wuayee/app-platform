/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop.stub;

/**
 * 嵌套操作单元接口。
 *
 * @author 刘信宏
 * @since 2024-07-29
 */
public interface NestedSpanTestService {
    /**
     * 嵌套操作的名称。
     */
    String NESTED_SPAN_NAME = "nested.service.invoke";

    /**
     * 嵌套属性的键。
     */
    String NESTED_ATTR_KEY = "nested.args";

    /**
     * 测试方法。
     *
     * @param arg 表示测试方法参数的 {@link String}。
     */
    void invoke(String arg);
}
