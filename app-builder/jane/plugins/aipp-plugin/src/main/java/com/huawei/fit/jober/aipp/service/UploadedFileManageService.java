/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import java.util.List;

public interface UploadedFileManageService {
    void cleanAippFiles(List<String> aippId, String createUserAccount);
    void addFileRecord(String aipp, String createUserAccount, String filename);
}
