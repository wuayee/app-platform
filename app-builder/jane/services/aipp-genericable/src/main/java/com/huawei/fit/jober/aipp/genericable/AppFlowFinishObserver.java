/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.genericable;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 表示 App 流程停止完成的观察者。
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-24
 */
public interface AppFlowFinishObserver {
    /**
     * 获取流程结束的数据。
     *
     * @param data 表示流程结束的数据的 {@link String}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.finished")
    void onFinished(String data);
}
