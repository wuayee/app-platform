/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.build.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.plugin.maven.support.AbstractManifest;
import modelengine.fitframework.util.XmlUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.util.Map;

/**
 * 表示错误信息的清单。
 *
 * @author 季聿阶
 * @since 2023-06-18
 */
public class ErrorManifest extends AbstractManifest {
    private static final String ROOT_TAG = "errors";

    private final Map<String, Integer> errors;

    public ErrorManifest(Map<String, Integer> errors) {
        this.errors = errors;
    }

    /**
     * 将错误码的元数据信息写入到指定的输出流中。
     *
     * @param out 表示待写入 XML 元数据信息的输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws MojoExecutionException 当写入构成中发生输入输出异常时。
     */
    public void write(OutputStream out) throws MojoExecutionException {
        notNull(out, "The output stream to write manifest cannot be null.");
        Document document = XmlUtils.createDocument();
        document.setXmlStandalone(true);
        Element root = XmlUtils.appendElement(document, ROOT_TAG);
        for (Map.Entry<String, Integer> entry : this.errors.entrySet()) {
            Element error = XmlUtils.appendElement(root, "error");
            error.setAttribute("class", entry.getKey());
            error.setAttribute("code", entry.getValue().toString());
        }
        outputDocument(out, document);
    }
}
