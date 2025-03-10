/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.maven.compile.parser;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.jade.carver.tool.info.entity.ToolJsonEntity;
import modelengine.jade.maven.compile.plugin.UrlClassLoaderInitializer;

import net.bytebuddy.pool.TypePool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 添加组解析的测试。
 *
 * @author 曹嘉美
 * @since 2024-10-30
 */
@DisplayName("测试 ByteBuddyGroupParserTest 类")
public class ByteBuddyGroupParserTest {
    @Test
    @DisplayName("解析class文件，生成出正确的json文件")
    void givenClassesThenParseJsonSuccess() throws IOException {
        String outputDirectory = Paths.get("").toAbsolutePath().resolve("target/test-classes").toString();
        String fitRootDirectory = "";
        UrlClassLoaderInitializer urlClassLoaderInitializer = new UrlClassLoaderInitializer();
        URLClassLoader urlClassLoader = urlClassLoaderInitializer.initUrlClassLoader(outputDirectory, fitRootDirectory);
        GroupParser groupParser = new ByteBuddyGroupParser(TypePool.Default.of(urlClassLoader), outputDirectory);
        ToolJsonEntity toolJsonEntity = groupParser.parseJson(outputDirectory);
        urlClassLoaderInitializer.outputToolManifest(outputDirectory, toolJsonEntity);
        Path correctJsonPath = Paths.get(
                "src/test/java/modelengine/jade/maven/compile/parser/weather/weather-tools.json");
        Path testJsonPath = Paths.get("target/test-classes/tools.json");
        List<String> correctJson = Files.readAllLines(correctJsonPath);
        List<String> testJson = Files.readAllLines(testJsonPath);
        assertThat(correctJson).containsExactlyInAnyOrderElementsOf(testJson);
    }
}
