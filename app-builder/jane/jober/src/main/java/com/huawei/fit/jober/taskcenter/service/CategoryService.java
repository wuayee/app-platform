/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyCategoryDeclaration;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 为任务实例的类目提供管理。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-18
 */
public interface CategoryService {
    /**
     * 获取指定属性的类目匹配器。
     *
     * @param propertyIds 表示待获取类目匹配器的属性的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示以属性唯一标识作为键的属性类目匹配器的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link PropertyCategory}{@code >>}。
     */
    Map<String, List<PropertyCategory>> matchers(List<String> propertyIds);

    /**
     * saveMatchers
     *
     * @param categories categories
     * @return Map<String, List < PropertyCategory>>
     */
    Map<String, List<PropertyCategory>> saveMatchers(Map<String, List<PropertyCategoryDeclaration>> categories);

    /**
     * 保存对象与类目的关系。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param categories 表示与对象关联的类目的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void saveUsages(String objectType, String objectId, List<String> categories, OperationContext context);

    /**
     * 获取指定对象的类目。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的列表的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示类目的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> listUsages(String objectType, String objectId, OperationContext context);

    /**
     * 获取指定对象的类目。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectIds 表示对象的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示以对象唯一标识为键的类目的列表的 {@link Map}{@code <}{@link String}{@code , }
     * {@link List}{@code <}{@link String}{@code >>}。
     */
    Map<String, List<String>> listUsages(String objectType, List<String> objectIds, OperationContext context);

    /**
     * deleteByProperty
     *
     * @param propertyId propertyId
     */
    void deleteByProperty(String propertyId);

    /**
     * deleteByTaskIds
     *
     * @param taskIds taskIds
     */
    void deleteByTaskIds(Collection<String> taskIds);

    /**
     * listByNames
     *
     * @param categoryNames categoryNames
     * @return List<CategoryEntity>
     */
    List<CategoryEntity> listByNames(Collection<String> categoryNames);

    /**
     * listByIds
     *
     * @param categoryIds categoryIds
     * @return List<CategoryEntity>
     */
    List<CategoryEntity> listByIds(Collection<String> categoryIds);
}
