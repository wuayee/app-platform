/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.util;

import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示重写插件的常量定义。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public interface Constant {
    /**
     * 输入参数用户问题的键。
     */
    String QUERY_KEY = "query";

    /**
     * 输入参数历史记录的键。
     */
    String HISTORY_KEY = "histories";

    /**
     * 字符串列表类型。
     */
    Type TYPE_LIST_STRING = TypeUtils.parameterized(List.class, new Type[] {String.class});

    /**
     * 默认提示词文件名。
     */
    String BUILTIN_PROMPT = "/prompt.txt";
}