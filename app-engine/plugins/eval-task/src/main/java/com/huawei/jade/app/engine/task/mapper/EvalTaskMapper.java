/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.dto.EvalTaskQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.po.EvalTaskPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Mapper
public interface EvalTaskMapper {
    /**
     * 创建评估任务。
     *
     * @param evalTaskPo 表示评估任务信息的 {@link EvalTaskPo}。
     */
    void create(EvalTaskPo evalTaskPo);

    /**
     * 分页查询评估任务元数据。
     *
     * @param queryParam 表示评估任务查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务元数据查询结果的 {@link List}{@code <}{@link EvalTaskEntity}{@code >}。
     */
    List<EvalTaskEntity> listEvalTask(EvalTaskQueryParam queryParam);

    /**
     * 统计评估任务数量。
     *
     * @param queryParam 表示评估任务查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务统计结果的 {@code int}。
     */
    int countEvalTask(EvalTaskQueryParam queryParam);
}
