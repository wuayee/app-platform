/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.task;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 为标签提供管理。这里目前先增加对外服务接口，后续拆分task service包后可转为内部依赖
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
    @Genericable(id = "270f4613b57d41eda45c13a08b143222")
    void add(String objectType, String objectId, String tag, OperationContext context);

    /**
     * 保存标签信息。
     *
     * @param objectType 表示待保存标签的对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param tags 表示对象唯一标识与目标标签列表的映射的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "4481319f18ca421388ccc3e087a3e8e5")
    void save(String objectType, String objectId, List<String> tags, OperationContext context);

    /**
     * 保存标签信息。
     *
     * @param objectType 表示待保存标签的对象的类型的 {@link String}。
     * @param tags 表示对象唯一标识与目标标签列表的映射的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "37dc047e5f6545bc8936cf7a8d124e66")
    void save(String objectType, Map<String, List<String>> tags, OperationContext context);

    /**
     * 移除一个标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param tag 表示待移除的标签的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "925ea5c76ded4e22ae7fd7316c62bb2a")
    void remove(String objectType, String objectId, String tag, OperationContext context);

    /**
     * 列出指定对象的标签。
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param objectId 表示对象的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示对象上定义的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "e52238e98b4e46c1a0f7ffb3819ed7a5")
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
    @Genericable(id = "57479f590c1949699cfa878ad844a669")
    Map<String, List<String>> list(String objectType, List<String> objectIds, OperationContext context);

    /**
     * 根据标签查询到对应的flowId
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param tags 标签
     * @return 使用了标签的flowGraph对应的id
     */
    @Genericable(id = "f33856c586c74c07b3e88fbc32dbc78c")
    List<String> list(String objectType, List<String> tags);
}
