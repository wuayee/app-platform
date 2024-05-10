/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.memory.SharedMemoryKey;
import com.huawei.fitframework.inspection.Validation;

/**
 * DataBus 服务释放共享内存请求
 *
 * @author 王成 w00863339
 * @since 2024-05-07
 */
public class ReleaseMemoryRequest {
    private final SharedMemoryKey sharedMemoryKey;

    private ReleaseMemoryRequest(Builder builder) {
        if (builder.sharedMemoryKey != null) {
            this.sharedMemoryKey = builder.sharedMemoryKey;
        } else {
            Validation.notNull(builder.userKey,
                    () -> new IllegalArgumentException("User key cannot be null if sharedMemoryKey is unset."));
            this.sharedMemoryKey = new SharedMemoryKey(builder.userKey);
        }
    }

    /**
     * 获取被释放的内存句柄
     *
     * @return 表示内存句柄的 {@link SharedMemoryKey}
     */
    public SharedMemoryKey sharedMemoryKey() {
        return this.sharedMemoryKey;
    }

    /**
     * {@link ReleaseMemoryRequest} 的构造器
     */
    public static class Builder {
        private SharedMemoryKey sharedMemoryKey;
        private String userKey;

        /**
         * 向当前构建器中设置内存句柄。内存句柄的优先级高于自定义 key
         *
         * @param sharedMemoryKey 表示释放内存句柄的的 {@link SharedMemoryKey}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder sharedMemoryKey(SharedMemoryKey sharedMemoryKey) {
            this.sharedMemoryKey = sharedMemoryKey;
            return this;
        }

        /**
         * 向当前构建器中设置内存自定义 key 。
         *
         * @param userKey 表示申请内存的自定义 key 的 {@code String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ReleaseMemoryRequest}。
         */
        public ReleaseMemoryRequest build() {
            return new ReleaseMemoryRequest(this);
        }
    }
}
