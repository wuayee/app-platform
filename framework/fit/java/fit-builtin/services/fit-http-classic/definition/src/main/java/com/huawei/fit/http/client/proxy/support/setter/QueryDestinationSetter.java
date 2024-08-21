/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.support.setter;

import com.huawei.fit.http.client.proxy.DestinationSetter;
import com.huawei.fit.http.client.proxy.RequestBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

/**
 * 表示向查询参数设置值的 {@link DestinationSetter}。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class QueryDestinationSetter extends AbstractDestinationSetter {
    public QueryDestinationSetter(String key) {
        super(key);
    }

    @Override
    public void set(RequestBuilder requestBuilder, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof List) {
            List<?> list = ObjectUtils.cast(value);
            list.stream()
                .filter(Objects::nonNull)
                .forEach(item -> requestBuilder.query(this.key(), item.toString()));
        } else {
            requestBuilder.query(this.key(), value.toString());
        }
    }
}