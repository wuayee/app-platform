/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.entity.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FfmpegTask ffmpeg任务
 *
 * @author y00612997
 * @since 2024/1/8
 */
public class FfmpegTask {
    private final List<String> command;

    public FfmpegTask(String executable, Map<String, List<String>> inputs, Map<String, List<String>> outputs) {
        List<String> cmd = new ArrayList<>();
        cmd.add(executable);
        cmd.addAll(mergeArgsOpts(inputs, true));
        cmd.addAll(mergeArgsOpts(outputs, false));
        this.command = cmd;
    }

    public FfmpegTask(Map<String, List<String>> inputs, Map<String, List<String>> outputs) {
        this("ffmpeg", inputs, outputs);
    }

    private static List<String> mergeArgsOpts(Map<String, List<String>> optsMap, boolean isInput) {
        List<String> mergeList = new ArrayList<>();
        if (optsMap == null) {
            return mergeList;
        }
        optsMap.forEach((arg, opts) -> {
            if (opts != null) {
                mergeList.addAll(opts);
            }
            if (isInput) {
                mergeList.add("-i");
            }
            mergeList.add(arg);
        });
        return mergeList;
    }

    /**
     * exec ffmpeg任务执行
     *
     * @return String 执行打印结果
     * @throws IOException ffmpeg执行出错
     * @author y00612997
     * @since 2024/1/10 9:47
     */
    public String exec() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        StringBuilder sb = new StringBuilder();
        Process p = builder.command(command).redirectErrorStream(true).start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(),
                StandardCharsets.UTF_8))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        }
        return sb.toString();
    }
}