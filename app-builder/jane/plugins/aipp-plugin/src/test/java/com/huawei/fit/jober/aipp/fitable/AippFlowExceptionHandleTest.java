/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.common.ui.globalization.LocaleUiWord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link AippFlowExceptionHandle} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-20
 */
public class AippFlowExceptionHandleTest {
    private static final String UI_WORD_KEY = "aipp.fitable.AippFlowExceptionHandle";
    private static final String UI_WORD_KEY_HINT = "aipp.fitable.AippFlowExceptionHandle.hint";

    private AippFlowExceptionHandle aippFlowExceptionHandle;

    @Mock
    private AippLogService aippLogService;

    @Mock
    private MetaInstanceService metaInstanceService;

    @Mock
    private LocaleUiWord localeUiWord;

    @Test
    @DisplayName("测试构造方法")
    void shouldSuccessWhenConstruct() {
        this.aippLogService = mock(AippLogService.class);
        this.metaInstanceService = mock(MetaInstanceService.class);
        this.localeUiWord = mock(LocaleUiWord.class);
        this.aippFlowExceptionHandle = new AippFlowExceptionHandle(this.aippLogService,
                this.metaInstanceService,
                this.localeUiWord);
        String opContext = "{\"tenantId\": \"test\","
                + "\"operator\": \"test\","
                + "\"globalUserId\":\"test\","
                + "\"w3Account\":\"w3Account\","
                + "\"employeeNumber\": \"employeeNumber\","
                + "\"name\": \"name\","
                + "\"operatorIp\": \"operatorIp\","
                + "\"sourcePlatform\": \"sourcePlatform\","
                + "\"language\": \"language\"}";
        List<Map<String, Object>> flowData = Arrays.asList(MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, MapBuilder.<String, Object>get()
                        .put(AippConst.BS_HTTP_CONTEXT_KEY, opContext)
                        .put(AippConst.BS_META_VERSION_ID_KEY, "test")
                        .put(AippConst.BS_AIPP_INST_ID_KEY, "test")
                        .build())
                .build());
        Mockito.when(this.localeUiWord.getLocaleMessage(UI_WORD_KEY)).thenReturn("test");
        Mockito.when(this.localeUiWord.getLocaleMessage(UI_WORD_KEY_HINT)).thenReturn("test");
        this.aippFlowExceptionHandle.handleException("nodeId", flowData, "errorMessage");
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.aippLogService).insertErrorLog(stringCaptor.capture(), listCaptor.capture());
        String capturedString = stringCaptor.getValue();
        assertThat(capturedString).isEqualTo("test\ntest: errorMessage");
    }
}
