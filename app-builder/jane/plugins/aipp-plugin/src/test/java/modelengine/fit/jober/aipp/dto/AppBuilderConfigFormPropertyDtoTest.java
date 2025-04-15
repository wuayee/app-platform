/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link AppBuilderConfigFormPropertyDto} 的测试类。
 *
 * @author 鲁为
 * @since 2024-10-29
 */

public class AppBuilderConfigFormPropertyDtoTest {
    @Test
    @DisplayName("测试构造传输类")
    public void constructAppBuilderConfigFormPropertyDto() {
        AppBuilderConfigFormPropertyDto dto = new AppBuilderConfigFormPropertyDto();
        dto.setId("testId");
        dto.setName("hello");
        dto.setDataType("String");
        dto.setDefaultValue("basic");
        dto.setFrom("null");
        dto.setGroup("none");
        dto.setDescription("description");
        dto.setChildren(Collections.EMPTY_LIST);
        dto.setNodeId("jadenjbdf");
        String from = dto.getFrom();
        String description = dto.getDescription();
        List<AppBuilderConfigFormPropertyDto> children = dto.getChildren();
        Assertions.assertEquals("null", from);
        Assertions.assertEquals("description", description);
        Assertions.assertEquals(Collections.EMPTY_LIST, children);
    }
}
