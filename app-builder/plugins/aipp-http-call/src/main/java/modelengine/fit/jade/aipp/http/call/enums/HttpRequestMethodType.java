/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.enums;

import lombok.Getter;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.inspection.Nullable;

/**
 * 鉴权类型。
 *
 * @author 张越
 * @since 2024-12-16
 */
@Getter
public enum HttpRequestMethodType {
    GET(false, HttpRequestMethod.GET),
    POST(true, HttpRequestMethod.POST),
    PUT(true, HttpRequestMethod.PUT),
    DELETE(true, HttpRequestMethod.DELETE),
    PATCH(true, HttpRequestMethod.PATCH);

    private final boolean isBodyEnable;
    private final HttpRequestMethod originMethod;

    HttpRequestMethodType(boolean isBodyEnable, HttpRequestMethod originMethod) {
        this.isBodyEnable = isBodyEnable;
        this.originMethod = originMethod;
    }

    /**
     * 获取指定方法名的方法枚举。
     * <p>获取方法时<b>忽略大小写</b>。</p>
     *
     * @param methodName 表示指定方法名的 {@link String}。
     * @return 表示获取的方法的 {@link HttpRequestMethodType}。如果无匹配的方法，则返回 {@code null}。
     */
    @Nullable
    public static HttpRequestMethodType from(String methodName) {
        for (HttpRequestMethodType method : HttpRequestMethodType.values()) {
            if (method.name().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return null;
    }
}
