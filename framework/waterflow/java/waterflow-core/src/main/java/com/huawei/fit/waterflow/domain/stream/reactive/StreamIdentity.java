/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.reactive;

import com.huawei.fit.waterflow.domain.utils.Identity;

/**
 * StreamIdentity
 *
 * @since 1.0
 */
public interface StreamIdentity extends Identity {
    /**
     * getStreamId
     *
     * @return String
     */
    String getStreamId();
}
