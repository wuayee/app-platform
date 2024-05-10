/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.common.enums.JaneCategory;
import com.huawei.fit.jober.taskcenter.domain.util.PrimaryValue;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultPrimaryValue;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示任务定义。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public class TaskEntity {
    private String id;

    private String name;

    private String tenantId;

    private String templateId;

    private JaneCategory category;

    private List<TaskProperty> properties = Collections.emptyList();

    private List<TaskType> types = Collections.emptyList();

    private List<Index> indexes = Collections.emptyList();

    private List<TaskCategoryTriggerEntity> categoryTriggers = Collections.emptyList();

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;

    private Map<String, Object> attributes;

    private volatile List<TaskProperty> primaryProperties;

    private Map<String, TaskProperty> nameIndexedProperties;

    private Map<String, TaskProperty> idIndexedProperties;

    private Set<String> indexedPropertyIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public JaneCategory getCategory() {
        return category;
    }

    public void setCategory(JaneCategory category) {
        this.category = category;
    }

    public List<TaskProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TaskProperty> properties) {
        this.properties = Optional.ofNullable(properties)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.primaryProperties = null;
        this.nameIndexedProperties = null;
        this.idIndexedProperties = null;
    }

    public List<TaskType> getTypes() {
        return types;
    }

    public void setTypes(List<TaskType> types) {
        this.types = Optional.ofNullable(types)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = Optional.ofNullable(indexes)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<TaskCategoryTriggerEntity> getCategoryTriggers() {
        return categoryTriggers;
    }

    public void setCategoryTriggers(List<TaskCategoryTriggerEntity> categoryTriggers) {
        this.categoryTriggers = Optional.ofNullable(categoryTriggers)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public LocalDateTime getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(LocalDateTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * 获取任务定义中所有的数据源。
     * <p>根据调整后的模型，应该通过 {@link #getTypes()} 中的 {@link TaskType#sources()} 来获取数据源。</p>
     *
     * @return 表示数据源列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
     * @deprecated 兼容性
     */
    @Deprecated
    public List<SourceEntity> getSources() {
        List<SourceEntity> sources = new LinkedList<>();
        TaskType.traverse(nullIf(this.types, Collections.emptyList()),
                type -> Optional.ofNullable(type.sources()).ifPresent(sources::addAll));
        return sources;
    }

    /**
     * 获取主键属性。
     *
     * @return 表示主键属性的列表的 {@link List}{@code <}{@link TaskProperty}{@code >}。
     */
    public List<TaskProperty> getPrimaryProperties() {
        if (this.primaryProperties == null) {
            this.primaryProperties = this.getProperties()
                    .stream()
                    .filter(TaskProperty::identifiable)
                    .collect(Collectors.toList());
        }
        return this.primaryProperties;
    }

    public PrimaryValue computePrimaryValue(Map<String, Object> info) {
        notNull(info, "The info to compute primary value cannot be null.");
        List<TaskProperty> primaries = this.getPrimaryProperties();
        if (primaries.isEmpty()) {
            return PrimaryValue.empty();
        }
        Map<String, Object> values = new HashMap<>(primaries.size());
        for (TaskProperty property : primaries) {
            Object value = info.get(property.name());
            values.put(property.name(), value);
        }
        return new DefaultPrimaryValue(values);
    }

    public TaskProperty getPropertyByName(String propertyName) {
        if (this.nameIndexedProperties == null) {
            this.nameIndexedProperties = this.getProperties()
                    .stream()
                    .collect(Collectors.toMap(TaskProperty::name, Function.identity()));
        }
        return this.nameIndexedProperties.get(propertyName);
    }

    public TaskProperty getPropertyById(String propertyId) {
        if (this.idIndexedProperties == null) {
            this.idIndexedProperties = this.getProperties()
                    .stream()
                    .collect(Collectors.toMap(TaskProperty::id, Function.identity()));
        }
        return this.idIndexedProperties.get(propertyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskEntity that = (TaskEntity) obj;
            return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName())
                    && Objects.equals(this.getTenantId(), that.getTenantId()) && Objects.equals(this.getCategory(),
                    that.getCategory()) && Objects.equals(this.getProperties(), that.getProperties()) && Objects.equals(
                    this.getTypes(), that.getTypes()) && Objects.equals(this.getIndexes(), that.getIndexes())
                    && Objects.equals(this.getCategoryTriggers(), that.getCategoryTriggers()) && Objects.equals(
                    this.getCreator(), that.getCreator()) && Objects.equals(this.getCreationTime(),
                    that.getCreationTime())
                    && Objects.equals(this.getLastModifier(), that.getLastModifier()) && Objects.equals(
                    this.getLastModificationTime(), that.getLastModificationTime()) && Objects.equals(
                    this.getAttributes(),
                    that.getAttributes());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.getId(), this.getName(), this.getTenantId(), this.getCategory(),
                this.getProperties(),
                this.getTypes(), this.getIndexes(), this.getCategoryTriggers(), this.getCreator(),
                this.getCreationTime(),
                this.getLastModifier(), this.getLastModificationTime(), this.getAttributes()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("id={0}, name={1}, category={2}, properties={3}, types={4}, indexes={5}, "
                        + "categoryTriggers={6}, creator={7}, creationTime={8}, lastModifier={9}, lastModificationTime={10}, "
                        + "attributes={11}, tenantId={12}", this.getId(), this.getName(), this.getCategory(),
                this.getProperties(),
                this.getTypes(), this.getIndexes(), this.getCategoryTriggers(), this.getCreator(),
                this.getCreationTime(),
                this.getLastModifier(), this.getLastModificationTime(), this.getAttributes(), this.getTenantId());
    }

    /**
     * 检查指定名称的属性是否用于索引。
     * TODO 待将 Task 模块重构后，需要在 TaskProperty 中增加对 Task 的引用，并提供方法 indexed() 直接判断属性是否用作索引
     *
     * @param propertyName 表示待检查的属性的名称的 {@link String}。
     * @return 若属性用于索引，则为 {@code true}，否则为 {@code false}。
     */
    public boolean isPropertyIndexed(String propertyName) {
        TaskProperty property = this.getPropertyByName(propertyName);
        if (property == null || !property.dataType().indexable()) {
            return false;
        }
        return this.getIndexedPropertyIds().contains(property.id());
    }

    private Set<String> getIndexedPropertyIds() {
        if (this.indexedPropertyIds == null) {
            this.indexedPropertyIds = Optional.ofNullable(this.getIndexes())
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .filter(Objects::nonNull)
                    .map(Index::properties)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(TaskProperty::id)
                    .collect(Collectors.toSet());
        }
        return this.indexedPropertyIds;
    }
}
