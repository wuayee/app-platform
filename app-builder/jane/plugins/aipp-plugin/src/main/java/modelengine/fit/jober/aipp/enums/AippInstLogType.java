/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.enums;

/**
 * aipp 实例历史数据类型
 *
 * @author 刘信宏
 * @since 2024/01/08
 */
public enum AippInstLogType {
    /**
     * 表单历史数据。
     */
    FORM,

    /**
     * 提示消息历史数据
     */
    MSG,

    /**
     * 带有元数据的结构化消息，如溯源场景的应用结束响应。
     */
    META_MSG,

    /**
     * 错误消息历史数据
     */
    ERROR,

    /**
     * 问题
     */
    QUESTION,

    /**
     * 不显示的问题
     */
    HIDDEN_QUESTION,

    /**
     * 不显示的消息
     */
    HIDDEN_MSG,

    /**
     * 文件消息
     */
    FILE,

    /**
     * 不显示的表单消息。
     */
    HIDDEN_FORM,

    /**
     * 携带文件的问题。
     */
    QUESTION_WITH_FILE,
}
