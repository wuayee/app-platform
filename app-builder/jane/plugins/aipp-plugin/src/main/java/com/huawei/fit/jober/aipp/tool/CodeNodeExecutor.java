/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * Code节点功能的接口
 *
 * @author 方誉州
 * @since 2024-06-21
 */
public interface CodeNodeExecutor {
    /**
     * Code节点执行用户定义的代码
     *
     * @param args 表示code节点引用的其他节点的参数的{@link Map}
     * @param code 表示用户定义的代码的{@link String}
     * @param language 表示用户代码的语言的{@link String}
     * @return 返回后端沙盒环境代码执行的结果
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.tool.execute.code")
    Object executeNodeCode(Map<String, Object> args, String code, String language);
}
