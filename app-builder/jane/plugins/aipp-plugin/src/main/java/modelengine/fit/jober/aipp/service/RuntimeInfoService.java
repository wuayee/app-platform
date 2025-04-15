/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.runtime.entity.Parameter;

import java.util.List;
import java.util.Map;

/**
 * 流程运行时信息接口
 *
 * @author 邬涨财
 * @since 2024-12-17
 */
public interface RuntimeInfoService {
    /**
     * 根据业务数据判断应用是否已发布。
     *
     * @param businessData 业务数据。
     * @return 是否已发布。
     */
    boolean isPublished(Map<String, Object> businessData);

    /**
     * 构建参数集合
     *
     * @param map 业务数据。
     * @param nodeId 节点id。
     * @return 构建的参数集合
     */
    List<Parameter> buildParameters(Map<String, Object> map, String nodeId);

    /**
     * 插入流程运行时信息
     *
     * @param instanceId 实例 id
     * @param map 业务数据
     * @param status 流程状态
     * @param errorMsg 流程运行时的错误信息
     * @param context 操作上下文
     */
    void insertRuntimeInfo(String instanceId, Map<String, Object> map, MetaInstStatusEnum status, String errorMsg,
            OperationContext context);
}
