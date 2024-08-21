/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.service;

import java.time.Instant;

/**
 * 表示进程的过期缓存。
 *
 * @author 季聿阶
 * @since 2023-07-19
 */
public interface WorkerCache {
    /**
     * 刷新缓存中进程的过期时间。
     *
     * @param workerId 表示进程的唯一标识的 {@link String}。
     * @param expireTime 表示进程的到期时间的 {@link Instant}。
     */
    void refreshWorker(String workerId, Instant expireTime);

    /**
     * 判断指定进程是否过期。
     *
     * @param workerId 表示进程的唯一标识的 {@link String}。
     * @return 当进程过期时，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isExpired(String workerId);
}
