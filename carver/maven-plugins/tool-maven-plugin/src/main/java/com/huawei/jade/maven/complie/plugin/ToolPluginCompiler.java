/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.plugin;

import com.huawei.fitframework.jvm.classfile.ClassFile;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.maven.support.AbstractCompiler;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.maven.complie.entity.MethodEntity;
import com.huawei.jade.maven.complie.parser.ByteBuddyToolParser;
import com.huawei.jade.maven.complie.util.JsonConvertUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 为工具插件提供编译程序。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-13
 */
public class ToolPluginCompiler extends AbstractCompiler {
    private static final Logger log = Logger.get(ToolPluginCompiler.class);
    private static final String TOOL_MANIFEST = "tools.json";

    private final List<MethodEntity> methodEntities = new ArrayList<>();

    ToolPluginCompiler(MavenProject project, Log log) {
        super(project, log, null);
    }

    @Override
    protected void output(String outputDirectory, String fitRootDirectory) throws MojoExecutionException {
        scanClassFiles(outputDirectory);
        outputToolManifest(fitRootDirectory);
    }

    private void scanClassFiles(String outputDirectory) throws MojoExecutionException {
        try (Stream<Path> paths = Files.walk(Paths.get(outputDirectory))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(ClassFile.FILE_EXTENSION))
                    .forEach(this::addMethodEntity);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to list class files.", e);
        }
    }

    private void addMethodEntity(Path classPath) {
        ByteBuddyToolParser.INSTANCE.parseTool(classPath)
                .getMethods()
                .stream()
                .filter(method -> method != null && method.getMethodName() != null)
                .forEach(this.methodEntities::add);
    }

    private void outputToolManifest(String outputDirectory) throws MojoExecutionException {
        if (this.methodEntities.isEmpty()) {
            return;
        }
        String fileName = outputDirectory + File.separator + TOOL_MANIFEST;
        File jsonFile = new File(fileName);
        try {
            List<Map<String, Object>> tools = new ArrayList<>();
            for (MethodEntity methodEntity : this.methodEntities) {
                tools.add(JsonConvertUtils.convertMethodEntityObjectMap(methodEntity));
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> toolJson = new HashMap<>();
            toolJson.put("tools", tools);
            objectMapper.writeValue(jsonFile, toolJson);
            log.info("Write tool json successfully. [file={}]", fileName);
        } catch (IOException e) {
            log.info("Failed to write tool manifest. [file={}]", fileName);
            throw new MojoExecutionException(StringUtils.format("Failed to write tool manifest. [file={0}]", fileName),
                    e);
        }
    }
}
