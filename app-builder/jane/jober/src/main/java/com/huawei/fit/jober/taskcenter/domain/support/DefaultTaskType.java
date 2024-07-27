/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import com.huawei.fit.jane.task.domain.AbstractDomainObject;
import com.huawei.fit.jane.task.domain.AbstractDomainObjectBuilder;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link TaskType} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-10-09
 */
public class DefaultTaskType extends AbstractDomainObject implements TaskType {
    private final String name;

    private final String parentId;

    private final List<TaskType> children;

    private final List<SourceEntity> sources;

    /**
     * 默认任务类型
     *
     * @param id 表示id的{@link String}
     * @param name 表示名称的{@link String}
     * @param parentId 表示父任务id的{@link String}
     * @param children 表示子任务类型列表的{@link List}{@code <}{@link TaskType}{@code >}
     * @param sources 表示任务数据源的列表的的{@link List}{@code <}{@link SourceEntity}{@code >}
     * @param creator 表示创建者的{@link String}
     * @param creationTime 表示创建时间的{@link LocalDateTime}
     * @param lastModifier 表示最后一个修改者的{@link String}
     * @param lastModificationTime 表示最后的修改时间的{@link LocalDateTime}
     */
    public DefaultTaskType(String id, String name, String parentId, List<TaskType> children, List<SourceEntity> sources,
            String creator, LocalDateTime creationTime, String lastModifier, LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.parentId = parentId;
        this.name = name;
        this.children = children;
        this.sources = sources;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String parentId() {
        return this.parentId;
    }

    @Override
    public List<TaskType> children() {
        return this.children;
    }

    @Override
    public List<SourceEntity> sources() {
        return this.sources;
    }

    /**
     * 任务类型声明
     */
    public static class Declaration implements TaskType.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<String> parentId;

        private final UndefinableValue<List<String>> sourceIds;

        public Declaration(UndefinableValue<String> name, UndefinableValue<String> parentId,
                UndefinableValue<List<String>> sourceIds) {
            this.name = name;
            this.parentId = parentId;
            this.sourceIds = sourceIds;
        }

        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        @Override
        public UndefinableValue<String> parentId() {
            return this.parentId;
        }

        @Override
        public UndefinableValue<List<String>> sourceIds() {
            return this.sourceIds;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Declaration) {
                Declaration another = (Declaration) obj;
                return Objects.equals(this.name, another.name) && Objects.equals(this.parentId, another.parentId);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.name, this.parentId});
        }

        @Override
        public String toString() {
            Map<String, Object> values = new HashMap<>(2);
            this.name.ifDefined(value -> values.put("name", value));
            this.parentId.ifDefined(value -> values.put("parentId", value));
            return values.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        /**
         * 任务类型声明构造器
         */
        public static class Builder implements TaskType.Declaration.Builder {
            private UndefinableValue<String> name;

            private UndefinableValue<String> parentId;

            private UndefinableValue<List<String>> sourceIds;

            public Builder() {
                this.name = UndefinableValue.undefined();
                this.parentId = UndefinableValue.undefined();
                this.sourceIds = UndefinableValue.undefined();
            }

            @Override
            public TaskType.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            @Override
            public TaskType.Declaration.Builder parentId(String parentId) {
                this.parentId = UndefinableValue.defined(parentId);
                return this;
            }

            @Override
            public TaskType.Declaration.Builder sourceIds(List<String> sourceIds) {
                this.sourceIds = UndefinableValue.defined(sourceIds);
                return this;
            }

            @Override
            public TaskType.Declaration build() {
                return new Declaration(this.name, this.parentId, this.sourceIds);
            }
        }
    }

    /**
     * 任务类型构造器
     */
    public static class Builder
            extends AbstractDomainObjectBuilder<TaskType, TaskType.Builder> implements TaskType.Builder {
        private String name;

        private String parentId;

        private List<TaskType> children;

        private List<SourceEntity> sources;

        @Override
        public TaskType.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public TaskType.Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        @Override
        public TaskType.Builder children(List<TaskType> children) {
            this.children = children;
            return this;
        }

        @Override
        public TaskType.Builder sources(List<SourceEntity> sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public TaskType build() {
            return new DefaultTaskType(this.id(),
                    this.name,
                    this.parentId,
                    this.children,
                    this.sources,
                    this.creator(),
                    this.creationTime(),
                    this.lastModifier(),
                    this.lastModificationTime());
        }
    }
}
