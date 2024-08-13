/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegTask;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegUtil;
import com.huawei.fit.jober.aipp.service.FfmpegService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FfmpegServiceImpl
 *
 * @author 易文渊
 * @since 2024/1/7
 */
@Component
public class FfmpegServiceImpl implements FfmpegService {
    private static final Pattern STAT_PATTERN = Pattern.compile("Duration: (.*?),(.*?)Audio: (.*?) ");
    private static final Logger log = Logger.get(FfmpegServiceImpl.class);

    @Override
    public FfmpegMeta stat(String inputFilePath) throws IOException {
        HashMap<String, List<String>> inputs = new HashMap<String, List<String>>() {{
            put(inputFilePath, null);
        }};
        String output = new FfmpegTask(inputs, null).exec();
        Matcher matcher = STAT_PATTERN.matcher(output);
        if (!matcher.find()) {
            throw new IOException("ffmpeg parse meta fail.");
        }
        return new FfmpegMeta(FfmpegUtil.parseDuration(matcher.group(1)), matcher.group(3));
    }

    @Override
    public void extractAudio(String inputFilePath, String outputFilePath) throws IOException {
        HashMap<String, List<String>> inputs = new HashMap<String, List<String>>() {{
            put(inputFilePath, null);
        }};
        HashMap<String, List<String>> outputs = new HashMap<String, List<String>>() {{
            put(outputFilePath, Arrays.asList("-vn", "-acodec", "copy", "-hide_banner", "-loglevel", "quiet"));
        }};
        new FfmpegTask(inputs, outputs).exec();
        log.info("extract {} to {} success.", inputFilePath, outputFilePath);
    }

    @Override
    public void splitAudio(String inputFilePath, String outputPatten, int segmentSize) throws IOException {
        HashMap<String, List<String>> inputs = new HashMap<String, List<String>>() {{
            put(inputFilePath, null);
        }};
        HashMap<String, List<String>> outputs = new HashMap<String, List<String>>() {{
            put(outputPatten,
                    Arrays.asList("-f", "segment", "-segment_time", String.valueOf(segmentSize),
                            "-c", "copy", "-hide_banner", "-loglevel", "quiet"));
        }};
        new FfmpegTask(inputs, outputs).exec();
        log.info("split {} to {} success.", inputFilePath, outputPatten);
    }
}
