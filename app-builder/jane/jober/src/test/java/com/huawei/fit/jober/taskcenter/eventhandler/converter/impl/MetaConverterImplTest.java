/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter.impl;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.meta.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.definition.MetaFilter;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.enums.JaneCategory;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;

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
 * @author 陈镕希 c00572808
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
    void testConvert() {
        TaskEntity entity = new TaskEntity();
        entity.setId("id");
        entity.setName("name");
        entity.setCategory(JaneCategory.META);
        entity.setProperties(Collections.emptyList());
        metaConverter.convert(entity, OperationContext.custom().build());
    }

    @Test
    void testConvert1() {
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