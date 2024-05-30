/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.genericable.AppFlowFinishObserver;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.app.engine.eval.algorithm.EvalAlgorithmArg;
import com.huawei.jade.app.engine.eval.mapper.EvalReportMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.eval.po.EvalReportPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolExecuteService;
import com.huawei.jade.carver.tool.service.ToolService;

import com.alibaba.fastjson2.JSON;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * app结束回调实现。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class AppFlowFinishObserverImpl implements AppFlowFinishObserver {
    @Fit
    private EvalReportMapper evalReportMapper;

    @Fit
    private EvalTaskMapper evalTaskMapper;

    @Fit
    private ToolService toolService;

    @Fit
    private ToolExecuteService toolExecuteService;

    /**
     * app调用结束回调接口。
     *
     * @param data       表示流程结束的数据的 {@link String}。
     * @param attributes 表示流程的属性 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Override
    @Fitable(id = "eval.onfinished")
    public void onFinished(String data, Map<String, Object> attributes) {
        Object obj = attributes.get(AippConst.BS_AIPP_INST_ID_KEY);
        String aippInstance = (obj instanceof String) ? (String) obj : null;
        if (aippInstance == null) {
            return;
        }
        EvalReportPo report = evalReportMapper.getByInstanceId(aippInstance);
        if (report == null) {
            return;
        }
        EvalTaskPo task = evalTaskMapper.getById(report.getEvalTaskId());
        double score = executeEval(task.getEvalAlgorithmId(), report.getExpectedOutput(), data);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        EvalReportPo po =
                EvalReportPo.builder().endTime(now).score(score).output(data).instanceId(aippInstance).build();
        evalReportMapper.setFinishByInstanceId(po);
    }

    /**
     * 执行评估算法。
     *
     * @param id 表示评估算法id(唯一命名)的 {@link String}
     * @param gt 表示参照标准内容的 {@link String}
     * @param gm 表示应用生成内容的 {@link String}
     * @return 表示评估得分的 {@link Double}
     */
    private double executeEval(String id, String gt, String gm) {
        ToolData alg = toolService.getTool(id);
        if (alg == null) {
            return 0;
        }

        return Double.parseDouble(toolExecuteService.executeTool(id, JSON.toJSONString(new EvalAlgorithmArg(gt, gm))));
    }
}
