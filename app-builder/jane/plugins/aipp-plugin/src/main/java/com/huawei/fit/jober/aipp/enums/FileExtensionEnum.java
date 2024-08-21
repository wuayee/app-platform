/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.util.StringUtils;

import lombok.Getter;

import java.util.Optional;

/**
 * 文件后缀
 *
 * @author: 00644640
 * @since: 2024-05-10
 */
@Getter
public enum FileExtensionEnum {
    WORD_DOCX("docx", OperatorService.FileType.WORD),
    WORD_DOC("doc", OperatorService.FileType.WORD),
    PDF("pdf", OperatorService.FileType.PDF),
    EXCEL("xls", OperatorService.FileType.EXCEL),
    EXCEL_X("xlsx", OperatorService.FileType.EXCEL),
    PNG("png", OperatorService.FileType.IMAGE),
    JPG("jpg", OperatorService.FileType.IMAGE),
    JPGE("jpge", OperatorService.FileType.IMAGE),
    AUDIO("mp3", OperatorService.FileType.AUDIO);

    private final String extension;
    private final OperatorService.FileType fileType;

    FileExtensionEnum(String fileExtension, OperatorService.FileType fileType) {
        extension = fileExtension;
        this.fileType = fileType;
    }

    /**
     * 根据文件名找到对应的文件类型枚举值
     *
     * @param fileName 文件名
     * @return 文件类型枚举值 {@link OperatorService.FileType}
     */
    public static Optional<OperatorService.FileType> findType(String fileName) {
        String fileExtension = getFileExtension(fileName);
        for (FileExtensionEnum fileExtensionEnum : FileExtensionEnum.values()) {
            if (StringUtils.equalsIgnoreCase(fileExtensionEnum.extension, fileExtension)) {
                return Optional.of(fileExtensionEnum.fileType);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据文件名获取文件后缀
     *
     * @param fileName 文件名
     * @return 文件后缀
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
