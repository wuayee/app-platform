/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.value.ValueFetcher;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;

/**
 * 表示创建 {@link HttpTool} 的工厂。
 *
 * @author 何天放
 * @since 2024-06-15
 */
public class HttpToolFactory implements ToolFactory {
    private final HttpClassicClientFactory factory;
    private final ObjectSerializer serializer;
    private final ValueFetcher valueFetcher;

    public HttpToolFactory(HttpClassicClientFactory factory, ObjectSerializer serializer, ValueFetcher valueFetcher) {
        this.factory = notNull(factory, "The factory cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.valueFetcher = notNull(valueFetcher, "The valueFetcher cannot be null.");
    }

    @Override
    public String type() {
        return HttpTool.TYPE;
    }

    @Override
    public Tool create(Tool.Info itemInfo, Tool.Metadata metadata) {
        return new HttpTool(this.factory, this.serializer, this.valueFetcher, itemInfo, metadata);
    }
}
