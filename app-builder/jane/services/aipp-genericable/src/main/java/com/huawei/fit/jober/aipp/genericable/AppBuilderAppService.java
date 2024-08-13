/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.genericable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fitframework.annotation.Genericable;

/**
 * 表示 app 应用相关的 Genericable 接口。
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
public interface AppBuilderAppService {
    /**
     * 查询 app 详情。
     *
     * @param appId 表示 app 唯一标识的 {@link String}。
     * @return 表示 app 应用详情的 dto 对象的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.query")
    AppBuilderAppDto query(String appId);

    /**
     * 调试 app。
     *
     * @param appDto 表示 app 应用详情的 dto 对象的 {@link AppBuilderAppDto}。
     * @param contextOf 表示操作者上下文的 {@link OperationContext}。
     * @return 表示创建的 Aipp 响应体实体类对象的 {@link AippCreate}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.debug")
    AippCreate debug(AppBuilderAppDto appDto, OperationContext contextOf);

    /**
     * 如果app更新过则调试 app。
     *
     * @param appId 表示 app 应用的id。
     * @param contextOf 表示操作者上下文的 {@link OperationContext}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.updateFlow")
    void updateFlow(String appId, OperationContext contextOf);

    /**
     * 查询 app 最新可编排的版本。
     *
     * @param appId 表示 app 唯一标识的 {@link String}。
     * @param context 表示操作者上下文的 {@link OperationContext}。
     * @return 表示查询到的 app 最新可编排的版本的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.query.latest.orchestration")
    AppBuilderAppDto queryLatestOrchestration(String appId, OperationContext context);

    /**
     * 查询 app 最新可编排的版本。
     *
     * @param appId 表示 app 唯一标识的 {@link String}。
     * @param context 表示操作者上下文的 {@link OperationContext}。
     * @return 表示查询到的 app 最新可编排的版本的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.query.latest.published")
    AippCreate queryLatestPublished(String appId, OperationContext context);
}
