/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.service.ItemDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示验证 {@link ItemDto} 结构的序列化格式。
 *
 * @author 王攀博
 * @since 2024-04-26
 */
@DisplayName("测试 序列化格式")
public class ItemJsonSchemaFormatTest {
    private final Method testMethod;
    private final ObjectSerializer serializer;

    ItemJsonSchemaFormatTest() throws NoSuchMethodException {
        this.testMethod = MethodToolMetadataTest.TestInterface.class.getDeclaredMethod("testMethod", String.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
    }

    @Test
    @DisplayName("校验完整的item返回结果")
    void shouldReturnItemsResult() {
        // given
        String expectedValue =
                "[{\"category\":\"Tool\",\"group\":\"t1\",\"name\":\"test_schema_default_implementation_name\","
                        + "\"description\":\"This is a demo FIT function.\",\"uniqueName\":\"schema-uuid\","
                        + "\"schema\":{\"name\":\"test_schema_default_implementation_name\",\"description\":\"This is"
                        + " a demo FIT function.\",\"parameters\":{\"type\":\"object\","
                        + "\"properties\":{\"p1\":{\"type\":\"string\",\"description\":\"This is the first parameter"
                        + ".\"}},\"required\":[\"p1\"],\"order\":[\"p1\"]},\"return\":{\"type\":\"string\"},"
                        + "\"tags\":\"FIT\"},\"source\":null,\"tags\":[\"FIT\"]}]";

        // when
        ItemInfo itemInfo = this.buildItemInfo();
        ItemDto item = ItemDto.from(itemInfo);

        List<ItemDto> items = new ArrayList<>();
        items.add(item);
        String itemJsonSchema = new String(this.serializer.serialize(items, UTF_8), UTF_8);

        // then
        assertThat(itemJsonSchema).isEqualTo(expectedValue);
    }

    interface Interface {
        /**
         * 测试方法。
         *
         * @param p1 表示测试参数的 {@link String}。
         * @return 表示测试结果的 {@link String}。
         */
        @Genericable(id = "t1", description = "desc")
        String testMethod(@Property(name = "P1") String p1);
    }

    ItemInfo buildItemInfo() {
        return ItemInfo.custom()
                .category("Tool")
                .group("t1")
                .name("test_schema_default_implementation_name")
                .uniqueName("schema-uuid")
                .tags(Collections.singleton("FIT"))
                .description("This is a demo FIT function.")
                .schema(buildSchema())
                .build();
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("name", "test_schema_default_implementation_name")
                .put("description", "This is a demo FIT function.")
                .put("parameters",
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .build())
                                .put("order", Collections.singletonList("p1"))
                                .put("required", Collections.singletonList("p1"))
                                .build())
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .put("tags", "FIT")
                .build();
    }
}