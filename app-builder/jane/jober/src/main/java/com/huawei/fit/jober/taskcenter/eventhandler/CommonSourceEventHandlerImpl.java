/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler;

import static com.huawei.fit.jober.common.ErrorCodes.CANNOT_FIND_CORRESPONDING_CONSUMER;

import com.huawei.fit.jober.common.event.CommonSourceEvent;
import com.huawei.fit.jober.common.event.entity.SourceMetaData;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.SourceValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.event.EventHandler;
import modelengine.fitframework.log.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 通用任务数据源事件Handler
 *
 * @author 董建华
 * @since 2023-08-29
 */
@Component
public class CommonSourceEventHandlerImpl implements EventHandler<CommonSourceEvent> {
    private static final Logger log = Logger.get(CommonSourceEventHandlerImpl.class);

    private final DynamicSqlExecutor executor;

    private final SourceValidator sourceValidator;

    private final Map<String, Consumer<SourceMetaData>> sourceEventHandlerStrategy = new HashMap<>();

    /**
     * CommonSourceEventHandlerImpl
     *
     * @param executor executor
     * @param sourceValidator sourceValidator
     */
    public CommonSourceEventHandlerImpl(DynamicSqlExecutor executor, SourceValidator sourceValidator) {
        this.executor = executor;
        this.sourceValidator = sourceValidator;
        this.sourceEventHandlerStrategy.put("delete", this::deleteEventStrategy);
    }

    @Override
    public void handleEvent(CommonSourceEvent event) {
        Optional.ofNullable(event.getType()).map(this.sourceEventHandlerStrategy::get).orElseThrow(() -> {
            log.error("Cannot find corresponding consumer of event. [Event type is {}]", event.getType());
            return new BadRequestException(CANNOT_FIND_CORRESPONDING_CONSUMER);
        }).accept(event.data());
    }

    private void deleteEventStrategy(SourceMetaData metaData) {
        // 删除task_node_source表中对应的记录
        String taskSourceId = metaData.getTaskSourceId();
        sourceValidator.validateSourceId(taskSourceId);
        this.executor.executeUpdate("DELETE FROM task_node_source WHERE source_id = ?",
                Collections.singletonList(taskSourceId));
    }
}
