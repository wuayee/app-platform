/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.utils;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.util.StringUtils;

/**
 * 表示文件提取内容处理的工具类。
 *
 * @author 兰宇晨
 * @since 2025-01-16
 */
public class ContentUtils {
    /**
     * 按照模板拼接文件内容。
     *
     * @param fileName 表示文件名的 {@link String}。
     * @param content 表示文件内容的 {@link String}。
     * @return 表示拼接后文件内容的 {@link String}。
     */
    public static String buildContent(String fileName, String content) {
        return StringUtils.format("File: {0}\n<content>\n{1}\n</content>\n\n", fileName, content);
    }

    /**
     * 获取文件名。
     *
     * @param fileUrl 表示文件链接的 {@link String}。
     * @return 表示文件名的 {@link String}。
     */
    public static String getFileName(String fileUrl) {
        notBlank(fileUrl, "File url must not be empty.");
        int lastIndex = fileUrl.lastIndexOf('/');
        if (lastIndex == -1 || lastIndex == fileUrl.length() - 1) {
            throw new IllegalArgumentException("File url ends with '/' or does not contain '/'.");
        }
        return fileUrl.substring(lastIndex + 1);
    }
}
