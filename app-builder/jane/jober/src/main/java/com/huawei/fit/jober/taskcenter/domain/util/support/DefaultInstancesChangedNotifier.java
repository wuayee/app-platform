/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.DataService;
import com.huawei.fit.jober.entity.InstanceChanged;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.domain.util.InstancesChangedNotifier;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link InstancesChangedNotifier} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-10-28
 */
public class DefaultInstancesChangedNotifier extends AbstractNotifier implements InstancesChangedNotifier {
    private static final Logger log = Logger.get(DefaultInstancesChangedNotifier.class);

    private static final String GENERICABLE_ID = "e2bb4c43e3ff4f649210eb39d3a8fc77";

    private final Map<String, Map<String, List<String>>> triggers;

    private final Map<String, Map<String, InstanceChanged>> changes;

    private Map<String, TaskProperty> idIndexedProperties;

    /**
     * 构造函数
     *
     * @param brokerClient 调度器
     * @param task 任务
     */
    public DefaultInstancesChangedNotifier(BrokerClient brokerClient, TaskEntity task) {
        super(brokerClient, task);
        this.changes = new HashMap<>();
        this.triggers = new HashMap<>();
    }

    private List<String> triggers(SourceEntity source, String propertyName) {
        Map<String, List<String>> updatedTriggers =
                this.triggers.computeIfAbsent(source.getId(), key -> this.loadTriggerOfSource(source));
        return nullIf(updatedTriggers.get(propertyName), Collections.emptyList());
    }

    private TaskProperty getPropertyById(String propertyId) {
        if (this.idIndexedProperties == null) {
            this.idIndexedProperties = this.task()
                    .getProperties()
                    .stream()
                    .collect(Collectors.toMap(TaskProperty::id, Function.identity()));
        }
        return this.idIndexedProperties.get(propertyId);
    }

    private Map<String, List<String>> loadTriggerOfSource(SourceEntity source) {
        List<TriggerEntity> filterTriggers = nullIf(source.getTriggers(), Collections.emptyList());
        Map<String, List<String>> results = new HashMap<>();
        for (TriggerEntity trigger : filterTriggers) {
            String propertyName = this.getPropertyById(trigger.getPropertyId()).name();
            List<String> fitableIds = results.computeIfAbsent(propertyName, key -> new LinkedList<>());
            fitableIds.add(trigger.getFitableId());
        }
        return results;
    }

    private InstanceChanged getInstanceChanged(String fitableId, TaskInstance instance, OperationContext context) {
        Map<String, InstanceChanged> instances = this.changes.computeIfAbsent(fitableId, key -> new HashMap<>());
        return instances.computeIfAbsent(instance.id(), key -> {
            InstanceChanged changed = new InstanceChanged();
            this.fillInstanceInfo(changed, instance, context);
            changed.setChanges(new LinkedList<>());
            return changed;
        });
    }

    @Override
    public InstancesChangedNotifier notice(TaskInstance newInstance, TaskInstance oldInstance,
            OperationContext context) {
        if (newInstance.source() == null) {
            return this;
        }
        for (TaskProperty property : this.properties().values()) {
            Object oldValue = oldInstance.info().get(property.name());
            Object newValue = newInstance.info().get(property.name());
            if (Objects.equals(oldValue, newValue)) {
                continue;
            }
            SourceEntity source = newInstance.source();
            List<String> fitableIds = this.triggers(source, property.name());
            InstanceChanged.ChangedPropertyValue change = null;
            for (String fitableId : fitableIds) {
                InstanceChanged changed = this.getInstanceChanged(fitableId, newInstance, context);
                if (change == null) {
                    change = new InstanceChanged.ChangedPropertyValue();
                    change.setProperty(property.name());
                    change.setDataType(Enums.toString(property.dataType()));
                    change.setValue(property.dataType().toString(newValue));
                    change.setOriginValue(property.dataType().toString(oldValue));
                }
                changed.getChanges().add(change);
            }
        }
        return this;
    }

    @Override
    public void run() {
        for (Map.Entry<String, Map<String, InstanceChanged>> entry : this.changes.entrySet()) {
            String fitableId = entry.getKey();
            List<InstanceChanged> actualChanges = new ArrayList<>(entry.getValue().values());
            log.info("notify fitable that instances changed. [fitableId={}, changes.size={}]",
                    fitableId,
                    actualChanges.size());
            try {
                this.broker()
                        .getRouter(DataService.class, GENERICABLE_ID)
                        .route(new FitableIdFilter(fitableId))
                        .invoke(actualChanges);
            } catch (FitException t) {
                log.error("Failed to notify fitable that task instances has been changed. [fitableId={}]",
                        entry.getKey(),
                        t);
            }
        }
    }
}
