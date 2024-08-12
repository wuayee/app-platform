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
    /**
     * 查询过期文件
     *
     * @return 查询结果list
     */
    List<String> queryExpiredFiles();

    /**
     * 通过用户Aipp查询文件
     *
     * @param aippId aipp唯一标识
     * @param createUserAccount 创建用户帐户
     * @return 查询结果list
     */
    List<String> queryFilesByUserAipp(String aippId, String createUserAccount);

    /**
     * 删除文件记录
     *
     * @param aippId aipp唯一标识
     * @param createUserAccount 创建用户帐户
     * @param fileNames 文件名称
     */
    void deleteFileRecords(String aippId, String createUserAccount, List<String> fileNames);

    /**
     * 插入文件记录
     *
     * @param fileInfo 文件信息
     */
    void insertFileRecord(AippUploadedFileInfoDto fileInfo);

    /**
     * 删除文件记录
     *
     * @param aippIds aipp唯一标识
     */
    void deleteByAippIds(List<String> aippIds);
}
