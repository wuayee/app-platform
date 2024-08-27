/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示从 {@link ObjectEntity} 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
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
