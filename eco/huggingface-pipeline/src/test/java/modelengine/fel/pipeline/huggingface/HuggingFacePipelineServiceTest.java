/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.pipeline.huggingface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import modelengine.fel.pipeline.huggingface.service.HuggingFacePipelineServiceImpl;
import modelengine.fel.service.pipeline.HuggingFacePipelineService;

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
                new HuggingFacePipelineServiceImpl("http://localhost:" + this.server.getPort());
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

    @Test
    public void testPipelineOutputError() {
        this.server.enqueue(new MockResponse().setResponseCode(404));
        this.server.enqueue(new MockResponse().setResponseCode(500));
        Map<String, Object> args = new HashMap<>();
        Map<String, Object> response = (Map<String, Object>) this.service.call("task", "model", args);
        assertNull(response);
    }

    @Test
    public void testPipelineOutputErrorMessage() {
        this.server.enqueue(new MockResponse().setResponseCode(500));
        Map<String, Object> args = new HashMap<>();
        String url = "http://localhost:" + this.server.getPort() + "/v1/huggingface/pipeline";
        try {
            this.service.call("task", "model", args);
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Failed to call Hugging Face pipeline: Response{protocol=http/1.1, code=500, "
                    + "message=Server Error, url=" + url + "}");
        }
    }
}
