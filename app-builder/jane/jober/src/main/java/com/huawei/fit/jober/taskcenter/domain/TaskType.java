/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.support.DefaultTaskType;

import modelengine.fitframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 表示任务的类型。
 *
 * @author 梁济时
 * @since 2023-09-12
 */
public interface TaskType extends DomainObject {
    /**
     * 获取任务类型的名称。
     *
     * @return 表示任务类型名称的 {@link String}。
     */
    String name();

    /**
     * 获取父类型的唯一标识。
     *
     * @return 表示父类型唯一标识的 {@link String}。
     */
    String parentId();

    /**
     * 获取子任务类型。
     *
     * @return 表示子任务类型的列表的 {@link List}{@code <}{@link TaskType}{@code >}。
     */
    List<TaskType> children();

    /**
     * 获取任务类型配置的数据源的列表。
     *
     * @return 表示数据源列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
     */
    List<SourceEntity> sources();

    /**
     * 遍历任务类型树。
     *
     * @param types 表示待遍历的任务类型树的根节点的 {@link Collection}{@code <}{@link TaskType}{@code >}。
     * @param consumer 表示当遍历到任务类型的消费方法的 {@link Consumer}{@code <}{@link TaskType}{@code >}。
     */
    static void traverse(Collection<TaskType> types, Consumer<TaskType> consumer) {
        if (CollectionUtils.isEmpty(types) || consumer == null) {
            return;
        }
        Queue<TaskType> queue = new LinkedList<>(types);
        while (!queue.isEmpty()) {
            TaskType type = queue.poll();
            queue.addAll(type.children());
            consumer.accept(type);
        }
    }

    /**
     * 递归查找指定唯一标识的任务类型。
     *
     * @param types 表示待查找的任务类型的集合的 {@link Collection}{@code <}{@link TaskType}{@code >}。
     * @param typeId 表示待查找的任务类型的唯一标识的 {@link String}。
     * @return 若存在该唯一标识的任务类型，则为表示该任务类型的 {@link TaskType}，否则为 {@code null}。
     */
    static TaskType lookup(Collection<TaskType> types, String typeId) {
        return lookup(types, type -> Entities.match(type.id(), typeId));
    }

    /**
     * 递归查找任务类型。
     *
     * @param types 表示待查找的任务类型的集合的 {@link Collection}{@code <}{@link TaskType}{@code >}。
     * @param predicate 表示任务类型的匹配方法的 {@link Predicate}。
     * @return 若存在匹配的任务类型，则为表示该任务类型的 {@link TaskType}，否则为 {@code null}。
     */
    static TaskType lookup(Collection<TaskType> types, Predicate<TaskType> predicate) {
        if (CollectionUtils.isEmpty(types)) {
            return null;
        }
        Queue<TaskType> queue = new LinkedList<>(types);
        while (!queue.isEmpty()) {
            TaskType type = queue.poll();
            if (predicate.test(type)) {
                return type;
            }
            queue.addAll(type.children());
        }
        return null;
    }

    /**
     * 为任务类型提供构建器。
     *
     * @author 梁济时
     * @since 2023-09-12
     */
    interface Builder extends DomainObject.Builder<TaskType, Builder> {
        /**
         * 设置任务类型的名称。
         *
         * @param name 表示任务类型的名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置父类型的唯一标识。
         *
         * @param parentId 表示父类型唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder parentId(String parentId);

        /**
         * 设置子任务类型。
         *
         * @param children 表示数据源列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder children(List<TaskType> children);

        /**
         * 设置任务类型上配置的数据源的列表。
         *
         * @param sources 表示数据源列表的 {@link SourceEntity}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder sources(List<SourceEntity> sources);
    }

    /**
     * 返回一个构建器，用以构建任务类型的新实例。
     *
     * @return 表示用以构建任务类型新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTaskType.Builder();
    }

    /**
     * 为 {@link TaskType} 提供声明。
     *
     * @author 梁济时
     * @since 2023-09-13
     */
    interface Declaration {
        /**
         * 获取任务类型的名称。
         *
         * @return 表示任务类型的名称的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> name();

        /**
         * 获取父任务类型的唯一标识。
         *
         * @return 表示父任务类型唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> parentId();

        /**
         * 获取任务类型的来源id。
         *
         * @return 表示父任务类型来源id的 {@link UndefinableValue}{@code <}{@link List<String>}{@code >}。
         */
        UndefinableValue<List<String>> sourceIds();

        /**
         * 为任务类型的声明提供构建器。
         *
         * @author 梁济时
         * @since 2023-09-13
         */
        interface Builder {
            /**
             * 设置任务类型的名称。
             *
             * @param name 表示任务类型的名称的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder name(String name);

            /**
             * 设置父任务类型的唯一标识。
             *
             * @param parentId 表示父任务类型唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder parentId(String parentId);

            /**
             * 设置任务类型的来源id。
             *
             * @param sourceIds 表示任务类型的来源id的 {@link List<String>}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder sourceIds(List<String> sourceIds);

            /**
             * 构建任务类型的声明。
             *
             * @return 表示新构建的任务类型的声明的实例的 {@link Declaration}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建任务类型声明的新实例。
         *
         * @return 表示用以构建任务类型声明的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskType.Declaration.Builder();
        }
    }

    /**
     * 为 {@link TaskType} 提供存储能力。
     *
     * @author 梁济时
     * @since 2023-09-13
     */
    interface Repo {
        /**
         * 创建任务类型。
         *
         * @param taskId 表示任务类型所属任务的唯一标识的 {@link String}。
         * @param declaration 表示任务类型的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的任务类型的 {@link TaskType}。
         */
        TaskType create(String taskId, Declaration declaration, OperationContext context);

        /**
         * 为指定的任务类型打一个补丁。
         *
         * @param taskId 表示任务类型所属任务的唯一标识的 {@link String}。
         * @param id 表示待修补的任务类型的唯一标识的 {@link String}。
         * @param declaration 表示待修补的内容的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(String taskId, String id, Declaration declaration, OperationContext context);

        /**
         * 删除指定唯一标识的任务类型。
         *
         * @param taskId 表示任务类型所属任务的唯一标识的 {@link String}。
         * @param id 表示待删除的任务类型的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String taskId, String id, OperationContext context);

        /**
         * 删除指定任务中定义的所有类型。
         *
         * @param taskId 表示任务类型所属任务的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void deleteByTasks(String taskId, OperationContext context);

        /**
         * 检查指定任务定义中是否包含任务类型。
         *
         * @param taskId 表示待检查的任务类型的唯一标识的 {@link String}。
         * @return 若存在任务类型，则为 {@code true}，否则为 {@code false}。
         */
        boolean exists(String taskId);

        /**
         * 检索指定任务中指定唯一标识的任务类型。
         *
         * @param taskId 表示任务类型所属任务的唯一标识的 {@link String}。
         * @param id 表示待检索的任务类型的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示该唯一标识对应的任务类型的 {@link TaskType}。
         */
        TaskType retrieve(String taskId, String id, OperationContext context);

        /**
         * 列出指定任务的类型信息。
         *
         * @param taskId 表示任务唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示该任务的类型信息的列表的 {@link List}{@code <}{@link TaskType}{@code >}。
         */
        List<TaskType> list(String taskId, OperationContext context);

        /**
         * 列出指定任务的类型信息。
         *
         * @param taskIds 表示任务的唯一标识的集合的 {@link Collection}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示该任务的类型信息的列表的
         * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link TaskType}{@code >>}。
         */
        Map<String, List<TaskType>> list(Collection<String> taskIds, OperationContext context);
    }
}

