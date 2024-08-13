/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.voice.service;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 提供语音的通用服务。
 *
 * @author 张粟
 * @since 2024-06-18
 */
public interface VoiceService {
    /**
     * 基于语音生成文本。
     *
     * @param voicePath 输入语音 {@link String}。
     * @param fileName 输入语音 {@link String}。
     * @return 输出文本 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.voice.getText")
    String getText(String voicePath, String fileName);

    /**
     * 基于文本生成语音。
     *
     * @param text 输入文本 {@link String}。
     * @param tone 输入音色 {@link int}。
     * @return 输出语音 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.voice.getVoice")
    String getVoice(String text, int tone);
}
