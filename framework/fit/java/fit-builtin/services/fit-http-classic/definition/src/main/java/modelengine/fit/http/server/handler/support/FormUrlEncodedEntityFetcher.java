/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示从 {@link MultiValueEntity} 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class FormUrlEncodedEntityFetcher extends EntityFetcher {
    private final String key;

    public FormUrlEncodedEntityFetcher(String key) {
        this.key = notBlank(key, "The key cannot be blank.");
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    protected Class<? extends Entity> entityType() {
        return MultiValueEntity.class;
    }

    @Override
    protected Object getFromRequest(HttpClassicServerRequest request, Entity entity) {
        MultiValueEntity multiValueEntity = ObjectUtils.cast(entity);
        return multiValueEntity.all(this.key);
    }
}
