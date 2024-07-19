/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.announcement.mapper;

import com.huawei.jade.app.engine.announcement.po.AnnouncementInfoPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 公告信息相关的db接口。
 *
 * @author z00591349
 * @since 2024-06-18
 */
@Mapper
public interface AnnouncementInfoMapper {
    /**
     * 插入一个公告信息。
     *
     * @param announcementInfoPo 表示公告信息实体类的 {@link AnnouncementInfoPo}
     */
    void insert(AnnouncementInfoPo announcementInfoPo);

    /**
     * 获取公告信息列表
     *
     * @param currentTime 表示当前时间 {@link Date}。
     * @return 表示公告信息列表的 {@link List}{@code <}{@link AnnouncementInfoPo}{@code >}
     */
    List<AnnouncementInfoPo> getAnnouncementInfo(@Param("currentTime") Date currentTime);
}
