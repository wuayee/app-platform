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
 * {@link AppBuilderSaveConfigDto} 的测试类。
 *
 * @author 鲁为
 * @since 2024-10-29
 */
public class AppBuilderSaveConfigDtoTest {
    @Test
    @DisplayName("测试无参构造传输类")
    public void testNoArgsConstruction() {
        AppBuilderSaveConfigDto dto = new AppBuilderSaveConfigDto();
        List<AppBuilderConfigFormPropertyDto> list = dto.getInput();
        String graph = dto.getGraph();
        Assertions.assertNull(list);
        Assertions.assertNull(graph);
    }

    @Test
    @DisplayName("测试满参构造传输类")
    public void testAllArgsConstruction() {
        AppBuilderSaveConfigDto dto = new AppBuilderSaveConfigDto(Collections.EMPTY_LIST, "");
        Assertions.assertEquals(Collections.EMPTY_LIST, dto.getInput());
    }

    @Test
    @DisplayName("测试构建器构造传输类")
    public void testBuilderConstruction() {
        AppBuilderSaveConfigDto dto = AppBuilderSaveConfigDto.builder()
                .input(Collections.EMPTY_LIST)
                .graph("")
                .build();
        Assertions.assertEquals(Collections.EMPTY_LIST, dto.getInput());
    }
}
