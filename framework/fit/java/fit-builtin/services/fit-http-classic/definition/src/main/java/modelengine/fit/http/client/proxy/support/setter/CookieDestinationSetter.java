/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.http.client.proxy.support.setter;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.RequestBuilder;

/**
 * 表示向 Cookie 设置值的 {@link DestinationSetter}。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class CookieDestinationSetter extends AbstractDestinationSetter {
    public CookieDestinationSetter(String key) {
        super(key);
    }

    @Override
    public void set(RequestBuilder requestBuilder, Object value) {
        if (value != null) {
            requestBuilder.cookie(this.key(), value.toString());
        }
    }
}