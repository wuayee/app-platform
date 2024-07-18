/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface LLMService {
    String askModelWithImage(File image, String prompt) throws IOException;

    String askModelWithAudio(File audio) throws IOException;

    String askModelWithText(String prompt, LlmModelNameEnum model) throws IOException;

    String askModelWithText(String prompt, int maxTokens, double temperature, LlmModelNameEnum model)
            throws IOException;

    String askXiaoHaiKnowledge(String w3Id, String question) throws IOException;

    List<FileDto> askXiaoHaiFile(String w3Id, String question) throws IOException;
}
