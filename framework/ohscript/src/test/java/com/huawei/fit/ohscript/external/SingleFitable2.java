/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.external;

import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.FitableSuite;

import java.util.List;
import java.util.Map;

/**
 * 表示 {@link SingleGenericable} 的实现 2。
 *
 * @author 季聿阶 j00559309
 * @since 2023-11-01
 */
@FitableSuite
public class SingleFitable2 implements SingleGenericable {
    @Override
    @Fitable(id = "f2")
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> param) {
        param.get(0).put("k2", "v2");
        return param;
    }
}
