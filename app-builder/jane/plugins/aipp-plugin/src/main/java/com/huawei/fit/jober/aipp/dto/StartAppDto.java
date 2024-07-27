/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fitframework.annotation.Property;

import lombok.Data;

/**
 * 启动aipp请求
 *
 * @author l00611472
 * @since 2023-12-08
 */
@Data
public class StartAppDto {
    @Property(description = "实例名称", example = "看图说话")
    private String name;

    @Property(description = "Aipp的流程定义ID")
    private int aippMetaId;

    @Property(description = "表单数据")
    private String formData;
}
