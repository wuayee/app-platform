/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.controller;

import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.getRunnableInfo;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.getSchemaInfo;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolsJsonSchema;

import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;

import org.apache.maven.surefire.shared.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
    private static final String TOOL_PATH = "/var/tools";

    private final ToolService toolService;

    /**
     * 通过工具的通用服务来初始化 {@link UploadFileController} 的新实例。
     *
     * @param toolService 表示工具服务的 {@link ToolService}。
     */
    public UploadFileController(@Fit ToolService toolService) {
        this.toolService = toolService;
    }

    /**
     * 表示上传工具文件的请求。
     *
     * @param fileName 表示上传的文件名的 {@link String}。
     * @param receivedFile 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @return 返回上传文件中的 JSON schema 数据列表的 {@link List}{@code <}{@link MethodEntity}{@code >}。
     * @throws IOException 上传文件失败后抛出的异常。
     */
    @PostMapping(path = "/file", description = "上传工具文件")
    public List<MethodEntity> uploadFile(
            @RequestHeader(value = "tool-filename", defaultValue = "blank") String fileName,
            PartitionedEntity receivedFile) throws IOException {
        String uniqueFileName = generateUniqueFileName(fileName);
        log.info("Upload tool file fileName={} uniqueFileName={}.", fileName, uniqueFileName);
        File targetFile = Paths.get(TOOL_PATH, uniqueFileName).toFile();
        List<NamedEntity> entityList =
                receivedFile.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (!entityList.isEmpty()) {
            storeFile(fileName, entityList, targetFile);
        }
        log.info("Upload file fileName={} uniqueFileName={} success.", fileName, uniqueFileName);
        addToolData(this.toolService, targetFile.getPath());
        return parseToolsJsonSchema(targetFile.getPath());
    }

    private static void storeFile(String fileName, List<NamedEntity> entityList, File targetFile) {
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
    }

    private String generateUniqueFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("The file name can not be empty.");
        }
        return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private static void addToolData(ToolService toolService, String filePath) throws IOException {
        ToolData toolData = new ToolData();
        List<MethodEntity> schemaList = parseToolsJsonSchema(filePath);
        List<Map<String, Object>> schemaInfo = getSchemaInfo(filePath);
        List<Map<String, Object>> runnableInfo = getRunnableInfo(filePath);
        for (int i = 0; i < schemaList.size(); i++) {
            MethodEntity methodEntity = schemaList.get(i);
            toolData.setName(methodEntity.getMethodName());
            toolData.setDescription(methodEntity.getMethodDescription());
            toolData.setSchema(schemaInfo.get(i));
            toolData.setRunnables(runnableInfo.get(i));
            toolData.setTags(runnableInfo.get(i).keySet());
            toolService.addTool(toolData);
        }
    }
}
