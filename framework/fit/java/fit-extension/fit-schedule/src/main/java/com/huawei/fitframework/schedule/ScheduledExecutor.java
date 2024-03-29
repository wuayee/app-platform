/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.schedule;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Initialize;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.schedule.annotation.Scheduled;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 表示定时调度执行器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-18
 */
@Component
public class ScheduledExecutor {
    private static final Map<Scheduled.Strategy, BiFunction<Runnable, Scheduled, Task>> TASK_CREATORS =
            MapBuilder.<Scheduled.Strategy, BiFunction<Runnable, Scheduled, Task>>get()
                    .put(Scheduled.Strategy.DISPOSABLE, ScheduledExecutor::createDisposableTask)
                    .put(Scheduled.Strategy.CRON, ScheduledExecutor::createCronTask)
                    .put(Scheduled.Strategy.FIXED_DELAY, ScheduledExecutor::createFixedDelayTask)
                    .put(Scheduled.Strategy.FIXED_RATE, ScheduledExecutor::createFixedRateTask)
                    .build();

    private final BeanContainer container;
    private final List<Tuple> initialDelayAndTasks = new ArrayList<>();

    public ScheduledExecutor(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Initialize
    void schedule() {
        this.container.factories().forEach(this::storeTasks);
        this.scheduleTasks();
    }

    private void storeTasks(BeanFactory factory) {
        Class<?> beanClass = TypeUtils.toClass(factory.metadata().type());
        Method[] beanMethods = beanClass.getDeclaredMethods();
        for (Method beanMethod : beanMethods) {
            AnnotationMetadata annotations = this.container.runtime().resolverOfAnnotations().resolve(beanMethod);
            if (annotations.isAnnotationPresent(Scheduled.class)) {
                Scheduled scheduled = annotations.getAnnotation(Scheduled.class);
                long initialDelay = scheduled.timeUnit().toMillis(scheduled.initialDelay());
                Runnable runnable = () -> ReflectionUtils.invoke(factory.get(), beanMethod);
                Task task = this.createTask(runnable, scheduled);
                this.initialDelayAndTasks.add(Tuple.duet(initialDelay, task));
            }
        }
    }

    private void scheduleTasks() {
        ThreadPoolScheduler scheduler = ThreadPoolScheduler.custom()
                .corePoolSize(this.initialDelayAndTasks.size())
                .threadPoolName("scheduled-task")
                .build();
        for (Tuple tuple : this.initialDelayAndTasks) {
            long initialDelay = ObjectUtils.cast(tuple.get(0).orElse(0));
            Task task =
                    ObjectUtils.cast(tuple.get(1).orElseThrow(() -> new IllegalStateException("No scheduled task.")));
            scheduler.schedule(task, initialDelay);
        }
    }

    private Task createTask(Runnable runnable, Scheduled scheduled) {
        BiFunction<Runnable, Scheduled, Task> taskCreator =
                TASK_CREATORS.getOrDefault(scheduled.strategy(), ScheduledExecutor::createUnsupportedTask);
        return taskCreator.apply(runnable, scheduled);
    }

    private static Task createDisposableTask(Runnable runnable, Scheduled scheduled) {
        return Task.builder().runnable(runnable).uncaughtExceptionHandler((thread, cause) -> {}).buildDisposable();
    }

    private static Task createCronTask(Runnable runnable, Scheduled scheduled) {
        return Task.builder()
                .runnable(runnable)
                .uncaughtExceptionHandler((thread, cause) -> {})
                .policy(ExecutePolicy.cron(scheduled.value(), scheduled.zone()))
                .build();
    }

    private static Task createFixedDelayTask(Runnable runnable, Scheduled scheduled) {
        return Task.builder()
                .runnable(runnable)
                .uncaughtExceptionHandler((thread, cause) -> {})
                .policy(ExecutePolicy.fixedDelay(Long.parseLong(scheduled.value())))
                .build();
    }

    private static Task createFixedRateTask(Runnable runnable, Scheduled scheduled) {
        return Task.builder()
                .runnable(runnable)
                .uncaughtExceptionHandler((thread, cause) -> {})
                .policy(ExecutePolicy.fixedRate(Long.parseLong(scheduled.value())))
                .build();
    }

    private static Task createUnsupportedTask(Runnable runnable, Scheduled scheduled) {
        throw new UnsupportedOperationException(StringUtils.format("Unsupported scheduled strategy. [strategy={0}]",
                scheduled.strategy()));
    }
}
