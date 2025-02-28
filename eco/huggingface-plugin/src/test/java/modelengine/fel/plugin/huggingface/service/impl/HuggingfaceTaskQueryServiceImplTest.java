/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import modelengine.jade.common.globalization.LocaleService;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceTaskInfo;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceTaskEntity;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceTaskMapper;
import modelengine.fel.plugin.huggingface.service.HuggingfaceTaskQueryService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link HuggingfaceTaskQueryServiceImpl} 的测试用例。
 *
 * @author 邱晓霞
 * @since 2024-09-10
 */
@FitTestWithJunit(includeClasses = HuggingfaceTaskQueryServiceImpl.class)
public class HuggingfaceTaskQueryServiceImplTest {
    @Fit
    private HuggingfaceTaskQueryService taskQueryService;

    @Mock
    private LocaleService localeService;

    @Mock
    private HuggingfaceTaskMapper taskMapper;

    @Test
    @DisplayName("查询可用任务列表成功")
    void shouldOkWhenQueryAvailableTasks() {
        HuggingfaceTaskEntity entity = new HuggingfaceTaskEntity(1L,
                "automatic-speech-recognition",
                "automatic-speech-recognition.description",
                0);
        List<HuggingfaceTaskEntity> entities = Collections.singletonList(entity);
        HuggingfaceTaskInfo taskInfo = new HuggingfaceTaskInfo(1L,
                "自动语音识别",
                "自动语音识别 （ASR），也称为语音转文本 （STT），是将给定音频转录为文本的任务。"
                        + "它有许多应用程序，例如语音用户界面。");
        when(this.taskMapper.listAvailableTasks()).thenReturn(entities);
        when(this.localeService.localize("automatic-speech-recognition")).thenReturn("自动语音识别");
        when(this.localeService.localize("automatic-speech-recognition.description")).thenReturn(
                "自动语音识别 （ASR），也称为语音转文本 （STT），是将给定音频转录为文本的任务。"
                        + "它有许多应用程序，例如语音用户界面。");
        List<HuggingfaceTaskInfo> response = this.taskQueryService.listAvailableTasks();
        assertThat(response.get(0).getTaskName()).isEqualTo("自动语音识别");
        assertThat(response.get(0).getTaskDescription()).isEqualTo("自动语音识别 （ASR），也称为语音转文本 （STT）"
                + "，是将给定音频转录为文本的任务。它有许多应用程序，例如语音用户界面。");
    }
}