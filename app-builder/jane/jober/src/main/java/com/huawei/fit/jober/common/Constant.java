/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

import static java.util.Collections.unmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 常数类
 *
 * @author 晏钰坤
 * @since 2023/6/15
 */
public final class Constant {
    /**
     * 定时任务获取
     */
    public static final String SCHEDULE = "schedule";

    /**
     * 定时任务间隔
     */
    public static final String SCHEDULER_INTERVAL = "schedulerInterval";

    /**
     * 延迟时间
     */
    public static final String DELAY_TIME = "delayTime";

    /**
     * 任务定义ID
     */
    public static final String TASK_DEFINITION_ID = "taskDefinitionId";

    /**
     * 默认每页大小
     */
    public static final String PAGE_SIZE = "100";

    /**
     * 系统自动补充的属性字段
     */
    public static final List<String> SYSTEM_FIELDS = unmodifiableList(
            Arrays.asList("id", "decomposed_from", "created_by", "created_date", "modified_by", "modified_date",
                    "source_app", "target_url", "owner", "state"));

    /**
     * 任务第三方id
     */
    public static final String REQUIREMENT_ID = "id";

    /**
     * 创建人
     */
    public static final String APPLIER = "applier";

    /**
     * 责任人
     */
    public static final String OWNER = "owner";

    /**
     * 宽表字段名前缀
     */
    public static final String FIELD_VALUE = "field_value_";

    /**
     * 系统级任务property字段数目
     */
    public static final Integer SYSTEM_PROPERTY_NUM = 30;

    /**
     * 任务property字段数目
     */
    public static final Integer PROPERTY_NUM = 100;

    /**
     * 任务状态
     */
    public static final String STATE = "state";

    /**
     * 任务作废
     */
    public static final String STATE_DELETED = "deleted";

    /**
     * 重试的最大次数
     */
    public static final Integer RETRY_TIMES = 10;

    /**
     * 重试的时间间隔
     */
    public static final Integer RETRY_INTERVAL = 1000;

    /**
     * 数据拉取类型
     */
    public static final String DATA_FETCH_TYPE = "dataFetchType";

    /**
     * 任务数据源定义唯一标识
     */
    public static final String TASK_SOURCE_ID = "taskSourceId";

    /**
     * 数据来源平台
     */
    public static final String SOURCE_APP = "sourceApp";

    /**
     * value change的url
     */
    public static final String VALUE_CHANGE_URL = "//fit-jane-dev.paas.huawei.com/props-value/";

    /**
     * 任务定义status字段
     */
    public static final String STATUS = "status";

    /**
     * 任务定义优先级字段
     */
    public static final String PRIORITY = "priority";

    /**
     * 创建时间
     */
    public static final String CREATED_DATE = "created_date";

    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"),
            new Locale("zh", "CN"));

    /**
     * 任务定义title字段
     */
    public static final String TITLE = "title";

    /**
     * 任务定义created_by字段
     */
    public static final String CREATED_BY = "created_by";

    /**
     * 流程上下文 ID字段
     */
    public static final String FLOW_CONTEXT_ID_KEY = "flowContextId";

    /**
     * 流程trace ID字段
     */
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 流程节点 ID字段
     */
    public static final String NODE_ID_KEY = "nodeId";

    /**
     * 流程业务数据businessData字段
     */
    public static final String BUSINESS_DATA_KEY = "businessData";

    /**
     * 流程业务数据operator字段
     */
    public static final String OPERATOR_KEY = "operator";

    /**
     * 合法跨域访问域名
     */
    public static final String VALID_DOMAIN = ".huawei.com";
}
