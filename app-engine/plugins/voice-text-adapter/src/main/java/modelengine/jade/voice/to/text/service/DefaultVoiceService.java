/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.voice.to.text.service;

import modelengine.jade.voice.service.VoiceService;

import modelengine.fel.service.pipeline.HuggingFacePipelineService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.HashMap;
import java.util.Map;

/**
 * 语音的 Http 请求的服务层实现。
 *
 * @author 张粟
 * @since 2024-06-18
 */
@Component
public class DefaultVoiceService implements VoiceService {
    private static final int TEXT_JSON_BEGIN_INDEX = 6;

    // 定义自定义词汇表
    private static final Map<String, String> CUSTOM_WORDS = new HashMap<>();

    private static final int VOICE_JSON_BEGIN_INDEX = 1;

    static {
        CUSTOM_WORDS.put("生疼", "昇腾");
        CUSTOM_WORDS.put("升腾", "昇腾");
        CUSTOM_WORDS.put("生腾", "昇腾");
        CUSTOM_WORDS.put("生藤", "昇腾");
        CUSTOM_WORDS.put("升藤", "昇腾");
        CUSTOM_WORDS.put("深藤", "昇腾");
        CUSTOM_WORDS.put("生糖", "昇腾");
        CUSTOM_WORDS.put("伤疼", "昇腾");
        CUSTOM_WORDS.put("生存", "昇腾");
        CUSTOM_WORDS.put("生酮", "昇腾");
        CUSTOM_WORDS.put("升堂", "昇腾");
        CUSTOM_WORDS.put("升头", "昇腾");
        CUSTOM_WORDS.put("昏盆", "鲲鹏");
        CUSTOM_WORDS.put("昆蓬", "鲲鹏");
        CUSTOM_WORDS.put("昆彭", "鲲鹏");
        CUSTOM_WORDS.put("关旁", "鲲鹏");
        CUSTOM_WORDS.put("困忙", "鲲鹏");
        CUSTOM_WORDS.put("坤鹏", "鲲鹏");
        CUSTOM_WORDS.put("官房", "鲲鹏");
        CUSTOM_WORDS.put("正起", "政企");
        CUSTOM_WORDS.put("正气", "政企");
        CUSTOM_WORDS.put("正期", "政企");
        CUSTOM_WORDS.put("正企", "政企");
        CUSTOM_WORDS.put("正启", "政企");
        CUSTOM_WORDS.put("振起", "政企");
        CUSTOM_WORDS.put("震起", "政企");
        CUSTOM_WORDS.put("挣起", "政企");
    }

    private final HuggingFacePipelineService pipelineService;

    public DefaultVoiceService(HuggingFacePipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    /**
     * 语音转文字(面向小魔方的临时代码，后续得整改)
     *
     * @param voiceUrl 输入语音文件路径 {@link String}。
     * @return 文本 {@link String}。
     */
    @Override
    @Fitable(id = "voice-get-text")
    public String getText(String voiceUrl) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("inputs", voiceUrl);
        String resultJson =
                this.pipelineService.call("automatic-speech-recognition", "openai/whisper-large-v3", postParam)
                        .toString();
        String text = resultJson.substring(TEXT_JSON_BEGIN_INDEX, resultJson.length() - 1);
        return this.postProcessTranscription(text, CUSTOM_WORDS);
    }

    /**
     * 后处理函数
     *
     * @param transcription transcription
     * @param customWords customWords
     * @return String
     */
    public String postProcessTranscription(String transcription, Map<String, String> customWords) {
        StringBuilder processedTranscription = new StringBuilder(transcription);
        for (Map.Entry<String, String> entry : customWords.entrySet()) {
            int index;
            while ((index = processedTranscription.indexOf(entry.getKey())) != -1) {
                processedTranscription.replace(index, index + entry.getKey().length(), entry.getValue());
            }
        }
        return processedTranscription.toString();
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
