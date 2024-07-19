/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.voice.to.text.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;
import com.huawei.jade.voice.service.VoiceService;

import java.util.HashMap;
import java.util.Map;

/**
 * 语音的 Http 请求的服务层实现。
 *
 * @author 张粟 z00605602
 * @since 2024-06-18
 */
@Component
public class DefaultVoiceService implements VoiceService {
    private static final int TEXT_JSON_BEGIN_INDEX = 6;

    private static final int VOICE_JSON_BEGIN_INDEX = 1;

    private final HuggingFacePipelineService pipelineService;

    public DefaultVoiceService(HuggingFacePipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    /**
     * 语音转文字
     *
     * @param voicePath 输入语音文件路径 {@link String}。
     * @param fileName  输入语音文件名称 {@link String}。
     * @return 文本 {@link String}。
     */
    @Override
    @Fitable(id = "voice-get-text")
    public String getText(String voicePath, String fileName) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("inputs", voicePath + "&fileName=" + fileName);
        String resultJson = this.pipelineService.call("automatic-speech-recognition", "openai/whisper-large-v3",
                postParam).toString();
        return resultJson.substring(TEXT_JSON_BEGIN_INDEX, resultJson.length() - 1);
    }

    /**
     * 文字转语音
     *
     * @param text 输入文本 {@link String}。
     * @param tone 输入音色 {@link int}。
     * @return 语音 {@link String}。
     */
    @Override
    @Fitable(id = "voice-get-voice")
    public String getVoice(String text, int tone) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("text_inputs", text);
        String resultJson = this.pipelineService.call("text-to-speech", "2Noise/ChatTTS",
                postParam).toString();
        return resultJson.split(",")[0].split("data")[1].substring(VOICE_JSON_BEGIN_INDEX);
    }
}
