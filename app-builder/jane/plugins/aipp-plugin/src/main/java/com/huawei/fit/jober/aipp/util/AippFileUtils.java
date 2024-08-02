/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 文件操作工具
 *
 * @author l00611472
 * @since 2024/1/22
 */
public class AippFileUtils {
    /**
     * 上传文件会上传到该共享目录。
     */
    public static final String NAS_SHARE_DIR = "/var/share";
    private static final String DOWNLOAD_FILE_ORIGIN = "/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?";
    private static final Logger log = Logger.get(AippFileUtils.class);

    /**
     * 在本地临时目录 AippFileUtils.NAS_SHARE_DIR 创建子目录及文件。
     *
     * @param dirName 表示子目录名称的 {@link String}。
     * @param fileName 表示文件名字的 {@link String}。
     * @return 表示创建的临时文件的 {@link File}。
     * @throws IOException 创建文件出现问题时抛出该异常
     */
    public static File createFile(String dirName, String fileName) throws IOException {
        Validation.notBlank(dirName, "dirName cant be blank.");
        Validation.notBlank(fileName, "fileName cant be blank.");

        File dir = Paths.get(NAS_SHARE_DIR, dirName).toFile();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException(dir.getCanonicalPath() + " created failed.");
            }
        }
        File docFile = Paths.get(dir.getCanonicalPath(), fileName).toFile();
        if (!docFile.exists()) {
            if (!docFile.createNewFile()) {
                log.warn(docFile.getCanonicalPath() + " already exist.");
            }
        }
        return docFile;
    }

    /**
     * 删除文件。
     *
     * @param file 文件句柄
     */
    public static void deleteFile(File file) {
        try {
            FileUtils.delete(file);
        } catch (IllegalStateException e) {
            log.warn("delete file {} failed, error = {}", file.getName(), e.getMessage());
        }
    }

    /**
     * 从s3获取文件到本地临时目录 {@link AippFileUtils#NAS_SHARE_DIR}。
     * <p><b>注意：</b>使用结束需要手动删除临时文件。</p>
     *
     * @param instId 表示实例唯一标识的 {@link String}, 作为子目录名称。
     * @param s3Url 表示s3的访问地址的 {@link String}。
     * @param fileType 表示文件类型的 {@link String}。
     * @param httpClient 表示发送http请求的客户端的{@link HttpClassicClient}
     * @return 表示下载下来的临时文件的 {@link File}。
     * @throws JobberException 下载文件异常时抛出
     */
    public static File getFileFromS3(String instId, String s3Url, String fileType, HttpClassicClient httpClient)
            throws JobberException {
        HttpClassicClientRequest request = httpClient.createRequest(HttpRequestMethod.GET, s3Url);
        File tmpFile;
        try (HttpClassicClientResponse<Object> response = HttpUtils.execute(request)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                throw new IOException(String.format(Locale.ROOT,
                        "bad result code=%d",
                        response.statusCode()));
            }
            tmpFile = createFile(instId, fileType + "_" + UUIDUtil.uuid());
            try (InputStream inStream = new ByteArrayInputStream(response.entityBytes());
                 OutputStream outStream = Files.newOutputStream(tmpFile.toPath())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    StringUtils.format("Fail to get file from s3. [fileType={0}, url={1}, error={2}]",
                            fileType,
                            s3Url,
                            e.getMessage()));
        }
        return tmpFile;
    }

    /**
     * 获得文件的可下载地址
     *
     * @param endpoint 表示app-engine启动环境地址的{@link String}
     * @param pathPrefix 表示文件url根的{@link String}
     * @param filePath 表示文件路径的{@link String}
     * @param fileName 表示下载后保存的文件名的{@link String}
     * @return 表示文件的可下载的url
     */
    public static String getFileDownloadUrl(String endpoint, String pathPrefix, String filePath, String fileName) {
        return endpoint + (StringUtils.isBlank(pathPrefix) ? StringUtils.EMPTY : pathPrefix) + DOWNLOAD_FILE_ORIGIN
                + "filePath=" + filePath + "&fileName=" + fileName;
    }

    /**
     * 获得音频文件的文件路径的url
     *
     * @param endpoint 表示app-engine启动环境地址的{@link String}
     * @param pathPrefix 表示文件url根的{@link String}
     * @param filePath 表示文件路径的{@link String}
     * @return 表示音频文件路径的url
     */
    public static String getFileDownloadFilePath(String endpoint, String pathPrefix, String filePath) {
        return endpoint + (StringUtils.isBlank(pathPrefix) ? StringUtils.EMPTY : pathPrefix) + DOWNLOAD_FILE_ORIGIN
                + "filePath=" + filePath;
    }
}
