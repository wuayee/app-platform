/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.enums.FileExtensionEnum;
import com.huawei.fit.jober.aipp.service.OperatorService;
import com.huawei.fit.jober.aipp.tool.FileTool;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.io.File;
import java.nio.file.Paths;

/**
 * 文件工具类实现。
 *
 * @author 孙怡菲 s00664640
 * @since 2024-05-30
 */
@Component
public class FileToolImpl implements FileTool {
    private final OperatorService operatorService;

    public FileToolImpl(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Override
    @Fitable("extract.multi.type")
    public String extractFile(String filePath) {
        Integer defaultToken = 20000;
        File file = Paths.get(filePath).toFile();
        String fileContent = this.operatorService.fileExtractor(file, FileExtensionEnum.findType(file.getName()));
        return Utils.textLenLimit(fileContent, defaultToken);
    }
}
