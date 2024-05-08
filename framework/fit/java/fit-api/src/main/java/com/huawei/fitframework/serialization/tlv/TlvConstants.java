/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.tlv;

/**
 * 表示 {@link com.huawei.fitframework.serialization.TagLengthValues} 的标签常量值。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class TlvConstants {
    /** 表示进程唯一标识的标签值。 */
    public static final int WORKER_ID_TAG = 0x00;

    /** 表示进程实例唯一标识的标签值。 */
    public static final int WORKER_INSTANCE_ID_TAG = 0x01;

    /** 表示异常属性的标签值。 */
    public static final int EXCEPTION_PROPERTIES_TAG = 0x10;
}
