/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.util.HttpUtils.sendHttpRequest;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileListDto;
import com.huawei.fit.jober.aipp.dto.xiaohai.RespMsgDto;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.HttpUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.Role;
import com.huawei.jade.fel.model.openai.entity.chat.message.content.UserContent;
import com.huawei.jade.voice.service.VoiceService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 大模型服务实现类
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class LLMServiceImpl implements LLMService {
    private static final Logger log = Logger.get(LLMServiceImpl.class);

    private final String xiaoHaiKnowledgeUrl;
    private final String xiaoHaiFileUrl;
    private final int xiaoHaiReadTimeout;
    private final OpenAiClient openAiClient;
    private final String appengineEndPoint;
    private final String pathPrefix;
    private final VoiceService voiceService;
    private final HttpClassicClientFactory httpClientFactory;

    public LLMServiceImpl(@Value("${model.xiaohai.knowledge}") String xiaoHaiKnowledgeUrl,
            @Value("${model.xiaohai.file}") String xiaoHaiFileUrl,
            @Value("${model.xiaohai.read-timeout}") int xiaoHaiReadTimeout,
            @Value("${app-engine.endpoint}") String endpoint, @Value("${app-engine.pathPrefix}") String pathPrefix,
            @Fit OpenAiClient openAiClient, @Fit VoiceService voiceService,
            @Fit HttpClassicClientFactory httpClientFactory) {
        this.xiaoHaiKnowledgeUrl = xiaoHaiKnowledgeUrl;
        this.xiaoHaiFileUrl = xiaoHaiFileUrl;
        this.xiaoHaiReadTimeout = xiaoHaiReadTimeout;
        this.openAiClient = openAiClient;
        this.appengineEndPoint = endpoint;
        this.pathPrefix = pathPrefix;
        this.voiceService = voiceService;
        this.httpClientFactory = httpClientFactory;
    }

    /**
     * 构造小海请求体
     * 修复question带有\n的情况， 手动拼接导致json无效的问题
     *
     * @param w3Id 工号
     * @param question 问题
     * @return 请求体的json字符串
     */
    public String getAskXiaoHaiReqBody(String w3Id, String question) {
        XiaohaiReq req = XiaohaiReq.builder().employeeId(w3Id).question(question).build();
        return JsonUtils.toJsonString(req);
    }

    @Override
    public String askModelWithImage(File image, String prompt) {
        String imageUrl = AippFileUtils.getFileDownloadUrl(appengineEndPoint, this.pathPrefix, image.getPath(),
                image.getName());
        log.info("get image url: {}", imageUrl);
        UserContent promptContent = UserContent.text(prompt);
        UserContent imageContent = UserContent.image(imageUrl);
        OpenAiChatMessage msg = OpenAiChatMessage.builder().role(Role.USER.name())
                .content(Arrays.asList(promptContent, imageContent)).build();
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.builder()
                .model(LlmModelNameEnum.QWEN_VL.getValue()).messages(Collections.singletonList(msg)).build();
        try {
            OpenAiChatCompletionResponse response = openAiClient.createChatCompletion(request);
            String ans = ObjectUtils.cast(response.getChoices().get(0).getMessage().getContent());
            log.info("imageName={} ans={}", image.getName(), ans);
            return ans;
        } catch (IOException e) {
            log.error("askModelWithImage chat with LLM meet error: {}", e.getMessage());
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
    }

    @Override
    public String askModelWithAudio(File audio) throws IOException {
        String audioFilePath = AippFileUtils.getFileDownloadFilePath(
                appengineEndPoint, this.pathPrefix, audio.getPath());
        log.info("audioPath: {}, audioName: {}", audioFilePath, audio.getName());
        return voiceService.getText(audioFilePath.replaceAll("\\\\", "/"), audio.getName());
    }

    @Override
    public String askModelWithText(String prompt, LlmModelNameEnum model) throws IOException {
        OpenAiChatMessage promptMsg = OpenAiChatMessage.builder().role(Role.USER.name())
                .content(prompt).build();
        OpenAiChatCompletionRequest requset = OpenAiChatCompletionRequest.builder().model(model.getValue())
                .messages(Collections.singletonList(promptMsg)).build();

        return askModel(requset);
    }

    @Override
    public String askModelWithText(String prompt, int maxTokens, double temperature, LlmModelNameEnum model)
            throws IOException {
        OpenAiChatMessage promptMsg = OpenAiChatMessage.builder().role(Role.USER.name())
                .content(prompt).build();
        OpenAiChatCompletionRequest requset = OpenAiChatCompletionRequest.builder().model(model.getValue())
                .messages(Collections.singletonList(promptMsg)).temperature(temperature).maxTokens(maxTokens).build();
        return askModel(requset);
    }

    private String askModel(OpenAiChatCompletionRequest request) throws IOException {
        OpenAiChatCompletionResponse chatCompletionRes = openAiClient.createChatCompletion(request);
        String ans = ObjectUtils.cast(chatCompletionRes.getChoices().get(0).getMessage().getContent());
        log.info("question={} ans={}", ObjectUtils.<String>cast(request.getMessages().get(0).getContent()), ans);
        return ans;
    }

    @Override
    public String askXiaoHaiKnowledge(String w3Id, String question) throws IOException {
        RespMsgDto respMsgDto = askXiaoHai(w3Id, question, this.xiaoHaiKnowledgeUrl, RespMsgDto.class);
        log.info("w3Id={} question={} respMsgDto={}", w3Id, question, respMsgDto);
        return respMsgDto.getMsg();
    }

    private String encodeFileName(String fileName) {
        String res = "default_file_name";
        try {
            res = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("encodeFileName failed, {}", e.getMessage());
        }
        return res;
    }

    @Override
    public List<FileDto> askXiaoHaiFile(String w3Id, String question) throws IOException {
        FileListDto fileListDto = askXiaoHai(w3Id, question, this.xiaoHaiFileUrl, FileListDto.class);
        log.info("w3Id={} question={} fileListDto={}", w3Id, question, fileListDto);
        fileListDto.getRes().forEach(fileDto -> {
            String url64 = Base64.getEncoder().encodeToString(fileDto.getFileUrl().getBytes(StandardCharsets.UTF_8));
            // 由前端拼接域名等信息
            String aippFileReq = String.format(Locale.ROOT, "/file?fileUrl=%s&fileName=%s", url64,
                    encodeFileName(fileDto.getFileName()));
            fileDto.setFileUrl(aippFileReq);
            String pic64 = Base64.getEncoder().encodeToString(fileDto.getFilePic().getBytes(StandardCharsets.UTF_8));
            fileDto.setFilePic(pic64);
        });
        return fileListDto.getRes();
    }

    private <T> T askXiaoHai(String w3Id, String question, String url, Class<T> respDtoCls) throws IOException {
        HttpClassicClientRequest postRequest = httpClientFactory
                .create(HttpUtils.requestConfig(this.xiaoHaiReadTimeout))
                .createRequest(HttpRequestMethod.POST, url);
        postRequest.entity(ObjectEntity.create(postRequest, getAskXiaoHaiReqBody(w3Id, question)));
        String respContent = sendHttpRequest(postRequest);
        return JsonUtils.parseObject(respContent, respDtoCls);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class XiaohaiReq {
        private String employeeId;
        private String question;
    }
}
