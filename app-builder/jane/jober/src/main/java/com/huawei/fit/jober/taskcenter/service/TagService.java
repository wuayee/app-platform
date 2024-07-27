/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;

import java.util.List;
import java.util.Map;

/**
 * 为标签提供管理。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-14
 */
public interface TagService {
    /**
     * 为指定对象添加一个标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param tag 表示待添加的标签的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void add(String objectType, String objectId, String tag, OperationContext context);

    /**
     * 保存标签信息。
     *
     * @param objectType 表示待保存标签的对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param tags 表示对象唯一标识与目标标签列表的映射的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void save(String objectType, String objectId, List<String> tags, OperationContext context);

    /**
     * 保存标签信息。
     *
     * @param objectType 表示待保存标签的对象的类型的 {@link String}。
     * @param tags 表示对象唯一标识与目标标签列表的映射的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void save(String objectType, Map<String, List<String>> tags, OperationContext context);

    /**
     * 移除一个标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param tag 表示待移除的标签的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void remove(String objectType, String objectId, String tag, OperationContext context);

    /**
     * 列出指定对象的标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示对象上定义的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> list(String objectType, String objectId, OperationContext context);

    /**
     * 列出指定对象的标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectIds 表示对象的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示对象上定义的标签的列表的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
     */
    Map<String, List<String>> list(String objectType, List<String> objectIds, OperationContext context);

    /**
     * 根据标签查询到对应的flowId
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param tags 标签
     * @return 使用了标签的flowGraph对应的id
     */
    List<String> list(String objectType, List<String> tags);

    /**
     * 识别指定标签。
     *
     * @param tag 表示待识别的标签的 {@link String}。
     * @param context 上下文
     * @return 识别后的标签
     */
    String identify(String tag, OperationContext context);

    /**
     * 识别指定标签。
     *
     * @param tags 表示待识别的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示标签的名称至唯一标识的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, String> identify(List<String> tags, OperationContext context);
}
