/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.contextdata;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 待删除
 *
 * @author 晏钰坤
 * @since 1.0
 */
@Deprecated
public class GlobalFileData {
    private static final Map<String, Object> PASS_DATA_MAP = new ConcurrentHashMap<>();

    /**
     * 待删除
     *
     * @param id 待删除
     * @return 待删除
     */
    public static Object getPassData(String id) {
        return PASS_DATA_MAP.get(id);
    }

    /**
     * 待删除
     *
     * @param id 待删除
     * @param value 待删除
     */
    public static void put(String id, Object value) {
        Optional.ofNullable(value).ifPresent(val -> PASS_DATA_MAP.put(id, val));
    }

    /**
     * 待删除
     *
     * @param ids 待删除
     */
    public static void remove(List<String> ids) {
        ids.forEach(PASS_DATA_MAP::remove);
    }
}
