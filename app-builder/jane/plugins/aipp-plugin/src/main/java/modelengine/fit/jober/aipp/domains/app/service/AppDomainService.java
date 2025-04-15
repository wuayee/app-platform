/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.app.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;

import java.util.Map;

/**
 * 应用服务.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface AppDomainService {
    /**
     * 通过appId删除应用.
     *
     * @param appId 应用版本id.
     * @param context 操作人上下文对象.
     */
    void deleteByAppId(String appId, OperationContext context);

    /**
     * 导入一个应用。
     *
     * @param appConfig 应用导入配置。
     * @param context 操作上下文。
     * @return {@link AppBuilderAppDto} 应用dto对象。
     */
    AppBuilderAppDto importApp(String appConfig, OperationContext context);

    /**
     * 导出一个应用。
     *
     * @param appId 导出应用的版本id。
     * @param exportMeta 导出应用元数据。
     * @param context 操作上下文。
     * @return {@link AppExportDto} 导出应用配置dto对象。
     */
    AppExportDto exportApp(String appId, Map<String, String> exportMeta, OperationContext context);
}
