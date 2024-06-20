/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface OperatorService {

    enum FileType {
        PDF,
        WORD,
        EXCEL,
        IMAGE,
        AUDIO
    }

    String fileExtractor(File file, Optional<FileType> optionalFileType);

    String outlineExtractor(File file, FileType fileType);

    File createDoc(String instanceId, String fileName, String txt) throws IOException;
}
