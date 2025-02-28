/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.value.ValueFetcher;
import modelengine.jade.carver.tool.Tool;
import modelengine.jade.carver.tool.ToolFactory;

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
    public Tool create(Tool.ToolInfo itemInfo, Tool.Metadata metadata) {
        return new HttpTool(this.factory, this.serializer, this.valueFetcher, itemInfo, metadata);
    }
}
