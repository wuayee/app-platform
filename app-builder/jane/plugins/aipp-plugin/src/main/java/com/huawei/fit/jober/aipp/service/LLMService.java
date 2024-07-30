/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 大模型多模态交互接口
 *
 * @author 孙怡菲 s00664640
 * @since 2024-05-10
 */
public interface LLMService {
    /**
     * 使用图像文件与大模型交互
     *
     * @param image 输入的图像
     * @param prompt 大模型提示词
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithImage(File image, String prompt) throws IOException;

    /**
     * 使用音频文件与大模型交互
     *
     * @param audio 输入的音频文件
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithAudio(File audio) throws IOException;

    /**
     * 使用文本文件与大模型交互
     *
     * @param prompt 大模型提示词
     * @param model 大模型
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithText(String prompt, LlmModelNameEnum model) throws IOException;

    /**
     * 使用文本与大模型交互
     *
     * @param prompt 大模型提示词
     * @param maxTokens 大模型最大生成的 token 数量
     * @param temperature 温度
     * @param model 大模型
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithText(String prompt, int maxTokens, double temperature, LlmModelNameEnum model)
            throws IOException;

    /**
     * 使用知识库与小海大模型交互
     *
     * @param w3Id 用户信息
     * @param question 用户提问
     * @return 小海的返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askXiaoHaiKnowledge(String w3Id, String question) throws IOException;

    /**
     * 使用文件与小海交互
     *
     * @param w3Id 用户信息
     * @param question 用户提问
     * @return 小海的返回值
     * @throws IOException 读取数据过程发生异常
     */
    List<FileDto> askXiaoHaiFile(String w3Id, String question) throws IOException;
}
