/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain.entity;

import java.util.Map;

/**
 * 表示代码执行器接口定义。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
public interface CodeExecutor {
    /**
     * 执行用户自定义代码。
     *
     * @param args 表示函数入参的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param code 表示用户定义的代码的 {@link String}。
     * @return 表示执行结果的 {@link Object}。
     */
    Object run(Map<String, Object> args, String code);

    /**
     * 获取支持的编程语言。
     *
     * @return 表示编程语言类型的 {@link ProgrammingLanguage}。
     */
    ProgrammingLanguage language();
}