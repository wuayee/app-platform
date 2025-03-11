/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package modelengine.jade.oms.entity;


import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;
import java.util.Optional;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

/**
 * 表示文件类型的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class FileEntity implements Entity {
    private static final Map<String, MimeType> MIME_TYPE_MAP = MapBuilder.<String, MimeType>get()
            .put(".js", MimeType.APPLICATION_JAVASCRIPT)
            .put(".css", MimeType.TEXT_CSS)
            .build();

    private final String filename;
    private final InputStream in;
    private final long length;

    /**
     * 创建文件类型的消息体数据对象。
     *
     * @param filename 表示消息体内容所属文件的名字的  {@link String}。
     * @param in 表示消息体内容所属文件的输入流的 {@link InputStream}。
     * @param length 表示消息体内容所属文件的大小的 {@code long}。
     */
    public FileEntity(String filename, InputStream in, long length) {
        this.filename = notBlank(filename, "The filename cannot be blank.");
        this.in = getIfNull(in, () -> new ByteArrayInputStream(new byte[0]));
        this.length = greaterThanOrEquals(length, 0, "The file length must not be negative. [length={0}]", length);
    }

    /**
     * 获取文件名。
     *
     * @return 返回文件名的 {@link String}。
     */
    public String filename() {
        return this.filename;
    }

    /**
     * 获取文件的输入流。
     *
     * @return 返回文件的输入流的 {@link InputStream}。
     */
    public InputStream inputStream() {
        return this.in;
    }

    /**
     * 获取文件的长度。
     *
     * @return 返回文件的长度的 {@code long}。
     */
    public long length() {
        return this.length;
    }

    private String getFileExtension() {
        return StringUtils.toLowerCase(FileUtils.extension(this.filename));
    }

    @Override
    @Nonnull
    public MimeType resolvedMimeType() {
        return Optional.ofNullable(MimeType.from(URLConnection.getFileNameMap().getContentTypeFor(this.filename)))
                .orElseGet(() -> {
                    String fileExtension = this.getFileExtension();
                    return Optional.ofNullable(MIME_TYPE_MAP.get(fileExtension))
                            .orElse(MimeType.APPLICATION_OCTET_STREAM);
                });
    }

    @Override
    public void close() throws IOException {
    }
}
