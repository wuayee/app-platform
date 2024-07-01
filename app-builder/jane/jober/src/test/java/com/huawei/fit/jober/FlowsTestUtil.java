/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.waterflow.common.utils.SleepUtil;

import java.util.List;
import java.util.function.Supplier;

/**
 * ClassName
 * 简述
 *
 * @author g00564732
 * @since 2023/10/31
 */
public final class FlowsTestUtil {
    /**
     * waitFortyMillis
     *
     * @param supplier supplier
     * @return list
     */
    public static <T> List<T> waitFortyMillis(Supplier<List<T>> supplier) {
        return waitMillis(supplier, 40);
    }

    /**
     * 等待时间
     *
     * @param supplier supplier
     * @param millis 时间
     * @return 等待时间
     */
    public static <T> List<T> waitMillis(Supplier<List<T>> supplier, long millis) {
        SleepUtil.sleep(millis);
        return supplier.get();
    }

    /**
     * 等待结果为空
     *
     * @param supplier supplier
     * @return list
     */
    public static <T> List<T> waitEmpty(Supplier<List<T>> supplier) {
        return waitSize(supplier, 0);
    }

    /**
     * waitSingle
     *
     * @param supplier supplier
     * @return list
     */
    public static <T> List<T> waitSingle(Supplier<List<T>> supplier) {
        return waitSize(supplier, 1);
    }

    /**
     * 等待大小
     *
     * @param supplier supplier
     * @param size 大小
     * @return list
     */
    public static <T> List<T> waitSize(Supplier<List<T>> supplier, int size) {
        while (true) {
            List<T> ts = supplier.get();
            if (ts.size() == size) {
                return ts;
            }
            SleepUtil.sleep(5);
        }
    }

    /**
     * 等待时间
     *
     * @param stop stop
     * @param most most
     */
    public static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 5;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }

    /**
     * 等待时间
     *
     * @param stop stop时间
     */
    public static void waitUntil(Supplier<Boolean> stop) {
        waitUntil(stop, 500);
    }
}

