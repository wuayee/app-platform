/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.entity;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.Duration;

/**
 * 表示文本事件消息实体。
 *
 * @author 易文渊
 * @since 2024-07-16
 */
public final class TextEvent {
    private final String id;
    private final String event;
    private final Duration retry;
    private final String comment;
    private final Object data;

    private TextEvent(String id, String event, Duration retry, String comment, Object data) {
        this.id = id;
        this.event = event;
        this.retry = retry;
        this.comment = comment;
        this.data = data;
    }

    /**
     * 获取 SSE 消息序列化后的文本。
     *
     * @param objectSerializer 表示对象序列化器的 {@link ObjectSerializer}。
     * @return 表示序列化后的 {@link String}。
     */
    public String serialize(ObjectSerializer objectSerializer) {
        Validation.notNull(objectSerializer, "The serializer cannot be null.");
        StringBuilder sb = new StringBuilder();
        serializeField("id", this.id, sb);
        serializeField("event", this.event, sb);
        serializeField("retry", this.retry == null ? null : this.retry.toMillis(), sb);
        serializeComment(this.comment, sb);
        serializeData(objectSerializer, this.data, sb);
        return sb.toString();
    }

    private static void serializeField(String fieldName, Object fieldValue, StringBuilder sb) {
        if (fieldValue == null) {
            return;
        }
        sb.append(fieldName).append(':').append(fieldValue).append('\n');
    }

    private static void serializeComment(String comment, StringBuilder sb) {
        if (comment == null) {
            return;
        }
        sb.append(':').append(StringUtils.replace(comment, "\n", "\n:")).append('\n');
    }

    private static void serializeData(ObjectSerializer objectSerializer, Object data, StringBuilder sb) {
        if (data == null) {
            sb.append("\n");
            return;
        }
        sb.append("data:");
        if (data instanceof String) {
            sb.append(StringUtils.replace(ObjectUtils.cast(data), "\n", "\ndata:"));
        } else {
            sb.append(objectSerializer.serialize(data));
        }
        sb.append("\n\n");
    }

    /**
     * 创建 {@link TextEvent} 的创建器。
     *
     * @return 表示 {@link TextEvent} 创建器的 {@link Builder}。
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建 {@link TextEvent} 的构建器。
     *
     * @param data 表示事件内容的 {@link Object}。
     * @return 表示 {@link TextEvent} 构建器的 {@link Builder}。
     */
    public static Builder builder(Object data) {
        return new Builder(data);
    }

    /**
     * {@link TextEvent} 的构建器。
     */
    public static class Builder {
        private String id;
        private String event;
        private Duration retry;
        private String comment;
        private Object data;

        /**
         * 默认创建 {@link Builder}。
         */
        public Builder() {
        }

        /**
         * 根据事件内容创建 {@link Builder}。
         *
         * @param data 表示事件内容的 {@link Object}。
         */
        public Builder(Object data) {
            this.data = data;
        }

        /**
         * 向当前构建器中设置事件编号。
         *
         * @param id 表示事件编号的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 向当前构建器中设置事件名称。
         *
         * @param event 表示事件名称的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder event(String event) {
            this.event = event;
            return this;
        }

        /**
         * 向当前构建器中设置客户端重试时间。
         *
         * @param retry 表示客户端重试时间的 {@link Duration}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder retry(Duration retry) {
            this.retry = retry;
            return this;
        }

        /**
         * 向当前构建器中设置事件注释。
         *
         * @param comment 表示事件注释的 {@link String}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * 向当前构建器中设置事件内容。
         *
         * @param data 表示事件内容的 {@link Object}.
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来对象的 {@link TextEvent}。
         */
        public TextEvent build() {
            return new TextEvent(this.id, this.event, this.retry, this.comment, this.data);
        }
    }
}