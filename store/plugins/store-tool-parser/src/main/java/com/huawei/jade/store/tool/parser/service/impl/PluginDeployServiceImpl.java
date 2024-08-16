/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.service.impl;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.service.RegistryService;
import com.huawei.fit.service.entity.FitableAddressInstance;
import com.huawei.fit.service.entity.FitableInfo;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.Task;
import com.huawei.fitframework.schedule.ThreadPoolExecutor;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.SecurityUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.ThreadUtils;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.PluginToolService;
import com.huawei.jade.store.service.support.DeployStatus;
import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;
import com.huawei.jade.store.tool.parser.exception.PluginDeployException;
import com.huawei.jade.store.tool.parser.service.PluginDeployService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 插件部署服务实现类
 *
 * @since 2024/8/13
 */
@Component
public class PluginDeployServiceImpl implements PluginDeployService {
    private static final Logger log = Logger.get(PluginDeployServiceImpl.class);
    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";
    private static final String PERSISTENT_PATH = "/opt/fit/tools";
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
    private static final String GROUP_ID = "groupId";
    private static final String PLUGIN_NAME = "pluginName";
    private static final String NAME = "name";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String PLUGIN_FULL_NAME = "pluginFullName";
    private static final String DESCRIPTION = "description";
    private static final String FIT = "FIT";
    private static final String FITABLE_ID = "fitableId";
    private static final String GENERICABLE_ID = "genericableId";
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String TOOL_SCHEMA = "schema";
    private static final String PARAMETERS = "parameters";
    private static final String REQUIRED = "required";
    private static final String PROPERTIES = "properties";
    private static final String ORDER = "order";
    private static final String RUNNABLES = "runnables";
    private static final String EXTENSIONS = "extensions";
    private static final String TAGS = "tags";

    private final PluginService pluginService;
    private final ObjectSerializer serializer;
    private final ThreadPoolExecutor registerQueryThread;
    private final RegistryService registryService;
    private final PluginToolService pluginToolService;
    private final int timeout;
    private final int interval;

    /**
     * 通过插件服务来初始化 {@link PluginDeployServiceImpl} 的新实例。
     *
     * @param pluginService 表示插件服务的 {@link PluginService}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     * @param registryService 表示注册中心的 {@link RegistryService}。
     * @param pluginToolService 表示插件工具服务的 {@link PluginToolService}。
     * @param timeout 表示部署超时时间的 {@link Integer}。
     * @param interval 表示部署状态查询间隔的 {@link Integer}。
     */
    public PluginDeployServiceImpl(PluginService pluginService, @Fit(alias = "json") ObjectSerializer serializer,
        RegistryService registryService, PluginToolService pluginToolService,
        @Value("${plugin.deploy.timeout}") Integer timeout,
        @Value("${plugin.deploy.query.interval}") Integer interval) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        this.pluginToolService = notNull(pluginToolService, "The plugin tool service cannot be null.");
        this.serializer = notNull(serializer, "The object serializer cannot be null.");
        this.registryService = notNull(registryService, "The registry service cannot be null.");
        this.registerQueryThread = ThreadPoolExecutor.custom()
            .threadPoolName("registerQueryThread")
            .awaitTermination(500L, TimeUnit.MILLISECONDS)
            .isImmediateShutdown(false)
            .corePoolSize(20)
            .maximumPoolSize(20)
            .keepAliveTime(1, TimeUnit.SECONDS)
            .workQueueCapacity(10)
            .isDaemonThread(false)
            .exceptionHandler((thread, throwable) -> {})
            .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
            .build();
        this.timeout = timeout;
        this.interval = interval;
        this.initDeployStatus();
    }

    private void initDeployStatus() {
        List<String> expiredStatusIds = this.pluginService.getPlugins(DeployStatus.DEPLOYING)
            .stream()
            .map(PluginData::getPluginId)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(expiredStatusIds)) {
            return;
        }
        expiredStatusIds.forEach(this::undeployPlugin);
        this.pluginService.updateDeployStatus(expiredStatusIds, DeployStatus.UNDEPLOYED);
    }

    @Override
    public void uploadPlugins(List<NamedEntity> namedEntities, String toolNames) {
        for (NamedEntity namedEntity : namedEntities) {
            FileEntity file = namedEntity.asFile();
            String filename = file.filename();
            if (!filename.endsWith(".zip")) {
                throw new PluginDeployException(PluginDeployRetCode.UPLOADED_FILE_FORMAT_ERROR);
            }
            File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, filename).toFile();
            this.storeTemporaryFile(filename, file, targetTemporaryFile);
            log.info("Save the file {} to the temporary file directory ", filename);
            File tempDir = new File(TEMPORARY_TOOL_PATH, "unzip");
            try {
                FileUtils.unzip(targetTemporaryFile).target(tempDir).start();
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format("Failed to unzip plugin file.", e));
            }
            FileUtils.delete(targetTemporaryFile);
            this.savePlugin(tempDir, toolNames);
            log.info("The plugin is added successfully, and the selected tools are added successfully.");
        }
    }

    @Override
    public void deployPlugins(List<String> toDeployPluginIds) {
        toDeployPluginIds.forEach(s -> Optional.ofNullable(pluginService.getPlugin(s))
            .orElseThrow(() -> new PluginDeployException(PluginDeployRetCode.PLUGIN_NOT_EXISTS, s)));
        List<PluginData> deployedPlugins = pluginService.getPlugins(DeployStatus.DEPLOYED);
        List<String> deployedPluginIds = deployedPlugins.stream()
            .map(PluginData::getPluginId)
            .collect(Collectors.toList());
        List<String> toUnDeployedIds = new ArrayList<>(
            CollectionUtils.difference(deployedPluginIds, toDeployPluginIds));
        List<String> newDeployedIds = new ArrayList<>(CollectionUtils.difference(toDeployPluginIds, deployedPluginIds));
        if (CollectionUtils.isNotEmpty(toUnDeployedIds)) {
            pluginService.updateDeployStatus(toUnDeployedIds, DeployStatus.UNDEPLOYED);
            toUnDeployedIds.forEach(this::undeployPlugin);
        }
        if (CollectionUtils.isNotEmpty(newDeployedIds)) {
            pluginService.updateDeployStatus(newDeployedIds, DeployStatus.DEPLOYING);
            newDeployedIds.forEach(this::deployPlugin);
        }
    }

    @Override
    public int deletePlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() == null) {
            // 无此插件时，返回删除数量为0
            log.warn("no plugin found when try to delete. [pluginFile={0}]", pluginId);
            return 0;
        }
        this.pluginService.deletePlugin(pluginId);
        Path deployPath = Paths.get(this.generateDeployPath(this.getPluginFullName(pluginData)).toString(),
            this.getPluginFullName(pluginData));
        FileUtils.delete(deployPath.toFile());
        Path persistentPath = this.generatePersistentPath(pluginData);
        FileUtils.delete(persistentPath.toFile());
        // 正常删除，返回删除数量为1
        return 1;
    }

    @Override
    public int queryCountByDeployStatus(DeployStatus deployStatus) {
        return this.pluginService.getPluginsCount(deployStatus);
    }

    @Override
    public List<PluginData> queryPluginsByDeployStatus(DeployStatus deployStatus) {
        return this.pluginService.getPlugins(deployStatus);
    }

    private void undeployPlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        Path deployedPath = Paths.get(this.generateDeployPath(this.getPluginFullName(pluginData)).toString(),
            this.getPluginFullName(pluginData));
        try {
            FileUtils.delete(deployedPath.toFile());
        } catch (IllegalArgumentException e) {
            log.error("failed to delete plugin, [pluginFile={0}]", pluginId, e);
        }
    }

    private void deployPlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        String pluginFullName = this.getPluginFullName(pluginData);
        this.registerQueryThread.execute(Task.builder()
            .runnable(() -> this.deploy(pluginData, pluginFullName, pluginId))
            .uncaughtExceptionHandler((thread, cause) -> this.exceptionCaught(cause, pluginFullName, pluginId))
            .buildDisposable());
    }

    private void deploy(PluginData pluginData, String pluginFullName, String pluginId) {
        log.info("start deploy plugin, pluginId: {}", pluginId);
        Path deployPath = this.generateDeployPath(pluginFullName).resolve(pluginFullName);
        Path persistentPath = this.generatePersistentPath(pluginData);
        if (!this.completenessCheck(persistentPath.resolve(pluginFullName).toFile(),
            this.getChecksumFromPluginData(pluginData))) {
            log.error("Completeness check failed before deploy, [pluginFile={0}]", pluginId);
            pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYMENT_FAILED);
            return;
        }
        try {
            FileUtils.ensureDirectory(deployPath.getParent().toFile());
            Files.copy(persistentPath.resolve(pluginFullName), deployPath, StandardCopyOption.REPLACE_EXISTING);
            List<FitableInfo> fitableInfos = this.pluginToolService.getPluginTools(pluginId)
                .stream()
                .map(pluginToolData -> this.getFitableInfo(pluginToolData))
                .collect(Collectors.toList());
            if (this.queryToolsRegisterResult(fitableInfos)) {
                this.pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYED);
            } else {
                this.undeployPlugin(pluginId);
                this.pluginService.updateDeployStatus(Collections.singletonList(pluginId),
                    DeployStatus.DEPLOYMENT_FAILED);
            }
        } catch (IOException e) {
            log.error(StringUtils.format("Failed to deploy plugin. [pluginFile={0}]", pluginFullName), e);
            this.undeployPlugin(pluginId);
            pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYMENT_FAILED);
        }
    }

    private String getChecksumFromPluginData(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        return this.getStringInMapObject(extension.get(CHECKSUM));
    }

    private boolean queryToolsRegisterResult(List<FitableInfo> fitableInfos) {
        long startTimestamp = System.currentTimeMillis();
        while (!isQueryTimeout(startTimestamp)) {
            ThreadUtils.sleep(this.interval * 1000L);
            List<FitableAddressInstance> result = registryService.queryFitables(fitableInfos, "");
            if (result.size() == fitableInfos.size() && result.stream()
                .allMatch(info -> info.getApplicationInstances().size() > 0)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQueryTimeout(long startTimestamp) {
        return System.currentTimeMillis() - startTimestamp > 1000L * 60 * this.timeout;
    }

    private FitableInfo getFitableInfo(PluginToolData pluginToolData) {
        Map<String, Object> runnable = cast(pluginToolData.getRunnables().get(FIT));
        FitableInfo fitableInfo = new FitableInfo();
        fitableInfo.setFitableId(this.getStringInMapObject(runnable.get(FITABLE_ID)));
        fitableInfo.setFitableVersion(DEFAULT_VERSION);
        fitableInfo.setGenericableId(this.getStringInMapObject(runnable.get(GENERICABLE_ID)));
        fitableInfo.setGenericableVersion(DEFAULT_VERSION);
        return fitableInfo;
    }

    private void exceptionCaught(Throwable cause, String pluginFullName, String pluginId) {
        log.error(StringUtils.format("Failed to deploy file. [pluginFile={0}]", pluginFullName), cause);
        this.undeployPlugin(pluginId);
        pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYMENT_FAILED);
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
                if (targetFileName.equals(PLUGIN_JSON)) {
                    this.validatePluginJson(file);
                }
                if (targetFileName.equals(TOOLS_JSON)) {
                    this.validateToolsJson(file);
                }
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
            this.saveMetadata(toolsJsonFile, toolNames, validationFile, toolFile);
        } else {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_COMPLETENESS_CHECK_ERROR);
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

    private boolean completenessCheck(File pluginFile, String expectCheckSum) {
        String fileChecksum = SecurityUtils.signatureOf(pluginFile, "sha-256", 1024);
        return expectCheckSum.equals(fileChecksum);
    }

    private PluginData saveMetadata(File toolsJsonFile, String toolsName, File validationFile, File pluginFile) {
        String pluginId = this.generatePluginId(this.getJsonInfo(validationFile));
        this.checkUniquePluginId(pluginId);
        List<PluginToolData> pluginToolData = new ArrayList<>();
        List<Object> toolList = cast(this.getJsonInfo(toolsJsonFile).get(TOOLS));
        for (Object tool : toolList) {
            Map<String, Object> toolFile = cast(tool);
            PluginToolData parserData = FileParser.getPluginData(toolFile, toolsName);
            if (parserData.getName() != null) {
                pluginToolData.add(parserData);
            }
        }
        String oldFileName = pluginFile.getName();
        File newFile = new File(pluginFile.getParent(),
            generateNewName(FileUtils.ignoreExtension(oldFileName), FileUtils.extension(oldFileName)));
        pluginFile.renameTo(newFile);
        PluginData pluginData = new PluginData();
        pluginData.setPluginId(pluginId);
        pluginData.setExtension(this.parseUniquenessData(validationFile, newFile.getName()));
        pluginData.setPluginName(this.getStringInMapObject(
            pluginData.getExtension().getOrDefault(PLUGIN_NAME, FileUtils.ignoreExtension(pluginFile.getName()))));
        pluginData.setPluginToolDataList(pluginToolData);
        pluginData.setDeployStatus(DeployStatus.UNDEPLOYED.name());
        this.savePluginToPersistentPath(newFile, pluginData);
        this.pluginService.addPlugin(pluginData);
        return pluginData;
    }

    private String generateNewName(String oldName, String extension) {
        long timestamp = System.currentTimeMillis();
        return oldName + "_" + timestamp + extension;
    }

    private Map<String, Object> parseUniquenessData(File validationFile, String pluginFullName) {
        Map<String, Object> jsonInfo = this.getJsonInfo(validationFile);
        Object description = jsonInfo.getOrDefault(DESCRIPTION, StringUtils.EMPTY);
        Object codeType = jsonInfo.get(TYPE);
        Object pluginName = jsonInfo.getOrDefault(NAME, FileUtils.ignoreExtension(pluginFullName));
        Object checkSum = jsonInfo.get(CHECKSUM);
        if (codeType instanceof String && description instanceof String && pluginName instanceof String
            && checkSum instanceof String) {
            Map<String, Object> extension = cast(jsonInfo.get((String) codeType));
            extension.put(CHECKSUM, this.getStringInMapObject(checkSum));
            extension.put(TYPE, codeType);
            extension.put(DESCRIPTION, this.getStringInMapObject(description));
            extension.put(PLUGIN_FULL_NAME, pluginFullName);
            extension.put(PLUGIN_NAME, this.getStringInMapObject(pluginName));
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

    private String generatePluginId(String input) {
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

    private void savePluginToPersistentPath(File pluginFile, PluginData pluginData) {
        try {
            Path persistentPath = this.generatePersistentPath(pluginData);
            FileUtils.ensureDirectory(persistentPath.toFile());
            Files.copy(pluginFile.toPath(), Paths.get(persistentPath.toString(), pluginFile.getName()),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile={0}]", pluginFile), e);
        }
    }

    private void checkUniquePluginId(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() == null) {
            return;
        }
        // 如果已有相同插件的状态不是未部署，插件唯一性校验失败，插件上传失败
        if (!Objects.equals(pluginData.getDeployStatus(), DeployStatus.UNDEPLOYED.toString())) {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
        // 未部署的相同插件可以被新插件替换
        this.deletePlugin(pluginId);
    }

    private String getPluginFullName(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        return this.getStringInMapObject(extension.get(PLUGIN_FULL_NAME));
    }

    private Path generateDeployPath(String toolName) {
        return toolName.endsWith(JAR) ? Paths.get(TOOL_PATH, JAVA) : Paths.get(TOOL_PATH, PYTHON);
    }

    private String generatePluginId(Map<String, Object> extension) {
        String combinedString = "";
        if (extension.get(TYPE).equals(PYTHON)) {
            Map<String, String> pythonParameters = notNull(cast(extension.get(PYTHON)),
                "Python plugin extension parameters cannot be null.");
            combinedString = pythonParameters.get(NAME);
            return this.generatePluginId(combinedString);
        }
        Map<String, String> javaParameters = notNull(cast(extension.get(JAVA)),
            "Java plugin extension parameters cannot be null.");
        combinedString = javaParameters.get(ARTIFACT_ID) + javaParameters.get(GROUP_ID);
        return this.generatePluginId(combinedString);
    }

    private Path generatePersistentPath(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        if (StringUtils.equalsIgnoreCase(JAVA, this.getStringInMapObject(extension.get(TYPE)))) {
            return Paths.get(PERSISTENT_PATH, JAVA, this.getStringInMapObject(extension.get(ARTIFACT_ID)),
                this.getStringInMapObject(extension.get(GROUP_ID)));
        }
        return Paths.get(PERSISTENT_PATH, PYTHON, this.getStringInMapObject(extension.get(NAME)));
    }

    private String getStringInMapObject(Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        throw new IllegalStateException("Object can not cast to string.");
    }

    private void validateToolsJson(File toolsJson) {
        Map<String, Object> tool = this.getJsonInfo(toolsJson);
        this.validateToolSchema(tool);
        this.validateToolRunnables(tool);
        this.validateToolExtensions(tool);
    }

    private void validateToolExtensions(Map<String, Object> tool) {
        if (!tool.containsKey(EXTENSIONS)) {
            throw new FitException("Tools.json should contain extensions.");
        }
        Map<String, Object> extensions = cast(tool.get(EXTENSIONS));
        if (!extensions.containsKey(TAGS)) {
            throw new FitException("Extensions in tools.json should contain tags.");
        }
        List<String> tags = cast(extensions.get(TAGS));
        if (tags.isEmpty()) {
            throw new FitException("Tags in tools.json extensions cannot be empty.");
        }
    }

    private void validateToolRunnables(Map<String, Object> tool) {
        if (!tool.containsKey(RUNNABLES)) {
            throw new FitException("Tools.json should contain runnables.");
        }
        Map<String, Object> runnables = cast(tool.get(RUNNABLES));
        if (runnables.containsKey(FIT)) {
            Map<String, Object> fit = cast(runnables.get(FIT));
            if (!fit.containsKey(FITABLE_ID)) {
                throw new FitException("Runnables in tools.json should contain fitableId.");
            }
            if (!fit.containsKey(GENERICABLE_ID)) {
                throw new FitException("Runnables in tools.json should contain genericableId.");
            }
        }
    }

    private void validateToolSchema(Map<String, Object> tool) {
        if (!tool.containsKey(TOOL_SCHEMA)) {
            throw new FitException("Tools.json should contain schema.");
        }
        Map<String, Object> schema = cast(tool.get(TOOL_SCHEMA));
        if (!schema.containsKey(PARAMETERS)) {
            throw new FitException("Schema in tools.json should contain parameters.");
        }
        Map<String, Object> parameters = cast(schema.get(PARAMETERS));
        if (!parameters.containsKey(REQUIRED)) {
            throw new FitException("Parameters in tools.json should contain required.");
        }
        if (!parameters.containsKey(PROPERTIES)) {
            throw new FitException("Parameters in tools.json should contain properties.");
        }
        List<String> required = cast(parameters.get(REQUIRED));
        Map<String, Object> properties = cast(parameters.get(PROPERTIES));
        if (required.size() > properties.size()) {
            throw new FitException("The size of required in tools.json cannot be larger than properties size.");
        }
        if (!schema.containsKey(ORDER)) {
            throw new FitException("Schema in tools.json should contain order.");
        }
        List<String> order = cast(schema.get(REQUIRED));
        if (!order.isEmpty() && order.size() != properties.size()) {
            throw new FitException("Order size should be zero or equal to properties size in tools.json.");
        }
    }

    private void validatePluginJson(File pluginJson) {
        Map<String, Object> plugin = this.getJsonInfo(pluginJson);
        if (!plugin.containsKey(TYPE)) {
            throw new FitException("Plugin.json should contain type.");
        }
        if (plugin.get(TYPE).equals(PYTHON)) {
            if (!plugin.containsKey(PYTHON)) {
                throw new FitException("Plugin.json should contain python.");
            }
            Map<String, Object> python = cast(plugin.get(PYTHON));
            if (python.size() > 1) {
                throw new FitException("Plugin.json cannot contain other properties.");
            }
            if (!python.containsKey(NAME)) {
                throw new FitException("Python plugin.json must contain name.");
            }
        } else if (plugin.get(TYPE).equals(JAVA)) {
            Map<String, Object> java = cast(plugin.get(JAVA));
            if (java.size() > 2) {
                throw new FitException("Plugin.json cannot contain other properties.");
            }
            if (!java.containsKey(ARTIFACT_ID)) {
                throw new FitException("Java plugin.json must contain artifactId.");
            }
            if (!java.containsKey(GROUP_ID)) {
                throw new FitException("Java plugin.json must contain groupId.");
            }
        } else {
            throw new FitException("Plugin.json type can only contain python and java.");
        }
    }
}