/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * aipp instance展示类.
 *
 * @author z00559346
 * @since 2024-05-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceVO {
    private List<String> ancestors;
    private String aippInstanceId;
    private String tenantId;
    private String aippInstanceName;
    private String status;
    private String formMetadata;
    private Map<String, String> formArgs;
    private String startTime;
    private String endTime;
    private List<AippLogVO> aippInstanceLogs;
}