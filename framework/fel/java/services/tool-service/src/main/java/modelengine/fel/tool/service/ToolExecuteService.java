/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.tool.service;

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
     * 执行指定工具，返回格式化内容。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param toolName 表示指定工具名字的 {@link String}。
     * @param jsonArgs 表示执行参数的 Json 字符串的 {@link String}。
     * @return 表示执行结果的 {@link Object}。
     */
    @Genericable(id = "modelengine.fel.tool.execute.json")
    String execute(String namespace, String toolName, String jsonArgs);

    /**
     * 执行指定工具，返回原始对象。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param toolName 表示指定工具名字的 {@link String}。
     * @param jsonObject 表示执行参数的 Json 对象的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示执行结果的 {@link Object}。
     */
    @Genericable(id = "modelengine.fel.tool.execute.object")
    String execute(String namespace, String toolName, Map<String, Object> jsonObject);
}
