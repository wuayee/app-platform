/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.AippFileUtils;
import com.huawei.fit.jober.aipp.common.AudioTextFunction;
import com.huawei.fit.jober.aipp.common.AudioUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.LLMUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.audio.AudioSplitInfo;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.FfmpegService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.hllm.HllmClient;
import com.huawei.hllm.entity.HllmChatEntity;
import com.huawei.hllm.entity.HllmTranscriptionEntity;
import com.huawei.hllm.model.LlmModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final ExecutorService AUDIO_EXECUTOR = Executors.newFixedThreadPool(8);
    private final HllmClient hllmClient;
    private final FfmpegService ffmpegService;
    private final AippLogService aippLogService;
    private final MetaInstanceService metaInstanceService;

    public LlmAudio2Task(@Fit HllmClient hllmClient, @Fit FfmpegService ffmpegService,
            @Fit AippLogService aippLogService, @Fit MetaInstanceService metaInstanceService) {
        this.hllmClient = hllmClient;
        this.ffmpegService = ffmpegService;
        this.aippLogService = aippLogService;
        this.metaInstanceService = metaInstanceService;
    }

    private AudioSplitInfo splitAudio(String instId, String audioUrl) throws JobberException {
        File audioFile = null;
        try {
            File targetDir = Paths.get(Utils.NAS_SHARE_DIR, UUID.randomUUID().toString()).toFile();
            FileUtils.forceMkdir(targetDir);
            audioFile = Utils.getFileFromS3(instId, audioUrl, "audio");
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
                file -> hllmClient.transcribe(HllmTranscriptionEntity.builder().file(file).build()).trim();
        List<String> output = AudioUtils.extractAudioTextParallel(AUDIO_EXECUTOR, audioList, extractor);
        StringBuilder stringBuilder = new StringBuilder();
        output.forEach(stringBuilder::append);

        String msg = "以下是音频中提取到的关键内容：\n" + stringBuilder.toString();
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        String llmOutput = hllmClient.generate(HllmChatEntity.builder()
                .prompt(String.format(Locale.ROOT, PROMPT, stringBuilder))
                .tokens(16000)
                .build(), LlmModel.QWEN_72B);
        return LLMUtils.tryFixLlmJsonString(llmOutput);
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
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("LlmAudio2Task businessData {}", businessData);

        String audioPathStr = (String) businessData.get(AippConst.BS_AUDIO_PATH);
        Map<String, Object> audioFileObject = JsonUtils.parseObject(audioPathStr);

        String audioUrl = (String) audioFileObject.get("s3_url");
        Validation.notNull(audioUrl, "audioUrl cant be null.");

        String msg = "首先我需要了解音频中的关键信息，我决定调用音频信息提取工具";
        Utils.persistAippMsgLog(aippLogService, msg, flowData);

        String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
        AudioSplitInfo audioInfo = splitAudio(instId, audioUrl);
        try (Stream<Path> audioPathStream = Files.list(Paths.get(audioInfo.getDirPath()))) {
            List<File> audioFiles = audioPathStream.map(Path::toFile).collect(Collectors.toList());
            Validation.notEmpty(audioFiles, "audio file list is empty.");

            String w3Task = generateTask(audioFiles, flowData);
            businessData.put(AippConst.BS_W3_TASK_RESULT, w3Task);

            InstanceDeclarationInfo info =
                    InstanceDeclarationInfo.custom().putInfo(AippConst.BS_W3_TASK_RESULT, w3Task).build();
            Utils.persistInstance(metaInstanceService, info, businessData, Utils.getOpContext(businessData));
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
