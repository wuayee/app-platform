/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.waterflow.graph.entity.FlowInfo;
import modelengine.fitframework.annotation.Component;

import java.util.concurrent.TimeUnit;

/**
 * 本类提供一些缓存机制
 * <p>当前提供从appid查询meta的缓存</p>
 *
 * @author 姚江
 * @since 2024-11-04
 */
@Component
public class CacheUtils {
    /**
     * 用于缓存appId to app
     */
    public static final Cache<String, AppBuilderAppPo> APP_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(48, TimeUnit.HOURS)
            .maximumSize(30)
            .build();

    /**
     * 用于缓存flowDefinitionId to flowInfo
     */
    public static final Cache<String, FlowInfo> FLOW_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(48, TimeUnit.HOURS)
            .maximumSize(30)
            .build();

    /**
     * 用于缓存app_id和aipp_id的关系
     */
    public static final Cache<String, String> APP_ID_TO_AIPP_ID_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.DAYS)
            .maximumSize(1000)
            .build();

    /**
     * 清理缓存
     */
    public static void clear() {
        APP_CACHE.invalidateAll();
        FLOW_CACHE.invalidateAll();
        APP_ID_TO_AIPP_ID_CACHE.invalidateAll();

        APP_CACHE.cleanUp();
        FLOW_CACHE.cleanUp();
        APP_ID_TO_AIPP_ID_CACHE.cleanUp();
    }

    /**
     * 用于获取flowdefinition的缓存
     *
     * @param flowsService 操作flow的service
     * @param flowDefinitionId 缓存的flowDefinition的id
     * @param context 人员上下文
     * @return 缓存的FlowInfo
     */
    public static FlowInfo getPublishedFlowWithCache(FlowsService flowsService, String flowDefinitionId,
                                                     OperationContext context) {
        return FLOW_CACHE.get(flowDefinitionId, id -> flowsService.getFlows(id, context));
    }
}
