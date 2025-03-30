/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import modelengine.fitframework.annotation.Genericable;

/**
 * http调用服务。
 *
 * @author 张越
 * @since 2024-11-21
 */
public interface HttpCallService {
    /**
     * http调用接口.
     *
     * @param request 请求参数 {@link HttpRequest}.
     * @return {@link HttpRequest} 对象.
     */
    @Genericable("modelengine.jober.aipp.http.call")
    HttpResult httpCall(HttpRequest request);
}
