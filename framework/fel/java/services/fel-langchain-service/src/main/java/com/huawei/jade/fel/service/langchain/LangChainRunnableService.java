/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.service.langchain;

import modelengine.fitframework.annotation.Genericable;

/**
 * LangChain Runnable 算子服务。
 *
 * @author 刘信宏
 * @since 2024-06-11
 */
public interface LangChainRunnableService {
    /**
     * LangChain Runnable 算子服务阻塞同步调用接口。
     *
     * @param taskId 表示任务名称的 {@link String}。
     * @param fitableId 表示任务实例名称的 {@link String}。
     * @param input 表示输入数据的 {@link Object}。
     * @return 表示输出数据的 {@link Object}。
     */
    @Genericable(id = "com.huawei.jade.fel.service.langchain.runnable")
    Object invoke(String taskId, String fitableId, Object input);
}
