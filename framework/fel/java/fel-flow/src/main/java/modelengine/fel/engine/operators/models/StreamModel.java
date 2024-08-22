/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.models;

import modelengine.fit.waterflow.bridge.fitflow.FiniteEmitter;
import modelengine.fel.core.model.Model;

/**
 * 流式模型。
 *
 * @param <O> 表示对话模型的输出类型。
 * @author 刘信宏
 * @since 2024-04-16
 */
public interface StreamModel<I, O> extends Model<I, FiniteEmitter<O, ChatChunk>> {}
