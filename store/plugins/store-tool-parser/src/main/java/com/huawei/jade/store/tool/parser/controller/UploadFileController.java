/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolSchema;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;
import com.huawei.jade.store.tool.parser.entity.MoveFileEntity;

import org.apache.maven.surefire.shared.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";
    private static final String TOOL_PATH = "/var/store/tools/";
    private static final ScheduledExecutorService EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1);

    /**
     * 表示解析上传工具文件的请求。
     * <p>创建的临时文件设置默认15分钟自动删除。</p>
     *
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 上传文件失败后抛出的异常。
     */
    @PostMapping(path = "/parse/file", description = "上传工具文件")
    public List<MethodEntity> parseUploadFile(PartitionedEntity receivedFile) throws IOException {
        List<NamedEntity> entityList =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (!entityList.isEmpty()) {
            FileEntity file = entityList.get(0).asFile();
            String filename = file.filename();
            String uniqueFileName = generateUniqueFileName(filename);
            log.info("Upload tool file fileName={} uniqueFileName={}.", filename, uniqueFileName);
            File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, uniqueFileName).toFile();
            storeTemporaryFile(filename, entityList, targetTemporaryFile);
            log.info("Upload file fileName={} uniqueFileName={} success.", filename, uniqueFileName);
            return parseToolSchema(targetTemporaryFile.getPath());
        }
        throw new IllegalStateException("No file entity found in the received file.");
    }

    /**
     * 确定加入工具时，需要将文件移动至挂载目标目录下。
     *
     * @param moveFileEntity 表示移动文件的实体的 {@link MoveFileEntity}。
     * @throws IOException 拷贝文件失败时抛出异常。
     */
    @PostMapping(path = "/move/file", description = "上传工具文件")
    public void moveFile(@RequestBody MoveFileEntity moveFileEntity) throws IOException {
        String sourceFilePath = moveFileEntity.getSourceFilePath();
        Path sourcePath = Paths.get(sourceFilePath);
        String targetFolderPath;
        if (sourceFilePath.endsWith(".jar")) {
            targetFolderPath = TOOL_PATH + "java";
        } else {
            targetFolderPath = TOOL_PATH + "python";
        }

        Path targetPath = Paths.get(targetFolderPath).resolve(sourcePath.getFileName());
        if (!Files.exists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void scheduleFileDeletion(File file) {
        EXECUTOR_SERVICE.schedule(() -> deleteFile(file.getPath()), 15, TimeUnit.MINUTES);
    }

    private static void storeTemporaryFile(String fileName, List<NamedEntity> entityList, File targetFile) {
        try (InputStream inStream = entityList.get(0).asFile().getInputStream()) {
            FileUtils.copyInputStreamToFile(inStream, targetFile);
            scheduleFileDeletion(targetFile);
        } catch (IOException e) {
            deleteFile(targetFile.getPath());
            log.error("Write file={} fail.", fileName, e);
            throw new IllegalStateException("Upload file failed.");
        }
    }

    private static void deleteFile(String filePath) {
        Path fileToDeletePath = Paths.get(filePath);
        if (Files.exists(fileToDeletePath)) {
            try {
                Files.delete(fileToDeletePath);
            } catch (IOException e) {
                log.error("Failed to delete file.", e);
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
