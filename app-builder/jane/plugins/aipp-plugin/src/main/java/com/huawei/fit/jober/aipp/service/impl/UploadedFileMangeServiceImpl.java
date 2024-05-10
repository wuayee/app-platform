/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.dto.aipplog.AippUploadedFileInfoDto;
import com.huawei.fit.jober.aipp.mapper.AippUploadedFileMapper;
import com.huawei.fit.jober.aipp.service.UploadedFileManageService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.annotation.Scheduled;
import com.huawei.fitframework.util.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * aipp上传的文件管理服务实现, 主要实现
 * 1. 用户主动清理文件
 * 2. 30天超期文件自动清理
 *
 * @author x00649642
 * @since 2024-02-04
 */
@Component
public class UploadedFileMangeServiceImpl implements UploadedFileManageService {
    private static final Logger log = Logger.get(UploadedFileMangeServiceImpl.class);
    private final AippUploadedFileMapper aippUploadedFileMapper;

    public UploadedFileMangeServiceImpl(AippUploadedFileMapper aippUploadedFileMapper) {
        this.aippUploadedFileMapper = aippUploadedFileMapper;
    }

    /**
     * 清除储存时长超过30天（在sql中限制）的文件, 按CRON表达式每天凌晨4点启动一次清理
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 4 * * ? ")
    public void cleanExpiredFiles() {
        try {
            List<String> fileNames = aippUploadedFileMapper.queryExpiredFiles();
            List<String> filesDeleted = batchDeleteFiles(fileNames);
            log.info("clean expired files, expect {}, cleaned {}.", fileNames.size(), filesDeleted.size());
            if (!filesDeleted.isEmpty()) {
                aippUploadedFileMapper.deleteFileRecords(null, null, filesDeleted);
            }
        } catch (Exception e) {
            log.error("delete files failed on exception. reason: {}", e.getMessage());
        }
    }

    /**
     * 根据aipp和用户删除相关的文件
     *
     * @param aippId aipp id
     * @param createUserAccount 创建用户
     */
    @Override
    public void cleanAippFiles(String aippId, String createUserAccount) {
        log.debug("clean files for aipp: {}, user: {}.", aippId, createUserAccount);
        if (aippId == null || createUserAccount == null) {
            return;
        }

        List<String> filesToDelete = aippUploadedFileMapper.queryFilesByUserAipp(aippId, createUserAccount);
        List<String> filesDeleted = batchDeleteFiles(filesToDelete);
        if (!filesDeleted.isEmpty()) {
            aippUploadedFileMapper.deleteFileRecords(aippId, createUserAccount, filesDeleted);
        }
    }

    private List<String> batchDeleteFiles(List<String> fileNames) {
        List<String> fileDeleted = new ArrayList<>();
        for (String filename : fileNames) {
            File fileToDelete = Paths.get(filename).toFile();
            if (!fileToDelete.exists()) {
                fileDeleted.add(filename);
                continue;
            }
            try {
                FileUtils.delete(fileToDelete);
                if (!fileToDelete.exists()) {
                    fileDeleted.add(filename);
                }
            } catch (IllegalStateException e) {
                log.error("delete file {} failed. reason: {}", filename, e.getMessage());
            }
        }
        return fileDeleted;
    }

    /**
     * 添加文件记录
     *
     * @param aipp 文件关联aipp Id
     * @param createUserAccount 文件关联创建人工号
     * @param filename 文件名或者文件夹名
     */
    @Override
    public void addFileRecord(String aipp, String createUserAccount, String filename) {
        if (Stream.of(aipp, createUserAccount, filename).allMatch(s -> s != null && !s.isEmpty())) {
            aippUploadedFileMapper.insertFileRecord(new AippUploadedFileInfoDto(aipp, createUserAccount, filename));
        }
    }
}
