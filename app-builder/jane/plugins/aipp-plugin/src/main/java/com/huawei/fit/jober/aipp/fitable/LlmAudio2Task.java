/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.AudioTextFunction;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.audio.AudioSplitInfo;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import com.huawei.fit.jober.aipp.enums.LlmModelNameEnum;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.FfmpegService;
import com.huawei.fit.jober.aipp.util.AippFileUtils;
import com.huawei.fit.jober.aipp.util.AudioUtils;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.LLMUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.thread.DefaultThreadFactory;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.Role;
import com.huawei.jade.fel.model.openai.entity.chat.message.content.UserContent;
import com.huawei.jade.voice.service.VoiceService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LlmAudio2Task
 *
 * @author l00611472
 * @since 2024/1/19
 */
@Component
public class LlmAudio2Task implements FlowableService {
    private static final Logger log = Logger.get(LlmAudio2Task.class);
    private static final String PROMPT =
            "\n<%s>\n" + "\n" + "give you a meeting minutes delimited by <>, perform the following actions: \n"
                    + "1. - Identify people in the meeting.\n" + "2. - Assign tasks to everyone.\n"
                    + "Finally Output a json list, every entity contains the following keys: owner, title, "
                    + "task_detail.\n" + "\n" + "Output json:\n";
    private static final ExecutorService AUDIO_EXECUTOR = new ThreadPoolExecutor(0, 10, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new DefaultThreadFactory("flows-event-handler-thread-pool", false, (thread, throwable) -> {
                log.error("[flows-event-handler-thread-pool]: The pool run failed, error cause: {}, message: {}.",
                        throwable.getCause(), throwable.getMessage());
                log.error("The flows event handler pool run failed details: ", throwable);
            }), new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());

    private final FfmpegService ffmpegService;
    private final AippLogService aippLogService;
    private final MetaInstanceService metaInstanceService;
    private final OpenAiClient openAiClient;
    private final VoiceService voiceService;
    private final String endpoint;
    private final String pathPrefix;

    public LlmAudio2Task(@Fit FfmpegService ffmpegService, @Fit AippLogService aippLogService,
                         @Fit MetaInstanceService metaInstanceService, @Fit OpenAiClient openAiClient,
                         @Fit VoiceService voiceService, @Value("${app-engine.endpoint}") String endpoint,
                         @Value("${app-engine.pathPrefix}") String pathPrefix) {
        this.ffmpegService = ffmpegService;
        this.aippLogService = aippLogService;
        this.metaInstanceService = metaInstanceService;
        this.openAiClient = openAiClient;
        this.voiceService = voiceService;
        this.endpoint = endpoint;
        this.pathPrefix = pathPrefix;
    }

    private AudioSplitInfo splitAudio(String instId, String audioUrl) throws JobberException {
        File audioFile = null;
        try {
            File targetDir = Paths.get(AippFileUtils.NAS_SHARE_DIR, UUID.randomUUID().toString()).toFile();
            FileUtils.forceMkdir(targetDir);
            audioFile = AippFileUtils.getFileFromS3(instId, audioUrl, "audio");
            FfmpegMeta meta = ffmpegService.stat(audioFile.getCanonicalPath());
            int segmentSize = meta.getDuration();
            if (meta.getDuration() >= 6 * 60) {
                int segmentCount = Math.max(1, Math.min(meta.getDuration() / 300, 8));
                segmentSize = (meta.getDuration() + segmentCount - 1) / segmentCount;
                ffmpegService.splitAudio(audioFile.getCanonicalPath(),
                        targetDir.getCanonicalPath() + "/split_%03d",
                        segmentSize);
            } else {
                FileUtils.copyFileToDirectory(audioFile, targetDir);
            }
            return new AudioSplitInfo(targetDir.getCanonicalPath(), segmentSize);
        } catch (IOException e) {
            log.error("splitAudio {} failed.", audioUrl);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "splitAudio failed.");
        } finally {
            // 删除临时音频文件
            AippFileUtils.deleteFile(audioFile);
        }
    }

    private String generateTask(List<File> audioList, List<Map<String, Object>> flowData)
            throws InterruptedException, IOException {
        AudioTextFunction<File, String> extractor =
                file -> {
                    String filePath = AippFileUtils.getFileDownloadFilePath(
                            this.endpoint, this.pathPrefix, file.getPath());
                    return voiceService.getText(filePath, file.getName());
                };
        List<String> output = AudioUtils.extractAudioTextParallel(AUDIO_EXECUTOR, audioList, extractor);
        StringBuilder stringBuilder = new StringBuilder();
        output.forEach(stringBuilder::append);

        String msg = "以下是音频中提取到的关键内容：\n" + stringBuilder.toString();
        this.aippLogService.insertMsgLog(msg, flowData);

        OpenAiChatMessage generateTaskMsg = OpenAiChatMessage.builder()
                .role(Role.USER)
                .content(Collections.singletonList(UserContent.text(String.format(Locale.ROOT, PROMPT, stringBuilder))))
                .build();
        OpenAiChatCompletionRequest request = OpenAiChatCompletionRequest.builder()
                .model(LlmModelNameEnum.QWEN_72B.getValue())
                .messages(Collections.singletonList(generateTaskMsg))
                .maxTokens(16000)
                .build();
        OpenAiChatCompletionResponse llmOutput = openAiClient.createChatCompletion(request);
        return LLMUtils.tryFixLlmJsonString(ObjectUtils.cast(llmOutput.getChoices().get(0).getMessage().getContent()));
    }

    /**
     * 处理流程中的任务调用
     *
     * @param flowData 流程执行上下文数据
     * @return 任务执行返回结果
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.LlmAudio2Task")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LlmAudio2Task businessData {}", businessData);

        String audioPathStr = ObjectUtils.cast(businessData.get(AippConst.BS_AUDIO_PATH));
        Map<String, Object> audioFileObject = JsonUtils.parseObject(audioPathStr);

        String audioUrl = ObjectUtils.cast(audioFileObject.get("s3_url"));
        Validation.notNull(audioUrl, "audioUrl cant be null.");

        String msg = "首先我需要了解音频中的关键信息，我决定调用音频信息提取工具";
        this.aippLogService.insertMsgLog(msg, flowData);

        String instId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        AudioSplitInfo audioInfo = splitAudio(instId, audioUrl);
        try (Stream<Path> audioPathStream = Files.list(Paths.get(audioInfo.getDirPath()))) {
            List<File> audioFiles = audioPathStream.map(Path::toFile).collect(Collectors.toList());
            Validation.notEmpty(audioFiles, "audio file list is empty.");

            String w3Task = generateTask(audioFiles, flowData);
            businessData.put(AippConst.BS_W3_TASK_RESULT, w3Task);

            InstanceDeclarationInfo info =
                    InstanceDeclarationInfo.custom().putInfo(AippConst.BS_W3_TASK_RESULT, w3Task).build();
            MetaInstanceUtils.persistInstance(
                    metaInstanceService, info, businessData, DataUtils.getOpContext(businessData));
        } catch (InterruptedException | IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "LlmAudio2Task failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        }
        return flowData;
    }
}
