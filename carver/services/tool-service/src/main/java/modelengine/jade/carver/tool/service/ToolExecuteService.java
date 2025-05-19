/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.service;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 表示工具的执行服务。
 *
 * @author 季聿阶
 * @since 2024-04-08
 */
public interface ToolExecuteService {
    /**
     * 执行指定工具。
     *
     * @param uniqueName 表示指定工具唯一名字的 {@link String}。
     * @param jsonArgs 表示执行参数的 Json 字符串的 {@link String}。
     * @return 表示执行结果的 Json 字符串的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.execute.json")
    String execute(String uniqueName, String jsonArgs);

    /**
     * 执行指定工具。
     *
     * @param uniqueName 表示指定工具唯一名字的 {@link String}。
     * @param jsonObject 表示执行参数的 Json 对象的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示执行结果的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.execute.jsonObject")
    String execute(String uniqueName, Map<String, Object> jsonObject);
}
