/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 提供trace的归属服务
 *
 * @author x00576283
 * @since 2024/4/29
 */
public interface TraceOwnerService {
    /**
     * own
     *
     * @param traceId traceId
     * @param transId transId
     */
    void own(String traceId, String transId);

    /**
     * tryOwn
     *
     * @param traceId traceId
     * @param transId transId
     * @return boolean
     */
    boolean tryOwn(String traceId, String transId);

    /**
     * release
     *
     * @param traceId traceId
     */
    void release(String traceId);

    /**
     * isOwn
     *
     * @param traceId traceId
     * @return boolean
     */
    boolean isOwn(String traceId);

    /**
     * getTraces
     *
     * @return List<String>
     */
    List<String> getTraces();

    /**
     * getTraces
     *
     * @param targetTransId targetTransId
     * @return List<String>
     */
    List<String> getTraces(String targetTransId);

    /**
     * 移除所有失效的trace
     *
     * @param invalidLock lock
     */
    void removeInvalidTrace(Lock invalidLock);
}
