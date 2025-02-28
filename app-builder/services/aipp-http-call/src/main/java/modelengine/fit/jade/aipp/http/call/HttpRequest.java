/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import lombok.Data;

import java.util.Map;

/**
 * http请求信息。
 *
 * @author 张越
 * @since 2024-11-21
 */
@Data
public class HttpRequest {
    private String httpMethod;
    private String url;
    private Integer timeout;
    private Map<String, Object> args;
    private Map<String, String> headers;
    private Map<String, String> params;
    private HttpBody requestBody;
    private Authentication authentication;
}
