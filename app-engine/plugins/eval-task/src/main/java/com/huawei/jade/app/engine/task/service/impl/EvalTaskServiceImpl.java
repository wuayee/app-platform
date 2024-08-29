/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import com.huawei.jade.app.engine.task.convertor.EvalTaskConvertor;
import com.huawei.jade.app.engine.task.dto.EvalTaskQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.task.po.EvalTaskPo;
import com.huawei.jade.app.engine.task.service.EvalTaskService;
import com.huawei.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 表示 {@link EvalTaskService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Component
public class EvalTaskServiceImpl implements EvalTaskService {
    private final EvalTaskMapper taskMapper;

    /**
     * 表示评估任务服务实现的构建器。
     *
     * @param taskMapper 表示评估任务服务持久层接口的 {@link EvalTaskMapper}。
     */
    public EvalTaskServiceImpl(EvalTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public void createEvalTask(EvalTaskEntity entity) {
        EvalTaskPo evalTaskPo = EvalTaskConvertor.INSTANCE.entityToPo(entity);
        this.taskMapper.create(evalTaskPo);
    }

    @Override
    public PageVo<EvalTaskEntity> listEvalTask(EvalTaskQueryParam queryParam) {
        List<EvalTaskEntity> evalDataset = this.taskMapper.listEvalTask(queryParam);
        int evalTaskCount = this.taskMapper.countEvalTask(queryParam);
        return PageVo.of(evalTaskCount, evalDataset);
    }
}