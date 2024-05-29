/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ConversationRecordPo类消息处理策略
 *
 * @author c00819987
 * @since 2024/05/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRecordPo {
    private Long id;
    private String appId;
    private String question;
    private String answer;
    private String createUser;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private String instanceId;
}
