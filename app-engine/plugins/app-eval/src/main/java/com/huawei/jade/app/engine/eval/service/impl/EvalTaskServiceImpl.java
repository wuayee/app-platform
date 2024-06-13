/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.utils.SleepUtil;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.genericable.AippRunTimeService;
import com.huawei.fit.jober.aipp.genericable.AppBuilderAppService;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Value;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalReportMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalReportTraceMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalTaskDatasetMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalReportStructMapper;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalReportTraceStructMapper;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalTaskStructMapper;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.po.EvalReportPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskDatasetPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskStatus;
import com.huawei.jade.app.engine.eval.query.EvalTaskListQuery;
import com.huawei.jade.app.engine.eval.service.EvalTaskService;
import com.huawei.jade.app.engine.eval.vo.EvalAlgorithmVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportSummaryVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportTraceVo;
import com.huawei.jade.app.engine.eval.vo.EvalReportVo;
import com.huawei.jade.app.engine.eval.vo.EvalTaskVo;
import com.huawei.jade.app.engine.eval.vo.Page;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;

import org.apache.ibatis.session.RowBounds;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 评估任务服务的实现。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class EvalTaskServiceImpl implements EvalTaskService {
    @Fit
    private EvalTaskMapper evalTaskMapper;

    @Fit
    private EvalDatasetMapper evalDatasetMapper;

    @Fit
    private EvalDataMapper evalDataMapper;

    @Fit
    private EvalReportMapper evalReportMapper;

    @Fit
    private EvalReportTraceMapper evalReportTraceMapper;

    @Fit
    private EvalTaskDatasetMapper evalTaskDatasetMapper;

    @Fit
    private AppBuilderAppService appBuilderAppService;

    @Fit
    private AippRunTimeService aippRunTimeService;

    @Fit
    private ToolService toolService;

    private ExecutorService threadPool;

    private final String tenantId;

    /**
     * 构造函数。
     *
     * @param taskNum 表示评估任务线程池的大小的 {@link Integer}
     * @param tenantId 表示租户id的 {@link String}
     */
    public EvalTaskServiceImpl(
            @Value("${eval.task-thread-num}") int taskNum, @Value("${eval.tenant-id}") String tenantId) {
        this.threadPool = Executors.newFixedThreadPool(taskNum);
        this.tenantId = tenantId;
    }

    /**
     * 创建并运行一个app实例
     *
     * @param task 表示评估任务信息的 {@link EvalTaskPo}
     * @param data 表示本次调用使用的评估数据的 {@link EvalDataPo}
     * @return 表示创建的app实例id的 {@link String}
     */
    private String runApp(EvalTaskPo task, EvalDataPo data) {
        AppBuilderAppDto appDto = appBuilderAppService.query(task.getAppId());
        OperationContext context = new OperationContext();
        context.setTenantId(this.tenantId);
        context.setW3Account(task.getAuthor());
        context.setOperator(task.getAuthor());
        AippCreate create = appBuilderAppService.debug(appDto, context);

        Map<String, Object> initContext = new HashMap<>();
        Map<String, Object> cont = new HashMap<>();
        cont.put(AippConst.BS_AIPP_QUESTION_KEY, data.getInput());
        cont.put(AippConst.IS_EVAL_INVOCATION, true);
        initContext.put(AippConst.BS_INIT_CONTEXT_KEY, cont);

        String aippInstance =
                aippRunTimeService.createAippInstance(create.getAippId(), create.getVersion(), initContext, context);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        EvalReportPo report =
                EvalReportPo.builder()
                        .instanceId(aippInstance)
                        .evalTaskId(task.getId())
                        .input(data.getInput())
                        .expectedOutput(data.getOutput())
                        .startTime(now)
                        .build();
        evalReportMapper.insert(report);
        return aippInstance;
    }

    /**
     * 评估任务线程函数
     *
     * @param evalTaskPo 表示提供评估任务信息的 {@link EvalTaskPo}
     * @param datasetIds 本表示次评估任务涉及到的数据集id列表的 {@link List}{@code <}{@link Long}{@code >}
     */
    private void execEval(EvalTaskPo evalTaskPo, List<Long> datasetIds) {
        // 设置启动信息 包括 启动时间、状态
        evalTaskPo.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        evalTaskPo.setStatus(EvalTaskStatus.IN_PROGRESS.getCode());
        evalTaskMapper.setStartById(evalTaskPo);

        // 每条数据调用一次app处理, app运行是异步接口
        List<String> instanceList = new ArrayList<>();
        for (Long datasetId : datasetIds) {
            EvalDatasetPo dataset = evalDatasetMapper.getById(datasetId);
            List<EvalDataPo> dataList = evalDataMapper.getByDatasetId(dataset.getId());
            for (EvalDataPo data : dataList) {
                instanceList.add(runApp(evalTaskPo, data));
            }
        }

        int passNum = 0;
        int failureNum = 0;
        double passScore = evalTaskPo.getPassScore();

        // 轮询每个app调用, 全部调用结束后任务结束
        for (String instanceId : instanceList) {
            EvalReportPo report = evalReportMapper.getByInstanceId(instanceId);
            while ((report == null) || (report.getEndTime() == null)) {
                SleepUtil.sleep(1000);
                report = evalReportMapper.getByInstanceId(instanceId);
            }

            if (report.getScore() >= passScore) {
                passNum++;
            } else {
                failureNum++;
            }
        }

        // 设置结束信息包括 状态、通过率、结束时间
        evalTaskPo.setStatus(EvalTaskStatus.FINISH.getCode());
        evalTaskPo.setPassRate((double) passNum / (passNum + failureNum));
        evalTaskPo.setFinishTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        evalTaskMapper.setFinishById(evalTaskPo);
    }

    /**
     * 创建一个评估任务。
     *
     * @param evalTaskPo 表示评估任务参数的 {@link EvalTaskPo}
     * @param datasetIds 表示评估任务涉及到数据集id列表的 {@link List}{@code <}{@link Long}{@code >}
     */
    @Override
    public void createEvalTask(EvalTaskPo evalTaskPo, List<Long> datasetIds) {
        evalTaskMapper.insert(evalTaskPo);
        List<EvalTaskDatasetPo> taskDatasetPoList = new ArrayList<>();
        for (Long datasetId : datasetIds) {
            taskDatasetPoList.add(new EvalTaskDatasetPo(evalTaskPo.getId(), datasetId));
        }

        evalTaskDatasetMapper.insertAll(taskDatasetPoList);
        threadPool.execute(() -> execEval(evalTaskPo, datasetIds));
    }

    /**
     * 复制一个评估任务的参数，并创建以此创建一个新的任务。
     *
     * @param taskId 表示被复制任务id的 {@link Long}
     * @param author 表示新任务的创建者的 {@link String}
     */
    @Override
    public void copyEvalTask(long taskId, String author) {
        EvalTaskPo evalTaskPo = getEvalTaskById(taskId);
        evalTaskPo.setAuthor(author);
        evalTaskPo.setStatus(EvalTaskStatus.NOT_START.getCode());
        evalTaskPo.setPassRate(0);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalTaskPo.setCreateTime(now);

        List<Long> datasetIds =
                evalTaskDatasetMapper.getByEvalTaskId(taskId).stream()
                        .map(EvalTaskDatasetPo::getEvalDatasetId)
                        .collect(Collectors.toList());

        createEvalTask(evalTaskPo, datasetIds);
    }

    /**
     * 根据id获取评估任务。
     *
     * @param id 表示评估任务id的 {@link Long}
     * @return 表示评估任务的 {@link EvalTaskPo}
     */
    @Override
    public EvalTaskPo getEvalTaskById(long id) {
        return evalTaskMapper.getById(id);
    }

    /**
     * 根据条件获取评估任务列表。
     *
     * @param query 表示筛选条件的 {@link EvalTaskListQuery}
     * @return 表示评估任务列表的 {@link Page}{@code <}{@link EvalTaskVo}{@code >}
     */
    @Override
    public Page<EvalTaskVo> getEvalTaskList(EvalTaskListQuery query) {
        RowBounds rowBounds = new RowBounds((query.getPageIndex() - 1) * query.getPageSize(), query.getPageSize());
        List<EvalTaskVo> taskList =
                evalTaskMapper.getByConditions(query, rowBounds).stream()
                        .map(
                                t -> {
                                    EvalTaskVo vo = EvalTaskStructMapper.INSTANCE.poToVO(t);
                                    List<EvalDatasetPo> datasetPOList = getDatasetListByEvalTaskId(vo.getId());
                                    for (EvalDatasetPo po : datasetPOList) {
                                        vo.getDatasets().add(po.getDatasetName() + "(id = " + po.getId() + ")");
                                    }
                                    return vo;
                                })
                        .collect(Collectors.toList());

        Page<EvalTaskVo> page = new Page<>();
        page.setPageIndex(query.getPageIndex());
        page.setPageSize(query.getPageSize());
        page.setTotal(evalTaskMapper.getCountByConditions(query));
        page.setData(taskList);

        return page;
    }

    /**
     * 根据任务id获取涉及的数据集列表。
     *
     * @param evalTaskId 表示任务id的 {@link Long}
     * @return 表示数据集列表的 {@link List}{@code <}{@link EvalDatasetPo}{@code >}
     */
    @Override
    public List<EvalDatasetPo> getDatasetListByEvalTaskId(long evalTaskId) {
        List<EvalTaskDatasetPo> poList = evalTaskDatasetMapper.getByEvalTaskId(evalTaskId);
        return poList.stream().map(po -> evalDatasetMapper.getById(po.getEvalDatasetId())).collect(Collectors.toList());
    }

    /**
     * 根据任务id获取评估报告。
     *
     * @param evalTaskId 表示评估任务id的 {@link Long}
     * @return 表示评估任务报告列表的 {@link List}{@code <}{@link EvalReportPo}{@code >}
     */
    @Override
    public List<EvalReportPo> getEvalReportByTaskId(long evalTaskId) {
        return evalReportMapper.getByEvalTaskId(evalTaskId);
    }

    /**
     * 生成评估任务报告摘要。
     *
     * @param evalTaskId 表示评估任务id的 {@link Long}
     * @return 表示报告摘要的 {@link EvalReportSummaryVo}
     */
    @Override
    public EvalReportSummaryVo generateReportSummary(long evalTaskId) {
        EvalTaskPo evalTaskPo = getEvalTaskById(evalTaskId);
        List<EvalReportPo> reportPoList = getEvalReportByTaskId(evalTaskId);
        EvalReportSummaryVo evalReportSummaryVO = new EvalReportSummaryVo();
        int passNum = 0;
        int failureNum = 0;
        for (EvalReportPo po : reportPoList) {
            EvalReportVo report = EvalReportVo.builder().id(po.getId()).input(po.getInput()).build();
            double score = po.getScore();
            if (score >= evalTaskPo.getPassScore()) {
                passNum++;
                evalReportSummaryVO.getPassInput().add(report);
            } else {
                failureNum++;
                evalReportSummaryVO.getFailureInput().add(report);
            }
        }

        evalReportSummaryVO.setAlgorithm(getEvalAlgorithmName(evalTaskPo.getEvalAlgorithmId()));
        evalReportSummaryVO.setPassNum(passNum);
        evalReportSummaryVO.setFailureNum(failureNum);
        evalReportSummaryVO.setPassScore(evalTaskPo.getPassScore());
        return evalReportSummaryVO;
    }

    /**
     * 生成评估报告。
     *
     * @param reportId 表示报告id的 {@link Long}
     * @return 表示评估报告的 {@link EvalReportVo}
     */
    @Override
    public EvalReportVo generateReport(long reportId) {
        EvalReportPo evalReportPo = evalReportMapper.getById(reportId);
        EvalReportVo evalReportVO = EvalReportStructMapper.INSTANCE.poToVO(evalReportPo);
        List<EvalReportTraceVo> trace =
                evalReportTraceMapper.getByEvalInstanceId(evalReportPo.getInstanceId()).stream()
                        .map(EvalReportTraceStructMapper.INSTANCE::poToVO)
                        .collect(Collectors.toList());

        if (evalReportPo.getStartTime() != null && evalReportPo.getEndTime() != null) {
            evalReportVO.setLatency(
                    Duration.between(evalReportPo.getStartTime(), evalReportPo.getEndTime()).toMillis());
        }

        evalReportVO.setTrace(trace);
        return evalReportVO;
    }

    /**
     * 获取可用的评估算法列表。
     *
     * @return 表示评估算法列表的 {@link List}{@code <}{@link EvalAlgorithmVo}{@code >}
     */
    @Override
    public List<EvalAlgorithmVo> getEvalAlgorithmList() {
        Set<String> tags = new HashSet<>();
        tags.add("CARVER-EVAL");
        ToolQuery tagQuery = new ToolQuery();
        tagQuery.setIncludeTags(tags);
        List<ToolData> algs = toolService.getTools(tagQuery).getData();
        return algs.stream().map(a -> new EvalAlgorithmVo(a.getName(), a.getUniqueName())).collect(Collectors.toList());
    }

    /**
     * 根据评估算法的id(唯一命名)获取对外展示评估算法名。
     *
     * @param id 表示唯一命名的 {@link String}
     * @return 表示对外展示评估算法名的 {@link  String}
     */
    private String getEvalAlgorithmName(String id) {
        ToolData alg = toolService.getTool(id);
        if (alg == null) {
            return "";
        }
        return alg.getName();
    }
}
