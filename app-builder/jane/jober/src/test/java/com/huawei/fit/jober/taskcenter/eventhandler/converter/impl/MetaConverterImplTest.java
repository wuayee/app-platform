/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter.impl;

import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.meta.definition.Meta;
import modelengine.fit.jane.meta.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaFilter;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.enums.JaneCategory;
import modelengine.fit.jober.taskcenter.declaration.TaskDeclaration;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import modelengine.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import modelengine.fit.jober.taskcenter.filter.TaskFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

/**
 * {@link MetaConverter}对应测试类。
 *
 * @author 陈镕希
 * @since 2023-12-25
 */
@ExtendWith(MockitoExtension.class)
class MetaConverterImplTest {
    private MetaConverter metaConverter;

    @Mock
    private TaskConverter taskConverter;

    @Mock
    private MetaPropertyConverter metaPropertyConverter;

    @BeforeEach
    void before() {
        metaConverter = new MetaConverterImpl(taskConverter, metaPropertyConverter);
    }

    @Test
    void convert() {
        MetaDeclarationInfo metaDeclarationInfo = new MetaDeclarationInfo();
        metaDeclarationInfo.setName(Undefinable.defined("metaTest"));
        metaDeclarationInfo.setCategory(Undefinable.defined("META"));
        metaDeclarationInfo.setAttributes(Undefinable.undefined());
        metaDeclarationInfo.setProperties(Undefinable.undefined());
        TaskDeclaration actual = metaConverter.convert(metaDeclarationInfo);
        Assertions.assertEquals("metaTest", actual.getName().get());
        Assertions.assertEquals("META", actual.getCategory().get());
        Assertions.assertFalse(actual.getAttributes().defined());
        Assertions.assertFalse(actual.getProperties().defined());
    }

    @Test
    void testMetaConvert() {
        TaskEntity entity = new TaskEntity();
        entity.setId("id");
        entity.setName("name");
        entity.setCategory(JaneCategory.META);
        entity.setProperties(Collections.emptyList());
        Meta meta = metaConverter.convert(entity, OperationContext.custom().build());
        Assertions.assertEquals("id", meta.getId());
        Assertions.assertEquals("name", meta.getName());
    }

    @Test
    void testMetaFilterConvert() {
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setIds(Collections.singletonList("testId"));
        metaFilter.setNames(Collections.singletonList("testName"));
        metaFilter.setCategories(Collections.singletonList("testCategory"));
        metaFilter.setCreators(Collections.singletonList("testCreatedBy"));
        TaskFilter actual = metaConverter.convert(metaFilter);
        Assertions.assertEquals("testId", actual.getIds().get().get(0));
        Assertions.assertEquals("testName", actual.getNames().get().get(0));
        Assertions.assertEquals("testCategory", actual.getCategories().get().get(0));
        Assertions.assertEquals("testCreatedBy", actual.getCreators().get().get(0));
    }
}