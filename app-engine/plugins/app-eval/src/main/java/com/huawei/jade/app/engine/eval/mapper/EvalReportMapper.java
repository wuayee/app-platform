/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import com.huawei.jade.app.engine.eval.po.EvalReportPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评估任务相关的db接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Mapper
public interface EvalReportMapper {
    /**
     * 插入一条评估报告。
     *
     * @param evalReportPO 评估报告实体类。
     */
    void insert(EvalReportPo evalReportPO);

    /**
     * 根据id获取一条评估报告。
     *
     * @param id 表示评估报告的id的 {@link Long}
     * @return 表示评估报告的 {@link EvalReportPo}
     */
    EvalReportPo getById(long id);

    /**
     * 通过实例id获取评估报告。
     *
     * @param instanceId 表示实例id的 {@link String}
     * @return 表示评估报告的 {@link EvalReportPo}
     */
    EvalReportPo getByInstanceId(String instanceId);

    /**
     * 通过任务id获取评估报告列表。
     *
     * @param evalTaskId 表示评估任务的id的 {@link Long}
     * @return 表示评估报告列表的 {@link List}{@code <}{@link EvalReportPo}{@code >}
     */
    List<EvalReportPo> getByEvalTaskId(long evalTaskId);

    /**
     * 通过id更新一条评估报告。
     *
     * @param evalReportPo 表示更新后评估报告实体类的 {@link EvalReportPo}
     */
    void updateById(EvalReportPo evalReportPo);

    /**
     * 设置报告中的任务完成信息。
     *
     * @param evalReportPo 表示包含任务完成信息的报告实体类的 {@link EvalReportPo}
     */
    void setFinishByInstanceId(EvalReportPo evalReportPo);
}
