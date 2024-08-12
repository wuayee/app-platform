/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import java.util.List;
import java.util.Objects;

/**
 * 用于生成序号
 *
 * @author 姚江
 * @since 2023-12-18
 */
public class SequenceUtils {
    /**
     * 从列表中获取序列
     *
     * @param exist 序列数
     * @return 序列数
     */
    public static int getSequenceFromList(Integer... exist) {
        int index = 0;
        int sequence = 1;
        while (index < exist.length && Objects.equals(exist[index], sequence)) {
            index++;
            sequence++;
        }
        return sequence;
    }

    /**
     * 从列表中获取序列
     *
     * @param exist 序列数集
     * @return 序列数
     */
    public static int getSequenceFromList(List<Integer> exist) {
        Integer[] array = exist.toArray(new Integer[0]);
        return getSequenceFromList(array);
    }
}
