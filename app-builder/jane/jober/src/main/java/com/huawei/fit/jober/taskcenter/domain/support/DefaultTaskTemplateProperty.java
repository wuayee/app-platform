/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import com.huawei.fit.jane.task.domain.AbstractDomainObject;
import com.huawei.fit.jane.task.domain.AbstractDomainObjectBuilder;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 为 {@link TaskTemplateProperty} 提供实现
 *
 * @author yWX1299574
 * @since 2023-12-05 09:40
 */
public class DefaultTaskTemplateProperty extends AbstractDomainObject implements TaskTemplateProperty {
    private final String name;

    private final PropertyDataType dataType;

    private final int sequence;

    private final String taskTemplateId;

    /**
     * 默认参数模板属性
     *
     * @param name 表示名称的{@link String}
     * @param dataType 表示数据类型的{@link PropertyDataType}
     * @param sequence 表示sequence的整数{@code int}
     * @param taskTemplateId 表示任务模板id的{@link String}
     * @param id 表示id的{@link String}
     * @param creator 表示创建者的{@link String}
     * @param creationTime 表示创建时间的{@link LocalDateTime}
     * @param lastModifier 表示最后一个修改者的{@link String}
     * @param lastModificationTime 表示最后的修改时间的{@link LocalDateTime}
     */
    public DefaultTaskTemplateProperty(String name, PropertyDataType dataType, int sequence, String taskTemplateId,
            String id, String creator, LocalDateTime creationTime, String lastModifier,
            LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.dataType = dataType;
        this.sequence = sequence;
        this.taskTemplateId = taskTemplateId;
    }

    /**
     * 获取任务模板属性名称
     *
     * @return 任务模板配置名称 {@link String}
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * 获取任务模板属性数据类型
     *
     * @return 任务模板属性数据类型 {@link PropertyDataType}
     */
    @Override
    public PropertyDataType dataType() {
        return this.dataType;
    }

    /**
     * 获取任务模板属性序号
     *
     * @return 任务模板属性的序号 32位整数
     */
    @Override
    public int sequence() {
        return this.sequence;
    }

    @Override
    public String column() {
        return StringUtils.toLowerCase(Enums.toString(this.dataType())) + '_' + this.sequence();
    }

    @Override
    public String taskTemplateId() {
        return this.taskTemplateId;
    }

    /**
     * 任务模板属性构造器
     */
    public static class Builder extends AbstractDomainObjectBuilder<TaskTemplateProperty, TaskTemplateProperty.Builder>
            implements TaskTemplateProperty.Builder {
        private String name;

        private PropertyDataType dataType;

        private int sequence;

        private String taskTemplateId;

        /**
         * 设置任务模板属性名称
         *
         * @param name 任务模板属性名称 {@link String}
         * @return 构建器 {@link Builder}
         */
        @Override
        public TaskTemplateProperty.Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置任务模板属性数据类型
         *
         * @param dataType 任务模板属性名称 {@link PropertyDataType}
         * @return 构建器 {@link Builder}
         */
        @Override
        public TaskTemplateProperty.Builder dataType(PropertyDataType dataType) {
            this.dataType = dataType;
            return this;
        }

        /**
         * 设置任务模板属性序号
         *
         * @param sequence 任务模板属性序号 32位整数
         * @return 构建器 {@link Builder}
         */
        @Override
        public TaskTemplateProperty.Builder sequence(int sequence) {
            this.sequence = sequence;
            return this;
        }

        @Override
        public TaskTemplateProperty.Builder taskTemplateId(String taskTemplateId) {
            this.taskTemplateId = taskTemplateId;
            return this;
        }

        /**
         * 构建领域对象的新实例。
         *
         * @return 表示新构建的领域对象实例的 {@link TaskTemplateProperty}。
         */
        @Override
        public TaskTemplateProperty build() {
            return new DefaultTaskTemplateProperty(this.name, this.dataType, this.sequence, this.taskTemplateId,
                    this.id(), this.creator(), this.creationTime(), this.lastModifier(), this.lastModificationTime());
        }
    }

    /**
     * 任务模板属性声明
     */
    public static class Declaration implements TaskTemplateProperty.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<String> dataType;

        private final UndefinableValue<String> id;

        public Declaration(UndefinableValue<String> id, UndefinableValue<String> name,
                UndefinableValue<String> dataType) {
            this.name = name;
            this.dataType = dataType;
            this.id = id;
        }

        @Override
        public UndefinableValue<String> id() {
            return this.id;
        }

        /**
         * 获取任务模板属性声明名称
         *
         * @return 任务模板属性声明名称 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        /**
         * 获取任务模板属性声明数据类型
         *
         * @return 任务模板属性声明数据类型 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        @Override
        public UndefinableValue<String> dataType() {
            return this.dataType;
        }

        /**
         * 任务模板属性构造器
         */
        public static class Builder implements TaskTemplateProperty.Declaration.Builder {
            private UndefinableValue<String> name = UndefinableValue.undefined();

            private UndefinableValue<String> dataType = UndefinableValue.undefined();

            private UndefinableValue<String> id = UndefinableValue.undefined();

            /**
             * 设置任务模板属性声明名称
             *
             * @param name 任务模板属性声明名称
             * @return 构建器 {@link Builder}
             */
            @Override
            public TaskTemplateProperty.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            /**
             * 设置任务模板属性声明数据类型
             *
             * @param dataType 任务模板属性声明名称
             * @return 构建器 {@link Builder}
             */
            @Override
            public TaskTemplateProperty.Declaration.Builder dataType(String dataType) {
                this.dataType = UndefinableValue.defined(dataType);
                return this;
            }

            @Override
            public TaskTemplateProperty.Declaration.Builder id(String id) {
                this.id = UndefinableValue.defined(id);
                return this;
            }

            /**
             * 构建任务模板属性声明
             *
             * @return 任务模板属性声明 {@link Declaration}
             */
            @Override
            public TaskTemplateProperty.Declaration build() {
                return new Declaration(this.id, this.name, this.dataType);
            }
        }
    }
}
