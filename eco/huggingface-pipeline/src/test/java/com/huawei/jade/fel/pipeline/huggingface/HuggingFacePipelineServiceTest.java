/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.fel.pipeline.huggingface.service.HuggingFacePipelineServiceImpl;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hugging Face 管道服务单元测试。
 *
 * @author 张庭怿
 * @since 2024-06-04
 */
public class HuggingFacePipelineServiceTest {
    private MockWebServer server;

    private HuggingFacePipelineService service;

    @BeforeEach
    public void setUp() throws IOException {
        this.server = new MockWebServer();
        this.service =
                new HuggingFacePipelineServiceImpl("http://localhost:" + this.server.getPort() + "/");
    }

    @AfterEach
    public void tearDown() throws IOException {
        this.server.shutdown();
    }

    @Test
    public void testPipelineOutputMap() {
        this.server.enqueue(new MockResponse().setBody("{\"test\": \"hello\"}"));
        Map<String, Object> args = new HashMap<>();
        args.put("input", "hello");
        Map<String, Object> response = (Map<String, Object>) this.service.call("task", "model", args);
        assertEquals(response.get("test"), "hello");
    }

    @Test
    public void testPipelineOutputList() {
        this.server.enqueue(new MockResponse().setBody("[\"hello\", \"world\"]"));
        Map<String, Object> args = new HashMap<>();
        args.put("input", "hello");
        List<String> response = (List<String>) this.service.call("task", "model", args);
        assertEquals(response, Arrays.asList("hello", "world"));
    }
}
