/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.external;

/**
 * 调用W3接口的代理类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-07
 */
public interface W3Client {
    GetDynamicTokenResponse getDynamicToken(String endpoint, SoaTokenInfo soaTokenInfo);

    /**
     * 获取动态Token返回结构体。
     *
     * @author 陈镕希 c00572808
     * @since 2023-08-07
     */
    class GetDynamicTokenResponse {
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    /**
     * 获取SoaToken时的body信息。
     *
     * @author 陈镕希 c00572808
     * @since 2023-08-03
     */
    class SoaTokenInfo {
        private String appId;

        private String credential;

        public SoaTokenInfo() {
            this(null, null);
        }

        public SoaTokenInfo(String appId, String credential) {
            this.appId = appId;
            this.credential = credential;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getCredential() {
            return credential;
        }

        public void setCredential(String credential) {
            this.credential = credential;
        }
    }
}
