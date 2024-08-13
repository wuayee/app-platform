/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.support;

import com.huawei.fitframework.build.util.ArtifactDownloader;
import com.huawei.fitframework.build.util.VersionHelper;
import com.huawei.fitframework.maven.MavenCoordinate;
import com.huawei.fitframework.parameterization.ParameterizedString;
import com.huawei.fitframework.parameterization.ParameterizedStringResolver;
import com.huawei.fitframework.parameterization.ResolvedParameter;
import com.huawei.fitframework.plugin.maven.support.AbstractExecutor;
import com.huawei.fitframework.plugin.maven.support.SharedDependency;
import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.protocol.jar.Jar.Entry;
import com.huawei.fitframework.protocol.jar.JarEntryLocation;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 为重打包程序提供基类。
 *
 * @author 梁济时
 * @since 2023-02-28
 */
public abstract class AbstractRepackager extends AbstractExecutor {
    /** 表示备份文件的后缀名。 */
    public static final String BACKUP_FILE_SUFFIX = ".backup";

    /** 表示在 Jar 包内的文件路径分隔符。 */
    protected static final String PATH_SEPARATOR = String.valueOf(JarEntryLocation.ENTRY_PATH_SEPARATOR);

    /** 表示基础的需要打包进 Jar in Jar 的依赖列表。 */
    protected static final List<String> BASE_ARTIFACT_IDS = Collections.singletonList("fit-protocol-nestable-jar");

    /** 表示框架的 groupId 名字。 */
    protected static final String FRAMEWORK_GROUP_ID = "com.huawei.fitframework";

    private static final String YML = "yml";
    private static final String YAML = "yaml";

    public AbstractRepackager(MavenProject project, Log log, List<SharedDependency> sharedDependencies) {
        super(project, log, sharedDependencies);
    }

    /**
     * 从项目的所有依赖中找出指定的基础依赖列表，并将其 Jar 包下载准备就绪。
     *
     * @param dependencies 表示项目的所有依赖列表的 {@link Set}{@code <}{@link Artifact}{@code >}。
     * @param downloader 表示依赖下载器的 {@link ArtifactDownloader}。
     * @return 表示准备就绪的依赖列表的 {@link List}{@code <}{@link Artifact}{@code >}。
     * @throws MojoExecutionException 当准备基础依赖列表过程中发生异常时。
     */
    protected static List<Artifact> prepareBaseArtifacts(Set<Artifact> dependencies, ArtifactDownloader downloader)
            throws MojoExecutionException {
        List<Artifact> artifacts = new ArrayList<>(BASE_ARTIFACT_IDS.size());
        for (String baseArtifactId : BASE_ARTIFACT_IDS) {
            Artifact artifact = AbstractRepackager.prepareBaseArtifact(dependencies, baseArtifactId, downloader);
            artifacts.add(artifact);
        }
        return artifacts;
    }

    private static Artifact prepareBaseArtifact(Set<Artifact> dependencies, String baseArtifactId,
            ArtifactDownloader downloader) throws MojoExecutionException {
        Artifact artifact = AbstractRepackager.lookupBaseArtifact(dependencies, baseArtifactId);
        if (artifact != null) {
            return artifact;
        }
        String version = VersionHelper.read();
        MavenCoordinate coordinate = MavenCoordinate.create(FRAMEWORK_GROUP_ID, baseArtifactId, version);
        List<Artifact> artifacts = downloader.download(coordinate);
        artifact = AbstractRepackager.lookupBaseArtifact(artifacts, baseArtifactId);
        if (artifact == null) {
            throw new MojoExecutionException(StringUtils.format("Failed to download depended JAR. [coordinate={0}]",
                    coordinate));
        }
        return artifact;
    }

    private static Artifact lookupBaseArtifact(Iterable<Artifact> artifacts, String baseArtifactId) {
        for (Artifact artifact : artifacts) {
            if (Objects.equals(artifact.getGroupId(), FRAMEWORK_GROUP_ID) && Objects.equals(artifact.getArtifactId(),
                    baseArtifactId)) {
                return artifact;
            }
        }
        return null;
    }

    /**
     * 备份原始文件。
     *
     * @param origin 表示原始文件的 {@link File}。
     * @return 表示备份后的文件的 {@link File}。
     * @throws MojoExecutionException 执行备份失败。
     */
    protected File backupFile(File origin) throws MojoExecutionException {
        String filename = origin.getName();
        filename += BACKUP_FILE_SUFFIX;
        File target = new File(origin.getParentFile(), filename);
        try {
            this.log().info(StringUtils.format("Prepare to backup original jar file. [origin={0}]", origin.getName()));
            target = Files.move(origin.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
            this.log().info(StringUtils.format("Backup original jar file successfully. [new={0}]", target.getName()));
            return target;
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to backup JAR. [file={0}]", origin.getPath()));
        }
    }

    /**
     * 将指定文件加载为 Jar 格式。
     *
     * @param file 表示待加载的文件的 {@link File}。
     * @return 表示加载后的文件的 {@link Jar}。
     * @throws MojoExecutionException 当加载过程发生异常时。
     */
    protected static Jar loadJar(File file) throws MojoExecutionException {
        try {
            return Jar.from(file);
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to load JAR from file. [file={0}]",
                    file.getPath()), e);
        }
    }

    private String replacePlaceholders(Entry entry, String value) {
        if (!isConfig(entry)) {
            return value;
        }
        ParameterizedStringResolver resolver = ParameterizedStringResolver.create("${", "}", '\0');
        ParameterizedString template = resolver.resolve(value);
        List<ResolvedParameter> parameters = template.getParameters();
        Map<String, String> arguments = new HashMap<>(parameters.size());
        for (ResolvedParameter parameter : parameters) {
            String argumentValue = System.getProperty(parameter.getName());
            if (StringUtils.isBlank(argumentValue)) {
                argumentValue = this.project().getProperties().getProperty(parameter.getName());
            }
            arguments.put(parameter.getName(), argumentValue);
        }
        return template.format(arguments);
    }

    private static boolean isConfig(Entry entry) {
        return StringUtils.endsWithIgnoreCase(entry.name(), YML) || StringUtils.endsWithIgnoreCase(entry.name(), YAML);
    }

    /**
     * 判断 Jar 中的指定子文件内容是不是 UTF-8 格式。
     *
     * @param entry 表示 Jar 中的指定子文件入口的 {@link Entry}。
     * @return 如果指定文件的内容是 UTF-8 格式，则返回 {@code true}，否则，返回 {@code false}。
     * @throws MojoExecutionException 当解析过程中发生异常时。
     */
    protected static boolean isUtf8(Entry entry) throws MojoExecutionException {
        try (InputStream in = entry.read()) {
            return StringUtils.isUtf8(in);
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format(
                    "Failed to read content of JAR entry to check whether the entry contains UTF-8 text. [url={0}]",
                    entry.location()), e);
        }
    }

    /**
     * 判断 Jar 中的指定子文件是不是还是一个 Jar。
     *
     * @param entry 表示 Jar 中的指定子文件入口的 {@link Entry}。
     * @return 如果指定子文件还是一个 Jar，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected static boolean isJar(Entry entry) {
        return StringUtils.endsWithIgnoreCase(entry.name(), Jar.FILE_EXTENSION);
    }

    /**
     * 获取 Jar 中的指定子文件的内容。
     *
     * @param entry 表示 Jar 中的指定子文件入口的 {@link Entry}。
     * @return 表示指定子文件的内容的 {@link String}。
     * @throws MojoExecutionException 当解析过程发生异常时。
     */
    protected static String contentOf(Entry entry) throws MojoExecutionException {
        try (InputStream in = entry.read()) {
            return IoUtils.content(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to read string content of JAR entry. [url={0}]",
                    entry.location()), e);
        }
    }

    /**
     * 获取 Jar 中的指定子文件的文本内容。
     *
     * @param entry 表示 Jar 中的指定子文件入口的 {@link Entry}。
     * @return 表示 Jar 中的指定子文件的文本内容的 {@link String}。
     * @throws MojoExecutionException 当获取文本内容过程发生异常时。
     */
    protected String getEntryContent(Entry entry) throws MojoExecutionException {
        String content = contentOf(entry);
        content = this.replacePlaceholders(entry, content);
        return content;
    }
}
