/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.PathVariable;
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
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;
import com.huawei.jade.store.tool.parser.exception.PluginDeployException;
import com.huawei.jade.store.tool.parser.support.FileParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    private static final String PERSISTENT_PATH = "/opt/fit/tools";

    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";

    private static final String TOOL_PATH = "/var/store/tools/";

    private static final String TOOLS = "tools";

    private static final String CHECKSUM = "checksum";

    private static final String TYPE = "type";

    private static final Set<String> TOOL_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".zip", ".tar", ".jar"));

    private static final String PLUGIN_JSON = "plugin.json";

    private static final String TOOLS_JSON = "tools.json";

    private static final String JAR = ".jar";

    private static final String JAVA = "java";

    private static final String PYTHON = "python";

    private static final String VERSION = "version";

    private static final String GROUP_ID = "groupId";

    private static final String NAME = "name";

    private static final String ARTIFACT_ID = "artifactId";

    private static final String PLUGIN_FULL_NAME = "pluginFullName";

    private static final String DESCRIPTION = "description";

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
     * @return 格式化之后的返回消息的 {@link Result}。
     * @throws IOException 当文件解压失败时抛出异常。
     */
    @PostMapping(path = "/save", description = "保存上传工具文件")
    public Result<String> saveUploadFile(PartitionedEntity receivedFiles, @RequestParam("toolNames") String toolNames)
        throws IOException {
        notNull(receivedFiles, "The file to be uploaded cannot be null.");
        notNull(toolNames, "The tools name cannot be null.");
        List<NamedEntity> entityList = receivedFiles.entities()
            .stream()
            .filter(NamedEntity::isFile)
            .collect(Collectors.toList());
        if (entityList.isEmpty()) {
            throw new PluginDeployException(PluginDeployRetCode.NO_FILE_UPLOADED_ERROR);
        }
        for (NamedEntity namedEntity : entityList) {
            FileEntity file = namedEntity.asFile();
            String filename = file.filename();
            if (!filename.endsWith(".zip")) {
                throw new PluginDeployException(PluginDeployRetCode.UPLOADED_FILE_FORMAT_ERROR);
            }
            // targetTemporaryFile为临时目录
            File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, filename).toFile();
            this.storeTemporaryFile(filename, file, targetTemporaryFile);
            log.info("Save the file {} to the temporary file directory ", filename);
            File tempDir = new File(TEMPORARY_TOOL_PATH, "unzip");
            FileUtils.unzip(targetTemporaryFile).target(tempDir).start();
            FileUtils.delete(targetTemporaryFile);
            this.savePlugin(tempDir, toolNames);
            log.info("The plugin is added successfully, and the selected tools are added successfully.");
        }
        return Result.ok(null, 1);
    }

    /**
     * 删除插件的请求。
     *
     * @param pluginId 插件id
     * @return 格式化之后的返回消息的 {@link Result}。
     */
    @DeleteMapping(value = "/delete/{pluginId}", description = "删除插件")
    public Result<String> deletePlugin(@PathVariable("pluginId") String pluginId) {
        notBlank(pluginId, "The plugin id cannot be blank.");
        try {
            // 通过id查找插件信息
            PluginData pluginData = this.pluginService.getPlugin(pluginId);
            if (pluginData.getPluginId() == null) {
                return Result.ok(null, 0);
            }
            // 删除热部署文件
            Path deployPath = Paths.get(this.generateDeployPath(this.getPluginFullName(pluginData)).toString(),
                this.getPluginFullName(pluginData));
            FileUtils.delete(deployPath.toFile());
            // 刪除持久化插件文件
            Path persistentPath = this.generatePersistentPath(pluginData);
            FileUtils.delete(persistentPath.toFile());
            // 删除数据库内元数据
            return Result.ok(this.pluginService.deletePlugin(pluginId), 1);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalStateException(StringUtils.format("Failed to delete the plugin, [pluginId={0}]", pluginId),
                e);
        }
    }

    private String getPluginFullName(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        return getStringInMapObject(extension.get(PLUGIN_FULL_NAME));
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
        throw new PluginDeployException(PluginDeployRetCode.NO_PLUGIN_FOUND_ERROR);
    }

    private File getFileByName(File tempDir, String targetFileName) {
        for (File file : this.getFiles(tempDir)) {
            if (file.getName().equals(targetFileName)) {
                return file;
            }
        }
        throw new PluginDeployException(PluginDeployRetCode.FILE_MISSING_ERROR, targetFileName);
    }

    private File[] getFiles(File tempDir) {
        File[] files = tempDir.listFiles();
        return notNull(files, "The file in the plugin cannot be null.");
    }

    private void saveTool(File toolFile, File validationFile, File toolsJsonFile, String toolNames) {
        if (this.completenessCheck(toolFile, validationFile)) {
            PluginData pluginData = this.saveMetadata(toolsJsonFile, toolNames, validationFile, toolFile);
            this.checkUniqueDeploy(pluginData);
            this.deployToolFile(toolFile);
        } else {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
    }

    private void checkUniqueDeploy(PluginData pluginData) {
        String deployName = this.getPluginFullName(pluginData);
        List<String> deployedFiles = FileUtils.list(this.generateDeployPath(deployName).toFile())
            .stream()
            .map(File::getName)
            .collect(Collectors.toList());
        if (deployedFiles.contains(deployName)) {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
        log.info("deploy plugin unique check success, plugin name: {}", deployName);
    }

    private boolean completenessCheck(File pluginFile, File validationFile) {
        String expectValidationValue = SecurityUtils.signatureOf(pluginFile, "sha-256", 1024);
        Object actualValidationValue = this.getJsonInfo(validationFile).get(CHECKSUM);
        if (actualValidationValue instanceof String) {
            return actualValidationValue.equals(expectValidationValue);
        }
        return false;
    }

    private Path generateDeployPath(String toolName) {
        return toolName.endsWith(JAR) ? Paths.get(TOOL_PATH, JAVA) : Paths.get(TOOL_PATH, PYTHON);
    }

    private void deployToolFile(File toolFile) {
        // 添加插件校验
        Path sourcePath = toolFile.toPath();
        Path targetPath = generateDeployPath(toolFile.getName()).resolve(sourcePath.getFileName());
        try {
            FileUtils.ensureDirectory(targetPath.getParent().toFile());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile={0}]", toolFile), e);
        }
    }

    private void checkUniquePluginId(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() != null) {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
    }

    private PluginData saveMetadata(File toolsJsonFile, String toolsName, File validationFile, File toolPath) {
        String pluginId = hashString(toolPath.getName());
        checkUniquePluginId(pluginId);
        List<PluginToolData> pluginToolData = new ArrayList<>();
        List<Object> toolList = ObjectUtils.cast(this.getJsonInfo(toolsJsonFile).get(TOOLS));
        for (Object tool : toolList) {
            Map<String, Object> toolFile = ObjectUtils.cast(tool);
            PluginToolData parserData = FileParser.getPluginData(toolFile, toolsName);
            if (parserData.getName() != null) {
                pluginToolData.add(parserData);
            }
        }
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        pluginData.setExtension(this.parseUniquenessData(validationFile, toolPath.getName()));
        pluginData.setPluginName(this.getStringInMapObject(
            pluginData.getExtension().getOrDefault(NAME, FileUtils.ignoreExtension(toolPath.getName()))));
        pluginData.setPluginToolDataList(pluginToolData);
        this.savePluginToPersistentPath(toolPath, toolsJsonFile, validationFile, pluginData);
        this.pluginService.addPlugin(pluginData);
        return pluginData;
    }

    private Path generatePersistentPath(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        if (StringUtils.equalsIgnoreCase(JAVA, this.getStringInMapObject(extension.get(TYPE)))) {
            return Paths.get(PERSISTENT_PATH, JAVA,
                StringUtils.replace(this.getStringInMapObject(extension.get(PLUGIN_FULL_NAME)), ".", "_"));
        }
        return Paths.get(PERSISTENT_PATH, PYTHON,
            StringUtils.replace(this.getStringInMapObject(extension.get(PLUGIN_FULL_NAME)), ".", "_"));
    }

    private String getStringInMapObject(Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        throw new IllegalStateException("Object can not cast to string.");
    }

    private void savePluginToPersistentPath(File toolFile, File validationFile, File toolsJsonFile,
        PluginData pluginData) {
        try {
            Path persistentPath = generatePersistentPath(pluginData);
            FileUtils.ensureDirectory(persistentPath.toFile());
            Files.copy(toolFile.toPath(), Paths.get(persistentPath.toString(), toolFile.getName()),
                StandardCopyOption.REPLACE_EXISTING);
            Files.copy(validationFile.toPath(), Paths.get(persistentPath.toString(), validationFile.getName()),
                StandardCopyOption.REPLACE_EXISTING);
            Files.copy(toolsJsonFile.toPath(), Paths.get(persistentPath.toString(), toolsJsonFile.getName()),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile={0}]", toolFile), e);
        }
    }

    private Map<String, Object> parseUniquenessData(File validationFile, String pluginFullName) {
        // 待 tool_plugin 表添加完再处理。
        Map<String, Object> jsonInfo = this.getJsonInfo(validationFile);
        Object description = jsonInfo.getOrDefault(DESCRIPTION, StringUtils.EMPTY);
        Object codeType = jsonInfo.get(TYPE);
        Object name = jsonInfo.getOrDefault(NAME, FileUtils.ignoreExtension(pluginFullName));
        if (codeType instanceof String && description instanceof String && name instanceof String) {
            Map<String, Object> extension = ObjectUtils.cast(jsonInfo.get((String) codeType));
            extension.put(TYPE, codeType);
            extension.put(DESCRIPTION, this.getStringInMapObject(description));
            extension.put(PLUGIN_FULL_NAME, pluginFullName);
            extension.put(NAME, this.getStringInMapObject(name));
            return extension;
        }

        throw new IllegalStateException("The data type is incorrect in plugin.json file.");
    }

    private Map<String, Object> getJsonInfo(File jsonFile) {
        notNull(jsonFile, "The json file cannot be null.");

        try (InputStream in = Files.newInputStream(jsonFile.toPath())) {
            return this.serializer.deserialize(in, Map.class);
        } catch (IOException e) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR, e);
        }
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate pluginId.");
            return "";
        }
    }
}