/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jober.entity.Filter;
import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fit.jober.entity.InstanceChanged;
import com.huawei.fit.jober.entity.InstanceChangedMessage;
import com.huawei.fit.jober.entity.Page;
import com.huawei.fit.jober.entity.TaskEntity;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 数据服务Genericable。
 *
 * @author 陈镕希
 * @since 2023-06-12
 */
public interface DataService {
    /**
     * 获取Domain中任务列表。
     *
     * @param filter 任务筛选结构体的 {@link Filter}。
     * @param page 分页查询结构体的 {@link Page}。
     * @return 符合筛选条件的任务列表的 {@link List}{@code <}{@link TaskEntity}{@code >}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "779eac15e9644eceba89d7798545a23a")
    List<TaskEntity> getTasksByFilter(Filter filter, Page page);

    /**
     * 获取可选的元数据列表。
     *
     * @return 元数据列表的 {@link List}{@code <}{@link String}{@code >}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "2b7e619019774a4ea69165bb42071f0c")
    List<String> getMetaDataList();

    /**
     * 实例变化通知。
     *
     * @param instanceChangedMessages 表示变化实例消息列表的 {@link List}{@code <}{@link InstanceChangedMessage}{@code >}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "7c1ebbc23f7d41258a042e04c4f5f50d")
    void notifyInstanceChanged(List<InstanceChangedMessage> instanceChangedMessages);

    /**
     * onInstancesChanged
     *
     * @param messages messages
     */
    @Genericable(id = "e2bb4c43e3ff4f649210eb39d3a8fc77")
    void onInstancesChanged(List<InstanceChanged> messages);

    /**
     * onInstancesCategoryChanged
     *
     * @param messages messages
     */
    @Genericable(id = "cb42cff4983047ea85adcf7dcde78091")
    void onInstancesCategoryChanged(List<InstanceCategoryChanged> messages);
}