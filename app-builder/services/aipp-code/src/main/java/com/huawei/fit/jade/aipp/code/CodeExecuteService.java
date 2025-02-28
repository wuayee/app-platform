/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 代码执行服务接口定义。
 *
 * @author 方誉州
 * @since 2024-06-21
 */
public interface CodeExecuteService {
    /**
     * 执行用户定义的代码。
     *
     * @param args 表示节点入参的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param code 表示用户定义的代码的 {@link String}。
     * @param language 表示用户代码的语言的 {@link String}。
     * @param output 表示节点出参规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 返回后端沙盒环境代码执行的结果。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.code.execute")
    Object executeCode(Map<String, Object> args, String code, String language, Map<String, Object> output);
}