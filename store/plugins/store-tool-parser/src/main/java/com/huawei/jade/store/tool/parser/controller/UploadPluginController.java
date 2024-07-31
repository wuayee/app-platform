/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.SecurityUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.tool.parser.support.FileParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇 h00675922
 * @since 2024-07-11
 */
@Component
@RequestMapping("/plugins")
public class UploadPluginController {
    private static final Logger log = Logger.get(UploadPluginController.class);
    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";
    private static final String TOOL_PATH = "/var/store/tools/";
    private static final String TOOLS = "tools";
    private static final String CHECKSUM = "checksum";
    private static final String TYPE = "type";
    private static final Set<String> TOOL_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".zip", ".tar", ".jar"));
    private static final String PLUGIN_JSON = "plugin.json";
    private static final String TOOLS_JSON = "tools.json";

    private final PluginService pluginService;
    private final ObjectSerializer serializer;

    /**
     * 通过插件服务来初始化 {@link UploadPluginController} 的新实例。
     *
     * @param pluginService 表示商品通用服务的 {@link PluginService}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     */
    public UploadPluginController(PluginService pluginService, @Fit(alias = "json") ObjectSerializer serializer) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        this.serializer = notNull(serializer, "The object serializer cannot be null.");
    }

    /**
     * 表示保存上传工具文件的请求。
     *
     * @param receivedFiles 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @param toolNames 表示工具名的列表的 {@link String}。
     * @throws IOException 当文件解压失败时抛出异常。
     */
    @PostMapping(path = "/save/tools", description = "保存上传工具文件")
    public void saveUploadFile(PartitionedEntity receivedFiles, @RequestParam("toolNames") String toolNames)
            throws IOException {
        notNull(receivedFiles, "The file to be uploaded cannot be null.");
        notNull(toolNames, "The tools name cannot be null.");
        List<NamedEntity> entityList =
                receivedFiles.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (entityList.isEmpty()) {
            throw new IllegalStateException("No file entity found in the received file.");
        }
        for (NamedEntity namedEntity : entityList) {
            FileEntity file = namedEntity.asFile();
            String filename = file.filename();
            if (!filename.endsWith(".zip")) {
                throw new IllegalStateException("The uploaded file must be a zip file.");
            }

            File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, filename).toFile();
            this.storeTemporaryFile(filename, file, targetTemporaryFile);
            log.info("Save the file {} to the temporary file directory ", filename);
            File tempDir = new File(TEMPORARY_TOOL_PATH, "unzip");
            FileUtils.unzip(targetTemporaryFile).target(tempDir).start();
            FileUtils.delete(targetTemporaryFile);

            this.savePlugin(tempDir, toolNames);
            log.info("The plugin is added successfully, and the selected tools are added successfully.");
        }
    }

    private void storeTemporaryFile(String fileName, FileEntity file, File targetFile) {
        // 添加对 targetFile 的校验
        File targetDirectory = targetFile.getParentFile();
        try {
            FileUtils.ensureDirectory(targetDirectory);
        } catch (IOException e) {
            throw new IllegalStateException(
                    StringUtils.format("Failed to create directories for the file. [fileName={0}]", fileName), e);
        }

        try (InputStream inStream = file.getInputStream();
             OutputStream outStream = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            FileUtils.delete(targetFile.getPath());
            throw new IllegalStateException(StringUtils.format("Failed to write file. [fileName={0}]", fileName), e);
        }
    }

    private void savePlugin(File tempDir, String toolNames) {
        try {
            File toolFile = this.getToolFile(tempDir);
            File validationFile = this.getFileByName(tempDir, PLUGIN_JSON);
            File toolsJsonFile = this.getFileByName(tempDir, TOOLS_JSON);
            this.saveTool(toolFile, validationFile, toolsJsonFile, toolNames);
        } finally {
            FileUtils.delete(tempDir);
        }
    }

    private File getToolFile(File tempDir) {
        for (File file : this.getFiles(tempDir)) {
            String fileExtension = FileUtils.extension(file.getName());
            if (TOOL_FILE_EXTENSIONS.contains(fileExtension)) {
                return file;
            }
        }
        throw new IllegalStateException("Missing the tool file in the uploaded file.");
    }

    private File getFileByName(File tempDir, String targetFileName) {
        for (File file : this.getFiles(tempDir)) {
            if (file.getName().equals(targetFileName)) {
                return file;
            }
        }
        throw new IllegalStateException(StringUtils.format("Missing {0} in the uploaded file.", targetFileName));
    }

    private File[] getFiles(File tempDir) {
        File[] files = tempDir.listFiles();
        return notNull(files, "The file in the plugin cannot be null.");
    }

    private void saveTool(File toolFile, File validationFile, File toolsJsonFile, String toolNames) {
        if (this.completenessCheck(toolFile, validationFile)) {
            this.saveToolFile(toolFile);
            this.saveMetadata(toolsJsonFile, toolNames);
        } else {
            throw new IllegalStateException("Failed to verify the file uniqueness.");
        }
    }

    private boolean completenessCheck(File pluginFile, File validationFile) {
        String expectValidationValue = SecurityUtils.signatureOf(pluginFile, "sha-256", 1024);
        Object actualValidationValue = this.getJsonInfo(validationFile).get(CHECKSUM);
        if (actualValidationValue instanceof String) {
            return actualValidationValue.equals(expectValidationValue);
        }
        return false;
    }

    private void saveToolFile(File toolFile) {
        // 添加插件校验
        Path sourcePath = toolFile.toPath();
        String targetFolderPath;
        if (toolFile.getName().endsWith(".jar")) {
            targetFolderPath = TOOL_PATH + "java";
        } else {
            targetFolderPath = TOOL_PATH + "python";
        }

        Path targetPath = Paths.get(targetFolderPath).resolve(sourcePath.getFileName());
        try {
            FileUtils.ensureDirectory(targetPath.getParent().toFile());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile={0}]", toolFile), e);
        }
    }

    private void saveMetadata(File toolsJsonFile, String toolsName) {
        List<Object> toolList = ObjectUtils.cast(this.getJsonInfo(toolsJsonFile).get(TOOLS));
        for (Object tool : toolList) {
            Map<String, Object> toolFile = ObjectUtils.cast(tool);
            PluginData pluginData = FileParser.getPluginData(toolFile, toolsName);
            if (pluginData.getName() != null) {
                this.pluginService.addPlugin(pluginData);
                log.info("The tool saved successfully. [toolName={}]", pluginData.getName());
            }
        }
    }

    private Map<String, Object> parseUniquenessData(File validationFile) {
        // 待 tool_plugin 表添加完再处理。
        Map<String, Object> jsonInfo = this.getJsonInfo(validationFile);
        Object type = jsonInfo.get(TYPE);
        if (type instanceof String) {
            return ObjectUtils.cast(jsonInfo.get((String) type));
        }
        throw new IllegalStateException("The data type is incorrect in plugin.json file.");
    }

    private Map<String, Object> getJsonInfo(File jsonFile) {
        notNull(jsonFile, "The json file cannot be null.");

        try (InputStream in = Files.newInputStream(jsonFile.toPath())) {
            return this.serializer.deserialize(in, Map.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert json data.", e);
        }
    }
}