/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.task.convertor.EvalTaskConvertor;
import com.huawei.jade.app.engine.task.dto.EvalTaskCreateDto;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.service.EvalTaskService;

/**
 * 表示评估任务管理接口集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Component
@RequestMapping(path = "/eval/task", group = "评估任务管理接口")
public class EvalTaskController {
    private final EvalTaskService evalTaskService;

    /**
     * 评估任务管理控制器构造函数。
     *
     * @param evalTaskService 表示评估任务服务的 {@link EvalTaskService}。
     */
    public EvalTaskController(EvalTaskService evalTaskService) {
        this.evalTaskService = evalTaskService;
    }

    /**
     * 创建评估任务。
     *
     * @param createDto 表示评估任务创建传输对象的 {@link EvalTaskCreateDto}。
     */
    @PostMapping(description = "创建评估任务")
    public void createEvalTask(@RequestBody @Validated EvalTaskCreateDto createDto) {
        EvalTaskEntity entity = EvalTaskConvertor.INSTANCE.convertDtoToEntity(createDto);
        this.evalTaskService.createEvalTask(entity);
    }
}
