/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AippDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.aipp.genericable.entity.AippCreate;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ConvertUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-16
 */
@ExtendWith(MockitoExtension.class)
public class ConvertUtilsTest {
    @Test
    @DisplayName("convertToAippDtoFromAppBuilderAppDto")
    void testConvertToAippDtoFromAppBuilderAppDto() {
        Map<String, Object> attributes = MapBuilder.get(() -> new HashMap<String, Object>())
                .put("description", "h")
                .put("icon", "i")
                .put("app_type", "写作助手")
                .build();
        AppBuilderAppDto build = AppBuilderAppDto.builder()
                .config(null)
                .attributes(attributes)
                .flowGraph(AppBuilderFlowGraphDto.builder().appearance(new HashMap<>()).build())
                .publishUrl("publishUrl")
                .id("id")
                .version("1.0.0")
                .type("app")
                .publishedDescription("pd")
                .build();
        AippDto aippDto = Assertions.assertDoesNotThrow(() -> ConvertUtils.convertToAippDtoFromAppBuilderAppDto(build));
        Assertions.assertEquals("写作助手", aippDto.getXiaohaiClassification());
        Assertions.assertEquals("h", aippDto.getDescription());
        Assertions.assertEquals("1.0.0", aippDto.getVersion());
    }

    @Test
    @DisplayName("convertToAippDtoFromAppBuilderApp")
    void testConvertToAippDtoFromAppBuilderApp() {
        AppBuilderApp app = AppBuilderApp.builder()
                .attributes(MapBuilder.get(() -> new HashMap<String, Object>())
                        .put("description", "h")
                        .put("icon", "i")
                        .put("app_type", "写作助手")
                        .build())
                .version("1.0.0")
                .name("app")
                .type("type")
                .id("id")
                .build();
        AippDto aippDto = Assertions.assertDoesNotThrow(() -> ConvertUtils.convertToAippDtoFromAppBuilderApp(app));

        Assertions.assertEquals("写作助手", aippDto.getXiaohaiClassification());
        Assertions.assertEquals("h", aippDto.getDescription());
        Assertions.assertEquals("1.0.0", aippDto.getVersion());
    }

    @Test
    @DisplayName("toAippCreate")
    void testToAippCreate() {
        AippCreateDto dto =
                AippCreateDto.builder().aippId("aippId").toolUniqueName("uniqueName").version("1.0.0").build();

        AippCreate aippCreate = Assertions.assertDoesNotThrow(() -> ConvertUtils.toAippCreate(dto));

        Assertions.assertEquals("uniqueName", aippCreate.getToolUniqueName());
        Assertions.assertEquals("aippId", aippCreate.getAippId());
        Assertions.assertEquals("1.0.0", aippCreate.getVersion());
    }

    @Test
    @DisplayName("toAippCreateDto")
    void testToAippCreateDto() {
        AippCreate create = AippCreate.builder().aippId("aippId").toolUniqueName("uniqueName").version("1.0.0").build();

        AippCreateDto dto = Assertions.assertDoesNotThrow(() -> ConvertUtils.toAippCreateDto(create));

        Assertions.assertEquals("uniqueName", dto.getToolUniqueName());
        Assertions.assertEquals("aippId", dto.getAippId());
        Assertions.assertEquals("1.0.0", dto.getVersion());
    }
}
