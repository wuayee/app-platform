/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity;

import com.huawei.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * aipp实例历史记录
 *
 * @author l00611472
 * @since 2024-01-08
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippInstLog {
    @Property(description = "创建时间")
    LocalDateTime createAt;

    @Property(description = "创建用户W3账号")
    String createUserAccount;

    @Property(description = "path")
    String path;

    /**
     * 预留字段, 暂时为null
     */
    @Property(description = "预留字段")
    String reserve;

    @Property(description = "log id")
    private Long logId;

    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp version")
    private String version;

    /**
     * {@link com.huawei.fit.jober.aipp.enums.AippTypeEnum}字面值
     */
    @Property(description = "aipp type(NORMAL/PREVIEW)")
    private String aippType;

    @Property(description = "aipp实例id")
    private String instanceId;

    @Property(description = "aipp实例历史数据")
    private String logData;

    @Property(description = "历史数据类型 {@link AippInstLogType}")
    private String logType;

}
