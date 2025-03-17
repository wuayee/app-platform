/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.support;

import modelengine.databus.sdk.api.DataBusRequest;
import modelengine.fitframework.inspection.Validation;

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
