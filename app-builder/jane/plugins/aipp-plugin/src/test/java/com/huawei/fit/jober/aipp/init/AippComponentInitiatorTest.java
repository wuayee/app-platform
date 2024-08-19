/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.huawei.fit.jober.aipp.common.ResourceLoader;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fitframework.plugin.Plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 表示 {@link AippComponentInitiator} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-20
 */
@DisplayName("测试 AippComponentInitiatorTest")
public class AippComponentInitiatorTest {
    private static final String RESOURCE_PATH = "component";
    private static final String FLOW_ZH_PATH = "/flow_zh.json";
    private static final String FLOW_EN_PATH = "/flow_en.json";
    private static final String FORM_ZH_PATH = "/form_zh.json";
    private static final String FORM_EN_PATH = "/form_en.json";
    private static final String BASIC_NODE_ZH_PATH = "/basic_node_zh.json";
    private static final String BASIC_NODE_EN_PATH = "/basic_node_en.json";

    private AippComponentInitiator aippComponentInitiator;
    private Plugin plugin;
    private MockedStatic<ResourceLoader> resourceLoaderMockedStatic;

    @BeforeEach
    void setUp() {
        this.plugin = mock(Plugin.class);
        this.aippComponentInitiator = new AippComponentInitiator(this.plugin);
        this.resourceLoaderMockedStatic = mockStatic(ResourceLoader.class);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + FLOW_ZH_PATH)).thenReturn(FLOW_ZH_PATH);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + FORM_ZH_PATH)).thenReturn(FORM_ZH_PATH);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + BASIC_NODE_ZH_PATH)).thenReturn(BASIC_NODE_ZH_PATH);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + FLOW_EN_PATH)).thenReturn(FLOW_EN_PATH);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + FORM_EN_PATH)).thenReturn(FORM_EN_PATH);
        this.resourceLoaderMockedStatic.when(() -> ResourceLoader.loadFileData(this.plugin,
                RESOURCE_PATH + BASIC_NODE_EN_PATH)).thenReturn(BASIC_NODE_EN_PATH);
    }

    @AfterEach
    void tearDown() {
        this.resourceLoaderMockedStatic.close();
    }

    @Test
    @DisplayName("初始化数据时，返回成功")
    void shouldSuccessWhenLoadComponentData()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method = AippComponentInitiator.class.getDeclaredMethod("loadComponentData");
        method.setAccessible(true);
        method.invoke(this.aippComponentInitiator);
        Field field = AippComponentInitiator.class.getDeclaredField("COMPONENT_DATA");
        Map<String, Object> mockedComponentData = (Map<String, Object>) field.get(this.aippComponentInitiator);
        assertThat(mockedComponentData.get(AippConst.FLOW_COMPONENT_DATA_ZH_KEY)).isEqualTo(FLOW_ZH_PATH);
        assertThat(mockedComponentData.get(AippConst.FORM_COMPONENT_DATA_ZH_KEY)).isEqualTo(FORM_ZH_PATH);
        assertThat(mockedComponentData.get(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY)).isEqualTo(BASIC_NODE_ZH_PATH);
        assertThat(mockedComponentData.get(AippConst.FLOW_COMPONENT_DATA_EN_KEY)).isEqualTo(FLOW_EN_PATH);
        assertThat(mockedComponentData.get(AippConst.FORM_COMPONENT_DATA_EN_KEY)).isEqualTo(FORM_EN_PATH);
        assertThat(mockedComponentData.get(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY)).isEqualTo(BASIC_NODE_EN_PATH);
    }
}
