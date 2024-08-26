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
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.FitRuntimeStartedObserver;
import com.huawei.fitframework.schedule.Task;
import com.huawei.fitframework.schedule.ThreadPoolExecutor;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.SecurityUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.ThreadUtils;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.service.PluginService;
import com.huawei.jade.store.service.PluginToolService;
import com.huawei.jade.store.service.support.DeployStatus;
import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;
import com.huawei.jade.store.tool.parser.config.PluginDeployQueryConfig;
import com.huawei.jade.store.tool.parser.config.RegistryQueryPoolConfig;
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
 * 插件部署服务实现类。
 *
 * @author 罗帅
 * @since 2024-8-13
 */
@Component
public class PluginDeployServiceImpl implements PluginDeployService, FitRuntimeStartedObserver {
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
    private static final String USER = "user";
    private static final String BUILTIN = "BUILTIN";
    private static final String AND = "AND";

    private final PluginService pluginService;
    private final ObjectSerializer serializer;
    private final ThreadPoolExecutor registerQueryThread;
    private final RegistryService registryService;
    private final PluginToolService pluginToolService;
    private final PluginDeployQueryConfig pluginDeployQueryConfig;
    private final RegistryQueryPoolConfig registryQueryPoolConfig;

    /**
     * 通过插件服务来初始化 {@link PluginDeployServiceImpl} 的新实例。
     *
     * @param pluginService 表示插件服务的 {@link PluginService}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     * @param registryService 表示注册中心的 {@link RegistryService}。
     * @param pluginToolService 表示插件工具服务的 {@link PluginToolService}。
     * @param pluginDeployQueryConfig 表示插件部署状态查询配置参数的 {@link PluginDeployQueryConfig}。
     * @param registryQueryPoolConfig 表示查询注册中心的线程池配置参数的 {@link RegistryQueryPoolConfig}。
     */
    public PluginDeployServiceImpl(PluginService pluginService, @Fit(alias = "json") ObjectSerializer serializer,
        RegistryService registryService, PluginToolService pluginToolService,
        PluginDeployQueryConfig pluginDeployQueryConfig, RegistryQueryPoolConfig registryQueryPoolConfig) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        this.pluginToolService = notNull(pluginToolService, "The plugin tool service cannot be null.");
        this.serializer = notNull(serializer, "The object serializer cannot be null.");
        this.registryService = notNull(registryService, "The registry service cannot be null.");
        this.pluginDeployQueryConfig = Validation.notNull(pluginDeployQueryConfig,
            "The plugin deploy query config cannot be null.");
        this.registryQueryPoolConfig = Validation.notNull(registryQueryPoolConfig,
            "The registry query pool config cannot be null.");
        this.registerQueryThread = ThreadPoolExecutor.custom()
            .threadPoolName("registry-query-pool")
            .awaitTermination(500L, TimeUnit.MILLISECONDS)
            .isImmediateShutdown(false)
            .corePoolSize(this.registryQueryPoolConfig.getCorePoolSize())
            .maximumPoolSize(this.registryQueryPoolConfig.getMaximumPoolSize())
            .keepAliveTime(60L, TimeUnit.SECONDS)
            .workQueueCapacity(this.registryQueryPoolConfig.getWorkQueueCapacity())
            .isDaemonThread(false)
            .exceptionHandler((thread, throwable) -> {})
            .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
            .build();
    }

    private void initDeployStatus() {
        List<String> expiredStatusIds = this.pluginService.getPlugins(DeployStatus.DEPLOYING)
            .stream()
            .map(PluginData::getPluginId)
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(expiredStatusIds)) {
            expiredStatusIds.forEach(this::undeployPlugin);
            this.pluginService.updateDeployStatus(expiredStatusIds, DeployStatus.UNDEPLOYED);
        }
        // 内置工具修改为已部署
        PluginQuery pluginQuery = new PluginQuery();
        pluginQuery.setExcludeTags(new HashSet<>());
        pluginQuery.setIncludeTags(new HashSet<>(Collections.singletonList(BUILTIN)));
        pluginQuery.setMode(AND);
        List<String> builtInPluginIds = this.pluginService.getPlugins(pluginQuery)
            .getData()
            .stream()
            .map(PluginData::getPluginId)
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(builtInPluginIds)) {
            this.pluginService.updateDeployStatus(builtInPluginIds, DeployStatus.DEPLOYED);
        }
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
            log.info("Save the file to the temporary file directory. [fileName={}]", filename);
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
        this.validatePluginIds(toDeployPluginIds);
        List<PluginData> deployedPlugins = this.pluginService.getPlugins(DeployStatus.DEPLOYED);
        List<String> deployedPluginIds = deployedPlugins.stream()
            .filter(pluginData -> !pluginData.getBuiltin())
            .map(PluginData::getPluginId)
            .collect(Collectors.toList());
        List<String> toUnDeployedIds = new ArrayList<>(
            CollectionUtils.difference(deployedPluginIds, toDeployPluginIds));
        List<String> newDeployedIds = new ArrayList<>(CollectionUtils.difference(toDeployPluginIds, deployedPluginIds));
        if (CollectionUtils.isNotEmpty(toUnDeployedIds)) {
            this.pluginService.updateDeployStatus(toUnDeployedIds, DeployStatus.UNDEPLOYED);
            toUnDeployedIds.forEach(this::undeployPlugin);
        }
        if (CollectionUtils.isNotEmpty(newDeployedIds)) {
            this.pluginService.updateDeployStatus(newDeployedIds, DeployStatus.DEPLOYING);
            newDeployedIds.forEach(this::deployPlugin);
        }
    }

    private void validatePluginIds(List<String> toDeployPluginIds) {
        toDeployPluginIds.forEach(s -> Optional.ofNullable(this.pluginService.getPlugin(s).getPluginId())
            .orElseThrow(() -> new PluginDeployException(PluginDeployRetCode.PLUGIN_NOT_EXISTS, s)));
    }

    @Override
    public int deletePlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() == null) {
            // 无此插件时，返回删除数量为 0
            log.warn("No plugin found when try to delete. [pluginId={}]", pluginId);
            return 0;
        }
        this.pluginService.deletePlugin(pluginId);
        Path deployPath = Paths.get(this.generateDeployPath(this.getPluginFullName(pluginData)).toString(),
            this.getPluginFullName(pluginData));
        FileUtils.delete(deployPath.toFile());
        Path persistentPath = this.generatePersistentPath(pluginData);
        FileUtils.delete(persistentPath.toFile());
        log.info("Succeeded to delete plugin. [pluginName={}]", pluginData.getPluginName());
        // 正常删除，返回删除数量为 1
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
        } catch (IllegalStateException e) {
            log.error("Failed to delete plugin. [pluginName={}]", pluginData.getPluginName(), e);
        }
    }

    private void deployPlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        String pluginFullName = this.getPluginFullName(pluginData);
        this.registerQueryThread.execute(Task.builder()
            .runnable(() -> this.deploy(pluginData, pluginFullName, pluginId))
            .uncaughtExceptionHandler(
                (thread, cause) -> this.exceptionCaught(cause, pluginData.getPluginName(), pluginId))
            .buildDisposable());
    }

    private void deploy(PluginData pluginData, String pluginFullName, String pluginId) {
        log.info("start deploy plugin, pluginId: {}", pluginId);
        Path deployPath = this.generateDeployPath(pluginFullName).resolve(pluginFullName);
        Path persistentPath = this.generatePersistentPath(pluginData);
        if (!this.completenessCheck(persistentPath.resolve(pluginFullName).toFile(),
            this.getChecksumFromPluginData(pluginData))) {
            log.error("Completeness check failed before deploy. [pluginFile={}]", pluginId);
            pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYMENT_FAILED);
            return;
        }
        try {
            FileUtils.ensureDirectory(deployPath.getParent().toFile());
            Files.copy(persistentPath.resolve(pluginFullName), deployPath, StandardCopyOption.REPLACE_EXISTING);
            List<FitableInfo> fitableInfos = this.pluginToolService.getPluginTools(pluginId)
                .stream()
                .map(this::getFitableInfo)
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
            List<FitableAddressInstance> result = registryService.queryFitables(fitableInfos, "");
            if (result.size() == fitableInfos.size() && result.stream()
                .allMatch(info -> info.getApplicationInstances().size() > 0)) {
                return true;
            }
            ThreadUtils.sleep(this.pluginDeployQueryConfig.getInterval() * 1000L);
        }
        return false;
    }

    private boolean isQueryTimeout(long startTimestamp) {
        return System.currentTimeMillis() - startTimestamp > this.pluginDeployQueryConfig.getTimeout() * 1000L;
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

    private void exceptionCaught(Throwable cause, String pluginName, String pluginId) {
        log.error(StringUtils.format("Failed to deploy file. [pluginFile={0}]", pluginName), cause);
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
            List<String> missFiles = this.checkNecessaryFiles(tempDir);
            if (CollectionUtils.isNotEmpty(missFiles)) {
                throw new PluginDeployException(PluginDeployRetCode.FILE_MISSING_ERROR, missFiles.toString());
            }
            File validationFile = this.getFileByName(tempDir, PLUGIN_JSON);
            File toolsJsonFile = this.getFileByName(tempDir, TOOLS_JSON);
            this.saveTool(toolFile, validationFile, toolsJsonFile, toolNames);
        } finally {
            FileUtils.delete(tempDir);
        }
    }

    private List<String> checkNecessaryFiles(File tempDir) {
        List<String> files = Arrays.stream(this.getFiles(tempDir)).map(File::getName).collect(Collectors.toList());
        List<String> missingFiles = new ArrayList<>();
        if (files.stream().noneMatch(PLUGIN_JSON::equals)) {
            missingFiles.add(PLUGIN_JSON);
        }
        if (files.stream().noneMatch(TOOLS_JSON::equals)) {
            missingFiles.add(TOOLS_JSON);
        }
        return missingFiles;
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
        pluginData.setCreator(USER);
        pluginData.setModifier(USER);
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
        // 如果已有相同插件的状态是已部署或者部署中，插件唯一性校验失败，插件上传失败
        if (Objects.equals(pluginData.getDeployStatus(), DeployStatus.DEPLOYED.toString()) || Objects.equals(
            pluginData.getDeployStatus(), DeployStatus.DEPLOYING.toString())) {
            throw new PluginDeployException(PluginDeployRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
        // 未部署或者部署失败的相同插件可以被新插件替换
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
        if (!tool.containsKey(TOOLS)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tools.json should contain key: tools.");
        }
        List<Map<String, Object>> objs = cast(tool.get(TOOLS));
        if (CollectionUtils.isEmpty(objs)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tools in tools.json extensions cannot be empty.");
        }
        objs.forEach(this::validateToolFile);
    }

    private void validateToolFile(Map<String, Object> tool) {
        this.validateToolSchema(tool);
        this.validateToolRunnables(tool);
        this.validateToolExtensions(tool);
    }

    private void validateToolExtensions(Map<String, Object> tool) {
        if (tool.containsKey(TAGS)) {
            List<String> tags = cast(tool.get(TAGS));
            if (tags.isEmpty()) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "tags in tools.json extensions cannot be empty.");
            }
            return;
        }
        if (!tool.containsKey(EXTENSIONS)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tools.json should contain key: tags.");
        }
        Map<String, Object> extensions = cast(tool.get(EXTENSIONS));
        if (!extensions.containsKey(TAGS)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "extensions in tools.json should contain key: tags.");
        }
        List<String> tags = cast(extensions.get(TAGS));
        if (tags.isEmpty()) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tags in tools.json extensions cannot be empty.");
        }
    }

    private void validateToolRunnables(Map<String, Object> tool) {
        if (!tool.containsKey(RUNNABLES)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tools.json should contain key: runnables.");
        }
        Map<String, Object> runnables = cast(tool.get(RUNNABLES));
        if (runnables.containsKey(FIT)) {
            Map<String, Object> fit = cast(runnables.get(FIT));
            if (!fit.containsKey(FITABLE_ID)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "runnables in tools.json should contain key: fitableId.");
            }
            if (!fit.containsKey(GENERICABLE_ID)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "runnables in tools.json should contain key: genericableId.");
            }
        }
    }

    private void validateToolSchema(Map<String, Object> tool) {
        if (!tool.containsKey(TOOL_SCHEMA)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "tools.json should contain key: schema.");
        }
        Map<String, Object> schema = cast(tool.get(TOOL_SCHEMA));
        if (!schema.containsKey(PARAMETERS)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "schema in tools.json should contain key: parameters.");
        }
        Map<String, Object> parameters = cast(schema.get(PARAMETERS));
        if (!parameters.containsKey(TYPE) || !Objects.equals("object",
            this.getStringInMapObject(parameters.get(TYPE)))) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "parameters in tools.json should contain key: type, value: object.");
        }
        if (!parameters.containsKey(REQUIRED)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "parameters in tools.json should contain key: required.");
        }
        if (!parameters.containsKey(PROPERTIES)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "parameters in tools.json should contain key: properties.");
        }
        List<String> required = cast(parameters.get(REQUIRED));
        Map<String, Object> properties = cast(parameters.get(PROPERTIES));
        if (required.size() > properties.size()) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "the size of required in tools.json cannot be larger than properties size.");
        }
        if (!schema.containsKey(ORDER)) {
            return;
        }
        List<String> order = cast(schema.get(ORDER));
        if (!order.isEmpty() && order.size() != properties.size()) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "order size should be zero or equal to properties size in tools.json.");
        }
    }

    private void validatePluginJson(File pluginJson) {
        Map<String, Object> plugin = this.getJsonInfo(pluginJson);
        if (!plugin.containsKey(TYPE)) {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "plugin.json should contain key: type.");
        }
        if (plugin.get(TYPE).equals(PYTHON)) {
            if (!plugin.containsKey(PYTHON)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "plugin.json should contain key: python.");
            }
            Map<String, Object> python = cast(plugin.get(PYTHON));
            if (!python.containsKey(NAME)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "python plugin.json must contain key: name.");
            } else {
                if (StringUtils.isBlank(this.getStringInMapObject(python.get(NAME)))) {
                    throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                        "python plugin.json must contain valid key: name.");
                }
            }
        } else if (plugin.get(TYPE).equals(JAVA)) {
            Map<String, Object> java = cast(plugin.get(JAVA));
            if (!java.containsKey(ARTIFACT_ID)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "java plugin.json must contain key: artifactId.");
            } else {
                if (StringUtils.isBlank(this.getStringInMapObject(java.get(ARTIFACT_ID)))) {
                    throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                        "java plugin.json must contain valid key: artifactId.");
                }
            }
            if (!java.containsKey(GROUP_ID)) {
                throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                    "java plugin.json must contain key: groupId.");
            } else {
                if (StringUtils.isBlank(this.getStringInMapObject(java.get(GROUP_ID)))) {
                    throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                        "java plugin.json must contain valid key: groupId.");
                }
            }
        } else {
            throw new PluginDeployException(PluginDeployRetCode.JSON_PARSE_ERROR,
                "plugin.json type can only contain python and java.");
        }
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        this.initDeployStatus();
    }
}