/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question.util;

/**
 * 表示重写插件的常量定义。
 *
 * @author 张越
 * @since 2024-11-18
 */
public interface Constant {
    /**
     * 输入参数用户问题的键。
     */
    String QUESTION_KEY = "query";

    /**
     * 输入参数历史记录的键。
     */
    String HISTORY_KEY = "history";

    /**
     * 类型列表的键。
     */
    String TYPE_LIST_KEY = "typeList";

    /**
     * 默认提示词文件名。
     */
    String BUILTIN_PROMPT = "/prompt.txt";
}