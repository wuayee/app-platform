/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.PropertyValueMapper;

/**
 * 表示空的 Http 值映射器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-02
 */
public class EmptyPropertyValueMapper implements PropertyValueMapper {
    /** 表示空的 Http 值映射器的单例。 */
    public static final PropertyValueMapper INSTANCE = new EmptyPropertyValueMapper();

    private EmptyPropertyValueMapper() {}

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        return null;
    }
}