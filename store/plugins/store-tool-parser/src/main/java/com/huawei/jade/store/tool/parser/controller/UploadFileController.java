/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.greaterThan;
import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.jade.store.tool.parser.support.FileParser.parseToolSchema;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private final int deleteTimeout;
    private final PluginService pluginService;

    /**
     * 通过插件服务来初始化 {@link UploadFileController} 的新实例。
     *
     * @param pluginService 表示商品通用服务的 {@link PluginService}。
     * @param deleteTimeout 表示临时文件删除的时间的 {@link Integer}。
     */
    public UploadFileController(PluginService pluginService, @Value("${file.temp.delete.timeout}") int deleteTimeout) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        this.deleteTimeout = greaterThan(deleteTimeout, 0, "The delete timeout must be positive. [deleteTimeout={0}]",
                deleteTimeout);
    }

    /**
     * 表示解析上传工具文件的请求。
     * <p>创建的临时文件设置根据配置超时时间自动删除，默认超时时间是 15 分钟。</p>
     *
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 上传文件失败后抛出的异常。
     */
    @PostMapping(path = "/parse/file", description = "上传工具文件")
    public List<MethodEntity> parseUploadFile(PartitionedEntity receivedFile) throws IOException {
        notNull(receivedFile, "The file to be uploaded cannot be null.");
        List<NamedEntity> entityList =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (entityList.isEmpty()) {
            throw new IllegalStateException("No file entity found in the received file.");
        }
        List<MethodEntity> methodEntities = new ArrayList<>();
        for (NamedEntity namedEntity : entityList) {
            FileEntity file = namedEntity.asFile();
            String filename = file.filename();
            String uniqueFileName = generateUniqueFileName(filename);
            File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, uniqueFileName).toFile();
            log.info("Save the file {} to the temporary file directory and rename it as {}.", filename, uniqueFileName);
            this.storeTemporaryFile(filename, file, targetTemporaryFile);
            methodEntities.addAll(parseToolSchema(targetTemporaryFile.getPath()));
            log.info("The file {} is parsed successfully.", filename);
        }
        return methodEntities;
    }

    private void copyFile(String toCopyFilePath) {
        Path sourcePath = Paths.get(toCopyFilePath);
        String targetFolderPath;
        if (toCopyFilePath.endsWith(".jar")) {
            targetFolderPath = TOOL_PATH + "java";
        } else {
            targetFolderPath = TOOL_PATH + "python";
        }

        Path targetPath = Paths.get(targetFolderPath).resolve(sourcePath.getFileName());
        try {
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.info("Failed to copy file. [toCopyFilePath={}]", toCopyFilePath);
            throw new IllegalStateException(
                    StringUtils.format("Failed to copy file. [toCopyFilePath={0}]", toCopyFilePath), e);
        } finally {
            FileUtils.delete(toCopyFilePath);
            log.info("The temporary file {} was deleted successfully.", toCopyFilePath);
        }
    }

    /**
     * 数据保存至数据库，并将文件复制到容器目录中，该目录为共享目录。
     *
     * @param toolInfo 表示工具的元数据信息的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     */
    @PostMapping(path = "/save/file", description = "保存工具文件")
    public void saveFiles(@RequestBody List<MethodEntity> toolInfo) {
        List<String> filePath = new CopyOnWriteArrayList<>();
        for (MethodEntity methodEntity : toolInfo) {
            String path = methodEntity.getTargetFilePath();
            if (Files.exists(Paths.get(path))) {
                filePath.add(path);
            } else {
                throw new IllegalStateException(
                        StringUtils.format("Tool={0} timeout, please re-send.", methodEntity.getMethodName()));
            }
            saveTool(methodEntity);
        }
        filePath.stream().distinct().forEach(this::copyFile);
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

    private void storeTemporaryFile(String fileName, FileEntity file, File targetFile) {
        try (InputStream inStream = file.getInputStream();
             OutputStream outStream = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            EXECUTOR_SERVICE.schedule(() -> FileUtils.delete(targetFile.getPath()), deleteTimeout, TimeUnit.MINUTES);
        } catch (IOException e) {
            FileUtils.delete(targetFile.getPath());
            log.error("Failed to write file. [fileName={}]", fileName, e);
            throw new IllegalStateException(StringUtils.format("Failed to write file. [fileName={0}]", fileName), e);
        }
    }

    private String generateUniqueFileName(String fileName) {
        String extension =
                notBlank(FileUtils.extension(fileName), "The file {0} must have a valid extension.", fileName);
        return UUID.randomUUID() + extension;
    }
}
