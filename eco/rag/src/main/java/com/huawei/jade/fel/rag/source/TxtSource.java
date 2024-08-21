/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.source;

import modelengine.fitframework.log.Logger;
import com.huawei.jade.fel.engine.operators.sources.Source;
import com.huawei.jade.fel.rag.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * txt类型的数据源。
 * <p>将txt类型的数据包装为Document类型。</p>
 *
 * @since 2024-05-07
 */
public class TxtSource extends Source<List<Document>> {
    private static final Logger logger = Logger.get(TxtSource.class);

    private String content;

    private void contentExtract(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.lineSeparator());
            }
        } catch (IOException e) {
            logger.debug(e.getMessage());
            throw new IllegalArgumentException("Invalid file");
        }

        content = sb.toString();
    }

    /**
     * 从给定路径读取数据。
     *
     * @param path 表示给定路径的 {@link String}。
     */
    public void load(String path) {
        contentExtract(path);
        List<Document> docs = new ArrayList<>();
        docs.add(new Document(IdGenerator.getId(), content, null));
        emit(docs);
    }

    /**
     * 从输入流读取数据。
     *
     * @param inputStream 表示输入流的 {@link InputStream}。
     */
    public void load(InputStream inputStream) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
                contentBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.debug(e.getMessage());
            throw new IllegalArgumentException("Invalid input stream");
        }
        List<Document> docs = new ArrayList<>();
        docs.add(new Document(IdGenerator.getId(), contentBuilder.toString(), null));
        emit(docs);
    }
}
