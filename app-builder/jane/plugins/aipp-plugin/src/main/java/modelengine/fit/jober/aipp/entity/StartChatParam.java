/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.entity;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;

import java.util.Map;

/**
 * 开始对话入参数据类
 *
 * @author 邬涨财
 * @since 2025-01-26
 */
public record StartChatParam(Map<String, Object> businessData, Meta meta, OperationContext context, Instance metaInst,
                             String flowDefinitionId, ChatSession<Object> chatSession) {}