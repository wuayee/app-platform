/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.po.EvalReportPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.query.EvalTaskListQuery;
import com.huawei.jade.app.engine.eval.vo.EvalAlgorithmVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportSummaryVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportVo;
import com.huawei.jade.app.engine.eval.vo.EvalTaskVo;
import com.huawei.jade.app.engine.eval.vo.Page;

import java.util.List;

/**
 * 评估任务相关服务。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
public interface EvalTaskService {
    /**
     * 创建一个评估任务。
     *
     * @param evalTaskPo 表示评估任务参数的 {@link EvalTaskPo}
     * @param datasetIds 表示评估任务涉及到数据集id列表的 {@link List}{@code <}{@link Long}{@code >}
     */
    void createEvalTask(EvalTaskPo evalTaskPo, List<Long> datasetIds);

    /**
     * 复制一个评估任务的参数，并创建以此创建一个新的任务。
     *
     * @param taskId 表示被复制任务id的 {@link Long}
     * @param author 表示新任务的创建者的 {@link String}
     */
    void copyEvalTask(long taskId, String author);

    /**
     * 根据id获取评估任务。
     *
     * @param id 表示评估任务id的 {@link Long}
     * @return 表示评估任务的 {@link EvalTaskPo}
     */
    EvalTaskPo getEvalTaskById(long id);

    /**
     * 根据条件获取评估任务列表。
     *
     * @param evalTaskListQuery 表示筛选条件的 {@link EvalTaskListQuery}
     * @return 表示评估任务列表的 {@link Page}{@code <}{@link EvalTaskVo}{@code >}
     */
    Page<EvalTaskVo> getEvalTaskList(EvalTaskListQuery evalTaskListQuery);

    /**
     * 根据任务id获取涉及的数据集列表。
     *
     * @param evalTaskId 表示任务id的 {@link Long}
     * @return 表示数据集列表的 {@link List}{@code <}{@link EvalDatasetPo}{@code >}
     */
    List<EvalDatasetPo> getDatasetListByEvalTaskId(long evalTaskId);

    /**
     * 根据任务id获取评估报告。
     *
     * @param evalTaskId 表示评估任务id的 {@link Long}
     * @return 表示评估任务报告列表的 {@link List}{@code <}{@link EvalReportPo}{@code >}
     */
    List<EvalReportPo> getEvalReportByTaskId(long evalTaskId);

    /**
     * 生成评估任务报告摘要。
     *
     * @param evalTaskId 表示评估任务id的 {@link Long}
     * @return 表示报告摘要的 {@link EvalReportSummaryVo}
     */
    EvalReportSummaryVo generateReportSummary(long evalTaskId);

    /**
     * 生成评估报告。
     *
     * @param reportId 表示报告id的 {@link Long}
     * @return 表示评估报告的 {@link EvalReportVo}
     */
    EvalReportVo generateReport(long reportId);

    /**
     * 获取可用的评估算法列表。
     *
     * @return 表示评估算法列表的 {@link List}{@code <}{@link EvalAlgorithmVo}{@code >}
     */
    List<EvalAlgorithmVo> getEvalAlgorithmList();
}
