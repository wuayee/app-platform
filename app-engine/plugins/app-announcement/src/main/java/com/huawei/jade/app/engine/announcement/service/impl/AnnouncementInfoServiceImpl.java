/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.announcement.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.app.engine.announcement.dto.AnnouncementInfoDto;
import com.huawei.jade.app.engine.announcement.mapper.AnnouncementInfoMapper;
import com.huawei.jade.app.engine.announcement.mapstruct.mapper.AnnouncementInfoStructMapper;
import com.huawei.jade.app.engine.announcement.po.AnnouncementInfoPo;
import com.huawei.jade.app.engine.announcement.service.AnnouncementInfoService;
import com.huawei.jade.app.engine.announcement.vo.AnnouncementInfoDisplayDataVo;
import com.huawei.jade.app.engine.announcement.vo.AnnouncementInfoDisplayVo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告信息服务实现。
 *
 * @author z00591349
 * @since 2024-06-18
 */
@Component
public class AnnouncementInfoServiceImpl implements AnnouncementInfoService {
    private final AnnouncementInfoMapper announcementInfoMapper;

    public AnnouncementInfoServiceImpl(AnnouncementInfoMapper announcementInfoMapper) {
        this.announcementInfoMapper = announcementInfoMapper;
    }

    @Override
    public AnnouncementInfoDisplayDataVo queryAnnouncements() {
        List<AnnouncementInfoPo> announcementInfoPoList = announcementInfoMapper.getAnnouncementInfo();
        if (announcementInfoPoList.isEmpty()) {
            return null;
        }

        List<AnnouncementInfoDisplayVo> announcementInfoDisplayVoList =
                AnnouncementInfoStructMapper.INSTANCE.poToVo(announcementInfoPoList);
        AnnouncementInfoPo latestAnnouncement =
                announcementInfoPoList.get(announcementInfoPoList.size() - 1);
        LocalDateTime latestCreateTime = latestAnnouncement.getCreateTime();
        Map<String, List<AnnouncementInfoDisplayVo>> groupedByType =
                announcementInfoDisplayVoList.stream()
                        .collect(Collectors.groupingBy(AnnouncementInfoDisplayVo::getAnnouncementType));

        AnnouncementInfoDisplayDataVo data = new AnnouncementInfoDisplayDataVo();
        data.setAnnouncementsByType(groupedByType);
        data.setLatestCreateTime(latestCreateTime);

        return data;
    }

    @Override
    public Long addAnnouncement(AnnouncementInfoDto announcementDto) {
        AnnouncementInfoPo announcementInfoPo = AnnouncementInfoStructMapper.INSTANCE.dtoToPO(announcementDto);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        announcementInfoPo.setCreateTime(now);
        announcementInfoMapper.insert(announcementInfoPo);
        return announcementInfoPo.getId();
    }
}
