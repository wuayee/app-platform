/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.merge.map;

import com.huawei.fitframework.merge.Merger;

import java.util.List;
import java.util.Map;

/**
 * {@link Map} 对象的合并器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-08-02
 */
public interface MapMerger<K, V> extends Merger<Map<K, V>> {
    /**
     * 获取冲突点的位置信息。
     *
     * @return 表示冲突点位置信息的 {@link List}{@code <}{@link Object}{@code >}。
     */
    List<Object> location();
}
