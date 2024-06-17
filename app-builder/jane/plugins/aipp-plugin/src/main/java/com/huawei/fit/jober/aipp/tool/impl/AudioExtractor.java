/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.LLMUtils;
import com.huawei.fit.jober.aipp.common.UUIDUtil;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.dto.audio.AudioSplitInfo;
import com.huawei.fit.jober.aipp.dto.audio.SummaryDto;
import com.huawei.fit.jober.aipp.dto.audio.SummarySection;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import com.huawei.fit.jober.aipp.service.FfmpegService;
import com.huawei.fit.jober.aipp.tool.FileExtractor;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.hllm.HllmClient;
import com.huawei.hllm.entity.HllmChatEntity;
import com.huawei.hllm.entity.HllmTranscriptionEntity;
import com.huawei.hllm.model.LlmModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LlmAudio2Summary
 *
 * @author y00612997
 * @since 2024/1/8
 */
@Component
public class AudioExtractor implements FileExtractor {
    private static final Logger log = Logger.get(AudioExtractor.class);
    private static final String PROMPT = "\nPerform the following actions:\n"
            + "1. - Use chinese summarize the following video delimited by <> limit in 100 words.\n"
            + "2. - Write a title for the summary.\n"
            + "3. - Output a json object that contains the following keys: title, text.\n\n" + "EXAMPLE\n"
            + "Video: <文本摘要旨在将文本或文本集合转换为包含关键信息的简短摘要...>\n" + "Output JSON:\n"
            + "{\"title\": \"文本摘要简介\", \"text\": \"文本摘要...\"}\n\n" + "--------\n" + "Video: <%s>\n"
            + "Output JSON:\n";
    private static final String TMP_DIR_PREFIX = "audioTmp-";
    private final static ExecutorService SUMMARY_EXECUTOR = Executors.newFixedThreadPool(8);
    private final HllmClient hllmClient;
    private final FfmpegService ffmpegService;

    public AudioExtractor(HllmClient hllmClient, FfmpegService ffmpegService) {
        this.hllmClient = hllmClient;
        this.ffmpegService = ffmpegService;
    }

    private SummaryDto batchSummary(List<File> audioList, int segmentSize) throws InterruptedException, IOException {
        int taskCnt = audioList.size();
        List<String> output = new ArrayList<>(Collections.nCopies(taskCnt, null));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CountDownLatch countDownLatch = new CountDownLatch(taskCnt);
        for (int i = 0; i < taskCnt; ++i) {
            int id = i;
            SUMMARY_EXECUTOR.execute(() -> {
                try {
                    String text =
                            hllmClient.transcribe(HllmTranscriptionEntity.builder().file(audioList.get(id)).build())
                                    .trim();
                    String summary = hllmClient.generate(HllmChatEntity.builder()
                            .prompt(String.format(PROMPT, text))
                            .tokens(16000)
                            .build(), LlmModel.QWEN_72B);
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
        stopWatch.stop();
        log.info("Summarize {} task use time {} seconds, segment size: {} seconds.",
                taskCnt,
                stopWatch.getTime(TimeUnit.SECONDS),
                segmentSize);
        return summaryDto;
    }

    private SummaryDto generateSummary(List<String> output, int segmentSize) {
        SummaryDto summaryDto = new SummaryDto(output, segmentSize);
        StringBuilder sb = new StringBuilder();
        summaryDto.getSectionList().forEach(sec -> sb.append(sec.getText()));
        try {
            String llmOutput = hllmClient.generate(HllmChatEntity.builder()
                    .prompt(String.format(PROMPT, sb))
                    .tokens(16000)
                    .build(), LlmModel.QWEN_72B);
            SummarySection section =
                    JsonUtils.parseObject(LLMUtils.tryFixLlmJsonString(llmOutput), SummarySection.class);
            summaryDto.setSummary(section.getText());
        } catch (Exception e) {
            log.error("Llm generate unexpect rsp, msg: {}.", e);
            summaryDto.setSummary("");
        }
        return summaryDto;
    }

    private AudioSplitInfo covertVideo(String dirName, File audio) throws IOException {
        File targetDir = Paths.get(Utils.NAS_SHARE_DIR, dirName).toFile();
        FfmpegMeta meta = ffmpegService.stat(audio.getAbsolutePath());
        FileUtils.copyFile(audio, Paths.get(targetDir.getPath(), audio.getName()).toFile());
        File copyAudio = Paths.get(targetDir.getPath(), audio.getName()).toFile();
        if (meta.getDuration() >= 6 * 60) {
            int segmentCount = Math.max(1, Math.min(meta.getDuration() / 300, 8));
            int segmentSize = (meta.getDuration() + segmentCount - 1) / segmentCount;
            ffmpegService.splitAudio(audio.getAbsolutePath(),
                    targetDir.getAbsolutePath() + "/split_%03d." + meta.getVideoExt(),
                    segmentSize);
            FileUtils.delete(copyAudio);
            return new AudioSplitInfo(targetDir.getAbsolutePath(), segmentSize);
        }
        return new AudioSplitInfo(targetDir.getAbsolutePath(), meta.getDuration());
    }

    @Fitable("llmAudio2Summary")
    @Override
    public String extractFile(File file) {
        // file -> audioDir 切分为多个音频文件，存在临时目录下
        String tmpDir = TMP_DIR_PREFIX + UUIDUtil.uuid();
        AudioSplitInfo audioSplitInfo;
        try {
            audioSplitInfo = this.covertVideo(tmpDir, file);
        } catch (IOException e) {
            log.error("切分音频文件时出现了问题。");
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "error occurs during audio segmentation.");
        }

        try (Stream<Path> audioPathStream = Files.list(Paths.get(audioSplitInfo.getDirPath()))) {
            List<File> audioFiles = audioPathStream.map(Path::toFile).collect(Collectors.toList());
            SummaryDto summaryDto = batchSummary(audioFiles, audioSplitInfo.getSegmentSize());
            if (summaryDto.getSectionList().isEmpty()) {
                log.error("很抱歉！无法识别文件中的内容，您可以尝试换个文件");
                throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "audio summary result is empty.");
            }
            return summaryDto.getSummary();
        } catch (InterruptedException | IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "extract audio content failed");
        } finally {
            // 删除临时目录
            try {
                FileUtils.deleteDirectory(Paths.get(Utils.NAS_SHARE_DIR, tmpDir).toFile());
            } catch (IOException e) {
                log.error("delete audio file tmp directory failed.");
            }
        }
    }
}