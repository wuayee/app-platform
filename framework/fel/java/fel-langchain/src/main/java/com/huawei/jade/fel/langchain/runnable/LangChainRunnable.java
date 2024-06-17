/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.langchain.runnable;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

/**
 * LangChain 通用 Runnable 算子代理。
 *
 * @author 刘信宏
 * @since 2024-06-06
 */
public class LangChainRunnable implements Pattern<Object, Object> {
    private final LangChainRunnableService runnableService;
    private final String taskId;
    private final String fitableId;

    /**
     * 使用数据处理器初始化 {@link LangChainRunnable}。
     *
     * @param runnableService 表示 LangChain 算子代理服务的 {@link LangChainRunnableService}。
     * @param taskId 表示任务名称的 {@link String}。
     * @param fitableId 表示实例名称的 {@link String}。
     * @throws IllegalArgumentException
     * <ul>
     *     <li>当 {@code runnableService} 为 {@code null} 时。</li>
     *     <li>当 {@code taskId} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     *     <li>当 {@code fitableId} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ul>
     */
    public LangChainRunnable(LangChainRunnableService runnableService, String taskId, String fitableId) {
        this.runnableService = Validation.notNull(runnableService, "The runnable service cannot be null.");
        this.taskId = Validation.notBlank(taskId, "The task id cannot be null.");
        this.fitableId = Validation.notBlank(fitableId, "The fitable id cannot be null.");
    }

    @Override
    public Object invoke(Object input) {
        Validation.notNull(input, "The input data cannot be null.");
        return this.runnableService.invoke(this.taskId, this.fitableId, input);
    }
}
