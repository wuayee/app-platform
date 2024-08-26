/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static com.huawei.fit.jober.taskcenter.controller.Views.filterOfInstances;

import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;

import lombok.Data;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 为数据统计提供 REST 风格 API。
 *
 * @author 陈镕希
 * @since 2024-01-08
 */
@Component("JaneStatisticsController")
@RequestMapping(value = "/v1/statistics", group = "数据统计接口")
public class StatisticsController extends AbstractController {
    private static final String QUERY_TASK_SQL =
            "SELECT c.name as tenant_name, b.name, count(1) FROM task_instance_wide a INNER JOIN task b on a.task_id "
                    + "= b.id INNER JOIN tenant c on b.tenant_id = c.id GROUP BY c.name, b.name ORDER BY c.name";

    private static final String QUERY_APPROVAL_TASK_COUNT_SQL =
            "SELECT count(1) FROM task WHERE task.attributes->>'taskType'='approvalTask'";

    private static final String QUERY_TASK_INSTANCE_COUNT_GROUP_BY_TAG_SQL_PREFIX =
            "SELECT t.name, count(1) FROM task_instance_wide a INNER JOIN tag_usage tu on tu.object_id = a.id INNER "
                    + "JOIN tag t on t.id = tu.tag_id WHERE ";

    private static final String QUERY_TASK_INSTANCE_COUNT_WITHOUT_TAG_SQL_PREFIX =
            " as column, COUNT(1) FROM task_instance_wide a LEFT JOIN tag_usage b ON a.id = b.object_id WHERE a"
                    + ".task_id = ? AND b.object_id IS NULL GROUP BY a.";

    private static final String QUERY_WIDE_COLUMN_FROM_PROPERTY_SQL =
            "SELECT data_type, \"sequence\" from task_property WHERE task_id = ? AND name = ?";

    private static final String QUERY_TASK_INSTANCE_COUNT_GROUP_BY_SPECIFY_COLUMN_SQL_PREFIX =
            " as column, COUNT(1) FROM task_instance_wide a WHERE a.task_id = ? ";

    private static final String QUERY_TASK_INSTANCE_COUNT_GROUP_BY_SPECIFY_COLUMN_SQL_POSTFIX = " GROUP BY a.";

    private final DynamicSqlExecutor sqlExecutor;

    private final TaskInstance.Repo repo;

    private final TaskService taskService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param sqlExecutor sql执行器
     * @param repo 持久化实例
     * @param taskService 任务服务
     */
    public StatisticsController(Authenticator authenticator, DynamicSqlExecutor sqlExecutor, TaskInstance.Repo repo,
            TaskService taskService) {
        super(authenticator);
        this.sqlExecutor = sqlExecutor;
        this.repo = repo;
        this.taskService = taskService;
    }

    /**
     * taskStatistic
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @return Map<String, Object>
     */
    @GetMapping(value = "/task-statistic", summary = "查询任务分类")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> taskStatistic(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse) {
        AtomicLong total = new AtomicLong(0L);
        Map<String, List<Map<String, Object>>> tenantEntityMap = sqlExecutor.executeQuery(QUERY_TASK_SQL)
                .stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("tenant_name"))));
        List<TaskStatisticInfo> taskStatisticInfos = tenantEntityMap.entrySet().stream().map(entry -> {
            TaskStatisticInfo info = new TaskStatisticInfo();
            info.setTenantName(entry.getKey());
            AtomicLong tenantTaskCount = new AtomicLong();
            List<Map<String, Object>> taskInfos = entry.getValue().stream().peek(row -> {
                Long count = ObjectUtils.<Long>cast(row.get("count"));
                total.addAndGet(count);
                tenantTaskCount.addAndGet((count));
            }).map(row -> {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskName", row.get("name"));
                taskInfo.put("count", row.get("count"));
                return taskInfo;
            }).collect(Collectors.toList());
            info.setTaskInfos(taskInfos);
            info.setCount(tenantTaskCount.get());
            return info;
        }).collect(Collectors.toList());
        Map<String, Object> view = new LinkedHashMap<>(4, 1);
        view.put("taskStatisticInfos", taskStatisticInfos);
        view.put("approvalTaskCount", sqlExecutor.executeScalar(QUERY_APPROVAL_TASK_COUNT_SQL));
        view.put("totalTask", total);
        return view;
    }

    /**
     * tagStatistic
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @return Map<String, Object>
     */
    @GetMapping(value = "/tag-statistic", summary = "查询标签分类")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> tagStatistic(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse) {
        List<String> taskIds = httpRequest.queries().all("taskId");
        SqlBuilder sql = SqlBuilder.custom()
                .append(QUERY_TASK_INSTANCE_COUNT_GROUP_BY_TAG_SQL_PREFIX)
                .appendRepeatedly("a.task_id = ? OR ", taskIds.size())
                .backspace(3)
                .append("group by t.name");
        List<SpecifyTaskTagCountInfo> specifyTaskTagCountInfos =
                sqlExecutor.executeQuery(sql.toString(), taskIds).stream().map(row -> {
                    SpecifyTaskTagCountInfo info = new SpecifyTaskTagCountInfo();
                    info.setTagName(ObjectUtils.cast(row.get("name")));
                    info.setCount(ObjectUtils.<Long>cast(row.get("count")));
                    return info;
                }).collect(Collectors.toList());
        Map<String, Object> view = new LinkedHashMap<>(4, 1);
        view.put("specifyTaskTagCountInfos", specifyTaskTagCountInfos);
        Map<String, Long> columnCountMap = new HashMap<>();
        String column = httpRequest.queries()
                .first("column")
                .orElseThrow(() -> new IllegalArgumentException("parameter column is required."));
        taskIds.forEach(taskId -> {
            List<Map<String, Object>> rows = sqlExecutor.executeQuery(QUERY_WIDE_COLUMN_FROM_PROPERTY_SQL,
                    new ArrayList<>(Arrays.asList(taskId, column)));
            if (rows.isEmpty()) {
                throw new IllegalArgumentException(StringUtils.format("column {0} is not exist in task. TaskId is {1}",
                        column,
                        taskId));
            }
            SqlBuilder sqlBuilder = SqlBuilder.custom()
                    .append("SELECT a.")
                    .append(rows.get(0).get("data_type") + "_" + rows.get(0).get("sequence"))
                    .append(QUERY_TASK_INSTANCE_COUNT_WITHOUT_TAG_SQL_PREFIX)
                    .append(rows.get(0).get("data_type") + "_" + rows.get(0).get("sequence"));
            rows = sqlExecutor.executeQuery(sqlBuilder.toString(), new ArrayList<>(Collections.singletonList(taskId)));
            rows.forEach(row -> {
                String columnValue = ObjectUtils.cast(row.get("column"));
                if (columnValue == null) {
                    return;
                }
                if (columnCountMap.get(columnValue) == null) {
                    columnCountMap.put(columnValue, ObjectUtils.<Long>cast(row.get("count")));
                } else {
                    columnCountMap.put(columnValue,
                            columnCountMap.get(columnValue) + ObjectUtils.<Long>cast(row.get("count")));
                }
            });
        });
        view.put("specifyTaskWithoutTaskColumnCountInfo", columnCountMap);
        return view;
    }

    /**
     * instanceStatistic
     *
     * @param httpRequest httpRequest
     * @param httpResponse httpResponse
     * @return Map<String, Object>
     */
    @GetMapping(value = "/instance-statistic", summary = "查询实例分类")
    @ResponseStatus(HttpResponseStatus.OK)
    public Map<String, Object> instanceStatistic(HttpClassicServerRequest httpRequest,
            HttpClassicServerResponse httpResponse) {
        OperationContext context = this.contextOf(httpRequest, null);
        TaskInstance.Filter filter = filterOfInstances(httpRequest, false);
        Map<String, Object> view = new LinkedHashMap<>(4, 1);
        List<String> taskIds = httpRequest.queries().all("taskId");
        String column = httpRequest.queries()
                .first("column")
                .orElseThrow(() -> new IllegalArgumentException("parameter column is required."));
        Map<String, Long> columnCountMap = new HashMap<>();
        taskIds.forEach(taskId -> {
            TaskEntity task = this.taskService.retrieve(taskId, context);
            repo.statistics(task, filter, column, context)
                    .forEach((key, value) -> columnCountMap.merge(key, value, Long::sum));
        });
        view.put("columnCount", columnCountMap);
        return view;
    }

    @Data
    private static class TaskStatisticInfo {
        private String tenantName;

        private List<Map<String, Object>> taskInfos;

        private long count;
    }

    @Data
    private static class SpecifyTaskTagCountInfo {
        private String tagName;

        private long count;
    }
}
