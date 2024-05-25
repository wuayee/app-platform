/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.po;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aipp用户反馈信息持久化类
 *
 * @since 2024-5-24
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsrFeedbackPo {
    @Property(description = "usrfeedback id")
    private Long id;

    @Property(description = "log id")
    private Long logId;

    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "usr feedback")
    private Integer usrFeedback;

    @Property(description = "usr feedback text")
    private String usrFeedbackText;
}
