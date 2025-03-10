/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util;

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
