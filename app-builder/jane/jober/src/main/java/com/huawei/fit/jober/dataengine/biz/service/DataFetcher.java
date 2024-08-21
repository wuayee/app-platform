/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.service;

import com.huawei.fit.jober.DataService;
import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.common.utils.SleepUtil;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.entity.Filter;
import com.huawei.fit.jober.entity.Page;
import com.huawei.fit.jober.entity.TaskEntity;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DataFetcher 从其他平台获取数据类
 *
 * @author 晏钰坤
 * @since 2023/7/18
 */
public class DataFetcher {
    private static final Logger log = Logger.get(DataFetcher.class);

    private static final String GET_METADATA_METHOD_GENERICABLE_ID = "2b7e619019774a4ea69165bb42071f0c";

    private static final String GET_TASK_ENTITY_METHOD_GENERICABLE_ID = "779eac15e9644eceba89d7798545a23a";

    private static final int TIME_OUT_PERIOD = 20000;

    private DataFetcher() {
    }

    /**
     * 循环查询metaData列表
     *
     * @param brokerClient 框架调度的 {@link BrokerClient}
     * @param timeScheduler 定时任务{@link TimeScheduler}
     * @param occurException occurException
     * @return 元数据列表 {@link List}
     */
    public static List<String> loopGetMetaData(BrokerClient brokerClient, TimeScheduler timeScheduler,
            AtomicBoolean occurException) {
        log.info("[dataEngine]: Start to get metaData list");
        String dataFetchType = timeScheduler.getProperties().get(Constant.DATA_FETCH_TYPE);
        List<String> metaDataList = new ArrayList<>();
        int times = 1;
        while (times <= Constant.RETRY_TIMES) {
            try {
                metaDataList = brokerClient.getRouter(DataService.class, GET_METADATA_METHOD_GENERICABLE_ID)
                        .route(new FitableIdFilter(dataFetchType))
                        .timeout(TIME_OUT_PERIOD, TimeUnit.MILLISECONDS)
                        .invoke();
                break;
            } catch (FitException e) {
                log.error("[dataEngine]: Fail to get metaData list from {}, retry times: {}, error: {}.",
                        timeScheduler.getSourceApp(), times, e);
                times++;
                SleepUtil.sleep(Constant.RETRY_INTERVAL);
            } catch (Exception e) {
                times = printLogAndSleep(dataFetchType, times, e);
            }
        }
        if (times > Constant.RETRY_TIMES) {
            log.error(
                    "[dataEngineSaver]: Get metaData list failed, send message to the application number owner. "
                            + "Time scheduler id is: {}",
                    timeScheduler.getSchedulerId());
            occurException.set(true);
        }
        log.info("[dataEngine]: Get metaData list success.");
        return metaDataList;
    }

    /**
     * 循环查询taskEntity列表
     *
     * @param brokerClient 框架调度的 {@link BrokerClient}
     * @param timeScheduler 框架调度的 {@link TimeScheduler}
     * @param pullDataFilter 过滤对象 {@link Filter}
     * @param pageSize 每页大小 {@link Integer}
     * @param pageNo 第几页 {@link Integer}
     * @param occurException occurException
     * @return 任务实例列表 {@link List}
     */
    public static List<TaskEntity> loopGetTaskEntity(BrokerClient brokerClient, TimeScheduler timeScheduler,
            Filter pullDataFilter, int pageSize, int pageNo, AtomicBoolean occurException) {
        String dataFetchType = timeScheduler.getProperties().get(Constant.DATA_FETCH_TYPE);
        List<TaskEntity> taskEntities = new ArrayList<>();
        int times = 1;
        while (times <= Constant.RETRY_TIMES) {
            try {
                taskEntities = brokerClient.getRouter(DataService.class, GET_TASK_ENTITY_METHOD_GENERICABLE_ID)
                        .route(new FitableIdFilter(dataFetchType))
                        .timeout(TIME_OUT_PERIOD, TimeUnit.MILLISECONDS)
                        .invoke(pullDataFilter, new Page(pageNo, pageSize));
                break;
            } catch (FitException e) {
                log.error("[dataEngine]: Fail to get tasks by filter, the metaData is {}, retry times: {}, error: {}.",
                        pullDataFilter.getMetaData(), times, e);
                times++;
                SleepUtil.sleep(Constant.RETRY_INTERVAL);
            } catch (Exception e) {
                log.error("Catch throwable when remote invoke, fitableId is {}, retry times: {}, error: {}.",
                        dataFetchType, times, e);
                times++;
                SleepUtil.sleep(Constant.RETRY_INTERVAL);
            }
        }
        if (times > Constant.RETRY_TIMES) {
            log.error(
                    "[dataEngineSaver]: Get tasks failed, send message to the application number owner. "
                            + "Time scheduler id is: {}, filter is {}",
                    timeScheduler.getSchedulerId(), JSON.toJSONString(pullDataFilter));
            occurException.set(true);
        }
        return taskEntities;
    }

    private static int printLogAndSleep(String dataFetchType, int times, Throwable e) {
        int timeCount = times;
        log.error("Catch throwable when remote invoke, fitableId is {}, retry times: {}, error: {}.", dataFetchType,
                timeCount, e);
        timeCount++;
        SleepUtil.sleep(Constant.RETRY_INTERVAL);
        return timeCount;
    }
}
