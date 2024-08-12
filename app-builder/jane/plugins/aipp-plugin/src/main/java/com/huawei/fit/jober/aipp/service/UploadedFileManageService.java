/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import java.util.List;

/**
 * 文件上传管理器
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
public interface UploadedFileManageService {
    /**
     * 清理aipp文件
     *
     * @param aippId id
     * @param createUserAccount 创建者账号
     */
    void cleanAippFiles(List<String> aippId, String createUserAccount);

    /**
     * 添加文件记录
     *
     * @param aipp id
     * @param createUserAccount 创建者账号
     * @param filename 文件名
     */
    void addFileRecord(String aipp, String createUserAccount, String filename);
}
