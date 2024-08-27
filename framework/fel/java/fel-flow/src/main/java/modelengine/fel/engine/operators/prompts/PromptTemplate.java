/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.prompts;

import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.pattern.Pattern;

/**
 * 提示词模板接口。
 *
 * @param <T> 表示提示词模板入参的类型。
 * @author 刘信宏
 * @since 2024-04-12
 */
public interface PromptTemplate<T> extends Pattern<T, Prompt> {}
