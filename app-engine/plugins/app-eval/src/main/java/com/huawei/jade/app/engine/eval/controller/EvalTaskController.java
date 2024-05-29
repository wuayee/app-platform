/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.eval.dto.EvalTaskDto;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalTaskStructMapper;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.query.EvalTaskListQuery;
import com.huawei.jade.app.engine.eval.service.EvalTaskService;
import com.huawei.jade.app.engine.eval.vo.EvalAlgorithmVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportSummaryVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportVo;
import com.huawei.jade.app.engine.eval.vo.EvalTaskVo;
import com.huawei.jade.app.engine.eval.vo.Page;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 评估任务相关接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
@RequestMapping(path = "/evalTask", group = "评估任务相关接口")
public class EvalTaskController {
    @Fit
    private EvalTaskService evalTaskService;

    /**
     * 创建评估任务接口。
     *
     * @param evalTaskDTO 表示创建任务的数据的 {@link EvalTaskDto}
     */
    @PostMapping(description = "创建评估任务接口")
    public void createEvalTask(@RequestBody EvalTaskDto evalTaskDTO) {
        EvalTaskPo evalTaskPO = EvalTaskStructMapper.INSTANCE.dtoToPO(evalTaskDTO);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalTaskPO.setCreateTime(now);
        evalTaskService.createEvalTask(evalTaskPO, evalTaskDTO.getDatasetIds());
    }

    /**
     * 复制任务接口。
     *
     * @param id 表示复制的任务的id的 {@link Long}
     * @param author 表示创建人的 {@link String}
     */
    @PostMapping(path = "/copy", description = "复制任务接口")
    public void copyEvalTask(@RequestParam("id") long id, @RequestParam("author") String author) {
        evalTaskService.copyEvalTask(id, author);
    }

    /**
     * 获取任务列表。
     *
     * @param evalTaskListQuery 表示查询数据的 {@link EvalTaskListQuery}
     * @return 表示任务列表的 {@link List}{@code <}{@link EvalTaskVo}{@code >}
     */
    @PostMapping(path = "/list", description = "获取评估任务列表")
    public Page<EvalTaskVo> getEvalTaskList(@RequestBody EvalTaskListQuery evalTaskListQuery) {
        return evalTaskService.getEvalTaskList(evalTaskListQuery);
    }

    /**
     * 获取评估报告概要信息。
     *
     * @param id 表示任务id的 {@link Long}
     * @return 表示任务报告概要信息的 {@link EvalReportSummaryVo}
     */
    @GetMapping(path = "/reportSummary", description = "获取评估报告概要信息")
    public EvalReportSummaryVo getEvalReportSummary(@RequestParam("evalTaskId") long id) {
        return evalTaskService.generateReportSummary(id);
    }

    /**
     * 获取任务报告。
     *
     * @param reportId 表示任务id的 {@link Long}
     * @return 表示任务报告的 {@link EvalReportVo}
     */
    @GetMapping(path = "/report", description = "获取评估报告内容")
    public EvalReportVo getEvalReport(@RequestParam("reportId") long reportId) {
        return evalTaskService.generateReport(reportId);
    }

    /**
     * 获取评估算法列表。
     *
     * @return 表示评估算法列表的 {@link List}{@code <}{@link EvalAlgorithmVo}{@code >}
     */
    @GetMapping(path = "/evalAlgorithmList", description = "获取评估算法列表")
    public List<EvalAlgorithmVo> getEvalAlgorithmList() {
        return evalTaskService.getEvalAlgorithmList();
    }
}
