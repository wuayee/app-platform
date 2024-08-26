/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务实例更新字段信息
 *
 * @author yangxiangyu
 * @since 2024/5/29
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskInstanceUpdateInfo {
    /**
     * 任务实例状态
     */
    private String status;
}
