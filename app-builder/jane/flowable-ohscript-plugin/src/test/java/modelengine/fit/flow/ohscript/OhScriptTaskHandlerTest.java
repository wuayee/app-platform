/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.flow.ohscript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于提供 {@link OhScriptTaskHandler} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-10-27
 */
@DisplayName("测试 OhScriptTaskHandler")
public class OhScriptTaskHandlerTest {
    private BeanContainer container;

    private BrokerClient brokerClient;

    private OhScriptTaskHandler handler;

    @BeforeEach
    void setup() {
        this.container = mock(BeanContainer.class);
        FitRuntime runtime = mock(FitRuntime.class);
        when(this.container.runtime()).thenReturn(runtime);
        AnnotationMetadataResolver resolver = mock(AnnotationMetadataResolver.class);
        when(runtime.resolverOfAnnotations()).thenReturn(resolver);
        AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        when(resolver.resolve(any())).thenReturn(annotations);
        Genericable genericable = mock(Genericable.class);
        when(annotations.getAnnotation(Genericable.class)).thenReturn(genericable);
        when(genericable.id()).thenReturn("genericableId");
        this.brokerClient = mock(BrokerClient.class);
        this.handler = new OhScriptTaskHandler(this.container, this.brokerClient);
    }

    @AfterEach
    void teardown() {
        this.brokerClient = null;
        this.container = null;
        this.handler = null;
    }

    @Test
    @Disabled
    @DisplayName("当传入上下文信息，可以正确调用 OhScript 脚本")
    void shouldInvokeOhScriptSuccessfully() {
        List<Map<String, Object>> flowData = Collections.singletonList(MapBuilder.<String, Object>get()
                .put("businessData", MapBuilder.<String, Object>get()
                        .put("entity", MapBuilder.<String, Object>get().put("code", "test").build())
                        .build())
                .build());
        assertThatNoException().isThrownBy(() -> this.handler.handleTask(flowData));
    }

    @Test
    @Disabled
    @DisplayName("当传入上下文信息，可以正确调用 OhScript 脚本来修改上下文信息")
    void shouldChangeContextSuccessfully() {
        String script = getJsonData("conditionForAipp.oh");
        Map<String, Object> context = MapBuilder.<String, Object>get()
                .put("businessData", MapBuilder.<String, Object>get()
                        .put("entity", MapBuilder.<String, Object>get().put("code", script).build())
                        .put("prompt", "123")
                        .build())
                .build();
        List<Map<String, Object>> flowData = new ArrayList<>();
        flowData.add(context);
        List<Map<String, Object>> resultMap = this.handler.handleTask(flowData);
        Map<String, String> businessData = ObjectUtils.cast(resultMap.get(0).get("businessData"));
        assertThat(businessData.get("prompt")).isEqualTo("123abc");
        assertThat(businessData.get("newKey")).isEqualTo("123");
    }

    /**
     * getJsonData
     *
     * @param fileName fileName
     * @return String
     */
    protected String getJsonData(String fileName) {
        try (InputStream in = IoUtils.resource(OhScriptTaskHandlerTest.class.getClassLoader(), fileName)) {
            return new String(IoUtils.read(in), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
