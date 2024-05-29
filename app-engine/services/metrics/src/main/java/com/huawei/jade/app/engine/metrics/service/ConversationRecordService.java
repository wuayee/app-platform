/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service;

import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;

/**
 * ConversationRecordService类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/28
 */
public interface ConversationRecordService {
    /**
     * insert into conversation_record
     *
     * @param conversationRecordPo conversation_record字段
     */
    void insertConversationRecord(ConversationRecordPo conversationRecordPo);
}
