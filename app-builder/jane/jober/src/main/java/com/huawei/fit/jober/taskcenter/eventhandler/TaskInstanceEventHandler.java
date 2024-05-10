/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler;

import com.huawei.fit.jober.InstanceChangedService;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.InstanceEventType;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceDeletedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifiedEvent;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fit.jober.taskcenter.fitable.util.ParamUtils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.log.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 为任务实例的事件提供处理程序。
 *
 * @author 梁济时 l00815032
 * @since 2023-10-19
 */
public abstract class TaskInstanceEventHandler<E extends TaskInstanceEvent> {
    private static final Logger log = Logger.get(TaskInstanceEventHandler.class);

    private static final int TIME_OUT_PERIOD = 500000000;

    private final BrokerClient brokerClient;

    private final String genericableId;

    private final InstanceEventType type;

    public TaskInstanceEventHandler(BrokerClient brokerClient, String genericableId, InstanceEventType type) {
        this.brokerClient = brokerClient;
        this.genericableId = genericableId;
        this.type = type;
    }

    public void handleEvent(E event) {
        List<SourceEntity> sources = this.getSourcesToPublish(event.task(), event.instance());
        for (SourceEntity source : sources) {
            this.publishToSource(source, event);
        }
    }

    /**
     * 获取待发布到的数据源的列表。
     *
     * @param task 表示发生变化的任务实例所属的任务定义的 {@link TaskEntity}。
     * @param instance 表示发生变化的任务实例的 {@link TaskInstance}。
     * @return 表示待通知到的任务数据源的列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
     */
    protected List<SourceEntity> getSourcesToPublish(TaskEntity task, TaskInstance instance) {
        if (instance.source() == null) {
            return TaskType.lookup(task.getTypes(), instance.type().id()).sources();
        } else {
            return Collections.singletonList(instance.source());
        }
    }

    private void publishToSource(SourceEntity source, E event) {
        List<String> fitableIds = source.getEvents()
                .stream()
                .filter(this::is)
                .map(InstanceEvent::fitableId)
                .collect(Collectors.toList());
        for (String fitableId : fitableIds) {
            Object[] args = this.arguments(event);
            log.info("Notify fitable that task instance has been {}. [fitableId={}, taskId={}, instanceId={}]",
                    this.type, fitableId, event.task().getId(), event.instance().id());
            long cost = System.currentTimeMillis();
            try {
                this.brokerClient.getRouter(InstanceChangedService.class, genericableId)
                        .route(new FitableIdFilter(fitableId))
                        .timeout(TIME_OUT_PERIOD, TimeUnit.MILLISECONDS)
                        .invoke(args);
                cost = System.currentTimeMillis() - cost;
                log.info("Successful to notify fitable. Total {} milliseconds cost.", cost);
            } catch (Throwable e) {
                cost = System.currentTimeMillis() - cost;
                log.error("Failed to notify fitable. Total {} milliseconds cost.", cost);
                log.error(e.getClass().getName(), e);
            }
        }
    }

    private boolean is(InstanceEvent event) {
        return event.type() == this.type;
    }

    /**
     * 生成通知 Genericable 的调用参数。
     *
     * @param event 表示事件数据的 {@link E}。
     * @return 表示 Genericable 的调用参数的 {@link Object}{@code []}。
     */
    protected abstract Object[] arguments(E event);

    /**
     * 任务实例创建事件Handler。
     *
     * @author 陈镕希 c00572808
     * @since 2023-09-08
     */
    @Component
    public static class Created extends TaskInstanceEventHandler<TaskInstanceCreatedEvent>
            implements EventHandler<TaskInstanceCreatedEvent> {
        private static final Logger log = Logger.get(Created.class);

        private static final String INSTANCE_CHANGED_CREATE_GENERICABLE_ID = "e1c7adbb69f148c3b81d0067ad02799f";

        private final TaskConverter taskConverter;

        private final InstanceConverter instanceConverter;

        public Created(TaskConverter taskConverter, InstanceConverter instanceConverter, BrokerClient brokerClient) {
            super(brokerClient, INSTANCE_CHANGED_CREATE_GENERICABLE_ID, InstanceEventType.CREATED);
            this.taskConverter = taskConverter;
            this.instanceConverter = instanceConverter;
        }

        @Override
        protected Object[] arguments(TaskInstanceCreatedEvent event) {
            Task task = taskConverter.convert(event.task(), event.context());
            Instance instance = instanceConverter.convert(event.task(), event.instance());
            return new Object[] {task, instance, ParamUtils.convertOperationContext(event.context())};
        }
    }

    /**
     * 任务实例修改事件Handler。
     *
     * @author 陈镕希 c00572808
     * @since 2023-09-08
     */
    @Component
    public static class Modified extends TaskInstanceEventHandler<TaskInstanceModifiedEvent>
            implements EventHandler<TaskInstanceModifiedEvent> {
        private static final Logger log = Logger.get(Modified.class);

        private static final String INSTANCE_CHANGED_UPDATE_GENERICABLE_ID = "59e2aeaeffad4242bf8c446be11d20e6";

        private final TaskConverter taskConverter;

        private final InstanceConverter instanceConverter;

        public Modified(TaskConverter taskConverter, InstanceConverter instanceConverter, BrokerClient brokerClient) {
            super(brokerClient, INSTANCE_CHANGED_UPDATE_GENERICABLE_ID, InstanceEventType.MODIFIED);
            this.taskConverter = taskConverter;
            this.instanceConverter = instanceConverter;
        }

        @Override
        protected Object[] arguments(TaskInstanceModifiedEvent event) {
            Task task = taskConverter.convert(event.task(), event.context());
            Instance instance = instanceConverter.convert(event.task(), event.instance());
            Map<String, Object> values = event.values();
            return new Object[] {task, instance, values, ParamUtils.convertOperationContext(event.context())};
        }
    }

    /**
     * 任务实例删除事件Handler。
     *
     * @author 陈镕希 c00572808
     * @since 2023-09-08
     */
    @Component
    public static class Deleted extends TaskInstanceEventHandler<TaskInstanceDeletedEvent>
            implements EventHandler<TaskInstanceDeletedEvent> {
        private static final Logger log = Logger.get(Deleted.class);

        private static final String INSTANCE_CHANGED_DELETE_GENERICABLE_ID = "47227b3e78924058b428f9a125938d59";

        private final TaskConverter taskConverter;

        private final InstanceConverter instanceConverter;

        public Deleted(TaskConverter taskConverter, InstanceConverter instanceConverter, BrokerClient brokerClient) {
            super(brokerClient, INSTANCE_CHANGED_DELETE_GENERICABLE_ID, InstanceEventType.DELETED);
            this.taskConverter = taskConverter;
            this.instanceConverter = instanceConverter;
        }

        @Override
        protected Object[] arguments(TaskInstanceDeletedEvent event) {
            Task task = this.taskConverter.convert(event.task(), event.context());
            Instance instance = this.instanceConverter.convert(event.task(), event.instance());
            return new Object[] {task, instance, ParamUtils.convertOperationContext(event.context())};
        }
    }
}
