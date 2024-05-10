/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.AbstractDomainObject;
import com.huawei.fit.jane.task.domain.AbstractDomainObjectBuilder;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 为 {@link Index} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-04
 */
public class DefaultIndex extends AbstractDomainObject implements Index {
    private final String name;

    private final TaskEntity task;

    private final List<TaskProperty> properties;

    DefaultIndex(String id, String name, TaskEntity task, List<TaskProperty> properties, String creator,
            LocalDateTime creationTime, String lastModifier, LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.task = task;
        this.properties = properties;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public TaskEntity task() {
        return this.task;
    }

    @Override
    public List<TaskProperty> properties() {
        return this.properties;
    }

    /**
     * 为 {@link Index.Builder} 提供默认实现。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-04
     */
    public static class Builder extends AbstractDomainObjectBuilder<Index, Index.Builder> implements Index.Builder {
        private String name;

        private TaskEntity task;

        private List<TaskProperty> properties;

        @Override
        public Index.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Index.Builder task(TaskEntity task) {
            this.task = task;
            return this;
        }

        @Override
        public Index.Builder properties(List<TaskProperty> properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public Index build() {
            return new DefaultIndex(this.id(), this.name, this.task, this.properties, this.creator(),
                    this.creationTime(), this.lastModifier(), this.lastModificationTime());
        }
    }

    /**
     * 为 {@link Index.Declaration} 提供默认实现。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-04
     */
    public static class Declaration implements Index.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<List<String>> propertyIds;

        public Declaration(UndefinableValue<String> name, UndefinableValue<List<String>> propertyIds) {
            this.name = name;
            this.propertyIds = propertyIds;
        }

        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        @Override
        public UndefinableValue<List<String>> propertyNames() {
            return this.propertyIds;
        }

        /**
         * 为 {@link Index.Declaration.Builder} 提供默认实现。
         *
         * @author 梁济时 l00815032
         * @since 2024-01-04
         */
        public static class Builder implements Index.Declaration.Builder {
            private UndefinableValue<String> name;

            private UndefinableValue<List<String>> propertyIds;

            public Builder(Index.Declaration declaration) {
                if (declaration == null) {
                    this.name = UndefinableValue.undefined();
                    this.propertyIds = UndefinableValue.undefined();
                } else {
                    this.name = nullIf(declaration.name(), UndefinableValue.undefined());
                    this.propertyIds = nullIf(declaration.propertyNames(), UndefinableValue.undefined());
                }
            }

            @Override
            public Index.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            @Override
            public Index.Declaration.Builder propertyNames(List<String> propertyNames) {
                this.propertyIds = UndefinableValue.defined(propertyNames);
                return this;
            }

            @Override
            public Index.Declaration build() {
                return new Declaration(this.name, this.propertyIds);
            }
        }
    }
}
