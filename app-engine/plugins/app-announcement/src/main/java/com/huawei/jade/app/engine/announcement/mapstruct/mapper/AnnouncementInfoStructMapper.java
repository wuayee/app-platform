/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.announcement.mapstruct.mapper;

import com.huawei.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import com.huawei.jade.app.engine.announcement.po.AnnouncementInfoPo;
import com.huawei.jade.app.engine.announcement.vo.AnnouncementInfoDisplayVo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 公告信息相关类转换接口。
 *
 * @author 张圆
 * @since 2024-06-18
 */
@Mapper(uses = LocalDateTimeMapper.class)
public interface AnnouncementInfoStructMapper {
    /**
     * Mapper实例。
     */
    AnnouncementInfoStructMapper INSTANCE = Mappers.getMapper(AnnouncementInfoStructMapper.class);

    /**
     * 创建DTO 转 PO 接口。
     *
     * @param announcementInfoDto 表示创建公告信息DTO的 {@link AnnouncementInfoDto}
     * @return 表示公告信息PO的 {@link AnnouncementInfoPo}
     */
    @Mapping(target = "createTime", ignore = true)
    AnnouncementInfoPo dtoToPO(AnnouncementInfoDto announcementInfoDto);

    /**
     * 创建PO List 转 VO List 接口。
     *
     * @param announcementInfoPoList 表示公告信息PO的 {@link List}{@code <}{@link AnnouncementInfoPo}{@code >}
     * @return 表示转换后公告信息VO的 {@link List}{@code <}{@link AnnouncementInfoDisplayVo}{@code >}
     */
    List<AnnouncementInfoDisplayVo> poToVo(List<AnnouncementInfoPo> announcementInfoPoList);
}
