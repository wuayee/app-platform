/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.support;

import static com.huawei.fit.http.protocol.MessageHeaderNames.HOST;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.HttpClassicRequest;
import com.huawei.fit.http.HttpResource;
import com.huawei.fit.http.QueryCollection;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.MessageHeaderNames;
import com.huawei.fit.http.protocol.MessageHeaders;
import com.huawei.fit.http.protocol.RequestLine;
import com.huawei.fitframework.resource.UrlUtils;

/**
 * 表示 {@link HttpClassicRequest} 的抽象实现类。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-23
 */
public abstract class AbstractHttpClassicRequest extends AbstractHttpMessage implements HttpClassicRequest {
    private static final char QUERY_SEPARATOR = '?';

    private final RequestLine startLine;
    private final QueryCollection queries;
    private final MessageHeaders headers;

    /**
     * 创建经典的 Http 请求对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param startLine 表示 Http 请求的起始行的 {@link RequestLine}。
     * @param headers 表示只读的 Http 消息头集合的 {@link MessageHeaders}。
     */
    public AbstractHttpClassicRequest(HttpResource httpResource, RequestLine startLine, MessageHeaders headers) {
        super(httpResource, startLine, headers);
        this.startLine = notNull(startLine, "The request line cannot be null.");
        this.queries = this.initQueries();
        this.headers = notNull(headers, "The message headers cannot be null.");
    }

    private QueryCollection initQueries() {
        String requestUri = this.startLine.requestUri();
        int index = requestUri.indexOf(QUERY_SEPARATOR);
        if (index < 0) {
            return QueryCollection.create();
        } else {
            String decoded = UrlUtils.decodeValue(requestUri.substring(index + 1));
            return QueryCollection.create(decoded);
        }
    }

    @Override
    public HttpRequestMethod method() {
        return this.headers.first(MessageHeaderNames.X_HTTP_METHOD_OVERRIDE)
                .map(HttpRequestMethod::from)
                .orElse(this.startLine.method());
    }

    @Override
    public String requestUri() {
        return this.startLine.requestUri();
    }

    @Override
    public String host() {
        return this.headers.first(HOST).orElse(null);
    }

    @Override
    public String path() {
        String requestUri = this.startLine.requestUri();
        int index = requestUri.indexOf(QUERY_SEPARATOR);
        if (index < 0) {
            return requestUri;
        } else {
            return requestUri.substring(0, index);
        }
    }

    @Override
    public QueryCollection queries() {
        return this.queries;
    }
}
