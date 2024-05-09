/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.prompts;

import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.engine.operators.AiRunnable;

/**
 * 提示词模板接口。
 *
 * @param <T> 表示提示词模板入参的类型。
 * @author 刘信宏
 * @since 2024-04-12
 */
public interface PromptTemplate<T> extends AiRunnable<T, Prompt> {}
