/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.okhttp.OkHttpClassicClientFactory;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jober.aipp.common.exception.AippNotFoundException;
import modelengine.fit.security.Decryptor;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * DefaultAippModelCenter 测试类。
 *
 * @author 方誉州
 * @since 2024-10-21
 */
@FitTestWithJunit(includeClasses = {OkHttpClassicClientFactory.class})
public class DefaultAippModelCenterTest {
    private MockWebServer server;
    private AippModelCenter aippModelCenter;
    @Fit
    private Config config;
    @Fit
    private HttpClassicClientFactory httpClientFactory;
    @Mock
    private Decryptor decryptor;
    @Fit
    private BeanContainer container;
    @Fit(alias = "json")
    private ObjectSerializer serializer;

    @BeforeEach
    void setup() {
        this.server = new MockWebServer();

        Map<String, String> modelBaseUrls = MapBuilder.<String, String>get()
                .put("internal", "http://internal/model/")
                .put("external", "http://external/model/")
                .build();
        this.aippModelCenter = new DefaultAippModelCenter(
                this.httpClientFactory,
                this.config,
                this.container,
                "http://localhost:" + this.server.getPort() + "/api/ui/serviceaccess/v1/service/LLM_SERVICE",
                modelBaseUrls, "Qwen2-72B-Instruct-GPTQ-Int4");
        doAnswer((Answer<String>) invocation -> invocation.getArgument(0)).when(this.decryptor)
                .decrypt(any(String.class));
    }

    @AfterEach
    void teardown() throws IOException {
        this.server.shutdown();
    }

    @Test
    @DisplayName("测试查询模型列表成功")
    void testFetchModelListSuccess() {
        Map<String, Object> rspContent = MapBuilder.<String, Object>get()
                .put("code", 0)
                .put("msg", "success")
                .put("data", Collections.singletonList(MapBuilder.get()
                        .put("id", 1)
                        .put("serviceName", "testService")
                        .put("modelType", "EXTERNAL")
                        .build()))
                .build();
        this.server.enqueue(new MockResponse().setBody(serializer.serialize(rspContent)).setHeader(
                "Content-Type", "application/json"
        ));
        ModelListDto modelListDto = aippModelCenter.fetchModelList(null, null);
        assertThat(modelListDto.getTotal()).isEqualTo(1);
        assertThat(modelListDto.getModels()).extracting(ModelAccessInfo::getServiceName).contains("testService");
        assertThat(modelListDto.getModels()).extracting(ModelAccessInfo::getTag).contains("EXTERNAL");
    }

    @Test
    @DisplayName("测试 Http 请求失败")
    void testHttpResponseFailed() {
        this.server.enqueue(new MockResponse().setHeader("Content-Type", "application/json")
                .setResponseCode(404));
        ModelListDto modelListDto = aippModelCenter.fetchModelList(null, null);
        assertThat(modelListDto.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试 Http 请正常返回但返回结果错误")
    void testHttpSuccessDataFailed() {
        Map<String, Object> rspContent = MapBuilder.<String, Object>get()
                .put("code", -1)
                .put("msg", "something failed")
                .put("data", Collections.emptyList())
                .build();

        this.server.enqueue(new MockResponse().setBody(serializer.serialize(rspContent)).setHeader(
                "Content-Type", "application/json"
        ));

        ModelListDto modelListDto = aippModelCenter.fetchModelList(null, null);
        assertThat(modelListDto.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试根据 tag 获取模型网管")
    void testGetModelBaseUrl() {
        assertThat(aippModelCenter.getModelAccessInfo("internal", null, null).getBaseUrl()).isEqualTo(
                "http://internal/model/");
        assertThat(aippModelCenter.getModelAccessInfo("external", null, null).getBaseUrl()).isEqualTo(
                "http://external/model/");
        assertThat(aippModelCenter.getModelAccessInfo("INTERNAL", null, null).getBaseUrl()).isEqualTo(
                "http://internal/model/");
        assertThrows(AippNotFoundException.class, () -> aippModelCenter.getModelAccessInfo("unknown", null, null));
    }
}


