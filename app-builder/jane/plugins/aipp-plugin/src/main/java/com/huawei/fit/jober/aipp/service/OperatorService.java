/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 提供操作服务
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
public interface OperatorService {
    /**
     * 文件类型枚举
     */
    enum FileType {
        PDF,
        WORD,
        EXCEL,
        IMAGE,
        AUDIO
    }

    /**
     * 文件提取
     *
     * @param file 文件
     * @param optionalFileType 可选文件类型
     * @return 文件字节内容
     */
    String fileExtractor(File file, Optional<FileType> optionalFileType);

    /**
     * 提取大纲
     *
     * @param file 文件
     * @param fileType 文件类型
     * @return 大纲
     */
    String outlineExtractor(File file, FileType fileType);

    /**
     * 创建文档
     *
     * @param instanceId 实例id
     * @param fileName 文件名
     * @param txt 文本
     * @return 文件
     * @throws IOException IO异常
     */
    File createDoc(String instanceId, String fileName, String txt) throws IOException;
}
