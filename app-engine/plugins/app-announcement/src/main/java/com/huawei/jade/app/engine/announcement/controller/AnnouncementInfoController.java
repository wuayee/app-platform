/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.announcement.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import com.huawei.jade.app.engine.announcement.service.AnnouncementInfoService;
import com.huawei.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;

/**
 * 公告信息相关操作接口。
 *
 * @author z00591349
 * @since 2024-06-18
 */
@Component
@RequestMapping(path = "/announcement", group = "公告信息相关操作接口")
public class AnnouncementInfoController {
    private final AnnouncementInfoService announcementInfoService;

    public AnnouncementInfoController(AnnouncementInfoService announcementInfoService) {
        this.announcementInfoService = announcementInfoService;
    }

    /**
     * 获取公告信息列表
     *
     * @return 表示公告信息内容的 {@link AnnouncementInfoDisplayDataVo}
     */
    @GetMapping(description = "获取公告信息列表")
    public AnnouncementInfoDisplayDataVo queryAnnouncements() {
        return announcementInfoService.queryAnnouncements();
    }

    /**
     * 创建公告接口。
     *
     * @param announcementInfoDto 表示创建公告信息的参数的 {@link AnnouncementInfoDto}
     * @return 公告id {@link Long}
     */
    @PostMapping(description = "创建公告信息")
    public Long addAnnouncement(@RequestBody AnnouncementInfoDto announcementInfoDto) {
        return announcementInfoService.addAnnouncement(announcementInfoDto);
    }
}
