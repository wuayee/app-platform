/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.event;

import com.huawei.fit.jober.common.event.entity.SourceMetaData;
import com.huawei.fitframework.event.Event;

/**
 * 定时任务数据源Event。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-21
 */
public class ScheduleSourceEvent implements Event {
    private final Object publisher;

    private final SourceMetaData data;

    private final String type;

    public ScheduleSourceEvent(Object publisher, SourceMetaData data, String type) {
        this.publisher = publisher;
        this.data = data;
        this.type = type;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    /**
     * 获取Event中数据的 {@link SourceMetaData}。
     *
     * @return Event中数据的 {@link SourceMetaData}。
     */
    public SourceMetaData data() {
        return this.data;
    }

    /**
     * 获取Event类型的 {@link String}。
     *
     * @return Event类型的 {@link String}。
     */
    public String getType() {
        return this.type;
    }
}
