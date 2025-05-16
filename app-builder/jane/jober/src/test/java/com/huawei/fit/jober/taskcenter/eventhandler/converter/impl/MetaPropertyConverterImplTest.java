/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter.impl;

import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.domain.PropertyScope;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

/**
 * {@link MetaPropertyConverterImpl}的对应测试。
 *
 * @author 陈镕希
 * @since 2024-01-02
 */
@ExtendWith(MockitoExtension.class)
class MetaPropertyConverterImplTest {
    private MetaPropertyConverter metaPropertyConverter;

    @BeforeEach
    void before() {
        metaPropertyConverter = new MetaPropertyConverterImpl();
    }

    @Test
    void convert() {
        TaskProperty taskProperty = TaskProperty.custom()
                .name("testName")
                .dataType(PropertyDataType.TEXT)
                .sequence(1)
                .description("testDescription")
                .isRequired(false)
                .isIdentifiable(false)
                .scope(PropertyScope.PRIVATE)
                .appearance(Collections.emptyMap())
                .categories(Collections.emptyList())
                .build();
        modelengine.fit.jober.entity.task.TaskProperty actual = metaPropertyConverter.convert(taskProperty);
        Assertions.assertEquals("testName", actual.getName());
        Assertions.assertEquals("TEXT", actual.getDataType());
        Assertions.assertEquals(1, actual.getSequence());
        Assertions.assertEquals("testDescription", actual.getDescription());
        Assertions.assertFalse(actual.isRequired());
        Assertions.assertFalse(actual.isIdentifiable());
        Assertions.assertEquals("PRIVATE", actual.getScope());
        Assertions.assertTrue(actual.getAppearance().isEmpty());
        Assertions.assertTrue(actual.getCategories().isEmpty());
    }

    @Test
    void testConvert() {
        MetaPropertyDeclarationInfo info = new MetaPropertyDeclarationInfo();
        info.setName(Undefinable.defined("testName"));
        info.setDataType(Undefinable.defined("TEXT"));
        info.setDescription(Undefinable.defined("testDescription"));
        info.setRequired(Undefinable.defined(false));
        info.setIdentifiable(Undefinable.defined(false));
        info.setScope(Undefinable.defined("PRIVATE"));
        info.setAttribute(Undefinable.defined(Collections.emptyMap()));
        TaskProperty.Declaration actual = metaPropertyConverter.convert(info);
        Assertions.assertEquals("testName", actual.name().get());
        Assertions.assertEquals("TEXT", actual.dataType().get());
        Assertions.assertEquals("testDescription", actual.description().get());
        Assertions.assertFalse(actual.required().get());
        Assertions.assertFalse(actual.identifiable().get());
        Assertions.assertTrue(actual.appearance().get().isEmpty());
    }
}