/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fitframework.event.Event;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 为任务实例相关的事件通知器提供基类。
 *
 * @author 梁济时
 * @since 2024-01-19
 */
public abstract class AbstractTaskInstanceEventNotifier implements Runnable {
    private static final Logger log = Logger.get(AbstractTaskInstanceEventNotifier.class);

    private final Plugin plugin;

    private final Map<String, TaskInstance> oldInstances;

    private final Map<String, TaskInstance> newInstances;

    public AbstractTaskInstanceEventNotifier(Plugin plugin) {
        this.plugin = plugin;
        this.oldInstances = new HashMap<>();
        this.newInstances = new HashMap<>();
    }

    private void publishEvent(Event event) {
        try {
            this.plugin.publisherOfEvents().publishEvent(event);
        } catch (NullPointerException e) {
            log.error("Get null publisher");
        } catch (RuntimeException ex) {
            log.error("Failed to publish instance event. {}", event);
            log.error(ex.getClass().getName(), ex);
        }
    }

    /**
     * 计算新实例和旧实例的修改
     *
     * @param oldInstance 旧实例
     * @param newInstance 新实例
     * @return 修改
     */
    protected static Map<String, Object> modifications(TaskInstance oldInstance, TaskInstance newInstance) {
        Set<String> keys = CollectionUtils.union(oldInstance.info().keySet(), newInstance.info().keySet());
        Map<String, Object> oldValues = new HashMap<>();
        for (String key : keys) {
            Object oldValue = oldInstance.info().get(key);
            Object newValue = newInstance.info().get(key);
            if (!Objects.equals(oldValue, newValue)) {
                oldValues.put(key, oldValue);
            }
        }
        return oldValues;
    }

    /**
     * 添加新实例
     *
     * @param instances 新的实例数组
     */
    protected void addNews(TaskInstance[] instances) {
        this.add(this.newInstances, instances);
    }

    /**
     * 添加旧实例
     *
     * @param instances 实例数组
     */
    protected void addOlds(TaskInstance[] instances) {
        this.add(this.oldInstances, instances);
    }

    private void add(Map<String, TaskInstance> cache, TaskInstance[] instances) {
        Optional.ofNullable(instances)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .forEach(instance -> cache.put(instance.id(), instance));
    }

    /**
     * 创建的事件
     *
     * @param instance 实例
     * @return 事件
     */
    protected abstract Event eventOfCreated(TaskInstance instance);

    /**
     * 修改的事件
     *
     * @param oldInstance 旧实例
     * @param newInstance 新实例
     * @return 修改事件的Optional
     */
    protected abstract Optional<? extends Event> eventOfModified(TaskInstance oldInstance, TaskInstance newInstance);

    /**
     * 删除的事件
     *
     * @param instance 实例
     * @return 删除的事件
     */
    protected abstract Event eventOfDeleted(TaskInstance instance);

    @Override
    public void run() {
        Set<String> createdIds = new HashSet<>();
        Set<String> modifiedIds = new HashSet<>();
        Set<String> deletedIds = new HashSet<>();
        this.analyse(createdIds, modifiedIds, deletedIds);
        createdIds.stream().map(this.newInstances::get).map(this::eventOfCreated).forEach(this::publishEvent);
        modifiedIds.stream()
                .map(id -> this.eventOfModified(this.oldInstances.get(id), this.newInstances.get(id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::publishEvent);
        deletedIds.stream().map(this.oldInstances::get).map(this::eventOfDeleted).forEach(this::publishEvent);
    }

    /**
     * 通过分析新旧实例数据，构造新增、修改和删除的集合
     *
     * @param createdIds 新增的id集合
     * @param modifiedIds 修改的id集合
     * @param deletedIds 删除的id集合
     */
    protected void analyse(Set<String> createdIds, Set<String> modifiedIds, Set<String> deletedIds) {
        Set<String> oldIds = new HashSet<>(this.oldInstances.keySet());
        Set<String> newIds = new HashSet<>(this.newInstances.keySet());
        for (String newId : newIds) {
            if (oldIds.remove(newId)) {
                modifiedIds.add(newId);
            } else {
                createdIds.add(newId);
            }
        }
        deletedIds.addAll(oldIds);
    }
}
