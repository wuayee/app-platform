/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * 用于转换时间的工具类
 *
 * @author y00679285
 * @since 2024/1/18
 */
public class TimeUtil {
    /**
     * 获取当前时间
     *
     * @return 时间字符串， 格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getFormatCurTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId localZone = ZoneId.systemDefault();
        ZoneId targetZone = ZoneId.of("Asia/Shanghai");
        Date date = Date.from(localDateTime.atZone(localZone).toInstant());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        outputFormat.setTimeZone(TimeZone.getTimeZone(targetZone));
        return outputFormat.format(date);
    }
}
