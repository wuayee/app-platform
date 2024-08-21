/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link FormUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-16
 */
@ExtendWith(MockitoExtension.class)
public class FormUtilsTest {
    @Mock
    AppBuilderFormRepository formRepository;

    @Mock
    AppBuilderFormPropertyRepository formPropRepos;

    @Test
    @DisplayName("queryFormDetailByPrimaryKey方法测试")
    void testQueryFormDetailByPrimaryKey() {
        AppBuilderForm form = AppBuilderForm.builder().id("id1").name("form1").tenantId("tenantId").build();
        Mockito.when(formRepository.selectWithId(Mockito.eq("id1"))).thenReturn(form);
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put("hello", "world");
        AppBuilderFormProperty formProperty =
                AppBuilderFormProperty.builder().formId("id1").name("fp1").defaultValue(defaultValue).build();
        Mockito.when(formPropRepos.selectWithFormId(Mockito.eq("id1")))
                .thenReturn(Collections.singletonList(formProperty));
        String exceptData = "{\"fp1\":\"{\\\"hello\\\":\\\"world\\\"}\"}";
        DynamicFormDetailEntity entity =
                Assertions.assertDoesNotThrow(() -> FormUtils.queryFormDetailByPrimaryKey("id1",
                        "version",
                        new OperationContext(),
                        formRepository,
                        formPropRepos));
        Assertions.assertEquals(exceptData, entity.getData());
        Assertions.assertEquals("id1", entity.getMeta().getId());
    }

    @Test
    @DisplayName("queryFormDetailByPrimaryKey方法测试：未查询到form")
    void testQueryFormDetailByPrimaryKeyWithNull() {
        DynamicFormDetailEntity entity =
                Assertions.assertDoesNotThrow(() -> FormUtils.queryFormDetailByPrimaryKey("id1",
                        "version",
                        new OperationContext(),
                        formRepository,
                        formPropRepos));
        Assertions.assertNull(entity);
    }

    @Test
    @DisplayName("buildLogDataWithFormData方法测试")
    void testBuildLogDataWithFormData() {
        Mockito.when(formRepository.selectWithId(Mockito.eq("id"))).thenReturn(buildForm());
        Mockito.when(formPropRepos.selectWithFormId(Mockito.eq("id")))
                .thenReturn(Collections.singletonList(buildFormProperty()));
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("id_id", MapBuilder.get(() -> new HashMap<String, Object>()).put("hello", "1").build());
        AippLogData aippLogData = FormUtils.buildLogDataWithFormData(formRepository, "id", "version", businessData);
        String formArgs = aippLogData.getFormArgs();
        Assertions.assertEquals("{\"id_id\":{\"hello\":\"1\"}}", formArgs);
    }

    @Test
    @DisplayName("buildFormData方法测试")
    void testBuildFormData() {
        AppBuilderForm inputForm = buildForm();
        inputForm.setFormProperties(Collections.singletonList(buildFormProperty()));
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("model", "Qianwen");
        String parentId = "parentId";
        Map<String, Object> form =
                Assertions.assertDoesNotThrow(() -> FormUtils.buildFormData(businessData, inputForm, parentId));
        Assertions.assertEquals(3, form.size());
        Assertions.assertTrue(form.containsKey(AippConst.FORM_APPEARANCE_KEY));
    }

    private AppBuilderForm buildForm() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("key", "key1");
        appearance.put("name", "name1");
        appearance.put("type", "TEXT");
        return AppBuilderForm.builder()
                .formPropertyRepository(this.formPropRepos)
                .name("form")
                .id("id")
                .appearance(Collections.singletonList(appearance))
                .type("component")
                .build();
    }

    private AppBuilderFormProperty buildFormProperty() {
        return AppBuilderFormProperty.builder()
                .dataType("String")
                .defaultValue("Qwen1.5-32B-Chat")
                .name("model")
                .formId("id")
                .id("id_id")
                .build();
    }
}