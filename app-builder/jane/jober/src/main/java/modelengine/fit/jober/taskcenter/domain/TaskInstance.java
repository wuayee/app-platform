/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.taskcenter.domain.support.DefaultTaskInstance;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;

import java.util.List;
import java.util.Map;

/**
 * 表示任务实例。
 *
 * @author 梁济时
 * @since 2023-12-12
 */
public interface TaskInstance {
    /**
     * 获取任务实例的唯一标识。
     *
     * @return 表示任务实例唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取任务实例所属的任务定义。
     *
     * @return 表示任务定义的 {@link TaskEntity}。
     */
    TaskEntity task();

    /**
     * 获取任务实例的类型。
     *
     * @return 表示任务实例的类型的 {@link TaskType}。
     */
    TaskType type();

    /**
     * 获取任务实例所属的数据源。
     *
     * @return 表示任务所属数据源的 {@link SourceEntity}。
     */
    SourceEntity source();

    /**
     * 获取任务实例的数据。
     *
     * @return 表示任务实例的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> info();

    /**
     * 获取任务实例拥有的标签。
     *
     * @return 获取实例拥有的标签的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> tags();

    /**
     * 获取任务实例所属的类目。
     *
     * @return 表示任务实例所属类目的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> categories();

    /**
     * 获取指定任务实例与当前任务实例的属性差异。
     *
     * @param another 表示待与当前任务实例计算差异的另一个任务实例的 {@link TaskInstance}。
     * @return 表示该任务实例与当前任务实例的属性差异的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> diff(TaskInstance another);

    /**
     * 为任务实例提供构建器。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    interface Builder {
        /**
         * 设置任务实例的唯一标识。
         *
         * @param id 表示任务实例唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder id(String id);

        /**
         * 设置任务实例所属的任务定义。
         *
         * @param task 表示任务定义的 {@link TaskEntity}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder task(TaskEntity task);

        /**
         * 设置任务实例的类型。
         *
         * @param type 表示任务实例的类型的 {@link TaskType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(TaskType type);

        /**
         * 设置任务实例所属的数据源。
         *
         * @param source 表示任务所属数据源的 {@link SourceEntity}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder source(SourceEntity source);

        /**
         * 设置任务实例的数据。
         *
         * @param info 表示任务实例的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder info(Map<String, Object> info);

        /**
         * 设置任务实例拥有的标签。
         *
         * @param tags 获取实例拥有的标签的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tags(List<String> tags);

        /**
         * 设置任务实例所属的类目。
         *
         * @param categories 表示任务实例所属类目的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder categories(List<String> categories);

        /**
         * 构建任务实例的新实例。
         *
         * @return 表示任务实例的新实例的 {@link TaskInstance}。
         */
        TaskInstance build();
    }

    /**
     * 返回一个构建器，用以构建任务实例的新实例。
     *
     * @return 表示用以构建任务实例新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTaskInstance.Builder();
    }

    /**
     * 为任务实例提供声明。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    interface Declaration {
        /**
         * 获取任务实例的类型的唯一标识。
         *
         * @return 表示任务类型唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> typeId();

        /**
         * 获取任务实例所属的数据源。
         *
         * @return 表示任务数据源唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> sourceId();

        /**
         * 获取任务实例的数据。
         *
         * @return 表示任务实例的数据的
         * {@link UndefinableValue}{@code <}{@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >>}。
         */
        UndefinableValue<Map<String, Object>> info();

        /**
         * 获取任务实例拥有的标签。
         *
         * @return 获取实例拥有的标签的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> tags();

        /**
         * 为任务实例的声明提供构建器。
         *
         * @author 梁济时
         * @since 2023-12-12
         */
        interface Builder {
            /**
             * 设置任务实例的类型。
             *
             * @param typeId 表示任务类型的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link TaskInstance.Builder}。
             */
            Builder type(String typeId);

            /**
             * 设置任务实例所属的数据源。
             *
             * @param sourceId 表示任务数据源的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link TaskInstance.Builder}。
             */
            Builder source(String sourceId);

            /**
             * 设置任务实例的数据。
             *
             * @param info 表示任务实例的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
             * @return 表示当前构建器的 {@link TaskInstance.Builder}。
             */
            Builder info(Map<String, Object> info);

            /**
             * 设置任务实例拥有的标签。
             *
             * @param tags 获取实例拥有的标签的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link TaskInstance.Builder}。
             */
            Builder tags(List<String> tags);

            /**
             * 构建任务实例的新实例。
             *
             * @return 表示任务实例的新实例的 {@link TaskInstance}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建任务实例声明的新实例。
         *
         * @return 表示用以构建任务实例定义的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskInstance.Declaration.Builder(null);
        }

        /**
         * 返回一个构建器，以当前声明作为初始值，用以构建任务实例声明的新实例。
         *
         * @return 表示用以构建任务实例定义的构建器的 {@link Builder}。
         */
        default Builder copy() {
            return new DefaultTaskInstance.Declaration.Builder(this);
        }
    }

    /**
     * 为任务实例提供查询条件。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    interface Filter {
        /**
         * 获取待查询的任务实例的唯一标识。（精确查询）
         *
         * @return 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> ids();

        /**
         * 获取待查询的任务实例所属任务类型的唯一标识。（精确查询）
         *
         * @return 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> typeIds();

        /**
         * 获取待查询的任务实例所属数据源的唯一标识。（精确查询）
         *
         * @return 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> sourceIds();

        /**
         * 获取待查询的任务实例的数据。
         *
         * @return 表示待查询的任务实例的值的
         * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
         */
        Map<String, List<String>> infos();

        /**
         * 获取待查询的任务实例拥有的标签。（精确查询）
         *
         * @return 表示待查询的标签的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> tags();

        /**
         * 表示待查询的类目。（精确查询）
         *
         * @return 表示待查询的类目的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> categories();

        /**
         * 获取一个值，该值指示是否查询已删除的任务。
         *
         * @return 若查询已删除的任务，则为 {@code true}，否则为 {@code false}。
         */
        boolean deleted();

        /**
         * 为任务实例的查询条件提供构建器。
         *
         * @author 梁济时
         * @since 2023-12-12
         */
        interface Builder {
            /**
             * 获取待查询的任务实例的唯一标识。（精确查询）
             *
             * @param ids 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder ids(List<String> ids);

            /**
             * 获取待查询的任务实例所属任务类型的唯一标识。（精确查询）
             *
             * @param typeIds 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder typeIds(List<String> typeIds);

            /**
             * 获取待查询的任务实例所属数据源的唯一标识。（精确查询）
             *
             * @param sourceIds 表示待查询的任务实例的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder sourceIds(List<String> sourceIds);

            /**
             * 获取待查询的任务实例的数据。
             *
             * @param infos 表示待查询的任务实例的值的
             * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder infos(Map<String, List<String>> infos);

            /**
             * 获取待查询的任务实例拥有的标签。（精确查询）
             *
             * @param tags 表示待查询的标签的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tags(List<String> tags);

            /**
             * 表示待查询的类目。（精确查询）
             *
             * @param categories 表示待查询的类目的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder categories(List<String> categories);

            /**
             * 设置一个值，该值指示是否查询已删除的任务。
             *
             * @param isDeleted 若查询已删除的任务，则为 {@code true}，否则为 {@code false}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder deleted(boolean isDeleted);

            /**
             * 构建新的任务实例的查询条件。
             *
             * @return 表示新构建的任务实例查询条件的 {@link Filter}。
             */
            Filter build();
        }

        /**
         * 返回一个构建器，用以构建任务实例查询条件的新实例。
         *
         * @return 表示用以构建任务实例查询条件的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskInstance.Filter.Builder();
        }
    }

    /**
     * 为任务实例提供数据持久化能力。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    interface Repo {
        /**
         * 创建任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param declaration 表示任务实例的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的任务实例的 {@link TaskInstance}。
         */
        TaskInstance create(TaskEntity task, Declaration declaration, OperationContext context);

        /**
         * 修改任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param id 表示任务实例的唯一标识的 {@link String}。
         * @param declaration 表示任务实例的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(TaskEntity task, String id, Declaration declaration, OperationContext context);

        /**
         * 删除任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param id 表示任务实例的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(TaskEntity task, String id, OperationContext context);

        /**
         * 删除指定数据源的任务实例。
         *
         * @param task 表示任务实体的{@link TaskEntity}
         * @param sourceId 表示数据源id的{@link String}
         * @param context 表示操作上下文的{@link OperationContext}
         */
        void deleteBySource(TaskEntity task, String sourceId, OperationContext context);

        /**
         * 检索任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param id 表示任务实例的唯一标识的 {@link String}。
         * @param isDeleted 若为 {@code true}，则查询已删除的实例，否则不查询已删除的实例。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示检索到的任务实例的 {@link TaskInstance}。
         */
        TaskInstance retrieve(TaskEntity task, String id, boolean isDeleted, OperationContext context);

        /**
         * 查询任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param filter 表示任务实例的查询条件的 {@link Filter}。
         * @param pagination 表示待查询的结果集在查询到的全量结果集中的数据范围的 {@link Pagination}。
         * @param orderBys 表示排序的 {@link List}{@code <}{@link OrderBy}{@code >}。
         * @param viewMode 表示待查询的视图的类型的 {@link ViewMode}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的分页结果集的 {@link PagedResultSet}{@code <}{@link TaskInstance}{@code >}。
         */
        PagedResultSet<TaskInstance> list(TaskEntity task, Filter filter, Pagination pagination,
                List<OrderBy> orderBys, ViewMode viewMode, OperationContext context);

        /**
         * 统计任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param filter 表示任务实例的查询条件的 {@link Filter}。
         * @param column 表示需要统计的列名的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的统计结果的 {@link Map}{@code <}{@link String}{@code ,}{@link Long}{@code >}。
         */
        Map<String, Long> statistics(TaskEntity task, Filter filter, String column, OperationContext context);

        /**
         * 恢复指定任务实例。
         *
         * @param task 表示任务实例所属的任务定义的 {@link TaskEntity}。
         * @param id 表示任务实例的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void recover(TaskEntity task, String id, OperationContext context);

        /**
         * 获取 meta 唯一标识。
         *
         * @param id 表示id的{@link String}
         * @return 表示 meta 唯一标识的 {@link String}。
         */
        String getMetaId(String id);
    }
}
