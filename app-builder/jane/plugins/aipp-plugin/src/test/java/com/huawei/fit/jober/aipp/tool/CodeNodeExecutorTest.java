/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.aipp.dto.CodeExecuteResDto;
import com.huawei.fit.jober.aipp.service.CodeExecuteService;
import com.huawei.fit.jober.aipp.service.impl.CodeExecuteServiceImpl;
import com.huawei.fit.jober.aipp.tool.impl.CodeNodeExecutorImpl;
import com.huawei.fit.jober.common.exceptions.JobberException;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * CodeNodeExecutor测试类
 *
 * @author 方誉州
 * @since 2024-06-28
 */
public class CodeNodeExecutorTest {
    private static CodeExecuteService mockService;

    @BeforeAll
    static void setup() {
        mockService = mock(CodeExecuteServiceImpl.class);
    }
    @Test
    void testParseObjectResultFromPython() {
        Map<String, Object> value = new HashMap<>();
        value.put("age", 18);
        value.put("info", new HashMap<>());
        ObjectUtils.<Map<String, Object>>cast(value.get("info")).put("name", "Zhang San");
        ObjectUtils.<Map<String, Object>>cast(value.get("info")).put("id", "123456");
        CodeExecuteResDto ret = CodeExecuteResDto.builder().isOk(true)
                .value(value)
                .build();

        when(mockService.run(any(), any(), any()))
                .thenReturn(ret);

        Map<String, Object> args = new HashMap<>();
        args.put("args", new HashMap<String, Object>());
        String code = "print(\"This is a test code.\")";
        CodeNodeExecutor codeNodeExecutor = new CodeNodeExecutorImpl(mockService);
        Object result = codeNodeExecutor.executeNodeCode(args, code, "python");

        assert(result instanceof Map);
        Map<String, Object> mapedResult = ObjectUtils.cast(result);
        assertEquals(18, ObjectUtils.<Integer>cast(mapedResult.get("age")));
        assertEquals("Zhang San",
                ObjectUtils.<String>cast(ObjectUtils.<Map<String, Object>>cast(mapedResult.get("info")).get("name")));
        assertEquals("123456",
                ObjectUtils.<String>cast(ObjectUtils.<Map<String, Object>>cast(mapedResult.get("info")).get("id")));
    }

    @Test
    void testParseStringResultFromPython() {
        String value = "Zhang San";
        CodeExecuteResDto ret = CodeExecuteResDto.builder().isOk(true)
                .value(value)
                .build();

        when(mockService.run(any(), any(), any()))
                .thenReturn(ret);

        Map<String, Object> args = new HashMap<>();
        args.put("args", new HashMap<String, Object>());
        String code = "print(\"This is a test code.\")";
        CodeNodeExecutor codeNodeExecutor = new CodeNodeExecutorImpl(mockService);
        Object result = codeNodeExecutor.executeNodeCode(args, code, "python");

        assert(result instanceof String);
        assertEquals("Zhang San", ObjectUtils.<String>cast(result));
    }

    @Test
    void testParseErrorResultFromPython() {
        String errorMsg = "Some Exception";
        CodeExecuteResDto ret = CodeExecuteResDto.builder().isOk(false)
                .msg(errorMsg)
                .build();

        when(mockService.run(any(), any(), any()))
                .thenReturn(ret);

        Map<String, Object> args = new HashMap<>();
        args.put("args", new HashMap<String, Object>());
        String code = "print(\"This is a test code.\")";
        CodeNodeExecutor codeNodeExecutor = new CodeNodeExecutorImpl(mockService);
        Assertions.assertThrows(JobberException.class,
                () -> codeNodeExecutor.executeNodeCode(args, code, "python"));
    }
}
