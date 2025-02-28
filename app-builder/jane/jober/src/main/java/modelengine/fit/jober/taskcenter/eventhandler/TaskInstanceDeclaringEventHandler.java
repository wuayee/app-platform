/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.event.TaskInstanceCreatingEvent;
import modelengine.fit.jober.taskcenter.event.TaskInstanceDeclaringEvent;
import modelengine.fit.jober.taskcenter.event.TaskInstanceModifyingEvent;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.event.EventHandler;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 为任务实例声明的事件提供处理程序。
 *
 * @author 陈镕希
 * @since 2023-10-24
 */
public abstract class TaskInstanceDeclaringEventHandler<E extends TaskInstanceDeclaringEvent> {
    /**
     * 处理任务实例声明事件。
     */
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

    /**
     * 处理任务实例修改事件。
     */
    @Order(5)
    @Component
    public static class TargetUrlHandler extends TaskInstanceDeclaringEventHandler<TaskInstanceCreatingEvent>
            implements EventHandler<TaskInstanceCreatingEvent> {
        private final String janeEndpoint;

        private final List<String> targetUrlNoSourceList;

        /**
         * 构造函数。
         *
         * @param janeEndpoint Jane后端服务的访问地址。
         * @param targetUrlNoSourceList 不需要sourceId的任务名列表，多个任务名以逗号分隔。
         */
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

    /**
     * 处理任务实例创建事件。
     */
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

    /**
     * 处理任务实例修改事件。
     */
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
