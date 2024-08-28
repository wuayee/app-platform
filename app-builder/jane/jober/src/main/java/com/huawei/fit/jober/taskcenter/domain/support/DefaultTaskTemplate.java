/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import com.huawei.fit.jane.task.domain.AbstractDomainObject;
import com.huawei.fit.jane.task.domain.AbstractDomainObjectBuilder;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;

import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 为 {@link TaskTemplate} 提供默认实现
 *
 * @author 姚江
 * @since 2023-12-04 16:09
 */
public class DefaultTaskTemplate extends AbstractDomainObject implements TaskTemplate {
    private final String name;

    private final String description;

    private final String tenantId;

    private final List<TaskTemplateProperty> properties;

    /**
     * 默认的任务模板
     *
     * @param name 表示任务名称的{@link String}
     * @param description 表示任务描述的{@link String}
     * @param tenantId 表示租户id的{@link String}
     * @param properties 表示任务模板属性的{@link List}{@code <}{@link TaskTemplateProperty}{@code >}
     * @param id 表示id的{@link String}
     * @param creator 表示创建者的{@link String}
     * @param creationTime 表示创建时间的{@link LocalDateTime}
     * @param lastModifier 表示最后的修改者的{@link String}
     * @param lastModificationTime 表示最后修改时间的{@link LocalDateTime}
     */
    public DefaultTaskTemplate(String name, String description, String tenantId, List<TaskTemplateProperty> properties,
            String id, String creator, LocalDateTime creationTime, String lastModifier,
            LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.description = description;
        this.tenantId = tenantId;
        this.properties = properties;
    }

    /**
     * 获取任务模板名称
     *
     * @return 任务模板名称 {@link String}
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * 获取任务模板描述
     *
     * @return 任务模板描述 {@link String}
     */
    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String tenantId() {
        return this.tenantId;
    }

    /**
     * 获取全部任务模板属性
     *
     * @return 任务模板属性列表 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
     */
    @Override
    public List<TaskTemplateProperty> properties() {
        return this.properties;
    }

    /**
     * 根据任务模板属性名称获取任务模板属性
     *
     * @param name 任务模板属性名称 {@link String}
     * @return 任务模板属性 {@link TaskTemplateProperty}
     */
    @Override
    public TaskTemplateProperty property(String name) {
        return this.properties.stream().filter(p -> StringUtils.equals(name, p.name())).findFirst().orElse(null);
    }

    /**
     * 任务模板构造器
     */
    public static class Builder
            extends AbstractDomainObjectBuilder<TaskTemplate, TaskTemplate.Builder> implements TaskTemplate.Builder {
        private String name;

        private String description;

        private String tenantId;

        private List<TaskTemplateProperty> properties;

        /**
         * 设置任务模板名称
         *
         * @param name 任务模板名称 {@link String}
         * @return 任务模板构建器 {@link Builder}
         */
        @Override
        public TaskTemplate.Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置任务模板描述
         *
         * @param description 任务模板描述 {@link String}
         * @return 任务模板构建器 {@link Builder}
         */
        @Override
        public TaskTemplate.Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置任务模板属性
         *
         * @param properties 任务模板属性 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
         * @return 任务模板构建器 {@link Builder}
         */
        @Override
        public TaskTemplate.Builder properties(List<TaskTemplateProperty> properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public TaskTemplate.Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * 构建领域对象的新实例。
         *
         * @return 表示新构建的领域对象实例的 {@link TaskTemplate}。
         */
        @Override
        public TaskTemplate build() {
            return new DefaultTaskTemplate(name, description, tenantId, properties, this.id(), this.creator(),
                    this.creationTime(), this.lastModifier(), this.lastModificationTime());
        }
    }

    /**
     * 任务模板声明
     */
    public static class Declaration implements TaskTemplate.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<String> description;

        private final UndefinableValue<List<TaskTemplateProperty.Declaration>> properties;

        private final UndefinableValue<String> parentTemplateId;

        public Declaration(UndefinableValue<String> name, UndefinableValue<String> description,
                UndefinableValue<List<TaskTemplateProperty.Declaration>> properties,
                UndefinableValue<String> parentTemplateId) {
            this.name = name;
            this.description = description;
            this.properties = properties;
            this.parentTemplateId = parentTemplateId;
        }

        /**
         * 获取任务模板名称的声明
         *
         * @return 任务模板名称的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        /**
         * 获取任务模板描述的声明
         *
         * @return 任务模板描述的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        @Override
        public UndefinableValue<String> description() {
            return this.description;
        }

        /**
         * 获取任务模板属性的声明
         *
         * @return 任务模板属性的声明
         * {@link UndefinableValue}{@code <}{@link List}{@code <}{@link TaskTemplateProperty}{@code >}{@code >}
         */
        @Override
        public UndefinableValue<List<TaskTemplateProperty.Declaration>> properties() {
            return this.properties;
        }

        /**
         * 获取父模板id的声明
         *
         * @return 父模板id的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        @Override
        public UndefinableValue<String> parentTemplateId() {
            return this.parentTemplateId;
        }

        /**
         * 任务模板声明构造器
         */
        public static class Builder implements TaskTemplate.Declaration.Builder {
            private UndefinableValue<String> name = UndefinableValue.undefined();

            private UndefinableValue<String> description = UndefinableValue.undefined();

            private UndefinableValue<List<TaskTemplateProperty.Declaration>> properties = UndefinableValue.undefined();

            private UndefinableValue<String> parentTemplateId = UndefinableValue.undefined();

            /**
             * 设置任务模板名称
             *
             * @param name 任务模板名称 {@link String}
             * @return 任务模板构建器 {@link Builder}
             */
            @Override
            public TaskTemplate.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            /**
             * 设置任务模板描述
             *
             * @param description 任务模板描述 {@link String}
             * @return 任务模板构建器 {@link TaskTemplate.Builder}
             */
            @Override
            public TaskTemplate.Declaration.Builder description(String description) {
                this.description = UndefinableValue.defined(description);
                return this;
            }

            /**
             * 设置任务模板属性
             *
             * @param properties 任务模板属性 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
             * @return 任务模板构建器 {@link TaskTemplate.Builder}
             */
            @Override
            public TaskTemplate.Declaration.Builder properties(List<TaskTemplateProperty.Declaration> properties) {
                this.properties = UndefinableValue.defined(properties);
                return this;
            }

            /**
             * 设置父模板id
             *
             * @param parentTemplateId 父模板id {@link String}
             * @return 父模板id构建器 {@link TaskTemplate.Builder}
             */
            @Override
            public TaskTemplate.Declaration.Builder parentTemplateId(String parentTemplateId) {
                this.parentTemplateId = UndefinableValue.defined(parentTemplateId);
                return this;
            }

            /**
             * 构建任务模板的声明
             *
             * @return 任务模板声明 {@link Declaration}
             */
            @Override
            public TaskTemplate.Declaration build() {
                return new Declaration(this.name, this.description, this.properties, parentTemplateId);
            }
        }
    }
}
