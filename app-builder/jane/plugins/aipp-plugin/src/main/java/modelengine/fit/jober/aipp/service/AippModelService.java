/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.dto.model.PromptGenerateDto;

/**
 * aipp 大模型服务接口.
 *
 * @author 张越
 * @since 2024-11-30
 */
public interface AippModelService {
    /**
     * 聊天接口.
     *
     * @param model 模型名称.
     * @param tag 模型标签.
     * @param temperature 温度.
     * @param prompt 提示词.
     * @return {@link String} 大模型返回值.
     */
    String chat(String model, String tag, Double temperature, String prompt);

    /**
     * 生成提示词模板.
     *
     * @param param {@link PromptGenerateDto} 对象.
     * @return {@link String} 提示词模板.
     */
    String generatePrompt(PromptGenerateDto param);
}
