/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

import static java.lang.System.identityHashCode;

import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fitframework.log.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WaterFlow的全局单列实例管理类
 * WaterFlows.getPublisher根据流程定义的唯一ID：streamID获取WaterFlow实例
 * 如果已经存在则直接返回，否则需要将FlowDefinition转换WaterFlow，并保存下来
 * 后续需要提供删除功能：流程定义删除时，对应的Lock和WaterFlow都要删除
 *
 * @author 高诗意
 * @since 1.0
 */
public final class WaterFlows {
    private static final Logger LOG = Logger.get(WaterFlows.class);

    private static final Map<String, Publisher<FlowData>> FLOWS = new ConcurrentHashMap<>();

    /**
     * 根据流程版本更新water flow
     *
     * @param streamId 流程版本
     * @param publisher water flow
     * @return {@link Publisher} water flow
     */
    public static Publisher<FlowData> putPublisher(String streamId, Publisher<FlowData> publisher) {
        Publisher<FlowData> exits = Optional.ofNullable(FLOWS.putIfAbsent(streamId, publisher))
                .orElseGet(() -> getPublisher(streamId));
        LOG.info("[WaterFlows::putPublisher] put new publisher only if not exists, "
                        + "streamId: {}, publisher: {}, exits: {}", streamId, identityHashCode(publisher),
                identityHashCode(exits));
        return exits;
    }

    /**
     * 根据流程版本获取water flow
     *
     * @param streamId 流程版本
     * @return {@link Publisher} water flow
     */
    public static Publisher<FlowData> getPublisher(String streamId) {
        return FLOWS.get(streamId);
    }

    /**
     * 根据流程版本删除water flow
     * TODO xiangyu 删除流程定义时，需要将对应的water flow缓存也删除
     *
     * @param streamId 流程版本
     */
    public static void removePublisher(String streamId) {
        FLOWS.remove(streamId);
    }

    /**
     * 清除flowable flow缓存
     */
    public static void clear() {
        FLOWS.clear();
    }
}
