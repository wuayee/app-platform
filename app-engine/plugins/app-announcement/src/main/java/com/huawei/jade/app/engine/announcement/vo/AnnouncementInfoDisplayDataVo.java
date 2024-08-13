/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.announcement.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 公告信息前端展示类。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementInfoDisplayDataVo {
    private Map<String, List<AnnouncementInfoDisplayVo>> announcementsByType;
    private LocalDateTime latestCreateTime;
}
