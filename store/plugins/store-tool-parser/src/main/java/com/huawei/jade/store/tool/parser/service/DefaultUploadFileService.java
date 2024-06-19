/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.service;

import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolsJsonSchema;

import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.parser.MethodEntity;
import com.huawei.jade.store.service.UploadFileService;

import org.apache.maven.surefire.shared.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 上传工具的服务层实现。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
@Component
public class DefaultUploadFileService implements UploadFileService {
    private static final Logger log = Logger.get(DefaultUploadFileService.class);
    private static final String TOOL_PATH = "/var/tools";

    /**
     * 初始化 {@link UploadFileService} 实例。
     */
    public DefaultUploadFileService() {
    }

    @Override
    @Fitable(id = "upload-tool-file")
    public List<MethodEntity> fileUpload(String fileName, PartitionedEntity receiveFile) {
        String uniqueFileName = generateUniqueFileName(fileName);
        log.info("Upload tool file fileName={} uniqueFileName={}", fileName, uniqueFileName);
        File targetFile = Paths.get(TOOL_PATH, uniqueFileName).toFile();
        List<NamedEntity> entityList =
                receiveFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        try (InputStream inStream = entityList.get(0).asFile().getInputStream()) {
            FileUtils.copyInputStreamToFile(inStream, targetFile);
        } catch (IOException e) {
            Path fileToDeletePath = Paths.get(targetFile.getPath());
            if (Files.exists(fileToDeletePath)) {
                try {
                    Files.delete(fileToDeletePath);
                } catch (IOException e1) {
                    log.error("Failed to delete file.", e1);
                }
            }
            log.error("Write file={} fail.", fileName, e);
            throw new IllegalStateException("Upload file failed.");
        }
        log.info("Upload file fileName={} uniqueFileName={} success.", fileName, uniqueFileName);
        try {
            return parseToolsJsonSchema(targetFile.getPath());
        } catch (IOException e) {
            log.error("Get schema fail in file={}.", fileName, e);
            throw new IllegalStateException("Can not parse file for schema info.");
        }
    }

    private String generateUniqueFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
