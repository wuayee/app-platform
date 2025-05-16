/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.adapter;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.dao.po.SourceObject;
import modelengine.fit.jober.taskcenter.declaration.SourceDeclaration;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceType;

import java.util.List;
import java.util.Map;

/**
 * 任务数据源适配器接口。
 *
 * @author 陈镕希
 * @since 2023-08-14
 */
public interface SourceAdapter {
    /**
     * 获取Adapter对应数据源类型的 {@link SourceType}。
     *
     * @return 表示Adapter对应数据源类型的 {@link SourceType}。
     */
    SourceType getType();

    /**
     * 保存数据源扩展字段。
     *
     * @param sourceObject 表示数据对象的 {@link SourceObject}。
     * @param sourceDeclaration 表示任务数据源声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return SourceEntity
     */
    SourceEntity createExtension(SourceObject sourceObject, SourceDeclaration sourceDeclaration,
            OperationContext context);

    /**
     * 更新任务数据源扩展字段。
     *
     * @param sourceObject 表示待更新的任务数据源对象的 {@link SourceObject}。
     * @param declaration 表示任务数据源声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patchExtension(SourceObject sourceObject, SourceDeclaration declaration, OperationContext context);

    /**
     * 删除数据源扩展字段。
     *
     * @param sourceId 表示数据源唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void deleteExtension(String sourceId, OperationContext context);

    /**
     * 列出指定数据源扩展字段。
     *
     * @param sourceObject 表示数据对象的 {@link SourceObject}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示扩展后的数据源的 {@link SourceEntity}。
     */
    SourceEntity retrieveExtension(SourceObject sourceObject, OperationContext context);

    /**
     * 扩展数据源列表。
     *
     * @param sourceObjects 表示数据对象列表的 {@link List}{@code <}{@link SourceObject}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示扩展后 {@code <}任务唯一标识{@code ,}数据源列表{@code >}的
     * {@link Map}{@code <}{@link String}{@code ,}{@link List}{@code <}{@link SourceEntity}{@code >}{@code >}。
     */
    Map<String, List<SourceEntity>> listExtension(List<SourceObject> sourceObjects, OperationContext context);
}
