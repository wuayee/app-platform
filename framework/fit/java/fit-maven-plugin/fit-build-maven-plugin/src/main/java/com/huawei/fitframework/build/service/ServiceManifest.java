/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.build.service;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.build.support.AbstractManifest;
import com.huawei.fitframework.util.XmlUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;

import java.io.OutputStream;

/**
 * 表示服务信息的清单。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-19
 */
public class ServiceManifest extends AbstractManifest {
    private static final String ROOT_TAG = "service";

    /**
     * 将服务的元数据信息写入到指定的输出流中。
     *
     * @param out 表示待写入 XML 元数据信息的输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws MojoExecutionException 当写入过程中发生输入输出异常时。
     */
    public void write(OutputStream out) throws MojoExecutionException {
        notNull(out, "The output stream to write manifest cannot be null.");
        Document document = XmlUtils.createDocument();
        document.setXmlStandalone(true);
        XmlUtils.appendElement(document, ROOT_TAG);
        outputDocument(out, document);
    }
}
