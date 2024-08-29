/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import com.huawei.jade.app.engine.task.dto.EvalInstanceQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.app.engine.task.po.EvalInstancePo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务实例持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Mapper
public interface EvalInstanceMapper {
    /**
     * 创建评估任务实例。
     *
     * @param po 表示评估任务实例的 {@link EvalInstancePo}。
     */
    void create(EvalInstancePo po);

    /**
     * 查询评估任务实例。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link List}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    List<EvalInstanceEntity> listEvalInstance(EvalInstanceQueryParam queryParam);

    /**
     * 统计评估任务实例数量。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例统计结果的 {@code int}。
     */
    int countEvalInstance(EvalInstanceQueryParam queryParam);
}