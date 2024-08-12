/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fitframework.annotation.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Aipp实例信息
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceDto {
    @Property(description = "Aipp实例ID")
    @JsonProperty("aipp_instance_id")
    private String aippInstanceId;

    @Property(description = "用户ID")
    @JsonProperty("tenant_id")
    private String tenantId;

    @Property(description = "aipp实例名称")
    @JsonProperty("aipp_instance_name")
    private String aippInstanceName;

    /** {@link com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum} **/
    @Property(description = "aipp实例当前的状态", example = "running,end ")
    @JsonProperty("status")
    private String status;

    @Property(description = "当前节点的表单元数据")
    @JsonProperty("form_metadata")
    private String formMetadata;

    @Property(description = "用于填充当前表单的数据")
    @JsonProperty("form_args")
    private Map<String, String> formArgs;

    @Property(description = "aipp实例开始执行时间")
    @JsonProperty("start_time")
    private String startTime;

    @Property(description = "aipp实例结束运行时间")
    @JsonProperty("end_time")
    private String endTime;

    @Property(description = "aipp实例运行记录")
    @JsonProperty("instance_log")
    private List<AippInstLog> aippInstanceLogs;
}
