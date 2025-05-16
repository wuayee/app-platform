/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.adapter;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.dao.po.SourceObject;
import modelengine.fit.jober.taskcenter.declaration.SourceDeclaration;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceType;

import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 三方推送数据源适配器。
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
@Component
public class ThirdPartyPushSourceAdapter extends AbstractSourceAdapter {
    @Override
    public SourceType getType() {
        return SourceType.THIRD_PARTY_PUSH;
    }

    @Override
    public SourceEntity createExtension(SourceObject sourceObject, SourceDeclaration sourceDeclaration,
            OperationContext context) {
        return this.convert(sourceObject);
    }

    @Override
    public void patchExtension(SourceObject sourceObject, SourceDeclaration declaration, OperationContext context) {

    }

    @Override
    public void deleteExtension(String sourceId, OperationContext context) {

    }

    @Override
    public SourceEntity retrieveExtension(SourceObject sourceObject, OperationContext context) {
        return this.convert(sourceObject);
    }

    @Override
    public Map<String, List<SourceEntity>> listExtension(List<SourceObject> sourceObjects, OperationContext context) {
        return sourceObjects.stream().collect(Collectors.groupingBy(SourceObject::getTaskId,
                Collectors.mapping(this::convert, Collectors.toList())));
    }

    private SourceEntity convert(SourceObject object) {
        SourceEntity entity = new SourceEntity();
        this.fill(entity, object);
        return entity;
    }
}
