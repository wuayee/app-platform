/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 音频操作工具
 *
 * @author l00611472
 * @since 2024/1/19
 */
public class AudioUtils {
    public static List<String> extractAudioTextParallel(ExecutorService executor, List<File> audioList,
            AudioTextFunction<File, String> extractor) throws InterruptedException {
        int taskCnt = audioList.size();
        List<String> output = new ArrayList<>(Collections.nCopies(taskCnt, null));
        CountDownLatch countDownLatch = new CountDownLatch(taskCnt);
        for (int i = 0; i < taskCnt; ++i) {
            int id = i;
            executor.execute(() -> {
                try {
                    String text = extractor.apply(audioList.get(id));
                    output.set(id, text);
                } catch (IOException e) {
                    output.set(id, "");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        return output;
    }
}
