/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;
import com.huawei.jade.app.engine.task.dto.EvalInstanceCreateDto;
import com.huawei.jade.app.engine.task.dto.EvalInstanceQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;
import com.huawei.jade.common.vo.PageVo;

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
     * @param createDto 表示评估任务实例创建传输对象的 {@link EvalInstanceCreateDto}。
     */
    @PostMapping(description = "创建评估任务实例")
    public void createEvalInstance(@RequestBody @Validated EvalInstanceCreateDto createDto) {
        this.evalInstanceService.createEvalInstance(createDto.getTaskId());
    }

    /**
     * 分页查询评估任务实例。
     *
     * @param queryParam 表示分页查询评估任务实例参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link PageVo}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    @GetMapping(description = "分页查询评估任务实例")
    public PageVo<EvalInstanceEntity> queryEvalInstance(@RequestBean @Validated EvalInstanceQueryParam queryParam) {
        return this.evalInstanceService.listEvalInstance(queryParam);
    }
}
