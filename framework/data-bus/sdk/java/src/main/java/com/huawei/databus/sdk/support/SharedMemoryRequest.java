/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.fitframework.inspection.Validation;

/**
 * DataBus 服务申请共享内存请求
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public class SharedMemoryRequest {
    private final long size;

    private SharedMemoryRequest(long size) {
        this.size = Validation.greaterThan(size, 0, "Applied memory size must be larger than 0");
    }

    /**
     * 返回申请内存的大小
     *
     * @return 内存字节数 {@code long}
     */
    public long size() {
        return size;
    }

    /**
     * {@link SharedMemoryRequest} 的构造器
     */
    public static class SharedMemoryRequestBuilder {
        private long size;

        /**
         * 向当前构建器中设置申请内存长度。
         *
         * @param size 表示申请内存长度的 {@code long}。
         * @return 表示当前构建器的 {@link SharedMemoryRequestBuilder}。
         */
        public SharedMemoryRequestBuilder size(long size) {
            this.size = size;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link SharedMemoryRequest}。
         */
        public SharedMemoryRequest build() {
            return new SharedMemoryRequest(this.size);
        }
    }
}
