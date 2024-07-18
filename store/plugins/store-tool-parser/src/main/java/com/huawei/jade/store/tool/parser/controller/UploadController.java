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
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.tool.parser.support.FileParser;
import com.huawei.jade.store.tool.parser.util.FileHashUtils;
import com.huawei.jade.store.tool.parser.util.UnzipUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇 h00675922
 * @since 2024-07-11
 */
@Component
@RequestMapping("/tools")
public class UploadController {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = Logger.get(UploadController.class);
    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";
    private static final String TOOL_PATH = "/var/store/tools/";
    private static final String TOOLS = "tools";
    private static final String COMMA = ",";
    private static final String CHECKSUM = "checksum";
    private static final String TYPE = "type";

    private final PluginService pluginService;
    private File toolFile = null;
    private File validationFile = null;
    private File toolsJsonFile = null;
    private JsonNode pluginJsonNode = null;

    /**
     * 通过插件服务来初始化 {@link UploadController} 的新实例。
     *
     * @param pluginService 表示商品通用服务的 {@link PluginService}。
     */
    public UploadController(PluginService pluginService) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
    }

    /**
     * 表示保存上传工具文件的请求。
     *
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @param toolsName 表示工具名的列表的 {@link String}。
     * @throws IOException 上传文件失败后抛出的异常。
     */
    @PostMapping(path = "/save/tool", description = "保存上传工具文件")
    public void saveUploadFile(PartitionedEntity receivedFile, @RequestParam("toolsName") String toolsName)
            throws IOException {
        notNull(receivedFile, "The file to be uploaded cannot be null.");
        notNull(toolsName, "The tools name cannot be null.");
        List<NamedEntity> entityList =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
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
            UnzipUtils.unzipFile(targetTemporaryFile, tempDir);

            this.getToolFile(tempDir);
            trySaveToolInfo(tempDir, targetTemporaryFile, toolsName);
        }
    }

    private void trySaveToolInfo(File tempDir, File targetTemporaryFile, String toolsName) {
        try {
            if (this.uniquenessVerification(this.toolFile, this.validationFile)) {
                this.saveFile(this.toolFile);
                this.saveMetadata(this.toolsJsonFile, toolsName);
                log.info("Files processed and saved successfully.");
            } else {
                throw new IllegalStateException("Failed to verify the file uniqueness.");
            }
        } finally {
            FileUtils.delete(tempDir);
            FileUtils.delete(targetTemporaryFile);
        }
    }

    private void saveMetadata(File toolsJsonFile, String toolsName) {
        Set<String> toolNames = Arrays.stream(toolsName.trim().split(COMMA)).collect(Collectors.toSet());
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(toolsJsonFile);
            JsonNode toolsNode = jsonNode.get(TOOLS);
            for (JsonNode node : toolsNode) {
                PluginData pluginData = FileParser.getPluginData(node, toolNames);
                if (pluginData.getName() != null) {
                    this.pluginService.addPlugin(pluginData);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse json from tools.json file.", e);
        }
    }

    private void saveFile(File toolFile) {
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
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile={0}]", toolFile), e);
        }
    }

    private boolean uniquenessVerification(File pluginFile, File validationFile) {
        try {
            String expectValidationValue = FileHashUtils.calculateFileHash(pluginFile.getPath(), "SHA-256");
            this.pluginJsonNode = OBJECT_MAPPER.readTree(validationFile);
            String actualValidationValue = this.pluginJsonNode.get(CHECKSUM).asText();
            return actualValidationValue.equals(expectValidationValue);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Uniqueness verification failed.", e);
        }
    }

    private Map<String, Object> parseUniquenessData(JsonNode jsonNode) {
        // 待 tool_plugin 表添加完再处理。
        String pluginType = jsonNode.get(TYPE).asText();
        try {
            Map<String, Object> toolInfo = FileParser.getToolInfo(jsonNode, pluginType);
            toolInfo.put("type", pluginType);
            return toolInfo;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse the plugin.json file.", e);
        }
    }

    private void getToolFile(File tempDir) {
        for (File extractedFile : Objects.requireNonNull(tempDir.listFiles())) {
            String extractedFileName = extractedFile.getName();
            if (extractedFileName.endsWith(".zip") || extractedFileName.endsWith(".tar") || extractedFileName.endsWith(
                    ".jar")) {
                this.toolFile = extractedFile;
            }
            if (extractedFileName.equals("plugin.json")) {
                this.validationFile = extractedFile;
            }
            if (extractedFileName.equals("tools.json")) {
                this.toolsJsonFile = extractedFile;
            }
        }

        if (this.toolFile == null || this.validationFile == null || this.toolsJsonFile == null) {
            throw new IllegalStateException("Missing required files in the uploaded zip.");
        }
    }

    private void storeTemporaryFile(String fileName, FileEntity file, File targetFile) {
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
}