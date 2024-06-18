/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.client.proxy.support.setter;

import com.huawei.fit.http.client.proxy.DestinationSetter;
import com.huawei.fit.http.client.proxy.RequestBuilder;

/**
 * 表示向 Http 请求路径设置值的 {@link DestinationSetter}。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class PathVariableDestinationSetter extends AbstractDestinationSetter {
    public PathVariableDestinationSetter(String key) {
        super(key);
    }

    @Override
    public void set(RequestBuilder requestBuilder, Object pathVariable) {
        if (pathVariable != null) {
            requestBuilder.pathVariable(this.key(), pathVariable.toString());
        }
    }
}