/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.audio.SummaryDto;
import com.huawei.fit.jober.aipp.dto.audio.SummarySection;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.LLMUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.voice.service.VoiceService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LlmAudio2Summary
 *
 * @author y00612997
 * @since 2024/1/8
 */
@Component
public class LlmAudio2Summary implements FlowableService {
    private static final Logger log = Logger.get(LlmAudio2Summary.class);
    private static final String PROMPT = "\nPerform the following actions:\n"
            + "1. - Use chinese summarize the following video delimited by <> limit in 100 words.\n"
            + "2. - Write a title for the summary.\n"
            + "3. - Output a json object that contains the following keys: title, text.\n\n" + "EXAMPLE\n"
            + "Video: <文本摘要旨在将文本或文本集合转换为包含关键信息的简短摘要...>\n" + "Output JSON:\n"
            + "{\"title\": \"文本摘要简介\", \"text\": \"文本摘要...\"}\n\n" + "--------\n" + "Video: <%s>\n"
            + "Output JSON:\n";
    private static final ExecutorService SUMMARY_EXECUTOR = Executors.newFixedThreadPool(8);

    private final OpenAiClient openAiClient;

    private final AippLogService aippLogService;
    private final VoiceService voiceService;
    private final String endpoint;
    private final String pathPrefix;

    public LlmAudio2Summary(AippLogService aippLogService, @Value("${app-engine.endpoint}") String endpoint,
                            @Fit OpenAiClient openAiClient, @Fit VoiceService voiceService,
                            @Value("${app-engine.pathPrefix}") String pathPrefix) {
        this.aippLogService = aippLogService;
        this.openAiClient = openAiClient;
        this.voiceService = voiceService;
        this.endpoint = endpoint;
        this.pathPrefix = pathPrefix;
    }

    private SummaryDto batchSummary(List<File> audioList, int segmentSize) throws InterruptedException, IOException {
        int taskCnt = audioList.size();
        List<String> output = new ArrayList<>(Collections.nCopies(taskCnt, null));
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(taskCnt);
        for (int i = 0; i < taskCnt; ++i) {
            int id = i;
            SUMMARY_EXECUTOR.execute(() -> {
                try {
                    File audio = audioList.get(id);
                    String audioFilePath = AippFileUtils.getFileDownloadFilePath(
                            endpoint, this.pathPrefix, audio.getPath());
                    String text = this.voiceService.getText(audioFilePath, audio.getName());
                    String summary = LLMUtils.askModelForSummary(openAiClient, String.format(PROMPT, text),
                            LlmModelNameEnum.QWEN_72B, 16000);
                    output.set(id, summary);
                } catch (IOException e) {
                    output.set(id, "");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        SummaryDto summaryDto = generateSummary(output, segmentSize);
        long endTime = System.currentTimeMillis();
        log.info("Summarize {} task use time {} seconds, segment size: {} seconds.",
                taskCnt,
                (endTime - startTime) / 1000,
                segmentSize);
        return summaryDto;
    }

    private SummaryDto generateSummary(List<String> output, int segmentSize) {
        SummaryDto summaryDto = new SummaryDto(output, segmentSize);
        StringBuilder sb = new StringBuilder();
        summaryDto.getSectionList().forEach(sec -> sb.append(sec.getText()));
        try {
            String llmOutput =
                    LLMUtils.askModelForSummary(openAiClient, String.format(PROMPT, sb),
                            LlmModelNameEnum.QWEN_72B, 16000);
            SummarySection section =
                    JsonUtils.parseObject(LLMUtils.tryFixLlmJsonString(llmOutput), SummarySection.class);
            summaryDto.setSummary(section.getText());
        } catch (IOException | AippJsonDecodeException e) {
            log.error("Llm generate unexpect rsp, msg: {}.", e);
            summaryDto.setSummary("");
        }
        return summaryDto;
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.LlmAudio2Summary")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LlmAudio2Summary businessData {}", businessData);
        String msg = "好的，我可以帮你分析这个视频并生成对应的摘要，我决定先调用视频解析工具，再生成视频中的摘要";
        this.aippLogService.insertMsgLog(msg, flowData);
        int segSize = (int) businessData.get(AippConst.BS_VIDEO_TO_AUDIO_SEG_SIZE);
        String audioDir = ObjectUtils.cast(businessData.get(AippConst.BS_VIDEO_TO_AUDIO_RESULT_DIR));
        try (Stream<Path> audioPathStream = Files.list(Paths.get(audioDir))) {
            List<File> audioFiles = audioPathStream.map(Path::toFile).collect(Collectors.toList());
            SummaryDto summaryDto = batchSummary(audioFiles, segSize);
            if (summaryDto.getSectionList().isEmpty()) {
                msg = "很抱歉！无法识别文件中的内容，您可以尝试换个文件";
                this.aippLogService.insertErrorLog(msg, flowData);
                throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "audio summary result is empty.");
            }
            businessData.put(AippConst.BS_VIDEO_TO_TEXT_RESULT, JsonUtils.toJsonString(summaryDto));
        } catch (InterruptedException | IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "LlmAudio2Summary failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        }
        return flowData;
    }
}