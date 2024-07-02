/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.huawei.fit.jober.aipp.entity.AippLogData;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertTrue((boolean) checkFormMsg.invoke(null, test, "MSG"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testBlankFormId() {
        AippLogData testNull = AippLogData.builder().formId(null).formVersion("1.1").formArgs("test").build();
        AippLogData testEmpty = AippLogData.builder().formId("").formVersion("1.1").formArgs("test").build();
        AippLogData testWhitespace = AippLogData.builder().formId("   ").formVersion("1.1").formArgs("test").build();
        try {
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertFalse((boolean) checkFormMsg.invoke(null, testNull, "FORM"));
            assertFalse((boolean) checkFormMsg.invoke(null, testEmpty, "FORM"));
            assertFalse((boolean) checkFormMsg.invoke(null, testWhitespace, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
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
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertFalse((boolean) checkFormMsg.invoke(null, testInvalidFormId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
        }
    }

    @Test
    void testBlankFormVersion() {
        AippLogData testNull = AippLogData.builder().formId("123456").formVersion(null).formArgs("test").build();
        AippLogData testEmpty = AippLogData.builder().formId("123456").formVersion("").formArgs("test").build();
        AippLogData testWhitespace = AippLogData.builder().formId("123456").formVersion("  ").formArgs("test").build();
        try {
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertFalse((boolean) checkFormMsg.invoke(null, testNull, "FORM"));
            assertFalse((boolean) checkFormMsg.invoke(null, testEmpty, "FORM"));
            assertFalse((boolean) checkFormMsg.invoke(null, testWhitespace, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
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
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertFalse((boolean) checkFormMsg.invoke(null, testInvalidVersionId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
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
            Method checkFormMsg = AippLogUtils.class.getDeclaredMethod(
                    "checkFormMsg", AippLogData.class, String.class);
            checkFormMsg.setAccessible(true);
            assertTrue((boolean) checkFormMsg.invoke(null, testInvalidVersionId, "FORM"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            fail("Failed to get checkFormMsg method: " + e.getMessage());
        }
    }
}