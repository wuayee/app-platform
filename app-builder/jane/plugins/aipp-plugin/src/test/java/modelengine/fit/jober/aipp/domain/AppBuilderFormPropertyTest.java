/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.domain;

import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;

import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link AppBuilderFormProperty} 的测试类。
 *
 * @author 鲁为
 * @since 2024-11-01
 */
public class AppBuilderFormPropertyTest {
    @Test
    @DisplayName("根据 property 构造 dto")
    public void testAppBuilderConfigFormPropertyDto() {
        AppBuilderFormProperty property = AppBuilderFormProperty.builder()
                .id("id")
                .formId("formId")
                .name("name")
                .dataType("dataType")
                .defaultValue("defaultValue")
                .from("from")
                .group("group")
                .description("description")
                .build();
        AppBuilderConfigFormPropertyDto dto = AppBuilderFormProperty.toAppBuilderConfigFormPropertyDto(property);
        Assertions.assertEquals("id", dto.getId());
    }
}
