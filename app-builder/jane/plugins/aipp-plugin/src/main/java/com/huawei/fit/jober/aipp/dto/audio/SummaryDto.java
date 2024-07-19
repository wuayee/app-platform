/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.audio;

import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegUtil;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.LLMUtils;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SummaryDto
 *
 * @author y00612997
 * @since 2024/1/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDto {
    private static final Random POS_RANDOM = new Random();
    private static final Logger log = Logger.get(SummaryDto.class);

    @Property(description = "视频摘要")
    private String summary;

    @Property(description = "分段总结")
    private List<SummarySection> sectionList;

    public SummaryDto(List<String> summaryList, int segmentSize) {
        this.sectionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(summaryList)) {
            for (int i = 0; i < summaryList.size(); ++i) {
                String item = summaryList.get(i);
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                try {
                    SummarySection section =
                            JsonUtils.parseObject(LLMUtils.tryFixLlmJsonString(item), SummarySection.class);
                    section.setPosition(FfmpegUtil.formatTimestamps(Math.max(
                            segmentSize * i - POS_RANDOM.nextInt(60) - 30, 0)));
                    sectionList.add(section);
                } catch (Exception e) {
                    log.warn("Llm generate unexpect rsp {}.", item, e);
                }
            }
        }
    }
}
