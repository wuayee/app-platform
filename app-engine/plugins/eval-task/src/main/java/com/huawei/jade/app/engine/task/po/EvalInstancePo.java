/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.po;

import com.huawei.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务实例 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Data
public class EvalInstancePo extends BasePo {
    /**
     * 评估任务实例唯一标识。
     */
    private Long taskId;
}
