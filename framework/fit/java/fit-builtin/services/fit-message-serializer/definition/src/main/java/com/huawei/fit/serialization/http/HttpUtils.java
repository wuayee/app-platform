/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.serialization.http;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.serialization.TagLengthValues.TLV_WORKER_ID;
import static com.huawei.fitframework.serialization.TagLengthValues.TLV_WORKER_INSTANCE_ID;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.serialization.TagLengthValues;

import java.util.Base64;

/**
 * 表示 Http 通信过程中通过 TLV 来传输扩展信息的工具类。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-17
 */
public class HttpUtils {
    private static final int TLV_ASYNC_TASK_ID = 0x40;

    /**
     * 从 TLV 中获取进程的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示进程唯一标识的 {@link String}。
     */
    public static String getWorkerId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(TLV_WORKER_ID), UTF_8);
    }

    /**
     * 向 TLV 中设置进程的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param workerId 表示进程唯一标识的 {@link String}。
     */
    public static void setWorkerId(TagLengthValues tagValues, String workerId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(workerId, "The worker id cannot be blank.");
        tagValues.putTag(TLV_WORKER_ID, workerId.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取进程实例的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示进程实例唯一标识的 {@link String}。
     */
    public static String getWorkerInstanceId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(TLV_WORKER_INSTANCE_ID), UTF_8);
    }

    /**
     * 向 TLV 中设置进程实例的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param workerInstanceId 表示进程实例唯一标识的 {@link String}。
     */
    public static void setWorkerInstanceId(TagLengthValues tagValues, String workerInstanceId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(workerInstanceId, "The worker instance id cannot be blank.");
        tagValues.putTag(TLV_WORKER_INSTANCE_ID, workerInstanceId.getBytes(UTF_8));
    }

    /**
     * 从 TLV 中获取异步任务的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @return 表示异步任务唯一标识的 {@link String}。
     */
    public static String getAsyncTaskId(TagLengthValues tagValues) {
        notNull(tagValues, "The TLV cannot be null.");
        return new String(tagValues.getValue(TLV_ASYNC_TASK_ID), UTF_8);
    }

    /**
     * 向 TLV 中设置异步任务的唯一标识。
     *
     * @param tagValues 表示 TLV 字段的 {@link TagLengthValues}。
     * @param asyncTaskId 表示异步任务唯一标识的 {@link String}。
     */
    public static void setAsyncTaskId(TagLengthValues tagValues, String asyncTaskId) {
        notNull(tagValues, "The TLV cannot be null.");
        notBlank(asyncTaskId, "The async task id cannot be blank.");
        tagValues.putTag(TLV_ASYNC_TASK_ID, asyncTaskId.getBytes(UTF_8));
    }

    /**
     * 将数据进行编码。
     *
     * @param data 表示待编码的数据的 {@code byte[]}。
     * @return 表示编码后的数据内容的 {@code String}。
     */
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 将数据进行解码。
     *
     * @param data 表示待解码的数据的 {@link String}。
     * @return 表示解码后的数据内容的 {@code byte[]}。
     */
    public static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
