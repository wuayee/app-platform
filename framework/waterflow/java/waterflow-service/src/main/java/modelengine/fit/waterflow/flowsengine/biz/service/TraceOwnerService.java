/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * 提供trace的归属服务
 *
 * @author 夏斐
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
     * trace map中包含任意一个trace列表的值，返回true
     *
     * @param traceIds trace id列表
     * @return true or false
     */
    boolean isAnyOwn(Set<String> traceIds);

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

    /**
     * 判断trace是否在初始化保护期
     * 针对首次offer trace先加入到内存，但是实际数据库中还未插入时的情况使用
     *
     * @param traceId traceId
     * @return true-处于保护时间，false-超过保护时间
     */
    boolean isInProtectTime(String traceId);
}
