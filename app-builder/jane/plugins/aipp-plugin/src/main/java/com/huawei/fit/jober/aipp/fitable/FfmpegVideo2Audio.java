/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.AippFileUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.audio.AudioSplitInfo;
import com.huawei.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.FfmpegService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * FfmpegVideo2Audio
 *
 * @author y00612997
 * @since 2024/1/9
 */
@Component
public class FfmpegVideo2Audio implements FlowableService {
    private static final Logger log = Logger.get(FfmpegVideo2Audio.class);
    private final FfmpegService ffmpegService;
    private final AippLogService aippLogService;

    public FfmpegVideo2Audio(FfmpegService ffmpegService, AippLogService aippLogService) {
        this.ffmpegService = ffmpegService;
        this.aippLogService = aippLogService;
    }

    private AudioSplitInfo covertVideo(String dirName, File video) throws IOException {
        File targetDir = Paths.get(Utils.NAS_SHARE_DIR, dirName).toFile();
        FfmpegMeta meta = ffmpegService.stat(video.getAbsolutePath());
        File audio = Paths.get(targetDir.getPath(), video.getName() + "." + meta.getVideoExt()).toFile();
        ffmpegService.extractAudio(video.getAbsolutePath(), audio.getAbsolutePath());
        if (meta.getDuration() >= 6 * 60) {
            int segmentCount = Math.max(1, Math.min(meta.getDuration() / 300, 8));
            int segmentSize = (meta.getDuration() + segmentCount - 1) / segmentCount;
            ffmpegService.splitAudio(audio.getAbsolutePath(),
                    targetDir.getAbsolutePath() + "/split_%03d." + meta.getVideoExt(),
                    segmentSize);
            FileUtils.delete(audio);
            return new AudioSplitInfo(targetDir.getAbsolutePath(), segmentSize);
        }
        return new AudioSplitInfo(targetDir.getAbsolutePath(), meta.getDuration());
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.FfmpegVideo2Audio")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("FfmpegVideo2Audio businessData {}", businessData);

        File videoFile = null;
        try {
            String videoPathStr = (String) businessData.get(AippConst.BS_VIDEO_PATH);
            Map<String, Object> videoFileObject = JsonUtils.parseObject(videoPathStr);
            String videoUrl = (String) videoFileObject.get("s3_url");
            String instId = (String) businessData.get(AippConst.BS_AIPP_INST_ID_KEY);
            videoFile = Utils.getFileFromS3(instId, videoUrl, "video");

            AudioSplitInfo result = covertVideo(instId, videoFile);
            businessData.put(AippConst.BS_VIDEO_TO_AUDIO_RESULT_DIR, result.getDirPath());
            businessData.put(AippConst.BS_VIDEO_TO_AUDIO_SEG_SIZE, result.getSegmentSize());
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "FfmpegVideo2Audio failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        } finally {
            // 删除临时视频文件
            AippFileUtils.deleteFile(videoFile);
        }
        return flowData;
    }
}