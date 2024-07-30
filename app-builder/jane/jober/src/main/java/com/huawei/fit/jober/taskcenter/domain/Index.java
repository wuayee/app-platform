/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.support.DefaultIndex;

import java.util.List;

/**
 * 为元数据提供索引。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-04
 */
public interface Index extends DomainObject {
    /**
     * 获取索引的名称。
     *
     * @return 表示索引名称的 {@link String}。
     */
    String name();

    /**
     * 获取索引所属的任务定义。
     *
     * @return 表示索引所属的任务定义的 {@link TaskEntity}。
     */
    TaskEntity task();

    /**
     * 获取索引中包含的属性的列表。
     *
     * @return 表示属性列表的 {@link List}{@code <}{@link TaskProperty}{@code >}。
     */
    List<TaskProperty> properties();

    /**
     * 为索引提供构建器。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-04
     */
    interface Builder extends DomainObject.Builder<Index, Builder> {
        /**
         * 设置索引的名称。
         *
         * @param name 表示索引名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置索引所属的任务。
         *
         * @param task 表示索引所属的任务的 {@link TaskEntity}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder task(TaskEntity task);

        /**
         * 设置索引中包含的属性的列表。
         *
         * @param properties 表示属性列表的 {@link List}{@code <}{@link TaskProperty}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder properties(List<TaskProperty> properties);
    }

    /**
     * 返回一个构建器，用以构建索引的新实例。
     *
     * @return 表示用以构建索引新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultIndex.Builder();
    }

    /**
     * 为索引提供声明。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-04
     */
    interface Declaration {
        /**
         * 获取索引的名称。
         *
         * @return 表示索引名称的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> name();

        /**
         * 获取索引包含任务属性的唯一标识的属性名称的列表。
         *
         * @return 表示任务属性名称列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> propertyNames();

        /**
         * 为索引声明提供构建器。
         *
         * @author 梁济时 l00815032
         * @since 2024-01-04
         */
        interface Builder {
            /**
             * 设置索引的名称。
             *
             * @param name 表示索引名称的 {@link String}。
             * @return {@link Builder}实例
             */
            Builder name(String name);

            /**
             * 设置索引包含任务属性名称的列表。
             *
             * @param propertyNames 表示任务属性名称列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return {@link Builder}实例
             */
            Builder propertyNames(List<String> propertyNames);

            /**
             * 构建索引声明的新实例。
             *
             * @return 表示新构建的索引声明的实例的 {@link Declaration}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建索引声明的新实例。
         *
         * @return 表示用以构建索引声明实例的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultIndex.Declaration.Builder(null);
        }

        /**
         * 返回一个构建器，以当前索引声明为源数据，用以构建新的索引声明。
         *
         * @return 表示用以构建索引声明实例的构建器的 {@link Builder}。
         */
        default Builder copy() {
            return new DefaultIndex.Declaration.Builder(this);
        }
    }

    /**
     * 为索引提供持久化能力。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-04
     */
    interface Repo {
        /**
         * 创建索引。
         *
         * @param task 表示索引所属的任务定义的 {@link TaskEntity}。
         * @param declaration 表示索引的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的索引的 {@link Index}。
         */
        Index create(TaskEntity task, Declaration declaration, OperationContext context);

        /**
         * 修改索引。
         *
         * @param task 表示索引所属的任务定义的 {@link TaskEntity}。
         * @param id 表示待修改的索引的唯一标识的 {@link String}。
         * @param declaration 表示索引的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(TaskEntity task, String id, Declaration declaration, OperationContext context);

        /**
         * 删除索引。
         *
         * @param task 表示索引所属的任务定义的 {@link TaskEntity}。
         * @param id 表示待删除的索引的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(TaskEntity task, String id, OperationContext context);

        /**
         * 检索索引。
         *
         * @param task 表示索引所属的任务定义的 {@link TaskEntity}。
         * @param id 表示待删除的索引的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示索引的 {@link Index}。
         */
        Index retrieve(TaskEntity task, String id, OperationContext context);

        /**
         * 列出指定任务定义的索引。
         *
         * @param task 表示索引所属的任务定义的 {@link TaskEntity}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示索引的列表的 {@link List}{@code <}{@link Index}{@code >}。
         */
        List<Index> list(TaskEntity task, OperationContext context);

        /**
         * 列出指定任务定义的索引。
         *
         * @param tasks 表示索引所属的任务定义的 {@link List}{@code <}{@link TaskEntity}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示索引的列表的 {@link List}{@code <}{@link Index}{@code >}。
         */
        List<Index> list(List<TaskEntity> tasks, OperationContext context);

        /**
         * 保存任务的索引信息。
         *
         * @param task 表示待保存索引信息的任务定义的 {@link TaskEntity}。
         * @param declarations 表示任务定义的索引信息的 {@link List}{@code <}{@link Declaration}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void save(TaskEntity task, List<Index.Declaration> declarations, OperationContext context);
    }
}
