/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static org.assertj.core.api.Assertions.assertThatCode;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.jade.voice.service.VoiceService;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jober.aipp.dto.chat.AudioTranslationParams;
import modelengine.fit.jober.aipp.dto.chat.TextTranslationParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link AudioController} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
@DisplayName("测试 AudioController")
@ExtendWith(MockitoExtension.class)
class AudioControllerTest {
    private AudioController controller;
    @Mock
    private Authenticator authenticator;
    @Mock
    private VoiceService voiceService;
    @Mock
    private HttpClassicServerRequest request;

    @BeforeEach
    void before() {
        this.controller = new AudioController(authenticator, voiceService);
    }

    @Test
    @DisplayName("当语音转文字时，返回正确结果。")
    void shouldReturnOkWhenAudioToText() {
        assertThatCode(() -> this.controller.audioToText(TextTranslationParams.builder()
                .fileName("fileName")
                .voicePath("voicePath")
                .build())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("当语音转文字时，返回正确结果。")
    void shouldReturnOkWhenTextToAudio() {
        assertThatCode(() -> this.controller.textToAudio(AudioTranslationParams.builder()
                .text("text")
                .tone(1)
                .build())).doesNotThrowAnyException();
    }
}