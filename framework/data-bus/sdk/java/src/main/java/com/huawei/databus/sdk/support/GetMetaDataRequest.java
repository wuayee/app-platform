/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.support;

import com.huawei.databus.sdk.api.DataBusRequest;
import com.huawei.fitframework.inspection.Validation;

/**
 * DataBus 服务读取内存元数据。
 *
 * @author 王成
 * @since 2024-05-25
 */
public class GetMetaDataRequest implements DataBusRequest {
    private final String userKey;

    private GetMetaDataRequest(Builder builder) {
        this.userKey = Validation.notBlank(builder.userKey, "User key could not be empty.");
    }

    /**
     * 获取希望读取用户数据的内存句柄。
     *
     * @return 表示内存句柄的 {@code String}。
     */
    @Override
    public String userKey() {
        return this.userKey;
    }

    /**
     * {@link GetMetaDataRequest} 的构造器。
     */
    public static class Builder {
        private String userKey;

        /**
         * 向当前构建器中设置内存自定义键。
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
         * @return 表示构建出来的对象的 {@link GetMetaDataRequest}。
         */
        public GetMetaDataRequest build() {
            return new GetMetaDataRequest(this);
        }
    }

    /**
     * 获取 {@link GetMetaDataRequest} 的构建器。
     *
     * @return 表示 {@link GetMetaDataRequest} 的构建器的 {@link GetMetaDataRequest.Builder}。
     */
    public static GetMetaDataRequest.Builder custom() {
        return new GetMetaDataRequest.Builder();
    }
}
