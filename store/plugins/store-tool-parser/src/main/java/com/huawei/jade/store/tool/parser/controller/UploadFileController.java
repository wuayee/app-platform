/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolSchema;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;

import org.apache.maven.surefire.shared.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

    private final PluginService pluginService;

    /**
     * 通过插件服务来初始化 {@link UploadFileController} 的新实例。
     *
     * @param pluginService 表示商品通用服务的 {@link PluginService}。
     */
    public UploadFileController(PluginService pluginService) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
    }

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

    private void copyFile(String sourceFilePath) throws IOException {
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

    /**
     * 将数据保存至数据库接口，并将文件复制到容器目录中。
     *
     * @param toolInfo 表示工具的元数据信息的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     */
    @PostMapping(path = "/save/file", description = "保存工具文件")
    public void saveFiles(@RequestBody List<MethodEntity> toolInfo) {
        List<String> filePath = new ArrayList<>();
        for (MethodEntity methodEntity : toolInfo) {
            saveTool(methodEntity);
            filePath.add(methodEntity.getTargetFilePath());
        }
        filePath.stream().distinct().forEach(copyFilePath -> {
            try {
                copyFile(copyFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format("Fail to copy file{}", copyFilePath), e);
            }
        });
        log.info("File information saved successfully.");
    }

    private void saveTool(MethodEntity methodEntity) {
        PluginData pluginData = new PluginData();
        pluginData.setTags(methodEntity.getTags());
        pluginData.setSchema(methodEntity.getSchemaInfo());
        pluginData.setRunnables(methodEntity.getRunnablesInfo());
        pluginData.setName(methodEntity.getMethodName());
        pluginData.setDescription(methodEntity.getMethodDescription());
        // 临时使用上传｀市场｀的接口，待上传｀我的｀接口开发完毕再同步过来。
        this.pluginService.addPlugin(pluginData);
    }

    private static void storeTemporaryFile(String fileName, List<NamedEntity> entityList, File targetFile) {
        try (InputStream inStream = entityList.get(0).asFile().getInputStream()) {
            FileUtils.copyInputStreamToFile(inStream, targetFile);
            EXECUTOR_SERVICE.schedule(() -> deleteFile(targetFile.getPath()), 15, TimeUnit.MINUTES);
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
