/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.support.setter;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.http.client.proxy.RequestBuilder;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

/**
 * 表示向消息体设置值的目标设置器。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class FormUrlEncodedEntitySetter extends EntitySetter {
    private final String key;

    public FormUrlEncodedEntitySetter(String key) {
        this.key = notBlank(key, "The key cannot be null.");
    }

    @Override
    protected void setToRequest(RequestBuilder requestBuilder, @Nonnull Object value) {
        if (value instanceof List) {
            List<?> list = ObjectUtils.cast(value);
            list.stream()
                .filter(Objects::nonNull)
                .forEach(item -> requestBuilder.formEntity(this.key, item.toString()));
        } else {
            requestBuilder.formEntity(this.key, value.toString());
        }
    }
}