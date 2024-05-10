/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultPrimaryKey;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示任务定义的主键。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-25
 */
public interface PrimaryKey {
    /**
     * 获取指定用户属性对应的主键值。
     *
     * @param info 表示待获取主键值的用户属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示主键值的 {@link PrimaryValue}。
     */
    PrimaryValue getPrimaryValue(Map<String, Object> info);

    /**
     * 根据指定用户属性获取任务实例的唯一标识。
     *
     * @param executor 表示 SQL 的执行器的 {@link DynamicSqlExecutor}。
     * @param info 表示用户属性的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param table 表示数据表的名称的 {@link String}。
     * @return 若存在对应的任务实例，则为表示该任务实例唯一标识的 {@link String}，否则为 {@code null}。
     */
    String selectId(DynamicSqlExecutor executor, Map<String, Object> info, String table);

    /**
     * selectIds
     *
     * @param executor executor
     * @param instances objects
     * @param table table
     * @return Map<PrimaryValue, String>
     */
    Map<PrimaryValue, String> selectIds(DynamicSqlExecutor executor, List<TaskInstance> instances, String table);

    /**
     * 获取指定任务定义的用户数据的主键。
     * <p>当用户定义中没有主键时，只能返回 {@code null} 而非一个空的主键对象。</p>
     * <p>因为空的主键对象将不记录任何数据列，会导致在计算主键值时都是空的 {@link Map}，结果是所有的任务实例都有相同的主键值（空映射）。</p>
     * <p>而实际情况是，当未定义任何主键时，任意任务实例应都是不同的，与结果刚好相反。</p>
     * <p>因此，当未定义主键时，必须返回 {@code null} 主键，由调用方处理这种的情况（例如，跳过针对主键的处理逻辑）。</p>
     *
     * @param task 表示待获取主键的任务定义的 {@link TaskEntity}。
     * @return 表示任务定义的主键的 {@link PrimaryKey}。
     */
    static PrimaryKey of(TaskEntity task) {
        List<TaskProperty> primaryProperties = task.getProperties().stream()
                .filter(TaskProperty::identifiable)
                .collect(Collectors.toList());
        if (primaryProperties.isEmpty()) {
            return null;
        } else {
            return new DefaultPrimaryKey(task, primaryProperties);
        }
    }
}
