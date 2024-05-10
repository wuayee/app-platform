/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fit.jober.aipp.common.JsonUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class AippComponentFlowItemDtoTest {
    private AippComponentFlowItemDto aippComponentFlowItemDto;

    @Test
    void shouldSuccessWhenParsedFromJsonString() {
        String jsonString = "{\"type\": \"aippState\",\n\"name\": \"提取图像信息\",\n\"icon\": \"xx.icon\",\n"
                + "      \"description\": \"这是一个图像信息提取节点\",\n" + "      \"triggerMode\": \"auto\",\n"
                + "      \"jober\": {\n" + "        \"type\": \"GENERAL_JOBER\",\n" + "        \"name\": \"提取图像信息\",\n"
                + "        \"fitables\": [\n" + "          \"com.huawei.fit.jober.aipp.fitable.LLMImage2Text\"\n"
                + "        ]\n" + "      },\n" + "      \"group\": [\"imageProcessor\"]}";
        AippComponentFlowItemDto obj = JsonUtils.parseObject(jsonString, AippComponentFlowItemDto.class);
        Assertions.assertEquals(obj,
                AippComponentFlowItemDto.builder()
                        .type("aippState")
                        .name("提取图像信息")
                        .icon("xx.icon")
                        .description("这是一个图像信息提取节点")
                        .triggerMode("auto")
                        .jober(Stream.of(new AbstractMap.SimpleImmutableEntry<>("type", "GENERAL_JOBER"),
                                        new AbstractMap.SimpleImmutableEntry<>("name", "提取图像信息"),
                                        new AbstractMap.SimpleImmutableEntry<>("fitables",
                                                Collections.singletonList(
                                                        "com.huawei.fit.jober.aipp.fitable" + ".LLMImage2Text")))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                        .group(Collections.singletonList("imageProcessor"))
                        .build());
    }
}
