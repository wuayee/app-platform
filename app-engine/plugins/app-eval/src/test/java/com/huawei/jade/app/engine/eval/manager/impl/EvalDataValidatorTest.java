/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import static com.huawei.jade.app.engine.eval.code.AppEvalRetCode.EVAL_DATA_INVALID_ERROR;
import static com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode.VALIDATE_CONTENT_INVALID_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.eval.exception.AppEvalException;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.schema.SchemaValidator;
import com.huawei.jade.app.engine.schema.exception.SchemaValidateException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataValidatorImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-7-27
 */
@FitTestWithJunit(includeClasses = EvalDataValidatorImpl.class)
public class EvalDataValidatorTest {
    private static final String SCHEMA =
            "{\"title\": \"UserInfo\", \"type\": \"object\", \"properties\": {\"name\": {\"type\": \"string\"}}}}";

    private static final String VALID_CONTENT = "{\"name\": \"Jerry\"\n}";

    private static final String INVALID_CONTENT = "{\"name\": 15\n}";

    @Fit
    private EvalDataValidator validator;

    @Mock
    private EvalDatasetMapper datasetMapperMock;

    @Mock
    private SchemaValidator schemaValidatorMock;

    @Test
    @DisplayName("批量校验评估数据成功")
    void shouldOkWhenBatchValidEvalData() {
        List<String> contents = Collections.singletonList(VALID_CONTENT);
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        doNothing().when(this.schemaValidatorMock).validate(any(), any());
        assertDoesNotThrow(() -> validator.verify(0L, contents));
    }

    @Test
    @DisplayName("批量校验评估数据失败")
    void shouldNotOkWhenBatchValidEvalData() {
        List<String> contents = Collections.singletonList(INVALID_CONTENT);
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        String content = contents.get(0);
        String error = "INVALID CONTENT";
        doThrow(new SchemaValidateException(VALIDATE_CONTENT_INVALID_ERROR,
                content,
                SCHEMA,
                error)).when(this.schemaValidatorMock).validate(any(), any());
        AppEvalException ex = assertThrows(AppEvalException.class, () -> validator.verify(0L, contents));
        assertThat(ex.getCode()).isEqualTo(EVAL_DATA_INVALID_ERROR.getCode());
    }

    @Test
    @DisplayName("单个校验评估数据成功")
    void shouldOkWhenValidEvalData() {
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        doNothing().when(this.schemaValidatorMock).validate(any(), any());
        assertDoesNotThrow(() -> validator.verify(0L, VALID_CONTENT));
    }

    @Test
    @DisplayName("单个校验评估数据失败")
    void shouldNotOkWhenValidEvalData() {
        String content = "{\"name\": \"Jerry\",\n}";
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        doThrow(new SchemaValidateException(VALIDATE_CONTENT_INVALID_ERROR, content, SCHEMA, "INVALID CONTENT")).when(
                this.schemaValidatorMock).validate(any(), any());
        AppEvalException ex = assertThrows(AppEvalException.class, () -> validator.verify(0L, content));
        assertThat(ex.getCode()).isEqualTo(EVAL_DATA_INVALID_ERROR.getCode());
    }
}
