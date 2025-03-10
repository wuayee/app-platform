/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.extractor;

import modelengine.jade.voice.service.VoiceService;

import modelengine.fit.jober.aipp.service.OperatorService.FileType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import java.util.Map;

/**
 * 音频提取工具。
 *
 * @author 马朝阳
 * @author 兰宇晨
 * @since 2024-12-12
 */
@Component
public class AudioExtractor implements BaseExtractor {
    private static final Logger LOG = Logger.get(ImageExtractor.class);

    private final VoiceService voiceService;

    public AudioExtractor(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    /**
     * 表示音频内容提取接口。
     *
     * @param fileUrl 音频链接。
     * @param context 文件提取额外参数。
     * @return 表示文件内容的 {@link String}。
     */
    @Override
    public String extract(String fileUrl, Map<String, Object> context) {
        return voiceService.getText(fileUrl);
    }

    @Override
    public FileType type() {
        return FileType.AUDIO;
    }
}
