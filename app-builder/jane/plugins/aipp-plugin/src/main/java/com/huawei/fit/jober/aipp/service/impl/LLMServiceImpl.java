/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import static com.huawei.fit.jober.aipp.common.HttpUtils.sendHttpRequest;

import com.huawei.fit.jober.aipp.common.HttpUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileDto;
import com.huawei.fit.jober.aipp.dto.xiaohai.FileListDto;
import com.huawei.fit.jober.aipp.dto.xiaohai.RespMsgDto;
import com.huawei.fit.jober.aipp.entity.LlmEventListener;
import com.huawei.fit.jober.aipp.service.DistributedMapService;
import com.huawei.fit.jober.aipp.service.LLMService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.hllm.HllmClient;
import com.huawei.hllm.entity.HllmChatEntity;
import com.huawei.hllm.entity.HllmImageEntity;
import com.huawei.hllm.entity.HllmTranscriptionEntity;
import com.huawei.hllm.model.LlmModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Component
public class LLMServiceImpl implements LLMService {
    private static final Logger log = Logger.get(LLMServiceImpl.class);
    private final String xiaoHaiKnowledgeUrl;
    private final String xiaoHaiFileUrl;
    private final int xiaoHaiReadTimeout;
    private final HllmClient hllmClient;
    private final DistributedMapService mapService;

    public LLMServiceImpl(@Value("${model.xiaohai.knowledge}") String xiaoHaiKnowledgeUrl,
            @Value("${model.xiaohai.file}") String xiaoHaiFileUrl,
            @Value("${model.xiaohai.read-timeout}") int xiaoHaiReadTimeout, @Fit HllmClient hllmClient,
            @Fit DistributedMapService mapService) {
        this.xiaoHaiKnowledgeUrl = xiaoHaiKnowledgeUrl;
        this.xiaoHaiFileUrl = xiaoHaiFileUrl;
        this.xiaoHaiReadTimeout = xiaoHaiReadTimeout;
        this.hllmClient = hllmClient;
        this.mapService = mapService;
    }



    // 修复question带有\n的情况， 手动拼接导致json无效的问题
    public String getAskXiaoHaiReqBody(String w3Id, String question) {
        XiaohaiReq req = XiaohaiReq.builder().employeeId(w3Id).question(question).build();
        return JsonUtils.toJsonString(req);
    }

    @Override
    public String askModelWithImage(File image, String prompt) throws IOException {
        // 不需要传prompt
        final String defaultPrompt = "";
        HllmImageEntity entity = new HllmImageEntity(image, StringUtils.isBlank(prompt) ? defaultPrompt : prompt);
        String ans = hllmClient.predict(entity, LlmModel.QWEN_VL);
        log.info("imageName={} ans={}", image.getName(), ans);
        return ans;
    }

    @Override
    public String askModelWithAudio(File audio) throws IOException {
        return hllmClient.transcribe(HllmTranscriptionEntity.builder().file(audio).build());
    }

    @Override
    public String askModelWithText(String prompt, LlmModel model) throws IOException {
        HllmChatEntity hllmChatEntity = HllmChatEntity.builder().prompt(prompt).build();
        return askModel(hllmChatEntity, model);
    }

    @Override
    public String askModelWithText(String prompt, int maxTokens, double temperature, LlmModel model)
            throws IOException {
        HllmChatEntity hllmChatEntity =
                HllmChatEntity.builder().prompt(prompt).temperature(temperature).tokens(maxTokens).build();
        return askModel(hllmChatEntity, model);
    }

    @Override
    public LlmEventListener askModelStreaming(String prompt, int maxTokens, LlmModel model, String instanceId,
            String modelResultKey) {
        HllmChatEntity hllmChatEntity = HllmChatEntity.builder().prompt(prompt).tokens(maxTokens).build();
        LlmEventListener listener = new LlmEventListener(instanceId, modelResultKey, mapService);
        hllmClient.chat(hllmChatEntity, listener, model);
        return listener;
    }

    private String askModel(HllmChatEntity entity, LlmModel model) throws IOException {
        String ans = hllmClient.generate(entity, model);
        log.info("question={} ans={}", entity.getPrompt(), ans);
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
            String aippFileReq = String.format(Locale.ROOT,
                    "/file?fileUrl=%s&fileName=%s",
                    url64,
                    encodeFileName(fileDto.getFileName()));
            fileDto.setFileUrl(aippFileReq);
            String pic64 = Base64.getEncoder().encodeToString(fileDto.getFilePic().getBytes(StandardCharsets.UTF_8));
            fileDto.setFilePic(pic64);
        });
        return fileListDto.getRes();
    }

    private <T> T askXiaoHai(String w3Id, String question, String url, Class<T> respDtoCls) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(Utils.requestConfig(this.xiaoHaiReadTimeout));
        httpPost.setEntity(new StringEntity(getAskXiaoHaiReqBody(w3Id, question), ContentType.APPLICATION_JSON));
        String respContent = sendHttpRequest(httpPost);
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
