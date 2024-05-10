/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.adapter;

import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;

/**
 * 为 {@link SourceAdapter} 提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-21
 */
public abstract class AbstractSourceAdapter implements SourceAdapter {
    /**
     * 向实体类中填充数据对象中包含的信息。
     *
     * @param entity 表示待填充的实体对象的 {@link SourceEntity}。
     * @param object 表示作为数据源的数据对象的 {@link SourceObject}。
     */
    protected void fill(SourceEntity entity, SourceObject object) {
        entity.setId(object.getId());
        entity.setName(object.getName());
        entity.setType(this.getType());
        entity.setApp(object.getApp());
    }
}
