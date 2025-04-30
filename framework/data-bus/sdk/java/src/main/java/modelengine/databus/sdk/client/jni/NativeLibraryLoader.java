/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.client.jni;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * DataBus JNI动态链接库加载器，将JAR包中的动态链接库转换为系统类可识别的绝对路径文件，并使用系统类加载。
 * <ol>
 *   <li>创建临时链接库文件路径</li>
 *   <li>将位于JAR包中资源目录下的链接库内容拷贝到临时链接库文件</li>
 *   <li>使用系统类加载临时链接库</li>
 * </ol>
 *
 * @author 李哲峰
 * @since 2024-05-17
 */
public class NativeLibraryLoader {
    private static final String NATIVE_LIBS_DIR = "/jni/";

    /**
     * 从JAR包中加载JNI动态链接库
     *
     * @param libraryName 动态链接库名称
     * @throws IOException 文件系统操作异常
     */
    public static void loadLibrary(String libraryName) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile(libraryName, ".so");
        // JVM 退出时删除临时文件
        tempFile.deleteOnExit();

        // 从 JAR 包中获取资源文件的输入流
        try (InputStream is = NativeLibraryLoader.class.getResourceAsStream(
                NATIVE_LIBS_DIR + libraryName + ".so")) {
            if (is == null) {
                throw new IllegalArgumentException("Library " + libraryName + " is not found in JAR.");
            }

            // 将输入流中的数据写入临时文件
            Files.copy(is, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // 加载临时文件中的链接库
        System.load(tempFile.getCanonicalPath());
    }
}
