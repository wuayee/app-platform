/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.ItemInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * 表示 {@link DefaultApplication} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-04-06
 */
@DisplayName("测试 DefaultApplication")
public class DefaultApplicationTest {
    private DefaultApplication application;

    @BeforeEach
    void setup() {
        this.application = new DefaultApplication(ItemInfo.custom()
                .category("Application")
                .group("G1")
                .name("N1")
                .uniqueName("UN")
                .description("This is a demo Application.")
                .tags(Collections.singleton("Application"))
                .schema(this.buildSchema())
                .build());
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("category", "Application")
                .put("group", "G1")
                .put("name", "N1")
                .put("uniqueName", "UN")
                .put("description", "This is a demo Application.")
                .put("tags", Collections.singleton("Application"))
                .build();
    }

    @Test
    @DisplayName("当获取元数的每一项成功，返回正确的结果")
    void shouldReturnCorrectSchemaWhenCallItemMetadataElementsGivenEmpty() {
        // given
        Map<String, Object> expectedSchema = buildSchema();
        // when
        Map<String, Object> actualSchema = this.application.itemInfo().schema();
        // then
        assertThat(this.application.itemInfo().tags()).contains("Application");
        assertThat(this.application.itemInfo().group()).isEqualTo("G1");
        assertThat(this.application.itemInfo().name()).isEqualTo("N1");
        assertThat(this.application.itemInfo().uniqueName()).isEqualTo("UN");
        assertThat(this.application.itemInfo().description()).isEqualTo("This is a demo Application.");
        assertThat(actualSchema).isEqualTo(expectedSchema);
        assertThat(actualSchema.get("category")).isEqualTo("Application");
    }
}
