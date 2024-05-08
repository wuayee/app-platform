/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.http;

import com.huawei.fitframework.serialization.tlv.TlvConstants;

/**
 * 表示 Http 通道中 {@link com.huawei.fitframework.serialization.TagLengthValues} 的标签常量值。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class HttpTlvConstants extends TlvConstants {
    /** 表示异步任务唯一标识的标签值。 */
    public static final int ASYNC_TASK_ID_TAG = 0x40;
}
