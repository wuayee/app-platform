/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import com.huawei.fit.http.entity.Entity;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.handler.SourceFetcher;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * 表示从 {@link ObjectEntity} 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-28
 */
public class ObjectEntityFetcher extends EntityFetcher {
    private final String propertyPath;

    public ObjectEntityFetcher(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    @Override
    protected Class<? extends Entity> entityType() {
        return ObjectEntity.class;
    }

    @Override
    protected Object getFromRequest(HttpClassicServerRequest request, Entity entity) {
        ObjectEntity<?> objectEntity = ObjectUtils.cast(entity);
        return request.httpResource().valueFetcher().fetch(objectEntity.object(), this.propertyPath);
    }
}
