/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.dto.aipplog.AippUploadedFileInfoDto;

import java.util.List;

/**
 * aipp实例文件记录db接口
 *
 * @author x00649642
 * @since 2024-02-04
 */
public interface AippUploadedFileMapper {
    List<String> queryExpiredFiles();

    List<String> queryFilesByUserAipp(String aippId, String createUserAccount);

    void deleteFileRecords(String aippId, String createUserAccount, List<String> fileNames);

    void insertFileRecord(AippUploadedFileInfoDto fileInfo);
}
