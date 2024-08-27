/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context.repo.flowcontext;

import modelengine.fit.waterflow.common.ErrorCodes;
import modelengine.fit.waterflow.common.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowTrace;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import java.util.List;
import java.util.Set;

/**
 * 流程上下文持久化Repo核心类型
 * 包含FlowContextMemoRepo和FlowContextPersistRepo两种实现
 *
 * @author 高诗意
 * @since 1.0
 */
public interface FlowContextRepo<T> {
    /**
     * 人工任务节点拉取边上的上下文，在节点的preprocess中处理
     *
     * @param streamId 版本ID
     * @param posIds posId
     * @param status status
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getContextsByPosition(String streamId, List<String> posIds, String status);

    /**
     * 获取节点处理完后产生的新的context，发送给下个节点处理，后续可以判断是否删除该方法
     *
     * @param streamId 版本ID
     * @param posId posId
     * @param batchId 批次ID
     * @param status status
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getContextsByPosition(String streamId, String posId, String batchId, String status);

    /**
     * getContextsByTrace
     *
     * @param traceId transId
     * @return List<FlowContext < T1>>
     */
    <T1> List<FlowContext<T1>> getContextsByTrace(String traceId);

    /**
     * 批量保存context
     *
     * @param contexts contexts
     */
    void save(List<FlowContext<T>> contexts);

    /**
     * 批量更新context的内容，不更新status和position
     *
     * @param contexts contexts
     */
    default void update(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    /**
     * updateToSent
     *
     * @param contexts contexts
     */
    void updateToSent(List<FlowContext<T>> contexts);

    /**
     * getContextsByParallel
     *
     * @param parallelId parallelId
     * @return List<FlowContext < T1>>
     */
    <T1> List<FlowContext<T1>> getContextsByParallel(String parallelId);

    /**
     * getById
     *
     * @param id id
     * @return FlowContext<T>
     */
    FlowContext<T> getById(String id);

    /**
     * 根据ids查找FlowContext<T>
     *
     * @param ids ids
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getByIds(List<String> ids);

    /**
     * 查找和指定一批ID对应的状态为PENDING且SENT了的流程上下文
     *
     * @param ids ids
     * @return List<FlowContext < T>>
     */
    List<FlowContext<T>> getPendingAndSentByIds(List<String> ids);

    /**
     * 查找map节点所有from事件上待处理的上下文
     *
     * @param streamId 流程版本ID
     * @param subscriptions from事件的事件ID
     * @param excludeTraceIds 排除的traceIds
     * @param filter 默认过滤器，map的场景永远使用默认过滤器过滤批次数据
     * @param validator block校验器
     * @return 待处理的上下文
     */
    List<FlowContext<T>> requestMappingContext(String streamId, List<String> subscriptions, Set<String> excludeTraceIds,
            Operators.Filter<T> filter, Operators.Validator<T> validator);

    /**
     * 查找produce节点所有from事件上待处理的上下文
     *
     * @param streamId 流程版本ID
     * @param subscriptions from事件的事件ID
     * @param excludeTraceIds 排除的traceIds
     * @param filter filter校验器
     * @return 待处理的上下文
     */
    List<FlowContext<T>> requestProducingContext(String streamId, List<String> subscriptions,
            Set<String> excludeTraceIds, Operators.Filter<T> filter);

    /**
     * 查找流程对应版本所有上下文
     *
     * @param metaId 流程metaId标识
     * @param version 流程对应版本
     * @return 对应所有上下文
     */
    default List<FlowContext<T>> findByStreamId(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findByStreamId");
    }

    /**
     * 查找流程对应版本正在运行的上下文
     *
     * @param metaId metaId 流程metaId标识
     * @param version 流程对应版本
     * @return 对应所有上下文
     */
    default List<FlowContext<T>> findRunningContextByMetaId(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "findRunningContextByMetaId");
    }

    /**
     * 删除流程对应版本所有上下文
     *
     * @param metaId metaId 流程metaId标识
     * @param version 流程对应版本
     */
    default void delete(String metaId, String version) {
        throw new WaterflowException(ErrorCodes.FLOW_ENGINE_DATABASE_NOT_SUPPORT, "delete");
    }

    /**
     * 批量更新trace的contextPool
     *
     * @param after context
     * @param traces 需要更新的tranceId列表
     */
    default void updateContextPool(List<FlowContext<T>> after, Set<String> traces) {
        save(after);
    }

    /**
     * 保存contexts
     *
     * @param trace 對應的trace
     * @param flowContext 待保存的contexts
     */
    void save(FlowTrace trace, FlowContext<T> flowContext);

    /**
     * 批量更新context的上下文数据flowData字段
     *
     * @param contexts contexts
     */
    void updateFlowData(List<FlowContext<T>> contexts);

    /**
     * 批量更新context的status和position
     *
     * @param contexts contexts
     * @param status 状态 {@link FlowNodeStatus}
     * @param position 位置
     */
    default void updateStatus(List<FlowContext<T>> contexts, String status, String position) {
        save(contexts);
    }

    /**
     * 更新context和trace的状态
     *
     * @param traceIds traceIds
     */
    default void updateToTerminated(List<String> traceIds) {
    }

    /**
     * 判断trace终止
     *
     * @param traceIds traceIds
     * @return boolean
     */
    default boolean isTracesTerminate(List<String> traceIds) {
        return false;
    }
}

