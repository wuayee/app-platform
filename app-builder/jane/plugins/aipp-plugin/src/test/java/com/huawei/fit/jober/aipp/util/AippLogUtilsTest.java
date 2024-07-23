/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * AippLogUtils测试类
 *
 * @author f00881613
 * @since 2024-06-25
 */
class AippLogUtilsTest {
    @Test
    void testMSGType() {
        AippLogData test = AippLogData.builder().msg("This is a MSG log").build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertTrue((boolean) validFormMsg.invoke(null, test, "MSG"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testBlankFormId() {
        AippLogData testNull = AippLogData.builder().formId(null).formVersion("1.1").formArgs("test").build();
        AippLogData testEmpty = AippLogData.builder().formId("").formVersion("1.1").formArgs("test").build();
        AippLogData testWhitespace = AippLogData.builder().formId("   ").formVersion("1.1").formArgs("test").build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertFalse((boolean) validFormMsg.invoke(null, testNull, "FORM"));
            assertFalse((boolean) validFormMsg.invoke(null, testEmpty, "FORM"));
            assertFalse((boolean) validFormMsg.invoke(null, testWhitespace, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testInvalidFormId() {
        AippLogData testInvalidFormId = AippLogData
                .builder()
                .formId("undefined")
                .formVersion("1.1")
                .formArgs("test")
                .build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertFalse((boolean) validFormMsg.invoke(null, testInvalidFormId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testBlankFormVersion() {
        AippLogData testNull = AippLogData.builder().formId("123456").formVersion(null).formArgs("test").build();
        AippLogData testEmpty = AippLogData.builder().formId("123456").formVersion("").formArgs("test").build();
        AippLogData testWhitespace = AippLogData.builder().formId("123456").formVersion("  ").formArgs("test").build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertFalse((boolean) validFormMsg.invoke(null, testNull, "FORM"));
            assertFalse((boolean) validFormMsg.invoke(null, testEmpty, "FORM"));
            assertFalse((boolean) validFormMsg.invoke(null, testWhitespace, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testInvalidFormVersion() {
        AippLogData testInvalidVersionId = AippLogData
                .builder()
                .formId("123456")
                .formVersion("undefined")
                .formArgs("test").build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertFalse((boolean) validFormMsg.invoke(null, testInvalidVersionId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testNormal() {
        AippLogData testInvalidVersionId = AippLogData
                .builder()
                .formId("123456")
                .formVersion("1.1")
                .formArgs("test")
                .build();
        try {
            Method validFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "validFormMsg", AippLogData.class, String.class);
            validFormMsg.setAccessible(true);
            assertTrue((boolean) validFormMsg.invoke(null, testInvalidVersionId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get validFormMsg method: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("testIsLogEnabled")
    void testIsLogEnabled() {
        Map<String, Object> contextData = MapBuilder.get(() -> new HashMap<String, Object>()).build();
        assertTrue(AippLogUtils.isLogEnabled(contextData));
        Map<String, Object> configKeyObj = MapBuilder.get(() -> new HashMap<String, Object>()).build();
        contextData.put(AippConst.BS_EXTRA_CONFIG_KEY, configKeyObj);
        assertTrue(AippLogUtils.isLogEnabled(contextData));
        configKeyObj.put(AippConst.BS_LOG_ENABLE_KEY, "false");
        assertFalse(AippLogUtils.isLogEnabled(contextData));
    }
}