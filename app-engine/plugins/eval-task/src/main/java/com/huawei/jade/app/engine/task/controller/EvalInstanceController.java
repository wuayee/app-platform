/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.task.dto.EvalInstanceCreateDto;
import com.huawei.jade.app.engine.task.dto.EvalTaskCreateDto;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;

/**
 * 表示评估任务实例管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Component
@RequestMapping(path = "/eval/task/instance", group = "评估任务管理接口")
public class EvalInstanceController {
    private final EvalInstanceService evalInstanceService;

    /**
     * 评估任务实例管理控制器构造函数。
     *
     * @param evalInstanceService 表示评估任务实例服务的 {@link EvalInstanceService}
     */
    public EvalInstanceController(EvalInstanceService evalInstanceService) {
        this.evalInstanceService = evalInstanceService;
    }

    /**
     * 创建评估任务实例。
     *
     * @param createDto 表示评估任务实例创建传输对象的 {@link EvalTaskCreateDto}。
     */
    @PostMapping(description = "创建评估任务实例")
    public void createEvalInstance(@RequestBody @Validated EvalInstanceCreateDto createDto) {
        this.evalInstanceService.createEvalInstance(createDto.getTaskId());
    }
}
