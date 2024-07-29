/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.entity.InstanceChanged;
import com.huawei.fit.jober.entity.InstanceMessage;
import com.huawei.fit.jober.entity.PropertyValue;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryKey;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryValue;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fitframework.broker.client.BrokerClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为通知程序提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-28
 */
public abstract class AbstractNotifier {
    private final BrokerClient brokerClient;

    private final TaskEntity task;

    private Map<String, TaskProperty> properties;

    private PrimaryKey primaryKey;

    private boolean isPrimaryKeyLoaded;

    /**
     * 构造函数
     *
     * @param brokerClient 调度器
     * @param task 任务
     */
    public AbstractNotifier(BrokerClient brokerClient, TaskEntity task) {
        this.brokerClient = brokerClient;
        this.task = task;
        this.isPrimaryKeyLoaded = false;
    }

    /**
     * 返回调度器
     *
     * @return 调度器
     */
    protected final BrokerClient broker() {
        return this.brokerClient;
    }

    /**
     * 返回任务
     *
     * @return 任务
     */
    protected final TaskEntity task() {
        return this.task;
    }

    /**
     * properties
     *
     * @return Map<String, PropertyEntity>
     */
    protected final Map<String, TaskProperty> properties() {
        if (this.properties == null) {
            this.properties = this.task().getProperties().stream().collect(
                    Collectors.toMap(TaskProperty::name, Function.identity()));
        }
        return this.properties;
    }

    /**
     * primaries
     *
     * @param instance instance
     * @return List<PropertyValue>
     */
    protected final List<PropertyValue> primaries(TaskInstance instance) {
        PrimaryKey pk = this.primaryKey();
        if (pk == null) {
            return Collections.emptyList();
        }
        PrimaryValue primaryValue = this.task().computePrimaryValue(instance.info());
        List<PropertyValue> primaries = new ArrayList<>(primaryValue.values().size());
        for (Map.Entry<String, Object> entry : primaryValue.values().entrySet()) {
            PropertyValue primary = new InstanceChanged.ChangedPropertyValue();
            TaskProperty property = this.properties().get(entry.getKey());
            primary.setProperty(property.name());
            primary.setDataType(Enums.toString(property.dataType()));
            primary.setValue(property.dataType().toString(entry.getValue()));
            primaries.add(primary);
        }
        return primaries;
    }

    private PrimaryKey primaryKey() {
        if (!this.isPrimaryKeyLoaded) {
            this.primaryKey = PrimaryKey.of(this.task());
            this.isPrimaryKeyLoaded = true;
        }
        return this.primaryKey;
    }

    /**
     * fillInstanceInfo
     *
     * @param info info
     * @param instance instance
     * @param context context
     */
    protected final void fillInstanceInfo(InstanceMessage info, TaskInstance instance, OperationContext context) {
        info.setTaskId(instance.task().getId());
        info.setInstanceId(instance.id());
        info.setTaskTypeId(instance.type().id());
        SourceEntity source = instance.source();
        if (source != null) {
            info.setSourceName(source.getName());
            info.setSourceApp(source.getApp());
        }
        info.setTenant(context.tenantId());
        info.setOperator(context.operator());
        info.setPrimaries(this.primaries(instance));
    }
}
