/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.utils;

/**
 * 表示信息提取算子的常量定义。
 *
 * @author 何嘉斌
 * @since 2024-10-26
 */
public interface Constant {
    /**
     * 输入参数需要提取文本的键。
     */
    String TEXT_KEY = "text";

    /**
     * 输入参数提取要求描述的键。
     */
    String DESC_KEY = "desc";

    /**
     * 输入参数历史记录的键。
     */
    String HISTORY_KEY = "histories";

    /**
     * 默认提示词文件名。
     */
    String BUILTIN_PROMPT = "/prompt.txt";
}