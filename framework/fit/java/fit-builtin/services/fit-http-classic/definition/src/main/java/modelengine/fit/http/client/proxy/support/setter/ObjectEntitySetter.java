/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.proxy.support.setter;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示向消息体设置值的目标设置器。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class ObjectEntitySetter extends EntitySetter {
    private final String propertyPath;

    public ObjectEntitySetter(String propertyPath) {
        this.propertyPath = notNull(propertyPath, "The property path cannot be null.");
    }

    @Override
    protected void setToRequest(RequestBuilder requestBuilder, @Nonnull Object value) {
        requestBuilder.jsonEntity(this.propertyPath, value);
    }
}