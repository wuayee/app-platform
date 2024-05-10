/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

public interface UploadedFileManageService {
    void cleanAippFiles(String aippId, String createUserAccount);
    void addFileRecord(String aipp, String createUserAccount, String filename);
}
