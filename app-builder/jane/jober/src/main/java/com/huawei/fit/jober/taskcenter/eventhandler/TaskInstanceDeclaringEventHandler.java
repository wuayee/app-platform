/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceDeclaringEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifyingEvent;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 为任务实例声明的事件提供处理程序。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-24
 */
public abstract class TaskInstanceDeclaringEventHandler<E extends TaskInstanceDeclaringEvent> {
    @Order(1)
    @Component
    public static class InstanceIdHandler extends TaskInstanceDeclaringEventHandler<TaskInstanceCreatingEvent>
            implements EventHandler<TaskInstanceCreatingEvent> {
        @Override
        public void handleEvent(TaskInstanceCreatingEvent event) {
            TaskInstance.Declaration declaration = event.declaration();
            Map<String, Object> infos = declaration.info()
                    .required(() -> new BadRequestException(ErrorCodes.INSTANCE_INFO_REQUIRED));
            if (StringUtils.isBlank(ObjectUtils.cast(infos.get("id")))) {
                infos.put("id", event.instanceId());
            }
        }
    }

    @Order(5)
    @Component
    public static class TargetUrlHandler extends TaskInstanceDeclaringEventHandler<TaskInstanceCreatingEvent>
            implements EventHandler<TaskInstanceCreatingEvent> {
        private final String janeEndpoint;

        private final List<String> targetUrlNoSourceList;

        public TargetUrlHandler(@Value("${jane.endpoint}") String janeEndpoint,
                @Value("${jane.targetUrl.noSource}") String targetUrlNoSourceList) {
            super();
            this.janeEndpoint = janeEndpoint;
            this.targetUrlNoSourceList = Arrays.asList(targetUrlNoSourceList.split(","));
        }

        @Override
        public void handleEvent(TaskInstanceCreatingEvent event) {
            TaskInstance.Declaration declaration = event.declaration();
            Map<String, Object> infos = declaration.info()
                    .required(() -> new BadRequestException(ErrorCodes.INSTANCE_INFO_REQUIRED));
            if (targetUrlNoSourceList.contains(event.task().getName())) {
                if (declaration.sourceId().defined() && !StringUtils.equals(
                        "00000000000000000000000000000000", declaration.sourceId().get())) {
                    return;
                }
            }
            if (StringUtils.isBlank(ObjectUtils.cast(infos.get("target_url")))) {
                infos.put("target_url",
                        janeEndpoint + "/#/home?taskId=" + event.task().getId() + "&instanceId=" + event.instanceId()
                                + "&displayType=drawer");
            }
            declaration = declaration.copy().info(infos).build();
            event.declaration(declaration);
        }
    }

    @Order(6)
    @Component
    public static class CreatingInfoHandler extends TaskInstanceDeclaringEventHandler<TaskInstanceCreatingEvent>
            implements EventHandler<TaskInstanceCreatingEvent> {
        @Override
        public void handleEvent(TaskInstanceCreatingEvent event) {
            TaskInstance.Declaration declaration = event.declaration();
            Map<String, Object> infos = declaration.info()
                    .required(() -> new BadRequestException(ErrorCodes.INSTANCE_INFO_REQUIRED));

            String operator = event.context().operator();
            if (StringUtils.isBlank(ObjectUtils.cast(infos.get("created_by")))) {
                infos.put("created_by", operator);
            }
            if (StringUtils.isBlank(ObjectUtils.cast(infos.get("modified_by")))) {
                infos.put("modified_by", operator);
            }
            LocalDateTime now = LocalDateTime.now();
            if (Objects.isNull(ObjectUtils.cast(infos.get("created_date")))) {
                infos.put("created_date", now);
            }
            if (Objects.isNull(ObjectUtils.cast(infos.get("modified_date")))) {
                infos.put("modified_date", now);
            }
            declaration = declaration.copy().info(infos).build();
            event.declaration(declaration);
        }
    }

    @Order(7)
    @Component
    public static class ModifyingInfoHandler extends TaskInstanceDeclaringEventHandler<TaskInstanceModifyingEvent>
            implements EventHandler<TaskInstanceModifyingEvent> {
        @Override
        public void handleEvent(TaskInstanceModifyingEvent event) {
            TaskInstance.Declaration declaration = event.declaration();
            if (declaration.info() == null || !declaration.info().defined()) {
                return;
            }
            Map<String, Object> infos = declaration.info().get();
            if (StringUtils.isBlank(ObjectUtils.cast(infos.get("modified_by")))) {
                infos.put("modified_by", event.context().operator());
            }

            if (Objects.isNull(ObjectUtils.cast(infos.get("modified_date")))) {
                LocalDateTime now = LocalDateTime.now();
                infos.put("modified_date", now);
            }
            declaration = declaration.copy().info(infos).build();
            event.declaration(declaration);
        }
    }
}
