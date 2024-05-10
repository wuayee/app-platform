/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 定时任务数据源ORM数据对象。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-14
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskSourceScheduleObject {
    private String id;

    private String fitableId;

    private Integer interval;

    private String filter;
}
