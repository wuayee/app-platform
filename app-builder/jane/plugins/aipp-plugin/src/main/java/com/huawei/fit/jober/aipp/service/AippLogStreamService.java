/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.vo.AippLogVO;

/**
 * log流式服务接口.
 *
 * @author z00559346 张越
 * @since 2024-05-23
 */
public interface AippLogStreamService {
    /**
     * 推送日志信息到前端.
     *
     * @param log 日志对象.
     */
    void send(AippLogVO log);
}
