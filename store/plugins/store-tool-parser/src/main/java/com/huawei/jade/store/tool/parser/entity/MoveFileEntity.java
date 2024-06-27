/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.entity;

/**
 * 为拷贝文件提供实体类。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-26
 */
public class MoveFileEntity {
    private String sourceFilePath;

    /**
     * 获取源文件的绝对路径值。
     *
     * @return 表示源文件的绝对路径值的 {@link String}。
     */
    public String getSourceFilePath() {
        return this.sourceFilePath;
    }
}
