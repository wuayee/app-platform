/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpResponse;
import modelengine.fit.http.protocol.HttpResponseStatus;

/**
 * 表示 {@link HttpResponse} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-11-28
 */
public class DefaultHttpResponse implements HttpResponse {
    private final HttpResponseStatus status;
    private final Object entity;

    public DefaultHttpResponse(HttpResponseStatus status, Object entity) {
        this.status = notNull(status, "The http response status cannot be null.");
        this.entity = entity;
    }

    @Override
    public HttpResponseStatus status() {
        return this.status;
    }

    @Override
    public Object entity() {
        return this.entity;
    }
}
