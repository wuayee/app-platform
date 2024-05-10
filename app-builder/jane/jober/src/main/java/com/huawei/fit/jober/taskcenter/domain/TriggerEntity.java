/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示任务属性触发器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerEntity {
    private String id;

    private String propertyId;

    private String fitableId;
}
