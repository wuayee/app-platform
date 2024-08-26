/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import com.huawei.jade.app.engine.metrics.po.ConversationRecordPo;
import com.huawei.jade.app.engine.metrics.service.ConversationRecordService;

/**
 * 历史对话服务的实现类。
 *
 * @author 董春寅
 * @since 2024-05-29
 */
@Component
public class ConversationRecordServiceImpl implements ConversationRecordService {
    private final ConversationRecordMapper conversationRecordMapper;

    public ConversationRecordServiceImpl(@Fit ConversationRecordMapper conversationRecordMapper) {
        this.conversationRecordMapper = conversationRecordMapper;
    }

    /**
     * 插入一条对话记录。
     *
     * @param conversationRecordPo 表示历史对话实体类的 {@link ConversationRecordPo}
     */
    @Override
    public void insertConversationRecord(ConversationRecordPo conversationRecordPo) {
        conversationRecordMapper.insertConversationRecord(conversationRecordPo);
    }
}
