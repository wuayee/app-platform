/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.support.DefaultTaskTemplate;
import com.huawei.fit.jober.taskcenter.filter.TaskTemplateFilter;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.List;

/**
 * 表示任务模板
 *
 * @author 姚江
 * @since 2023-12-04
 */
public interface TaskTemplate extends DomainObject {
    /**
     * 获取任务模板名称
     *
     * @return 任务模板名称 {@link String}
     */
    String name();

    /**
     * 获取任务模板描述
     *
     * @return 任务模板描述 {@link String}
     */
    String description();

    /**
     * 获取任务模板租户信息
     *
     * @return 任务模板描述 {@link String}
     */
    String tenantId();

    /**
     * 获取全部任务模板属性
     *
     * @return 任务模板属性列表 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
     */
    List<TaskTemplateProperty> properties();

    /**
     * 根据任务模板属性名称获取任务模板属性
     *
     * @param name 任务模板属性名称 {@link String}
     * @return 任务模板属性 {@link TaskTemplateProperty}
     */
    TaskTemplateProperty property(String name);

    /**
     * 为任务模板提供构建器
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Builder extends DomainObject.Builder<TaskTemplate, TaskTemplate.Builder> {
        /**
         * 设置任务模板名称
         *
         * @param name 任务模板名称 {@link String}
         * @return 任务模板构建器 {@link Builder}
         */
        Builder name(String name);

        /**
         * 设置任务模板描述
         *
         * @param description 任务模板描述 {@link String}
         * @return 任务模板构建器 {@link Builder}
         */
        Builder description(String description);

        /**
         * 设置任务模板租户唯一标识
         *
         * @param tenantId 任务模板租户唯一标识 {@link String}
         * @return 任务模板构建器 {@link Builder}
         */
        Builder tenantId(String tenantId);

        /**
         * 设置任务模板属性
         *
         * @param properties 任务模板属性 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
         * @return 任务模板构建器 {@link Builder}
         */
        Builder properties(List<TaskTemplateProperty> properties);
    }

    /**
     * 返回一个构建器，用以构建任务模板声明的新实例。
     *
     * @return 表示用以构建任务模板声明的构建器的 {@link TaskTemplate.Builder}。
     */
    static Builder custom() {
        return new DefaultTaskTemplate.Builder();
    }

    /**
     * 为任务模板提供声明
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Declaration {
        /**
         * 获取任务模板名称的声明
         *
         * @return 任务模板名称的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        UndefinableValue<String> name();

        /**
         * 获取任务模板描述的声明
         *
         * @return 任务模板描述的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}
         */
        UndefinableValue<String> description();

        /**
         * 获取任务模板属性的声明
         *
         * @return 任务模板属性的声明
         * {@link UndefinableValue}{@code <}{@link List}{@code <}{@link TaskTemplateProperty}{@code >}{@code >}
         */
        UndefinableValue<List<TaskTemplateProperty.Declaration>> properties();

        /**
         * 获取父模板id的声明
         *
         * @return 父模板id的声明 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> parentTemplateId();

        /**
         * 为任务模板声明提供构建器
         *
         * @author 姚江
         * @since 2023-12-04
         */
        interface Builder {
            /**
             * 设置任务模板名称
             *
             * @param name 任务模板名称 {@link String}
             * @return 任务模板构建器 {@link Builder}
             */
            Builder name(String name);

            /**
             * 设置任务模板描述
             *
             * @param description 任务模板描述 {@link String}
             * @return 任务模板构建器 {@link TaskTemplate.Builder}
             */
            Builder description(String description);

            /**
             * 设置任务模板属性
             *
             * @param properties 任务模板属性 {@link List}{@code <}{@link TaskTemplateProperty}{@code >}
             * @return 任务模板构建器 {@link TaskTemplate.Builder}
             */
            Builder properties(List<TaskTemplateProperty.Declaration> properties);

            /**
             * 设置父模板id
             *
             * @param parentTemplateId 父模板id {@link String}
             * @return 父模板id构建器 {@link TaskTemplate.Builder}
             */
            Builder parentTemplateId(String parentTemplateId);

            /**
             * 构建任务模板的声明
             *
             * @return 任务模板声明 {@link Declaration}
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建任务模板声明的新实例。
         *
         * @return 表示用以构建任务模板声明的构建器的 {@link TaskTemplate.Declaration.Builder}。
         */
        static Builder custom() {
            return new DefaultTaskTemplate.Declaration.Builder();
        }
    }

    /**
     * 为 {@link TaskTemplate} 提供数据库存储能力
     *
     * @author 姚江
     * @since 2023-12-04
     */
    interface Repo {
        /**
         * 创建任务模板
         *
         * @param declaration 任务模板的声明 {@link Declaration}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板 {@link TaskTemplate}
         */
        TaskTemplate create(Declaration declaration, OperationContext context);

        /**
         * 更新任务模板
         *
         * @param id 任务模板id {@link String}
         * @param declaration 任务模板的声明 {@link Declaration}
         * @param context 操作上下文 {@link OperationContext}
         */
        void patch(String id, Declaration declaration, OperationContext context);

        /**
         * 删除任务模板
         *
         * @param id 任务模板id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         */
        void delete(String id, OperationContext context);

        /**
         * 检索任务模板
         *
         * @param id 任务模板id {@link String}
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板 {@link TaskTemplate}
         */
        TaskTemplate retrieve(String id, OperationContext context);

        /**
         * 查询任务模板
         *
         * @param filter 任务模板的过滤器 {@link TaskTemplateFilter}
         * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
         * @param context 操作上下文 {@link OperationContext}
         * @return 任务模板列表
         */
        RangedResultSet<TaskTemplate> list(TaskTemplateFilter filter, long offset, int limit,
                OperationContext context);

        /**
         * 检索任务模板是否存在
         *
         * @param id 任务模板Id {@link String}
         * @return 是否存在 true/false
         */
        boolean exist(String id);

        /**
         * 获取默认模板 ”普通任务“
         *
         * @return 默认模板 {@link String}
         */
        String defaultTemplateId();
    }
}
