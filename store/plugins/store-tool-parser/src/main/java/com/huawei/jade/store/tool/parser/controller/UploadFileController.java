/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolSchema;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;

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
 * 表示上传文件的控制器。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-18
 */
@Component
@RequestMapping("/tools")
public class UploadFileController {
    private static final Logger log = Logger.get(UploadFileController.class);
    private static final String TOOL_PATH = "/var/temporary/tools";

    /**
     * 表示解析上传工具文件的请求。
     *
     * @param fileName 表示上传的文件名的 {@link String}。
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 上传文件失败后抛出的异常。
     */
    @PostMapping(path = "/parse/file", description = "上传工具文件")
    public List<MethodEntity> parseUploadFile(
            @RequestHeader(value = "tool-filename", defaultValue = "blank") String fileName,
            PartitionedEntity receivedFile) throws IOException {
        String uniqueFileName = generateUniqueFileName(fileName);
        log.info("Upload tool file fileName={} uniqueFileName={}.", fileName, uniqueFileName);
        File targetTemporaryFile = Paths.get(TOOL_PATH, uniqueFileName).toFile();
        List<NamedEntity> entityList =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (!entityList.isEmpty()) {
            storeTemporaryFile(fileName, entityList, targetTemporaryFile);
        }
        log.info("Upload file fileName={} uniqueFileName={} success.", fileName, uniqueFileName);
        List<MethodEntity> methodEntities = parseToolSchema(targetTemporaryFile.getPath());
        deleteFile(targetTemporaryFile);
        return methodEntities;
    }

    private static void storeTemporaryFile(String fileName, List<NamedEntity> entityList, File targetFile) {
        try (InputStream inStream = entityList.get(0).asFile().getInputStream()) {
            FileUtils.copyInputStreamToFile(inStream, targetFile);
        } catch (IOException e) {
            deleteFile(targetFile);
            log.error("Write file={} fail.", fileName, e);
            throw new IllegalStateException("Upload file failed.");
        }
    }

    private static void deleteFile(File targetFile) {
        Path fileToDeletePath = Paths.get(targetFile.getPath());
        if (Files.exists(fileToDeletePath)) {
            try {
                Files.delete(fileToDeletePath);
            } catch (IOException e1) {
                log.error("Failed to delete file.", e1);
            }
        }
    }

    private String generateUniqueFileName(String fileName) {
        notBlank(fileName, "The file name cannot be null or empty.");
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1 || extensionIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("The file name must have a valid extension.");
        }

        String extension = fileName.substring(extensionIndex + 1);
        return UUID.randomUUID() + "." + extension;
    }
}
